package com.mymovies.dao;

import com.mymovies.model.Category;

import java.util.List;

public interface CategoryDao {

    List<Category> getAllCategories();

    void deleteCategory(int id);

    void updateCategory(int id, String name);

    int createCategory(String name);

}
