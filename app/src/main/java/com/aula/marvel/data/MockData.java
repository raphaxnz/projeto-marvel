package com.aula.marvel.data;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import com.aula.marvel.R;
import java.util.Arrays;
import java.util.List;

/**
 * Modelos de UI + dados do protótipo (Figma). Usados como fallback quando não há api_key
 * ou a Comic Vine não responde. Os campos mutáveis são preenchidos pelo MarvelRepository.
 */
public final class MockData {
    private MockData() {}

    public static final String TEAM_XMEN = "X-Men";
    public static final String TEAM_AVENGERS = "Vingadores";
    public static final String TEAM_FANTASTIC = "Quarteto Fantástico";

    public static final class Team {
        public final long id;
        public final String name, firstAppearance, membersCount;
        @DrawableRes public final int cover, header, badge;
        /** Chave da equipe no app (TEAM_XMEN / TEAM_AVENGERS / TEAM_FANTASTIC). */
        public String key;
        public String imageUrl;
        public Team(long id, String name, String firstAppearance, String membersCount,
                    @DrawableRes int cover, @DrawableRes int header, @DrawableRes int badge) {
            this.id = id; this.name = name; this.firstAppearance = firstAppearance; this.membersCount = membersCount;
            this.cover = cover; this.header = header; this.badge = badge;
        }
        public String key() { return key != null ? key : name; }
    }

    public static final class Hero {
        public final long id;
        public final String name, realName, team, bio, firstAppearance, birth;
        public final List<String> powers;
        @ColorRes public final int color;
        /** Arte recortada local (0 = sem arte; usa a imagem da API). */
        @DrawableRes public final int art, avatar;
        /** Nome original da API (inglês), usado na busca. */
        public String apiName;
        public String imageUrl;
        public String apiBio;
        /** IDs das primeiras HQs do personagem (Comic Vine). */
        public List<Long> issueIds;
        public Hero(long id, String name, String realName, String team, String bio,
                    String firstAppearance, String birth, List<String> powers, @ColorRes int color,
                    @DrawableRes int art, @DrawableRes int avatar) {
            this.id = id; this.name = name; this.realName = realName; this.team = team; this.bio = bio;
            this.firstAppearance = firstAppearance; this.birth = birth; this.powers = powers; this.color = color;
            this.art = art; this.avatar = avatar;
        }
    }

    public static final class Comic {
        public final long id; public final String title, issue; @DrawableRes public final int cover;
        public String coverUrl;
        public Comic(long id, String title, String issue, @DrawableRes int cover) { this.id = id; this.title = title; this.issue = issue; this.cover = cover; }
    }

    public static final class TimelineEntry {
        public final long id; public final String year, title, description, issue, publication, authors, team;
        @DrawableRes public final int cover, header;
        public String coverUrl;
        public long firstIssueId;
        public List<Hero> characters;
        public TimelineEntry(long id, String year, String title, String description, String issue,
                             String publication, String authors, String team,
                             @DrawableRes int cover, @DrawableRes int header) {
            this.id = id; this.year = year; this.title = title; this.description = description; this.issue = issue;
            this.publication = publication; this.authors = authors; this.team = team;
            this.cover = cover; this.header = header;
        }
    }

    public static final class OnboardingPage {
        public final String title, description; @DrawableRes public final int art;
        public OnboardingPage(String title, String description, @DrawableRes int art) { this.title = title; this.description = description; this.art = art; }
    }

    // ---------- assets locais por equipe (artes do Figma) ----------

    @DrawableRes public static int coverFor(String teamKey) {
        return TEAM_AVENGERS.equals(teamKey) ? R.drawable.img_team_avengers
                : TEAM_FANTASTIC.equals(teamKey) ? R.drawable.img_team_ff
                : R.drawable.img_team_xmen;
    }

    @DrawableRes public static int headerFor(String teamKey) {
        return TEAM_AVENGERS.equals(teamKey) ? R.drawable.img_team_avengers
                : TEAM_FANTASTIC.equals(teamKey) ? R.drawable.img_team_ff
                : R.drawable.img_team_xmen_header;
    }

    /** Símbolo da equipe (0 = personagem sem uma das três equipes). */
    @DrawableRes public static int badgeFor(String teamKey) {
        if (TEAM_XMEN.equals(teamKey)) return R.drawable.img_badge_xmen;
        if (TEAM_AVENGERS.equals(teamKey)) return R.drawable.img_badge_avengers;
        if (TEAM_FANTASTIC.equals(teamKey)) return R.drawable.img_badge_ff;
        return 0;
    }

