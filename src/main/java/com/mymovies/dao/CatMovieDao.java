package com.mymovies.dao;

import com.mymovies.model.Category;
import com.mymovies.model.Movie;

import java.util.List;

public interface CatMovieDao {

    List<Movie> getMovies(int categoryId);

    List<Category> getCategories(int movieId);

    void deleteMovieFromCategory(int categoryId, int movieId);

    void moveMovieToCategory(int categoryId, int movieId);

}
