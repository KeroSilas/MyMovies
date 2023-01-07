package com.mymovies.dao;

import com.mymovies.model.Movie;
import javafx.scene.control.CheckBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses the DatabaseConnector class and sends queries to the Songs table on the database.
 */

public class MovieDaoImpl implements MovieDao {

    private final DatabaseConnector databaseConnector;

    public MovieDaoImpl() {
        databaseConnector = new DatabaseConnector();
    }

    //Retrieves all the songs on the Songs table and returns an ArrayList with them.
    @Override
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "SELECT * FROM Movies;";
            Statement statement = connection.createStatement();
            if (statement.execute(sql)) {
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
                    movies.add(movie);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return movies;
    }

    //Deletes a song.
    @Override
    public void deleteMovie(int id) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "DELETE FROM Movies WHERE movieID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Updates a song's values.
    @Override
    public void updateMovie(int id, String title, String director, Float rating, String moviePath, String trailerPath, int year, Float imdbScore) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "UPDATE Movies SET title = ?, director = ?, rating = ?, movie_path = ?, trailer_path = ?, year = ?, imdb_score = ? WHERE movieID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, director);
            statement.setFloat(3, rating);
            statement.setString(4, moviePath);
            statement.setString(5, trailerPath);
            statement.setInt(6, year);
            statement.setFloat(7, imdbScore);
            statement.setInt(8, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateMovieLike(int id, int like) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "UPDATE Movies SET fav = ? WHERE movieID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, like);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateMovieLastview(int id, Date lastview) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "UPDATE Movies SET lastview = ? WHERE movieID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, lastview);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Creates a new song.
    //Also returns an int value for the ID of the song that was just created.
    @Override
    public int createMovie(String title, String director, Float rating, Date lastview, String moviePath, String trailerPath, int year, Float imdbScore, CheckBox like) {
        int movieId = 0;
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "INSERT INTO Movies (title, director, rating, lastview, movie_path, trailer_path, year, imdb_score, fav) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, title);
            statement.setString(2, director);
            statement.setFloat(3, rating);
            statement.setDate(4, lastview);
            statement.setString(5, moviePath);
            statement.setString(6, trailerPath);
            statement.setInt(7, year);
            statement.setFloat(8, imdbScore);
            statement.setBoolean(9, like.isSelected());
            statement.executeUpdate();
            ResultSet generatedKey = statement.getGeneratedKeys();
            if (generatedKey.next())
                movieId = generatedKey.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return movieId;
    }

    //Returns a list of songs where the search input matches a title or an artist.
    @Override
    public List<Movie> searchMovie(String search) {
        List<Movie> movies = new ArrayList<>();
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "SELECT * FROM Movies WHERE title LIKE ? OR director LIKE ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + search + "%");
            statement.setString(2, "%" + search + "%");
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
                    movies.add(movie);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return movies;
    }
}
