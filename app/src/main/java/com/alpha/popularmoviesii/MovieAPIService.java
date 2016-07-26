package com.alpha.popularmoviesii;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interfaces
 */
public interface MovieAPIService {

    @GET("popular?api_key="+Config.APIKey)
    Call<MoviePagination> getPopularMovies();

    @GET("top_rated?api_key="+Config.APIKey)
    Call<MoviePagination> getTopRatedMovies();

    @GET("videos?api_key="+Config.APIKey)
    Call<MovieTrailerPagination> getMovieTrailers();

    @GET("reviews?api_key="+Config.APIKey)
    Call<MovieReviewPagination> getMovieReviews();
}