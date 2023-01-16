package com.mymovies.model;

import com.mymovies.dao.CatMovieDao;
import com.mymovies.dao.CatMovieDaoImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains a categories id and name, as well as all the movies in a category.
 * Also sends queries to the database.
 */

public class Category {

    private final CatMovieDao catMovieDao;

    private final int id;
    private String name;
    private List<Movie> movies;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        catMovieDao = new CatMovieDaoImpl();
        movies = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    //Adds a new movie on the category both locally and on the database.
    public void addMovie(Movie movie) {
        movies.add(movie);
        catMovieDao.moveMovieToCategory(getId(), movie.getId()); //adds the movie to the database as well
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
