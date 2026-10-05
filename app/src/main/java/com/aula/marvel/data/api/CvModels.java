package com.aula.marvel.data.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Formato da Comic Vine API. Toda resposta vem num envelope { error, status_code, results }:
 * em listas, results é um array; em detalhes (ex.: character/4005-1443/), um objeto.
 */
public final class CvModels {
    private CvModels() {}

    public static class ListResponse<T> {
        public String error;
        @SerializedName("status_code") public int statusCode;
        @SerializedName("number_of_total_results") public int total;
        public List<T> results;
    }

    public static class DetailResponse<T> {
        public String error;
        @SerializedName("status_code") public int statusCode;
        public T results;
    }

    public static class Image {
        @SerializedName("thumb_url") public String thumb;
        @SerializedName("small_url") public String small;
        @SerializedName("medium_url") public String medium;
        @SerializedName("super_url") public String superUrl;
        @SerializedName("original_url") public String original;
    }

    /** Referência resumida (id + nome) usada em listas aninhadas. */
    public static class Ref {
        public long id;
        public String name;
        @SerializedName("issue_number") public String issueNumber;
    }

    public static class Person {
        public long id;
        public String name;
        public String role;
    }

    public static class Character {
        public long id;
        public String name;
        @SerializedName("real_name") public String realName;
        public String deck;
        public String description;
        public String birth;
        public Image image;
        public Ref publisher;
        @SerializedName("first_appeared_in_issue") public Ref firstAppearedInIssue;
        @SerializedName("count_of_issue_appearances") public int appearances;
        public List<Ref> powers;
        public List<Ref> teams;
        @SerializedName("issue_credits") public List<Ref> issueCredits;
    }

    public static class Team {
        public long id;
        public String name;
        public String deck;
        public Image image;
        public Ref publisher;
        @SerializedName("first_appeared_in_issue") public Ref firstAppearedInIssue;
        @SerializedName(value = "count_of_isssue_appearances", alternate = {"count_of_issue_appearances"})
        public int appearances;
        @SerializedName("count_of_team_members") public int membersCount;
        /** Integrantes da equipe. */
        public List<Ref> characters;
        @SerializedName("story_arc_credits") public List<Ref> storyArcCredits;
    }

    public static class Issue {
        public long id;
        public String name;
        @SerializedName("issue_number") public String issueNumber;
        @SerializedName("cover_date") public String coverDate;
        public Image image;
        public Ref volume;
        @SerializedName("person_credits") public List<Person> personCredits;
        @SerializedName("character_credits") public List<Ref> characterCredits;
    }

    public static class StoryArc {
        public long id;
        public String name;
        public String deck;
        public Image image;
        public Ref publisher;
        @SerializedName("first_appeared_in_issue") public Ref firstAppearedInIssue;
        @SerializedName(value = "count_of_isssue_appearances", alternate = {"count_of_issue_appearances"})
        public int issueCount;
    }

    public static class Volume {
        public long id;
        public String name;
        @SerializedName("start_year") public String startYear;
        public Ref publisher;
        @SerializedName("count_of_issues") public int issueCount;
    }
}
