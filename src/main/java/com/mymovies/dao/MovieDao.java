package com.mymovies.dao;

import com.mymovies.model.Movie;
import javafx.scene.control.CheckBox;

import java.util.List;

public interface MovieDao {

    List<Movie> getAllMovies();

    void deleteMovie(int id);

    void updateMovie(int id, String title, String director, Float rating, String moviePath, String trailerPath, int year, Float imdbScore);

    void updateMovieLike(int id, int like);

    void updateMovieLastview(int id, java.sql.Date lastview);

    int createMovie(String title, String director, Float rating, java.sql.Date lastview, String moviePath, String trailerPath, int year, Float imdbScore, CheckBox like);

}
