package com.aula.marvel.data;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import com.aula.marvel.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Catálogo local dos integrantes das três equipes do app: nome em pt-BR, cor da paleta do
 * personagem e arte recortada sem fundo (do protótipo ou recortada e revisada à mão; 0 = sem arte,
 * a tela usa a foto da API). A Comic Vine devolve nomes em inglês,
 * então a chave é o nome da API.
 */
public final class HeroCatalog {
    private HeroCatalog() {}

    private static final class Entry {
        final String pt; @ColorRes final int color; @DrawableRes final int art;
        Entry(String pt, int color, int art) { this.pt = pt; this.color = color; this.art = art; }
    }

    private static final Map<String, Entry> BY_NAME = new HashMap<>();
    private static final Map<String, List<String>> CORE = new HashMap<>();

    private static void put(String api, String pt, @ColorRes int color, @DrawableRes int art) {
        BY_NAME.put(api.toLowerCase(Locale.ROOT), new Entry(pt, color, art));
    }

    static {
        // X-Men (ordem do carrossel do Figma: Noturno, Vampira, Wolverine)
        put("Nightcrawler", "Noturno", R.color.marvel_blue, R.drawable.img_char_noturno);
        put("Rogue", "Vampira", R.color.marvel_red, R.drawable.img_char_vampira);
        put("Wolverine", "Wolverine", R.color.marvel_yellow, R.drawable.img_char_wolverine);
        put("Cyclops", "Ciclope", R.color.marvel_blue, R.drawable.img_char_cyclops);
        put("Jean Grey", "Jean Grey", R.color.marvel_green, R.drawable.img_char_jean_grey);
        put("Storm", "Tempestade", R.color.hero_gray, R.drawable.img_char_storm);
        put("Beast", "Fera", R.color.marvel_blue_deep, R.drawable.img_char_beast);
        put("Iceman", "Homem de Gelo", R.color.marvel_blue_light, R.drawable.img_char_iceman);
        put("Colossus", "Colossus", R.color.hero_gray, R.drawable.img_char_colossus);
        put("Gambit", "Gambit", R.color.hero_purple, R.drawable.img_char_gambit);
        put("Professor X", "Professor X", R.color.hero_purple, R.drawable.img_char_professor_x);
        put("Jubilee", "Jubileu", R.color.marvel_yellow, R.drawable.img_char_jubilee);
        put("Psylocke", "Psylocke", R.color.hero_purple, R.drawable.img_char_psylocke);
        put("Shadowcat", "Lince Negra", R.color.marvel_yellow, R.drawable.img_char_kitty_pryde);
        put("Kitty Pryde", "Lince Negra", R.color.marvel_yellow, R.drawable.img_char_kitty_pryde);
        put("Archangel", "Arcanjo", R.color.marvel_blue_light, R.drawable.img_char_angel);
        put("Angel", "Anjo", R.color.marvel_blue_light, R.drawable.img_char_angel);
        put("Emma Frost", "Emma Frost", R.color.hero_gray, R.drawable.img_char_emma_frost);
        put("Cable", "Cable", R.color.hero_gray, R.drawable.img_char_cable);
        put("Bishop", "Bishop", R.color.hero_teal, R.drawable.img_char_bishop);
        put("Havok", "Destrutor", R.color.marvel_blue_deep, R.drawable.img_char_havok);
        put("Polaris", "Polaris", R.color.marvel_green, R.drawable.img_char_polaris);
        // Vingadores
        put("Spider-Man", "Homem-Aranha", R.color.marvel_red, R.drawable.img_char_spider);
        put("Captain America", "Capitão América", R.color.marvel_blue, R.drawable.img_char_captain);
        put("Hulk", "Hulk", R.color.marvel_green, R.drawable.img_char_hulk);
        put("Iron Man", "Homem de Ferro", R.color.marvel_red, R.drawable.img_char_iron_man);
        put("Thor", "Thor", R.color.marvel_blue_light, R.drawable.img_char_thor);
        put("Black Widow", "Viúva Negra", R.color.hero_gray, R.drawable.img_char_black_widow);
        put("Hawkeye", "Gavião Arqueiro", R.color.hero_purple, R.drawable.img_char_hawkeye);
        put("Vision", "Visão", R.color.marvel_green, R.drawable.img_char_vision);
        put("Scarlet Witch", "Feiticeira Escarlate", R.color.marvel_red, R.drawable.img_char_scarlet_witch);
        put("Black Panther", "Pantera Negra", R.color.hero_purple, R.drawable.img_char_black_panther);
        put("Ant-Man", "Homem-Formiga", R.color.marvel_red, R.drawable.img_char_ant_man);
        put("Wasp", "Vespa", R.color.marvel_yellow, R.drawable.img_char_wasp);
        put("Falcon", "Falcão", R.color.marvel_red, R.drawable.img_char_falcon);
        put("Captain Marvel", "Capitã Marvel", R.color.marvel_blue, R.drawable.img_char_captain_marvel);
        put("She-Hulk", "Mulher-Hulk", R.color.marvel_green, R.drawable.img_char_she_hulk);
        put("Wonder Man", "Magnum", R.color.marvel_red, R.drawable.img_char_wonder_man);
        // Quarteto Fantástico
        put("Mister Fantastic", "Senhor Fantástico", R.color.marvel_blue, R.drawable.img_char_mister_fantastic);
        put("Mr. Fantastic", "Senhor Fantástico", R.color.marvel_blue, R.drawable.img_char_mister_fantastic);
        put("Invisible Woman", "Mulher Invisível", R.color.marvel_blue_light, R.drawable.img_char_invisible_woman);
        put("Human Torch", "Tocha Humana", R.color.hero_orange, R.drawable.img_char_human_torch);
        put("Thing", "Coisa", R.color.hero_orange, R.drawable.img_char_thing);
        put("Crystal", "Crystal", R.color.hero_teal, R.drawable.img_char_crystal);
        put("Medusa", "Medusa", R.color.marvel_red, R.drawable.img_char_medusa);
        put("Franklin Richards", "Franklin Richards", R.color.marvel_blue, R.drawable.img_char_franklin_richards);

        CORE.put(MockData.TEAM_XMEN, Arrays.asList("Nightcrawler", "Rogue", "Wolverine", "Cyclops",
                "Jean Grey", "Storm", "Beast", "Iceman", "Colossus", "Gambit", "Professor X", "Jubilee",
                "Psylocke", "Shadowcat", "Kitty Pryde", "Archangel", "Angel", "Emma Frost", "Cable",
                "Bishop", "Havok", "Polaris"));
        CORE.put(MockData.TEAM_AVENGERS, Arrays.asList("Captain America", "Iron Man", "Thor", "Hulk",
                "Black Widow", "Hawkeye", "Spider-Man", "Vision", "Scarlet Witch", "Black Panther",
                "Ant-Man", "Wasp", "Falcon", "Captain Marvel", "She-Hulk", "Wonder Man"));
        CORE.put(MockData.TEAM_FANTASTIC, Arrays.asList("Mister Fantastic", "Mr. Fantastic",
                "Invisible Woman", "Human Torch", "Thing", "She-Hulk", "Crystal", "Medusa",
                "Franklin Richards"));
    }

