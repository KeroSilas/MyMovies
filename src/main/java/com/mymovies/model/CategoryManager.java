package com.mymovies.model;

import com.mymovies.dao.CategoryDao;
import com.mymovies.dao.CategoryDaoImpl;
import com.mymovies.dao.CatMovieDao;
import com.mymovies.dao.CatMovieDaoImpl;

import java.util.List;

/**
 * A class that is responsible for manipulating the retrieved lists from CategoryDao.
 * Also sends queries to the database.
 */

public class CategoryManager {

    private final CategoryDao categoryDao;
    private final List<Category> allCategories;

    //Constructor that retrieves all the movies in a category from the database and stores them in each category object locally.
    public CategoryManager() {
        categoryDao = new CategoryDaoImpl();
        CatMovieDao catMovieDao = new CatMovieDaoImpl();
        allCategories = categoryDao.getAllCategories();
        for (Category p : allCategories) {
            p.setMovies(catMovieDao.getMovies(p.getId()));
        }
    }

    public List<Category> getAllCategories() {
        return allCategories;
    }

    //Creates a new category both locally and on the database.
    public void addCategory(String name) {
        int categoryId = categoryDao.createCategory(name);
        allCategories.add(new Category(categoryId, name));
    }

    //Updates a category both locally and on the database.
    public void updateCategory(Category category, String name) {
        for (Category p : allCategories) {
            if (p.getId() == category.getId())
                allCategories.get(allCategories.indexOf(p)).setName(name);
        }
        categoryDao.updateCategory(category.getId(), name);
    }

    //Removes a category both locally and on the database.
    public void removeCategory(Category category) {
        allCategories.removeIf(p -> p.getId() == category.getId());
        categoryDao.deleteCategory(category.getId());
    }
}