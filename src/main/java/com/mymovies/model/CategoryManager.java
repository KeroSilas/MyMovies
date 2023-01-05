package com.mymovies.model;

import com.mymovies.dao.CategoryDao;
import com.mymovies.dao.CategoryDaoImpl;
import com.mymovies.dao.CatMovieDao;
import com.mymovies.dao.CatMovieDaoImpl;

import java.util.List;

/**
 * A class that is responsible for manipulating the retrieved lists from PlaylistDao.
 * Also sends queries to the database.
 */

public class CategoryManager {

    private final CategoryDao categoryDao;
    private final List<Category> allCategories;

    //Constructor that retrieves all the songs in a playlist from the database and stores them in each playlist object locally.
    public CategoryManager() {
        categoryDao = new CategoryDaoImpl();
        CatMovieDao catMovieDao = new CatMovieDaoImpl();
        allCategories = categoryDao.getAllCategories();
        for (Category p : allCategories) {
            p.setMovies(catMovieDao.getCategory(p.getId()));
        }
    }

    public List<Category> getAllPlaylists() {
        return allCategories;
    }

    //Creates a new playlist both locally and on the database.
    public void addPlaylist(String name) {
        int playlistId = categoryDao.createCategory(name);
        allCategories.add(new Category(playlistId, name));
    }

    //Updates a playlist both locally and on the database.
    public void updatePlaylist(Category category, String name) {
        for (Category p : allCategories) {
            if (p.getId() == category.getId())
                allCategories.get(allCategories.indexOf(p)).setName(name);
        }
        categoryDao.updateCategory(category.getId(), name);
    }

    //Removes a playlist both locally and on the database.
    public void removePlaylist(Category category) {
        allCategories.removeIf(p -> p.getId() == category.getId());
        categoryDao.deleteCategory(category.getId());
    }
}