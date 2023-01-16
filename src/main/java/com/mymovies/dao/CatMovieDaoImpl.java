package com.mymovies.dao;

import com.mymovies.model.Category;
import com.mymovies.model.Movie;
import javafx.scene.control.CheckBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses the DatabaseConnector class and sends queries to the SongsInPlaylist table on the database.
 */

public class CatMovieDaoImpl implements CatMovieDao {

    private final DatabaseConnector databaseConnector;

    public CatMovieDaoImpl() {
        databaseConnector = new DatabaseConnector();
    }

    //Retrieves a list of the movies in a category and returns an ArrayList with them.
    public List<Movie> getMovies(int categoryId) {
        List<Movie> categories = new ArrayList<>();
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "SELECT CatMovie.categoryID, Movies.movieID, Movies.title, Movies.director, Movies.rating, Movies.lastview, Movies.movie_path, Movies.trailer_path, Movies.year, Movies.imdb_score, Movies.fav " +
                         "FROM CatMovie " +
                         "INNER JOIN Movies " +
                         "ON CatMovie.movieID = Movies.movieID " +
                         "WHERE CatMovie.categoryID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    int id = resultSet.getInt("movieID");
                    String title = resultSet.getString("title");
                    String director = resultSet.getString("director");
                    Float rating = resultSet.getFloat("rating");
                    Date lastview = resultSet.getDate("lastview");
                    String moviePath = resultSet.getString("movie_path");
                    String trailerPath = resultSet.getString("trailer_path");
                    int year = resultSet.getInt("year");
                    Float imdbScore = resultSet.getFloat("imdb_score");
                    boolean fav = resultSet.getBoolean("fav");
                    CheckBox like = new CheckBox();
                    like.setSelected(fav);

                    Movie movie = new Movie(id, title, director, rating, lastview, moviePath, trailerPath, year, imdbScore, like);
                    categories.add(movie);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categories;
    }

    //Retrieves a list of the categories in a movie and returns an ArrayList with them.
    @Override
    public List<Category> getCategories(int movieId) {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "SELECT CatMovie.categoryID, Categories.categoryID, Categories.name " +
                    "FROM CatMovie " +
                    "INNER JOIN Categories " +
                    "ON CatMovie.categoryID = Categories.categoryID " +
                    "WHERE CatMovie.movieID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, movieId);
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    int id = resultSet.getInt("categoryID");
                    String name = resultSet.getString("name");

                    Category category = new Category(id, name);
                    categories.add(category);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categories;
    }

    //Deletes a movie from a category.
    @Override
    public void deleteMovieFromCategory(int categoryId, int movieId) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "DELETE TOP (1) FROM CatMovie WHERE categoryID = ? AND movieID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);
            statement.setInt(2, movieId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteMovie(int movieId) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "DELETE FROM CatMovie WHERE movieID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, movieId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Moves a movie into a category.
    @Override
    public void moveMovieToCategory(int categoryId, int movieId) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "INSERT INTO CatMovie (categoryID, movieID) VALUES (?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);
            statement.setInt(2, movieId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
