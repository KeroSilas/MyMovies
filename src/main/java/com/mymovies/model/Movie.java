package com.mymovies.model;

import javafx.scene.control.CheckBox;
import javafx.util.Duration;

import com.mymovies.dao.CatMovieDao;
import com.mymovies.dao.CatMovieDaoImpl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains a song's id, title, artist, category, duration and path.
 */

public class Movie {

    private final CatMovieDao catMovieDao;

    private final int id;
    private String title;
    private String director;
    private Float rating;
    private Date lastview;
    private String moviePath;
    private String trailerPath;
    private int year;
    private Float imdbScore;
    private final CheckBox like;

    private List<Category> categories;

    public Movie(int id, String title, String director, Float rating, java.sql.Date lastview, String moviePath, String trailerPath, int year, Float imdbScore, CheckBox like) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.rating = rating;
        this.lastview = lastview;
        this.moviePath = moviePath;
        this.trailerPath = trailerPath;
        this.year = year;
        this.imdbScore = imdbScore;
        this.like = like;

        catMovieDao = new CatMovieDaoImpl();
        categories = new ArrayList<>();
    }

    public void addCategory(Category category) {
        categories.add(category);
        catMovieDao.moveMovieToCategory(category.getId(), getId()); //adds the song to the database as well
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        catMovieDao.deleteMovieFromCategory(category.getId(), getId());
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getCategoriesInString() {
        String categoriesString = "";

        if (categories.size() != 0)
            for (int i = 0; i < categories.size(); i++) {
                if (i == 0)
                 categoriesString = categories.get(0).getName();
                else
                 categoriesString = categoriesString.concat(", " + categories.get(i).getName());
            }
        return categoriesString;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public Float getRating() {
        return rating;
    }

    public java.sql.Date getLastview() {
        return lastview;
    }

    public String getMoviePath() {
        return moviePath;
    }

    public String getTrailerPath() {
        return trailerPath;
    }

    public int getYear() {
        return year;
    }

    public Float getImdbScore() {
        return imdbScore;
    }

    public CheckBox getLike() {
        return like;
    }

    //Returns the song duration in a string such as this: "02:23".
    /*public String getDurationInString() {
        int duration = getDurationInInteger();
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }*/

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setLastview(Date lastview) {
        this.lastview = lastview;
    }

    public void setMoviePath(String moviePath) {
        this.moviePath = moviePath;
    }

    public void setTrailerPath(String trailerPath) {
        this.trailerPath = trailerPath;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setImdbScore(Float imdbScore) {
        this.imdbScore = imdbScore;
    }

    public void setLike(int like) {
        this.like.setSelected(like == 1);
    }

    //A toString() method that formats a song's artist and title as such: "Adele - Hello".
    @Override
    public String toString() {
        return String.format("%s", title);
    }
}