    private static Entry entry(String apiName) {
        return apiName == null ? null : BY_NAME.get(apiName.trim().toLowerCase(Locale.ROOT));
    }

    public static String displayName(String apiName) {
        Entry e = entry(apiName);
        return e != null ? e.pt : apiName;
    }

    @ColorRes public static int color(String apiName) {
        Entry e = entry(apiName);
        return e != null ? e.color : R.color.marvel_red;
    }

    /** Arte recortada local (0 quando não há recorte desse personagem). */
    @DrawableRes public static int art(String apiName) {
        Entry e = entry(apiName);
        return e != null ? e.art : 0;
    }

    /** Integrantes principais da equipe, na ordem de exibição. */
    public static List<String> core(String teamKey) {
        List<String> names = CORE.get(teamKey);
        return names == null ? new ArrayList<>() : names;
    }

    /** Posição do nome na lista principal da equipe (-1 se não estiver). */
    public static int coreIndex(String teamKey, String apiName) {
        if (apiName == null) return -1;
        List<String> names = core(teamKey);
        for (int i = 0; i < names.size(); i++) if (names.get(i).equalsIgnoreCase(apiName.trim())) return i;
        return -1;
    }

    // Títulos com que os arcos famosos saíram no Brasil. Tradução automática de nome próprio fica ruim
    // ("Saga de Phoenix escuro"), então os demais mantêm o título original.
    private static final Map<String, String> ARC_TITLES = new HashMap<>();
    static {
        String[][] arcs = {
            {"Dark Phoenix Saga", "Saga da Fênix Negra"},
            {"The Dark Phoenix Saga", "Saga da Fênix Negra"},
            {"Days of Future Past", "Dias de um Futuro Esquecido"},
            {"God Loves, Man Kills", "Deus Ama, o Homem Mata"},
            {"Mutant Massacre", "Massacre de Mutantes"},
            {"Fall of the Mutants", "A Queda dos Mutantes"},
            {"Inferno", "Inferno"},
            {"Age of Apocalypse", "A Era do Apocalipse"},
            {"Onslaught", "Massacre"},
            {"Operation: Zero Tolerance", "Operação Tolerância Zero"},
            {"House of M", "Dinastia M"},
            {"Messiah Complex", "Complexo de Messias"},
            {"Schism", "Cisma"},
            {"Avengers vs. X-Men", "Vingadores vs. X-Men"},
            {"Kree-Skrull War", "A Guerra Kree-Skrull"},
            {"The Korvac Saga", "A Saga de Korvac"},
            {"Civil War", "Guerra Civil"},
            {"Secret Invasion", "Invasão Secreta"},
            {"Secret Wars", "Guerras Secretas"},
            {"Infinity Gauntlet", "Desafio Infinito"},
            {"The Infinity Gauntlet", "Desafio Infinito"},
            {"Galactus Trilogy", "A Trilogia de Galactus"},
            {"The Galactus Trilogy", "A Trilogia de Galactus"},
        };
        for (String[] a : arcs) ARC_TITLES.put(a[0].toLowerCase(Locale.ROOT), a[1]);
    }

    /** Título do arco em português quando é um arco conhecido; senão, o original. */
    public static String arcTitle(String apiName) {
        if (apiName == null) return null;
        String pt = ARC_TITLES.get(apiName.trim().toLowerCase(Locale.ROOT));
        return pt != null ? pt : apiName;
    }
}
