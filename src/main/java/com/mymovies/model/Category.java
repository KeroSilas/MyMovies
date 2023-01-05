package com.mymovies.model;

import com.mymovies.dao.CatMovieDao;
import com.mymovies.dao.CatMovieDaoImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains a playlists id and name, as well as all the songs in a playlist.
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

    //Adds a new song on the playlist both locally and on the database.
    public void addMovie(Movie movie) {
        movies.add(movie);
        catMovieDao.moveMovieToCategory(getId(), movie.getId()); //adds the song to the database as well
    }

    public void setName(String name) {
        this.name = name;
    }

    //Removes a song from the playlist both locally and on the database.
    public void removeMovie(Movie movie) {
        movies.remove(movie);
        catMovieDao.deleteMovieFromCategory(getId(), movie.getId());
    }

    //Returns the playlist duration in seconds.
    private int getDurationInInteger() {
        int duration = 0;
        for (Movie movie : movies) {
            duration += movie.getDurationInInteger();
        }
        return duration;
    }

    //Returns the playlist duration in a string such as this: "02:32:23".
    public String getDurationInString() {
        int duration = getDurationInInteger();
        int hours = duration / 3600;
        int minutes = (duration / 60) % 60;
        int seconds = duration % 60;
        if (hours == 0)
            return String.format("%02d:%02d", minutes, seconds);
        else
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public int getNumberOfMovies() {
        return movies.size();
    }
}
