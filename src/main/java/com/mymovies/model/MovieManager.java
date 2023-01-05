package com.mymovies.model;

import com.mymovies.dao.MovieDao;
import com.mymovies.dao.MovieDaoImpl;

import java.util.List;

/**
 * A class that is responsible for manipulating the retrieved lists from SongDao.
 * Also sends queries to the database.
 */

public class MovieManager {

    private final MovieDao movieDao;
    private final List<Movie> allMovies;

    public MovieManager() {
        movieDao = new MovieDaoImpl();
        allMovies = movieDao.getAllMovies();
    }

    public List<Movie> getAllSongs() {
        return allMovies;
    }

    //Creates a new song both locally and on the database.
    public void addSong(String title, String artist, String category, int duration, String path) {
        int songId = movieDao.createMovie(title, artist, category, duration, path);
        allMovies.add(new Movie(songId, title, artist, category, duration, path));
    }

    //Updates a song both locally and on the database.
    public void updateSong(Movie movie, String title, String artist, String category, int duration, String path) {
        for (Movie s : allMovies) {
            if (s.getId() == movie.getId()) {
                allMovies.get(allMovies.indexOf(s)).setTitle(title);
                allMovies.get(allMovies.indexOf(s)).setArtist(artist);
                allMovies.get(allMovies.indexOf(s)).setCategory(category);
                allMovies.get(allMovies.indexOf(s)).setDuration(duration);
                allMovies.get(allMovies.indexOf(s)).setPath(path);
            }
        }
        movieDao.updateMovie(movie.getId(), title, artist, category, duration, path);
    }

    //Returns a list of the songs that the was searched for on the database.
    public List<Movie> searchSongs(String search) {
        return movieDao.searchMovie(search);
    }

    //Removes a song both locally and on the database.
    public void removeSong(Movie movie) {
        allMovies.removeIf(s -> s.getId() == movie.getId());
        movieDao.deleteMovie(movie.getId());
    }
}
