package com.mymovies.dao;

import com.mymovies.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses the DatabaseConnector class and sends queries to the Categories table on the database.
 */

public class CategoryDaoImpl implements CategoryDao {

    private final DatabaseConnector databaseConnector;

    public CategoryDaoImpl() {
        databaseConnector = new DatabaseConnector();
    }

    //Retrieves all categories.
    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "SELECT * FROM Categories;";
            Statement statement = connection.createStatement();
            if (statement.execute(sql)) {
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

    //Deletes a category.
    @Override
    public void deleteCategory(int id) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "DELETE FROM Categories WHERE categoryID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Updates a category's values.
    @Override
    public void updateCategory(int id, String name) {
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "UPDATE Categories SET name = ? WHERE categoryID = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Creates a new category.
    //Also returns an int value for the ID of the category that was just created.
    @Override
    public int createCategory(String name) {
        int categoryId = 0;
        try (Connection connection = databaseConnector.getConnection()) {
            String sql = "INSERT INTO Categories (name) VALUES (?);";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.executeUpdate();
            ResultSet generatedKey = statement.getGeneratedKeys();
            if (generatedKey.next())
                categoryId = generatedKey.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categoryId;
    }
}
