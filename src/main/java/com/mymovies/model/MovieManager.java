package com.mymovies.model;

import com.mymovies.dao.CatMovieDao;
import com.mymovies.dao.CatMovieDaoImpl;
import com.mymovies.dao.MovieDao;
import com.mymovies.dao.MovieDaoImpl;
import javafx.scene.control.CheckBox;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that is responsible for manipulating the retrieved lists from MovieDao.
 * Also sends queries to the database.
 */

public class MovieManager {

    private final MovieDao movieDao;
    private final CatMovieDao catMovieDao;
    private final List<Movie> allMovies;

    public MovieManager() {
        movieDao = new MovieDaoImpl();
        catMovieDao = new CatMovieDaoImpl();
        allMovies = movieDao.getAllMovies();
        for (Movie m : allMovies) {
            m.setCategories(catMovieDao.getCategories(m.getId()));
        }
    }

    public List<Movie> getAllMovies() {
        return allMovies;
    }

    //Creates a new movie both locally and on the database.
    public void addMovie(String title, String director, Float rating, Date lastview, String moviePath, String trailerPath, int year, Float imdbScore, CheckBox like) {
        int movieId = movieDao.createMovie(title, director, rating, lastview, moviePath, trailerPath, year, imdbScore, like);
        allMovies.add(new Movie(movieId, title, director, rating, lastview, moviePath, trailerPath, year, imdbScore, like));
    }

    //Updates a movie both locally and on the database.
    public void updateMovie(Movie movie, String title, String director, Float rating, String moviePath, String trailerPath, int year, Float imdbScore) {
        for (Movie s : allMovies) {
            if (s.getId() == movie.getId()) {
                allMovies.get(allMovies.indexOf(s)).setTitle(title);
                allMovies.get(allMovies.indexOf(s)).setDirector(director);
                allMovies.get(allMovies.indexOf(s)).setRating(rating);
                allMovies.get(allMovies.indexOf(s)).setMoviePath(moviePath);
                allMovies.get(allMovies.indexOf(s)).setTrailerPath(trailerPath);
            }
        }
        movieDao.updateMovie(movie.getId(), title, director, rating, moviePath, trailerPath, year, imdbScore);
    }

    public void updateMovieLike(Movie movie, int like) {
        for (Movie m : allMovies) {
            if (m.getId() == movie.getId()) {
                allMovies.get(allMovies.indexOf(m)).setLike(like);
            }
        }
        movieDao.updateMovieLike(movie.getId(), like);
    }

    public void updateMovieLastview(Movie movie, java.sql.Date lastview) {
        for (Movie m : allMovies) {
            if (m.getId() == movie.getId()) {
                allMovies.get(allMovies.indexOf(m)).setLastview(lastview);
            }
        }
        movieDao.updateMovieLastview(movie.getId(), lastview);
    }

    //Returns a list of the movies that the was searched for locally.
    public List<Movie> searchMovies(String search) {
        List<Movie> matchingMovies = new ArrayList<>();
        for (Movie m : allMovies) {
            if (m.getTitle().toLowerCase().contains(search.toLowerCase())) {
                matchingMovies.add(m);
            }
        }
        return matchingMovies;
    }

    //Removes a movie both locally and on the database.
    public void removeMovie(Movie movie) {
        allMovies.removeIf(s -> s.getId() == movie.getId());
        movieDao.deleteMovie(movie.getId());
    }

    public void removeMovieInAllCategories(Movie movie) {
        catMovieDao.deleteMovie(movie.getId());
    }
}
