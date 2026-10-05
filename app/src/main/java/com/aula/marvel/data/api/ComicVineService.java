package com.aula.marvel.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Comic Vine API — https://comicvine.gamespot.com/api/
 * api_key, format=json e User-Agent são adicionados por interceptor (MarvelRepository).
 * Prefixos de recurso: 4005 personagem, 4060 equipe, 4000 HQ, 4045 arco, 4050 volume.
 */
public interface ComicVineService {
    String BASE_URL = "https://comicvine.gamespot.com/api/";

    @GET("characters/")
    Call<CvModels.ListResponse<CvModels.Character>> characters(
            @Query("filter") String filter, @Query("field_list") String fields, @Query("limit") int limit);

    @GET("character/4005-{id}/")
    Call<CvModels.DetailResponse<CvModels.Character>> character(
            @Path("id") long id, @Query("field_list") String fields);

    @GET("teams/")
    Call<CvModels.ListResponse<CvModels.Team>> teams(
            @Query("filter") String filter, @Query("field_list") String fields, @Query("limit") int limit);

    @GET("team/4060-{id}/")
    Call<CvModels.DetailResponse<CvModels.Team>> team(
            @Path("id") long id, @Query("field_list") String fields);

    @GET("issues/")
    Call<CvModels.ListResponse<CvModels.Issue>> issues(
            @Query("filter") String filter, @Query("field_list") String fields,
            @Query("limit") int limit, @Query("sort") String sort);

    @GET("issue/4000-{id}/")
    Call<CvModels.DetailResponse<CvModels.Issue>> issue(
            @Path("id") long id, @Query("field_list") String fields);

    @GET("story_arcs/")
    Call<CvModels.ListResponse<CvModels.StoryArc>> storyArcs(
            @Query("filter") String filter, @Query("field_list") String fields, @Query("limit") int limit);

    @GET("story_arc/4045-{id}/")
    Call<CvModels.DetailResponse<CvModels.StoryArc>> storyArc(
            @Path("id") long id, @Query("field_list") String fields);

    @GET("volumes/")
    Call<CvModels.ListResponse<CvModels.Volume>> volumes(
            @Query("filter") String filter, @Query("field_list") String fields, @Query("limit") int limit);
}