    // ---------- dados do protótipo ----------

    public static List<OnboardingPage> onboarding() {
        return Arrays.asList(
            new OnboardingPage("Explore os personagens", "Conheça seus poderes, histórias e principais aparições.", R.drawable.img_char_spider),
            new OnboardingPage("Descubra as equipes", "Veja integrantes, informações e HQs dos X-Men, Vingadores e Quarteto Fantástico.", R.drawable.img_char_captain),
            new OnboardingPage("Viaje a linha do tempo", "Acompanhe os principais arcos e momentos de cada equipe.", R.drawable.img_char_hulk)
        );
    }

    public static List<Team> teams() {
        return Arrays.asList(
            new Team(1, TEAM_XMEN, "X-Men #1 - 1963", "20 membros", coverFor(TEAM_XMEN), headerFor(TEAM_XMEN), badgeFor(TEAM_XMEN)),
            new Team(2, TEAM_AVENGERS, "Avengers #1 - 1963", "30 membros", coverFor(TEAM_AVENGERS), headerFor(TEAM_AVENGERS), badgeFor(TEAM_AVENGERS)),
            new Team(3, TEAM_FANTASTIC, "Fantastic Four #1 - 1961", "4 membros", coverFor(TEAM_FANTASTIC), headerFor(TEAM_FANTASTIC), badgeFor(TEAM_FANTASTIC))
        );
    }

    public static Team team(long id) {
        for (Team t : teams()) if (t.id == id) return t;
        return teams().get(0);
    }

    public static List<Hero> heroes() {
        return Arrays.asList(
            new Hero(1, "Noturno", "Kurt Wagner", TEAM_XMEN,
                "Kurt Wagner é um mutante integrante dos X-Men. Criado em um circo, tornou-se um acrobata excepcional. Apesar da aparência demoníaca, possui personalidade gentil, bem-humorada e usa suas habilidades para proteger humanos e mutantes.",
                "Giant-Size X-Men #1 — 1975", "01/01/1960", Arrays.asList("Teletransporte", "Visão noturna"), R.color.marvel_blue,
                R.drawable.img_char_noturno, R.drawable.img_avatar_noturno),
            new Hero(2, "Vampira", "Anna Marie", TEAM_XMEN,
                "Uma mutante capaz de absorver memórias, habilidades e poderes por meio do contato físico. Sua força e coragem fizeram dela uma integrante essencial dos X-Men.",
                "Avengers Annual #10 — 1981", "—", Arrays.asList("Absorção", "Superforça", "Voo"), R.color.marvel_red,
                R.drawable.img_char_vampira, R.drawable.img_avatar_vampira),
            new Hero(3, "Homem-Aranha", "Peter Parker", TEAM_AVENGERS,
                "Peter Parker equilibra a vida comum com a responsabilidade de proteger Nova York usando suas habilidades aracnídeas.",
                "Amazing Fantasy #15 — 1962", "—", Arrays.asList("Agilidade", "Sentido aranha"), R.color.marvel_red,
                R.drawable.img_char_spider, R.drawable.img_avatar_spider),
            new Hero(4, "Tocha Humana", "Johnny Storm", TEAM_FANTASTIC,
                "Johnny Storm controla o fogo, envolve o corpo em chamas e voa para enfrentar ameaças ao lado do Quarteto Fantástico.",
                "Fantastic Four #1 — 1961", "—", Arrays.asList("Pirocinese", "Voo"), R.color.hero_orange,
                R.drawable.img_char_human_torch, R.drawable.img_avatar_torch),
            new Hero(5, "Wolverine", "Logan", TEAM_XMEN,
                "Um mutante com fator de cura, sentidos aguçados e garras de adamantium. Wolverine luta para proteger sua equipe enquanto enfrenta um passado fragmentado.",
                "Incredible Hulk #180 — 1974", "—", Arrays.asList("Fator de cura", "Garras"), R.color.marvel_yellow,
                R.drawable.img_char_wolverine, R.drawable.img_char_wolverine),
            new Hero(6, "Capitão América", "Steve Rogers", TEAM_AVENGERS,
                "Símbolo de coragem e liderança, Steve Rogers combate ameaças ao lado dos Vingadores.",
                "Captain America Comics #1 — 1941", "—", Arrays.asList("Força", "Liderança"), R.color.marvel_blue,
                R.drawable.img_char_captain, R.drawable.img_char_captain),
            new Hero(7, "Hulk", "Bruce Banner", TEAM_AVENGERS,
                "Quando sua raiva cresce, Bruce Banner se transforma no poderoso Hulk.",
                "Incredible Hulk #1 — 1962", "—", Arrays.asList("Superforça", "Resistência"), R.color.marvel_green,
                R.drawable.img_char_hulk, R.drawable.img_char_hulk)
        );
    }

