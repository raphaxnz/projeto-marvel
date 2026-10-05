package com.aula.marvel.data.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.text.HtmlCompat;
import com.aula.marvel.data.HeroCatalog;
import com.aula.marvel.data.MockData;
import com.aula.marvel.data.PtTranslator;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fonte de dados do app: Comic Vine API restrita às três equipes (X-Men, Vingadores, Quarteto).
 * - Integrantes vêm do campo "characters" da equipe; arcos do campo "story_arc_credits".
 * - Respostas ficam em cache (memória + HTTP 24h) para respeitar o limite da API.
 * - Sem api_key ou com falha de rede, usa os dados do protótipo (MockData). Nunca lança erro pra UI.
 */
public final class MarvelRepository {
    public interface Callback<T> { void onResult(T result); }

    private static final String TAG = "MarvelRepo";
    private static final long PUBLISHER_MARVEL = 31;
    private static final int MAX_SINGLE_CALLS = 12;

    private static final String HERO_FIELDS = "id,name,real_name,deck,image,publisher,first_appeared_in_issue,count_of_issue_appearances,birth";
    private static final String HERO_DETAIL_FIELDS = HERO_FIELDS + ",description,powers,teams,issue_credits";
    private static final String TEAM_FIELDS = "id,name,deck,image,publisher,first_appeared_in_issue,count_of_isssue_appearances,count_of_team_members";
    private static final String TEAM_DETAIL_FIELDS = TEAM_FIELDS + ",characters,story_arc_credits";
    private static final String ISSUE_FIELDS = "id,name,issue_number,cover_date,image,volume";
    private static final String ISSUE_DETAIL_FIELDS = ISSUE_FIELDS + ",person_credits,character_credits";
    private static final String ARC_FIELDS = "id,name,deck,image,publisher,first_appeared_in_issue,count_of_isssue_appearances";
    private static final String VOLUME_FIELDS = "id,name,start_year,publisher,count_of_issues";

    /** chave do app → nome da equipe na API */
    private static final String[][] TEAMS = {
            {MockData.TEAM_XMEN, "X-Men"},
            {MockData.TEAM_AVENGERS, "Avengers"},
            {MockData.TEAM_FANTASTIC, "Fantastic Four"},
    };
    /** chave do app → {termo de busca, nomes aceitos do volume principal} */
    private static final Map<String, String[]> VOLUMES = new HashMap<>();
    static {
        VOLUMES.put(MockData.TEAM_XMEN, new String[]{"X-Men", "X-Men", "Uncanny X-Men", "The X-Men"});
        VOLUMES.put(MockData.TEAM_AVENGERS, new String[]{"Avengers", "Avengers", "The Avengers"});
        VOLUMES.put(MockData.TEAM_FANTASTIC, new String[]{"Fantastic Four", "Fantastic Four"});
    }
    private static final String[] FEATURED = {"Spider-Man", "Wolverine", "Captain America", "Hulk"};

    private static volatile MarvelRepository instance;

    private final ComicVineService service;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Handler main = new Handler(Looper.getMainLooper());
    private final String apiKey;

    private final Map<Long, MockData.Hero> heroes = new ConcurrentHashMap<>();
    private final Map<Long, MockData.Team> teamsById = new ConcurrentHashMap<>();
    private final Map<Long, String> keyByTeamId = new ConcurrentHashMap<>();
    private final Map<String, Long> teamIdByKey = new ConcurrentHashMap<>();
    private final Map<String, CvModels.Team> rawTeams = new ConcurrentHashMap<>();
    private final Map<String, List<MockData.Hero>> membersByKey = new ConcurrentHashMap<>();
    private final Map<String, List<MockData.TimelineEntry>> timelineByKey = new ConcurrentHashMap<>();
    private final Map<Long, MockData.TimelineEntry> arcs = new ConcurrentHashMap<>();
    private final Map<String, List<MockData.Comic>> comicsByKey = new ConcurrentHashMap<>();
    private final Map<Long, List<MockData.Comic>> comicsByHero = new ConcurrentHashMap<>();
    private final Map<Long, String> issueLabels = new ConcurrentHashMap<>();
    private final Map<String, Object> locks = new ConcurrentHashMap<>();
    private volatile List<MockData.Team> teamList;
    private volatile List<MockData.Hero> featuredList;

