package com.mymovies.dao;

import com.mymovies.model.Movie;

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

    //Retrieves a list of the songs on a playlist and returns an ArrayList with them.
    @Override
    public List<Movie> getCategory(int categoryId) {
        List<Movie> categories = new ArrayList<>();
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "SELECT CatMovie.categoryID, Movies.movieID, Movies.title, Movies.artist, Movies.category, Movies.duration, Movirs.path " +
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
                    String artist = resultSet.getString("artist");
                    String category = resultSet.getString("category");
                    int duration = resultSet.getInt("duration");
                    String path = resultSet.getString("path");

                    Movie movie = new Movie(id, title, artist, category, duration, path);
                    categories.add(movie);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categories;
    }

    //Deletes a song from a playlist.
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

    //Moves a song into a playlist.
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
