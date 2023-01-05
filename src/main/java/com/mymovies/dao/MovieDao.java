package com.mymovies.dao;

import com.mymovies.model.Movie;

import java.util.List;

public interface MovieDao {

    List<Movie> getAllMovies();

    void deleteMovie(int id);

    void updateMovie(int id, String title, String artist, String category, int duration, String path);

    int createMovie(String title, String artist, String category, int duration, String path);

    List<Movie> searchMovie(String search);

}
