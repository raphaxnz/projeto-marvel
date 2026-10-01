package com.aula.marvel.data;

import androidx.annotation.ColorRes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MockData {
    private MockData() {}

    public static final class Hero {
        public final long id;
        public final String name, realName, team, initials, bio, firstAppearance, birth;
        public final List<String> powers;
        @ColorRes public final int color;
        public Hero(long id, String name, String realName, String team, String initials, String bio,
                    String firstAppearance, String birth, List<String> powers, @ColorRes int color) {
            this.id=id; this.name=name; this.realName=realName; this.team=team; this.initials=initials;
            this.bio=bio; this.firstAppearance=firstAppearance; this.birth=birth; this.powers=powers; this.color=color;
        }
    }

    public static final class Comic {
        public final long id; public final String title, issue, mark;
        public Comic(long id, String title, String issue, String mark) { this.id=id; this.title=title; this.issue=issue; this.mark=mark; }
    }

    public static final class TimelineEntry {
        public final long id; public final String year, title, description, issue;
        public TimelineEntry(long id, String year, String title, String description, String issue) { this.id=id; this.year=year; this.title=title; this.description=description; this.issue=issue; }
    }

    public static final class OnboardingPage {
        public final String mark, title, description;
        public OnboardingPage(String mark, String title, String description) { this.mark=mark; this.title=title; this.description=description; }
    }

    public static final class Member {
        public final String name, mark; @ColorRes public final int color;
        public Member(String name, String mark, @ColorRes int color) { this.name=name; this.mark=mark; this.color=color; }
    }

    public static List<OnboardingPage> onboarding() {
        return Arrays.asList(
            new OnboardingPage("SP", "Explore os personagens", "Conheça seus poderes, histórias e principais aparições."),
            new OnboardingPage("CA", "Descubra as equipes", "Veja integrantes, informações e HQs dos X-Men, Vingadores e Quarteto Fantástico."),
            new OnboardingPage("HK", "Viaje a linha do tempo", "Acompanhe os principais arcos e momentos de cada equipe.")
        );
    }

    public static List<Hero> heroes() {
        return Arrays.asList(
            new Hero(1,"Noturno","Kurt Wagner","X-Men","NW","Kurt Wagner é um mutante integrante dos X-Men. Criado em um circo, tornou-se um acrobata excepcional. Apesar da aparência demoníaca, possui personalidade gentil, bom humor e usa suas habilidades para proteger humanos e mutantes.","Giant-Size X-Men #1 · 1975","01/01/1960",Arrays.asList("Teletransporte","Visão noturna"), com.aula.marvel.R.color.app_blue),
            new Hero(2,"Vampira","Anna Marie","X-Men","VG","Uma mutante capaz de absorver memórias, habilidades e poderes por meio do contato físico. Sua força e coragem fizeram dela uma integrante essencial dos X-Men.","Avengers Annual #10 · 1981","",Arrays.asList("Absorção","Superforça"), com.aula.marvel.R.color.app_red_panel),
            new Hero(3,"Homem-Aranha","Peter Parker","Vingadores","SP","Peter Parker equilibra a vida comum com a responsabilidade de proteger Nova York usando suas habilidades aracnídeas.","Amazing Fantasy #15 · 1962","",Arrays.asList("Agilidade","Sentido aranha"), com.aula.marvel.R.color.app_primary),
            new Hero(4,"Tocha Humana","Johnny Storm","Quarteto Fantástico","TH","Johnny Storm controla o fogo, envolve o corpo em chamas e voa para enfrentar ameaças ao lado do Quarteto Fantástico.","Fantastic Four #1 · 1961","",Arrays.asList("Pirocinese","Voo"), com.aula.marvel.R.color.app_yellow),
            new Hero(5,"Wolverine","Logan","X-Men","WV","Um mutante com fator de cura, sentidos aguçados e garras de adamantium. Wolverine luta para proteger sua equipe enquanto enfrenta um passado fragmentado.","Incredible Hulk #180 · 1974","",Arrays.asList("Fator de cura","Garras"), com.aula.marvel.R.color.app_yellow),
            new Hero(6,"Capitão América","Steve Rogers","Vingadores","CA","Símbolo de coragem e liderança, Steve Rogers combate ameaças ao lado dos Vingadores.","Captain America Comics #1 · 1941","",Arrays.asList("Força","Liderança"), com.aula.marvel.R.color.app_blue),
            new Hero(7,"Hulk","Bruce Banner","Vingadores","HK","Quando sua raiva cresce, Bruce Banner se transforma no poderoso Hulk.","Incredible Hulk #1 · 1962","",Arrays.asList("Superforça","Resistência"), com.aula.marvel.R.color.app_chip_blue)
        );
    }

    public static Hero hero(long id) {
        for (Hero hero : heroes()) if (hero.id == id) return hero;
        return heroes().get(0);
    }

    public static List<Comic> comics() {
        return Arrays.asList(
            new Comic(1,"The Uncanny X-Men","#129","X"), new Comic(2,"Days of Future Past","#141","X"),
            new Comic(3,"God Loves, Man Kills","Especial","X"), new Comic(4,"Mutant Massacre","#210","X"),
            new Comic(5,"Age of Apocalypse","Alpha #1","A"), new Comic(6,"House of X","#1","HX")
        );
    }

    public static List<TimelineEntry> timeline() {
        return Arrays.asList(
            new TimelineEntry(1,"1980","Saga da Fênix Negra","O poder da Fênix começa a consumir Jean Grey e coloca os X-Men diante de uma escolha decisiva.","Edições #129–138"),
            new TimelineEntry(2,"1981","Dias de um Futuro Esquecido","Kitty Pryde retorna de um futuro dominado pelos Sentinelas para impedir uma tragédia.","Edições #141–142"),
            new TimelineEntry(3,"1982","Deus Ama, o Homem Mata","Os X-Men enfrentam uma campanha de perseguição contra os mutantes.","Edição especial")
        );
    }

    public static TimelineEntry timelineEntry(long id) {
        for (TimelineEntry entry : timeline()) if (entry.id == id) return entry;
        return timeline().get(0);
    }

    public static List<Member> members() {
        return Arrays.asList(
            new Member("noturno","NW",com.aula.marvel.R.color.app_blue),
            new Member("vampira","VG",com.aula.marvel.R.color.app_red_panel),
            new Member("wolverine","WV",com.aula.marvel.R.color.app_yellow)
        );
    }
}