    /** Heróis em destaque na Home (Figma: Aranha, Wolverine, Capitão, Hulk). */
    public static List<Hero> featured() {
        List<Hero> all = heroes();
        return Arrays.asList(all.get(2), all.get(4), all.get(5), all.get(6));
    }

    public static Hero hero(long id) {
        for (Hero hero : heroes()) if (hero.id == id) return hero;
        return heroes().get(0);
    }

    /** Membros do carrossel (Figma: Noturno, Vampira, Wolverine). */
    public static List<Hero> members(String teamKey) {
        List<Hero> all = heroes();
        if (TEAM_AVENGERS.equals(teamKey)) return Arrays.asList(all.get(5), all.get(6), all.get(2));
        if (TEAM_FANTASTIC.equals(teamKey)) return Arrays.asList(all.get(3));
        return Arrays.asList(all.get(0), all.get(1), all.get(4));
    }

    /** Personagens do arco (Figma 36:721: Vampira, Wolverine, Noturno). */
    public static List<Hero> arcCharacters() {
        List<Hero> all = heroes();
        return Arrays.asList(all.get(1), all.get(4), all.get(0));
    }

    public static List<Comic> comics() {
        return Arrays.asList(
            new Comic(1, "The uncanny", "#1", R.drawable.img_comic_uncanny_1),
            new Comic(2, "The uncanny", "#1", R.drawable.img_comic_uncanny_2),
            new Comic(3, "The uncanny", "#1", R.drawable.img_comic_uncanny_3),
            new Comic(4, "The uncanny", "#1", R.drawable.img_comic_uncanny_1),
            new Comic(5, "The uncanny", "#1", R.drawable.img_comic_uncanny_2),
            new Comic(6, "The uncanny", "#1", R.drawable.img_comic_uncanny_3),
            new Comic(7, "The uncanny", "#1", R.drawable.img_comic_uncanny_1),
            new Comic(8, "The uncanny", "#1", R.drawable.img_comic_uncanny_2),
            new Comic(9, "The uncanny", "#1", R.drawable.img_comic_uncanny_3)
        );
    }

    public static List<TimelineEntry> timeline() {
        return Arrays.asList(
            new TimelineEntry(1, "1980", "Saga da Fênix Negra",
                "O poder da Fênix começa a consumir Jean Grey, colocando os X-Men diante de uma escolha capaz de decidir o destino do universo.",
                "Edições #129 - #138.", "X-Men #1 - 1980", "Chris Claremont e John Byrne", TEAM_XMEN, R.drawable.img_arc_phoenix, R.drawable.img_arc_phoenix_header),
            new TimelineEntry(2, "1981", "Dias de um Futuro Esquecido",
                "Kitty Pryde retorna de um futuro dominado pelos Sentinelas para impedir um assassinato que levará à destruição dos mutantes.",
                "Edições #141", "X-Men #141 - 1981", "Chris Claremont e John Byrne", TEAM_XMEN, R.drawable.img_arc_future_past, R.drawable.img_arc_phoenix_header),
            new TimelineEntry(3, "1982", "Deus Ama, o Homem Mata",
                "Os X-Men e Magneto precisam unir forças contra William Stryker e sua campanha de ódio contra os mutantes.",
                "Edição especial", "Marvel Graphic Novel #5 - 1982", "Chris Claremont e Brent Anderson", TEAM_XMEN, R.drawable.img_arc_god_loves, R.drawable.img_arc_phoenix_header)
        );
    }

    public static TimelineEntry timelineEntry(long id) {
        for (TimelineEntry entry : timeline()) if (entry.id == id) return entry;
        return timeline().get(0);
    }
}