    private MarvelRepository(Context context, String apiKey) {
        this.apiKey = apiKey;
        // Começa a baixar o modelo de tradução já na abertura do app.
        if (hasApiKey()) PtTranslator.get();
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(new Cache(new File(context.getCacheDir(), "comicvine"), 20L * 1024 * 1024))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    HttpUrl url = request.url().newBuilder()
                            .addQueryParameter("api_key", apiKey)
                            .addQueryParameter("format", "json")
                            .build();
                    // Comic Vine exige User-Agent próprio (PDF do desafio).
                    return chain.proceed(request.newBuilder().url(url)
                            .header("User-Agent", "MarvelApp/1.0 (Android; projeto academico)").build());
                })
                .addNetworkInterceptor(chain -> {
                    okhttp3.Response response = chain.proceed(chain.request());
                    if (!response.isSuccessful()) return response;
                    return response.newBuilder().removeHeader("Pragma")
                            .header("Cache-Control", "public, max-age=86400").build();
                })
                .build();
        service = new Retrofit.Builder()
                .baseUrl(ComicVineService.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ComicVineService.class);
    }

    public static MarvelRepository get(Context context) {
        if (instance == null) {
            synchronized (MarvelRepository.class) {
                if (instance == null) {
                    Context app = context.getApplicationContext();
                    instance = new MarvelRepository(app, ApiKey.read(app));
                }
            }
        }
        return instance;
    }

    public boolean hasApiKey() { return apiKey != null && apiKey.length() > 10 && !apiKey.startsWith("$"); }

    // ======================= API pública (callbacks na main thread) =======================

    public void featuredHeroes(Callback<List<MockData.Hero>> cb) {
        async(this::loadFeatured, MockData::featured, cb);
    }

    public void teams(Callback<List<MockData.Team>> cb) {
        async(this::loadTeams, MockData::teams, cb);
    }

    public void team(long teamId, Callback<MockData.Team> cb) {
        async(() -> loadTeamDetail(teamId), () -> fallbackTeam(teamId), cb);
    }

    public void teamMembers(long teamId, Callback<List<MockData.Hero>> cb) {
        String key = keyForTeam(teamId);
        async(() -> loadMembers(key), () -> MockData.members(key), cb);
    }

    /** Personagens da busca: somente integrantes de X-Men, Vingadores e Quarteto. */
    public void searchable(Callback<List<MockData.Hero>> cb) {
        async(this::loadSearchable, MockData::heroes, cb);
    }

    public void character(long heroId, Callback<MockData.Hero> cb) {
        async(() -> loadHeroDetail(heroId), () -> fallbackHero(heroId), cb);
    }

    public void characterComics(long heroId, Callback<List<MockData.Comic>> cb) {
        async(() -> loadHeroComics(heroId), this::fallbackComics, cb);
    }

    public void teamComics(long teamId, Callback<List<MockData.Comic>> cb) {
        String key = keyForTeam(teamId);
        async(() -> loadTeamComics(key), this::fallbackComics, cb);
    }

    public void timeline(String teamKey, Callback<List<MockData.TimelineEntry>> cb) {
        async(() -> loadTimeline(teamKey),
                () -> MockData.TEAM_XMEN.equals(teamKey) ? MockData.timeline() : new ArrayList<>(), cb);
    }

    public void arc(long arcId, Callback<MockData.TimelineEntry> cb) {
        async(() -> loadArcDetail(arcId), () -> fallbackArc(arcId), cb);
    }

    // -------- leitura síncrona do cache (estado inicial das telas) --------

    public MockData.Hero cachedHero(long id) {
        MockData.Hero hero = heroes.get(id);
        return hero != null ? hero : (hasApiKey() ? null : MockData.hero(id));
    }

    public MockData.Team cachedTeam(long id) {
        MockData.Team team = teamsById.get(id);
        return team != null ? team : (hasApiKey() ? null : MockData.team(id));
    }

    public MockData.TimelineEntry cachedArc(long id) {
        MockData.TimelineEntry entry = arcs.get(id);
        return entry != null ? entry : (hasApiKey() ? null : MockData.timelineEntry(id));
    }

    public List<MockData.Hero> cachedMembers(long teamId) {
        return membersByKey.get(keyForTeam(teamId));
    }

    public String keyForTeam(long teamId) {
        String key = keyByTeamId.get(teamId);
        return key != null ? key : MockData.team(teamId).key();
    }

    // ======================= carregamento (thread de background) =======================

    private List<MockData.Hero> loadFeatured() {
        if (featuredList != null) return featuredList;
        List<MockData.Hero> out = new ArrayList<>();
        for (String name : FEATURED) {
            CvModels.Character best = null;
            for (CvModels.Character c : list(service.characters("name:" + name, HERO_FIELDS, 50))) {
                if (c.name == null || !c.name.equalsIgnoreCase(name) || !isMarvel(c.publisher)) continue;
                if (best == null || c.appearances > best.appearances) best = c;
            }
            if (best == null) return null;
            out.add(cacheHero(toHero(best, teamOfName(name), issueRefLabel(best.firstAppearedInIssue))));
        }
        featuredList = out;
        return out;
    }

    private List<MockData.Team> loadTeams() {
        if (teamList != null) return teamList;
        List<MockData.Team> out = new ArrayList<>();
        for (String[] t : TEAMS) {
            CvModels.Team summary = resolveTeam(t[0]);
            if (summary == null) return null;
            MockData.Team team = toTeam(summary, t[0], issueRefLabel(summary.firstAppearedInIssue));
            teamsById.put(team.id, team);
            out.add(team);
        }
        teamList = out;
        return out;
    }

    private MockData.Team loadTeamDetail(long teamId) {
        String key = keyByTeamId.get(teamId);
        if (key == null) return null;
        CvModels.Team raw = rawTeam(key);
        if (raw == null) return null;
        MockData.Team team = toTeam(raw, key, issueLabel(raw.firstAppearedInIssue));
        teamsById.put(team.id, team);
        return team;
    }

    private List<MockData.Hero> loadMembers(String key) {
        List<MockData.Hero> cached = membersByKey.get(key);
        if (cached != null) return cached;
        synchronized (lock("members:" + key)) {
            cached = membersByKey.get(key);
            if (cached != null) return cached;
            CvModels.Team raw = rawTeam(key);
            if (raw == null || raw.characters == null || raw.characters.isEmpty()) return null;

            List<CvModels.Ref> picked = pickMembers(key, raw.characters);
            List<Long> ids = new ArrayList<>();
            for (CvModels.Ref ref : picked) ids.add(ref.id);
            Map<Long, CvModels.Character> byId = new HashMap<>();
            for (CvModels.Character c : charactersByIds(ids)) byId.put(c.id, c);

            List<MockData.Hero> out = new ArrayList<>();
            for (CvModels.Ref ref : picked) {
                CvModels.Character c = byId.get(ref.id);
                out.add(cacheHero(c != null
                        ? toHero(c, key, issueRefLabel(c.firstAppearedInIssue))
                        : toHero(ref, key)));
            }
            membersByKey.put(key, out);
            Log.i(TAG, key + ": " + out.size() + " integrantes");
            return out;
        }
    }

    private List<MockData.Hero> loadSearchable() {
        Map<Long, MockData.Hero> out = new LinkedHashMap<>();
        boolean any = false;
        for (String[] t : TEAMS) {
            List<MockData.Hero> members = loadMembers(t[0]);
            if (members == null) continue;
            any = true;
            for (MockData.Hero h : members) if (!out.containsKey(h.id)) out.put(h.id, h);
        }
        return any ? new ArrayList<>(out.values()) : null;
    }

    private MockData.Hero loadHeroDetail(long heroId) {
        synchronized (lock("hero:" + heroId)) {
            return loadHeroDetailLocked(heroId);
        }
    }

    private MockData.Hero loadHeroDetailLocked(long heroId) {
        MockData.Hero cached = heroes.get(heroId);
        if (cached != null && cached.apiBio != null) return cached;
        CvModels.Character c = detail(service.character(heroId, HERO_DETAIL_FIELDS));
        if (c == null) return null;
        String key = cached != null && isTeamKey(cached.team) ? cached.team : teamOfCharacter(c);
        MockData.Hero hero = toHero(c, key, issueLabel(c.firstAppearedInIssue));
        String bio = cleanHtml(c.description, 900);
        hero.apiBio = pt(bio.isEmpty() ? cleanHtml(c.deck, 900) : bio);
        // A tela mostra até 8 poderes: traduz só esses.
        if (hero.powers.size() > 8) hero.powers.subList(8, hero.powers.size()).clear();
        for (int i = 0; i < hero.powers.size(); i++) hero.powers.set(i, pt(hero.powers.get(i)));
        hero.issueIds = new ArrayList<>();
        if (c.issueCredits != null) {
            for (CvModels.Ref ref : c.issueCredits) {
                if (hero.issueIds.size() >= 10) break;
                hero.issueIds.add(ref.id);
            }
        }
        // Sem o modelo de tradução ainda, não guarda: na próxima abertura vem traduzido.
        if (PtTranslator.get().isReady()) heroes.put(hero.id, hero);
        return hero;
    }

    private List<MockData.Comic> loadHeroComics(long heroId) {
        List<MockData.Comic> cached = comicsByHero.get(heroId);
        if (cached != null) return cached;
        MockData.Hero hero = loadHeroDetail(heroId);
        if (hero == null || hero.issueIds == null || hero.issueIds.isEmpty()) return null;
        List<MockData.Comic> out = toComics(issuesByIds(hero.issueIds));
        comicsByHero.put(heroId, out);
        return out;
    }

    private List<MockData.Comic> loadTeamComics(String key) {
        List<MockData.Comic> cached = comicsByKey.get(key);
        if (cached != null) return cached;
        long volumeId = resolveVolume(key);
        if (volumeId <= 0) return null;
        List<MockData.Comic> out = toComics(list(service.issues("volume:" + volumeId, ISSUE_FIELDS, 30, "cover_date:asc")));
        if (!out.isEmpty()) comicsByKey.put(key, out);
        return out;
    }

    private List<MockData.TimelineEntry> loadTimeline(String key) {
        List<MockData.TimelineEntry> cached = timelineByKey.get(key);
        if (cached != null) return cached;
        synchronized (lock("timeline:" + key)) {
            cached = timelineByKey.get(key);
            if (cached != null) return cached;
            CvModels.Team raw = rawTeam(key);
            if (raw == null) return null;
            List<MockData.TimelineEntry> out = new ArrayList<>();
            if (raw.storyArcCredits != null && !raw.storyArcCredits.isEmpty()) {
                List<Long> ids = new ArrayList<>();
                for (CvModels.Ref ref : raw.storyArcCredits) {
                    if (ids.size() >= 60) break;
                    ids.add(ref.id);
                }
                List<CvModels.StoryArc> found = byIds(ids,
                        filter -> list(service.storyArcs(filter, ARC_FIELDS, 100)),
                        id -> detail(service.storyArc(id, ARC_FIELDS)),
                        a -> a.id);

                List<Long> firstIssues = new ArrayList<>();
                for (CvModels.StoryArc a : found) {
                    if (a.firstAppearedInIssue != null && !firstIssues.contains(a.firstAppearedInIssue.id)) {
                        firstIssues.add(a.firstAppearedInIssue.id);
                    }
                }
                Map<Long, CvModels.Issue> issuesById = new HashMap<>();
                for (CvModels.Issue issue : issuesByIds(firstIssues)) issuesById.put(issue.id, issue);

                for (CvModels.StoryArc a : found) {
                    if (a.name == null) continue;
                    CvModels.Issue first = a.firstAppearedInIssue == null ? null : issuesById.get(a.firstAppearedInIssue.id);
                    String year = first != null ? year(first.coverDate) : "—";
                    String publication = first != null ? issueTitle(first) : issueRefLabel(a.firstAppearedInIssue);
                    String count = a.issueCount > 0 ? a.issueCount + (a.issueCount == 1 ? " edição" : " edições") : "";
                    MockData.TimelineEntry entry = new MockData.TimelineEntry(a.id, year, HeroCatalog.arcTitle(a.name),
                            pt(cleanHtml(a.deck, 400)), count, publication, "", key, 0, 0);
                    entry.coverUrl = imageUrl(a.image);
                    entry.firstIssueId = a.firstAppearedInIssue == null ? 0 : a.firstAppearedInIssue.id;
                    arcs.put(entry.id, entry);
                    out.add(entry);
                }
                Collections.sort(out, (x, y) -> {
                    int a = yearValue(x.year), b = yearValue(y.year);
                    return a != b ? Integer.compare(a, b) : x.title.compareToIgnoreCase(y.title);
                });
            }
            if (PtTranslator.get().isReady()) timelineByKey.put(key, out);
            Log.i(TAG, key + ": " + out.size() + " arcos");
            return out;
        }
    }

    private MockData.TimelineEntry loadArcDetail(long arcId) {
        MockData.TimelineEntry entry = arcs.get(arcId);
        if (entry == null) return null;
        if (entry.characters != null || entry.firstIssueId == 0) return entry;
        CvModels.Issue issue = detail(service.issue(entry.firstIssueId, ISSUE_DETAIL_FIELDS));
        if (issue == null) return entry;

        List<String> writers = new ArrayList<>();
        if (issue.personCredits != null) {
            for (CvModels.Person p : issue.personCredits) {
                if (p.name != null && p.role != null && p.role.toLowerCase(Locale.ROOT).contains("writer")
                        && !writers.contains(p.name)) writers.add(p.name);
            }
            if (writers.isEmpty()) {
                for (CvModels.Person p : issue.personCredits) {
                    if (writers.size() >= 2) break;
                    if (p.name != null) writers.add(p.name);
                }
            }
        }
        String authors = writers.isEmpty() ? "—" : String.join(" e ", writers.subList(0, Math.min(2, writers.size())));

        List<MockData.Hero> cast = new ArrayList<>();
        List<Long> missing = new ArrayList<>();
        if (issue.characterCredits != null) {
            // Mostra os personagens que têm arte de corpo inteiro no app; só usa os demais
            // (foto da API) quando a edição não tem nenhum deles.
            List<CvModels.Ref> picked = new ArrayList<>();
            for (CvModels.Ref ref : issue.characterCredits) {
                if (picked.size() >= 10) break;
                if (ref != null && HeroCatalog.art(ref.name) != 0) picked.add(ref);
            }
            if (picked.isEmpty()) {
                for (CvModels.Ref ref : issue.characterCredits) {
                    if (picked.size() >= 10) break;
                    if (ref != null) picked.add(ref);
                }
            }
            for (CvModels.Ref ref : picked) {
                MockData.Hero known = heroes.get(ref.id);
                if (known != null) cast.add(known); else missing.add(ref.id);
            }
        }
        for (CvModels.Character c : charactersByIds(missing)) {
            cast.add(cacheHero(toHero(c, teamOfName(c.name), issueRefLabel(c.firstAppearedInIssue))));
        }

        MockData.TimelineEntry full = new MockData.TimelineEntry(entry.id, entry.year, entry.title,
                entry.description, entry.issue, issueTitle(issue), authors, entry.team, entry.cover, entry.header);
        full.coverUrl = entry.coverUrl;
        full.firstIssueId = entry.firstIssueId;
        full.characters = cast;
        arcs.put(full.id, full);
        return full;
    }

    // ======================= equipes / volumes =======================

    /** Busca a equipe por nome exato (Marvel, maior nº de aparições) e guarda o id. */
    private CvModels.Team resolveTeam(String key) {
        String apiName = apiTeamName(key);
        CvModels.Team best = null;
        for (CvModels.Team t : list(service.teams("name:" + apiName, TEAM_FIELDS, 50))) {
            if (t.name == null || !t.name.equalsIgnoreCase(apiName) || !isMarvel(t.publisher)) continue;
            if (best == null || t.appearances > best.appearances) best = t;
        }
        if (best != null) {
            teamIdByKey.put(key, best.id);
            keyByTeamId.put(best.id, key);
        } else {
            Log.w(TAG, "equipe não encontrada: " + apiName);
        }
        return best;
    }

    /** Detalhe completo da equipe (integrantes + arcos), uma vez por sessão. */
    private CvModels.Team rawTeam(String key) {
        CvModels.Team cached = rawTeams.get(key);
        if (cached != null) return cached;
        synchronized (lock("team:" + key)) {
            cached = rawTeams.get(key);
            if (cached != null) return cached;
            Long id = teamIdByKey.get(key);
            if (id == null) {
                CvModels.Team summary = resolveTeam(key);
                if (summary == null) return null;
                id = summary.id;
            }
            CvModels.Team raw = detail(service.team(id, TEAM_DETAIL_FIELDS));
            if (raw != null) rawTeams.put(key, raw);
            return raw;
        }
    }

    private long resolveVolume(String key) {
        String[] spec = VOLUMES.get(key);
        if (spec == null) return 0;
        CvModels.Volume best = null, anyMarvel = null;
        for (CvModels.Volume v : list(service.volumes("name:" + spec[0], VOLUME_FIELDS, 100))) {
            if (v.name == null || !isMarvel(v.publisher)) continue;
            if (anyMarvel == null || v.issueCount > anyMarvel.issueCount) anyMarvel = v;
            for (int i = 1; i < spec.length; i++) {
                if (v.name.equalsIgnoreCase(spec[i]) && (best == null || v.issueCount > best.issueCount)) best = v;
            }
        }
        CvModels.Volume chosen = best != null ? best : anyMarvel;
        return chosen == null ? 0 : chosen.id;
    }

    /** Integrantes principais (catálogo) primeiro; completa com os demais até 12. */
    private static List<CvModels.Ref> pickMembers(String key, List<CvModels.Ref> all) {
        List<CvModels.Ref> core = new ArrayList<>();
        for (CvModels.Ref ref : all) if (ref != null && HeroCatalog.coreIndex(key, ref.name) >= 0) core.add(ref);
        Collections.sort(core, (a, b) -> Integer.compare(HeroCatalog.coreIndex(key, a.name), HeroCatalog.coreIndex(key, b.name)));

        List<CvModels.Ref> out = new ArrayList<>();
        Set<String> names = new HashSet<>();
        for (CvModels.Ref ref : core) {
            if (names.add(HeroCatalog.displayName(ref.name).toLowerCase(Locale.ROOT))) out.add(ref);
        }
        for (CvModels.Ref ref : all) {
            if (out.size() >= 12) break;
            if (ref == null || ref.name == null) continue;
            if (names.add(HeroCatalog.displayName(ref.name).toLowerCase(Locale.ROOT))) out.add(ref);
        }
        return out.size() > 24 ? out.subList(0, 24) : out;
    }

    // ======================= busca por lista de IDs =======================

    private List<CvModels.Character> charactersByIds(List<Long> ids) {
        return byIds(ids, filter -> list(service.characters(filter, HERO_FIELDS, 100)),
                id -> detail(service.character(id, HERO_FIELDS)), c -> c.id);
    }

    private List<CvModels.Issue> issuesByIds(List<Long> ids) {
        return byIds(ids, filter -> list(service.issues(filter, ISSUE_FIELDS, 100, null)),
                id -> detail(service.issue(id, ISSUE_FIELDS)), i -> i.id);
    }

    /**
     * Uma chamada com filter=id:a|b|c; o que não vier é buscado individualmente (limite de chamadas).
     * Resultados fora da lista pedida são descartados (evita "personagens aleatórios").
     */
    private static <T> List<T> byIds(List<Long> ids, Function<String, List<T>> batch,
                                     LongFunction<T> single, ToLongFunction<T> idOf) {
        List<T> out = new ArrayList<>();
        if (ids == null || ids.isEmpty()) return out;
        Set<Long> wanted = new HashSet<>(ids);
        StringBuilder filter = new StringBuilder("id:");
        for (int i = 0; i < ids.size(); i++) filter.append(i == 0 ? "" : "|").append(ids.get(i));

        Map<Long, T> found = new HashMap<>();
        for (T item : batch.apply(filter.toString())) {
            long id = idOf.applyAsLong(item);
            if (wanted.contains(id)) found.put(id, item);
        }
        int singles = 0;
        for (Long id : ids) {
            if (found.containsKey(id) || singles >= MAX_SINGLE_CALLS) continue;
            singles++;
            T item = single.apply(id);
            if (item != null) found.put(id, item);
        }
        for (Long id : ids) if (found.containsKey(id)) out.add(found.get(id));
        return out;
    }

    // ======================= mapeamento API → modelos de UI =======================

    private static MockData.Hero toHero(CvModels.Character c, String key, String firstAppearance) {
        List<String> powers = new ArrayList<>();
        if (c.powers != null) for (CvModels.Ref p : c.powers) if (p != null && p.name != null) powers.add(p.name);
        MockData.Hero hero = new MockData.Hero(c.id, HeroCatalog.displayName(c.name),
                c.realName == null || c.realName.isEmpty() ? "—" : c.realName,
                key, cleanHtml(c.deck, 400), firstAppearance,
                formatBirth(c.birth), powers,
                HeroCatalog.color(c.name), HeroCatalog.art(c.name), HeroCatalog.art(c.name));
        hero.apiName = c.name;
        hero.imageUrl = imageUrl(c.image);
        return hero;
    }

    private static MockData.Hero toHero(CvModels.Ref ref, String key) {
        MockData.Hero hero = new MockData.Hero(ref.id, HeroCatalog.displayName(ref.name), "—", key, "",
                "—", "—", new ArrayList<>(), HeroCatalog.color(ref.name), HeroCatalog.art(ref.name), HeroCatalog.art(ref.name));
        hero.apiName = ref.name;
        return hero;
    }

    private static MockData.Team toTeam(CvModels.Team t, String key, String firstAppearance) {
        String members = t.membersCount > 0 ? t.membersCount + " membros"
                : t.characters != null ? t.characters.size() + " membros" : "—";
        MockData.Team team = new MockData.Team(t.id, key, firstAppearance, members,
                MockData.coverFor(key), MockData.headerFor(key), MockData.badgeFor(key));
        team.key = key;
        team.imageUrl = imageUrl(t.image);
        return team;
    }

    private static List<MockData.Comic> toComics(List<CvModels.Issue> issues) {
        List<MockData.Comic> out = new ArrayList<>();
        for (CvModels.Issue i : issues) {
            String title = i.volume != null && i.volume.name != null ? i.volume.name : (i.name == null ? "HQ" : i.name);
            MockData.Comic comic = new MockData.Comic(i.id, title, "#" + nz(i.issueNumber, "?"), 0);
            comic.coverUrl = imageUrl(i.image);
            out.add(comic);
        }
        return out;
    }

    private MockData.Hero cacheHero(MockData.Hero hero) {
        MockData.Hero existing = heroes.get(hero.id);
        if (existing != null && existing.apiBio != null) return existing;
        heroes.put(hero.id, hero);
        return hero;
    }

    /** "Volume #N - AAAA" buscando a HQ (com cache); sem rede, usa o que a referência tem. */
    private String issueLabel(CvModels.Ref ref) {
        if (ref == null) return "—";
        String cached = issueLabels.get(ref.id);
        if (cached != null) return cached;
        CvModels.Issue issue = detail(service.issue(ref.id, ISSUE_FIELDS));
        String label = issue != null ? issueText(issue) : issueRefLabel(ref);
        issueLabels.put(ref.id, label);
        return label;
    }

    private static String issueText(CvModels.Issue issue) {
        String y = year(issue.coverDate);
        return issueTitle(issue) + ("—".equals(y) ? "" : " - " + y);
    }

    /** "Volume #N" (sem o ano). */
    private static String issueTitle(CvModels.Issue issue) {
        String volume = issue.volume != null && issue.volume.name != null ? issue.volume.name : nz(issue.name, "Edição");
        return volume + " #" + nz(issue.issueNumber, "?");
    }

    private static String issueRefLabel(CvModels.Ref ref) {
        if (ref == null) return "—";
        String n = ref.issueNumber == null ? "" : " #" + ref.issueNumber;
        return ref.name == null || ref.name.isEmpty() ? "Edição" + n : ref.name + n;
    }

    private String teamOfCharacter(CvModels.Character c) {
        if (c.teams != null) {
            for (String[] t : TEAMS) for (CvModels.Ref ref : c.teams) {
                if (ref != null && t[1].equalsIgnoreCase(ref.name)) return t[0];
            }
        }
        return teamOfName(c.name);
    }

    private static String teamOfName(String apiName) {
        for (String[] t : TEAMS) if (HeroCatalog.coreIndex(t[0], apiName) >= 0) return t[0];
        return "—";
    }

    private static boolean isTeamKey(String key) {
        for (String[] t : TEAMS) if (t[0].equals(key)) return true;
        return false;
    }

    private static String apiTeamName(String key) {
        for (String[] t : TEAMS) if (t[0].equals(key)) return t[1];
        return key;
    }

    private static boolean isMarvel(CvModels.Ref publisher) {
        return publisher == null || publisher.id == PUBLISHER_MARVEL
                || (publisher.name != null && publisher.name.toLowerCase(Locale.ROOT).contains("marvel"));
    }

    private static String imageUrl(CvModels.Image image) {
        if (image == null) return null;
        String url = image.medium != null ? image.medium : image.superUrl != null ? image.superUrl : image.small;
        // A Comic Vine usa uma imagem genérica quando o personagem não tem foto.
        return url == null || url.contains("blank.png") || url.contains("6373148-blank") ? null : url;
    }

    private static String year(String date) {
        return date != null && date.length() >= 4 ? date.substring(0, 4) : "—";
    }

    private static int yearValue(String year) {
        try { return Integer.parseInt(year); } catch (Exception e) { return 9999; }
    }

    private static String nz(String s, String fallback) { return s == null || s.isEmpty() ? fallback : s; }

    /** Conteúdo da Comic Vine é só em inglês: traduz para português no aparelho. */
    private static String pt(String text) { return PtTranslator.get().translate(text); }

    /** A Comic Vine envia o nascimento como "Mon d, yyyy"; exibimos dd/MM/yyyy. */
    private static String formatBirth(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "—";
        try {
            Date date = new SimpleDateFormat("MMM d, yyyy", Locale.US).parse(raw.trim());
            return date == null ? raw : new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(date);
        } catch (ParseException e) {
            return raw;
        }
    }

    /** Remove HTML da Comic Vine (figuras, tabelas, títulos) e limita o tamanho no fim de uma frase. */
    static String cleanHtml(String html, int max) {
        if (html == null || html.trim().isEmpty()) return "";
        String s = html.replaceAll("(?is)<figure.*?</figure>", " ")
                .replaceAll("(?is)<table.*?</table>", " ")
                .replaceAll("(?is)<h[1-6][^>]*>.*?</h[1-6]>", "\n");
        s = HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                .replace("\uFFFC", "")
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll("\\n\\s*\\n+", "\n\n")
                .trim();
        if (s.length() > max) {
            int cut = s.lastIndexOf(". ", max);
            s = cut > max / 2 ? s.substring(0, cut + 1) : s.substring(0, max).trim() + "…";
        }
        return s;
    }

    // ======================= fallbacks =======================

    private MockData.Team fallbackTeam(long teamId) {
        MockData.Team cached = teamsById.get(teamId);
        return cached != null ? cached : MockData.team(teamId);
    }

    private MockData.Hero fallbackHero(long heroId) {
        MockData.Hero cached = heroes.get(heroId);
        return cached != null ? cached : MockData.hero(heroId);
    }

    private MockData.TimelineEntry fallbackArc(long arcId) {
        MockData.TimelineEntry cached = arcs.get(arcId);
        return cached != null ? cached : MockData.timelineEntry(arcId);
    }

    private List<MockData.Comic> fallbackComics() {
        return hasApiKey() ? new ArrayList<>() : MockData.comics();
    }

    // ======================= infraestrutura =======================

    private <T> void async(Supplier<T> work, Supplier<T> fallback, Callback<T> cb) {
        executor.execute(() -> {
            T result = null;
            if (hasApiKey()) {
                try { result = work.get(); } catch (Exception e) { Log.w(TAG, "falha ao carregar", e); }
            }
            if (result == null || (result instanceof List && ((List<?>) result).isEmpty() && !hasApiKey())) {
                try { result = fallback.get(); } catch (Exception e) { Log.w(TAG, "falha no fallback", e); }
            }
            final T out = result;
            main.post(() -> { if (out != null) cb.onResult(out); });
        });
    }

    private <T> List<T> list(Call<CvModels.ListResponse<T>> call) {
        try {
            Response<CvModels.ListResponse<T>> r = call.execute();
            if (r.isSuccessful() && r.body() != null && r.body().results != null) return r.body().results;
            Log.w(TAG, "HTTP " + r.code() + " em " + call.request().url().encodedPath()
                    + (r.body() != null ? " — " + r.body().error : ""));
        } catch (Exception e) {
            Log.w(TAG, "erro em " + call.request().url().encodedPath(), e);
        }
        return new ArrayList<>();
    }

    private <T> T detail(Call<CvModels.DetailResponse<T>> call) {
        try {
            Response<CvModels.DetailResponse<T>> r = call.execute();
            if (r.isSuccessful() && r.body() != null) return r.body().results;
            Log.w(TAG, "HTTP " + r.code() + " em " + call.request().url().encodedPath());
        } catch (Exception e) {
            // "Object Not Found" vem com results = [] (array) e quebra o parse: tratado aqui.
            Log.w(TAG, "erro em " + call.request().url().encodedPath(), e);
        }
        return null;
    }

    private Object lock(String name) {
        return locks.computeIfAbsent(name, k -> new Object());
    }
}
