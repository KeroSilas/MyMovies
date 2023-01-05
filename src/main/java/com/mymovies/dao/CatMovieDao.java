package com.mymovies.dao;

import com.mymovies.model.Movie;

import java.util.List;

public interface CatMovieDao {

    List<Movie> getCategory(int categoryId);

    void deleteMovieFromCategory(int categoryId, int movieId);

    void moveMovieToCategory(int categoryId, int movieId);

}
