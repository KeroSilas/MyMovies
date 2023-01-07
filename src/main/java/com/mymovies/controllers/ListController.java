package com.mymovies.controllers;

import com.mymovies.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.util.*;

/**
 * Responsible for controlling various elements in the GUI by utilizing the model classes.
 */

public class ListController {

    //Initialize all the images that will be used when toggling different button states.
    private final Image searchImage = new Image("file:src/main/resources/com/mymovies/images/search.png");
    private final Image unsearchImage = new Image("file:src/main/resources/com/mymovies/images/cancel.png");

    protected static MovieManager movieManager;
    protected static CategoryManager categoryManager;
    protected static boolean isNewPressed;
    private boolean isSearching = false;

    private final ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();
    private final ObservableList<Movie> movieObservableList = FXCollections.observableArrayList();
    protected static Category selectedCategory;
    protected static Movie selectedMovie;

    @FXML private TableView<Movie> movieTableView;
    @FXML private TableColumn<Movie, String> titleCol, directorCol, ratingCol, lastviewCol, imdbCol, yearCol, categoryCol;
    @FXML private TableColumn<Movie, Boolean> likeCol;
    @FXML private ChoiceBox<Category> categoryChoiceBox;

    @FXML private TextField searchTextField;

    @FXML private ImageView searchUnsearchImage;

    @FXML private Button editCategoryButton, deleteCategoryButton;

    ///// --- LIST AND SELECTION METHODS --- /////

    @FXML void handleMovieClick(MouseEvent e) {
        selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null && e.getClickCount() == 2) {
            showPlayerWindow();
        }
        for (Category c : selectedMovie.getCategories())
            System.out.println(c.getName());
    }

    ///// --- MANAGER OBJECTS CONTROLS --- /////

    //searches for songs in the database
    @FXML void handleSearch() {
        if (!isSearching && !searchTextField.getText().isEmpty()) {
            movieObservableList.setAll(movieManager.searchMovies(searchTextField.getText()));
            searchUnsearchImage.setImage(unsearchImage);
            isSearching = true;
        } else {
            searchTextField.clear();
            movieObservableList.setAll(movieManager.getAllMovies());
            searchUnsearchImage.setImage(searchImage);
            isSearching = false;
        }
    }

    @FXML void handleAddCategory() {
        isNewPressed = true;
        showNewEditCategoryWindow();
        int index = categoryChoiceBox.getSelectionModel().getSelectedIndex();
        categoryObservableList.clear();
        categoryObservableList.add(new Category(0, "All movies"));
        categoryObservableList.addAll(categoryManager.getAllCategories());
        categoryChoiceBox.getSelectionModel().select(index);
    }

    @FXML void handleAddMovie() {
        isNewPressed = true;
        showNewEditMovieWindow();
        movieObservableList.setAll(movieManager.getAllMovies());
    }

    /*@FXML void handleAddMovieToCategory() {
        boolean containsDuplicate = false;
        if (selectedCategory != null && selectedMovie != null) {
            for (Movie s : selectedCategory.getMovies()) {
                if (s.getId() == selectedMovie.getId()) {
                    containsDuplicate = true;
                    Optional<ButtonType> result = showAlertWindow("You already have this movie in the selected category.\nDo you want to add a duplicate?");
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        selectedCategory.addMovie(selectedMovie);
                        categoryObservableList.setAll(categoryManager.getAllCategories());
                    }
                    break;
                }
            }
        }
        if (selectedCategory != null && selectedMovie != null && !containsDuplicate) {
            selectedCategory.addMovie(selectedMovie);
            categoryObservableList.setAll(categoryManager.getAllCategories());
        }
    }*/

    @FXML void handlePlayMovie() {
        showPlayerWindow();
    }

    @FXML void handlePlayTrailer() {
        showPlayerWindow();
    }

    @FXML void handleEditCategory() {
        editCategory();
    }

    @FXML void handleEditMovie() {
        editMovie();
    }

    @FXML void handleDeleteMovie() {
        deleteMovie();
    }

    @FXML void handleDeleteCategory() {
        deleteCategory();
    }

    @FXML void handleDeleteMovieFromCategory() {
        deleteMovieInCategory();
    }

    public void initialize() {
        //Initialize song and playlist managers.
        movieManager = new MovieManager();
        categoryManager = new CategoryManager();

        //Set up the table columns and cells for the song table.
        titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        directorCol.setCellValueFactory(new PropertyValueFactory<>("Director"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("Rating"));
        lastviewCol.setCellValueFactory(new PropertyValueFactory<>("Lastview"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("Year"));
        imdbCol.setCellValueFactory(new PropertyValueFactory<>("ImdbScore"));
        likeCol.setCellValueFactory(new PropertyValueFactory<>("Like"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("CategoriesInString"));

        movieObservableList.addAll(movieManager.getAllMovies());
        movieTableView.setItems(movieObservableList);
        movieTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        movieTableView.setContextMenu(getMovieContextMenu());

        categoryObservableList.add(new Category(0, "All movies"));
        categoryObservableList.addAll(categoryManager.getAllCategories());
        categoryChoiceBox.setItems(categoryObservableList);
        categoryChoiceBox.getSelectionModel().select(0);

        categoryChoiceBox.setOnAction(e -> {
            selectedCategory = categoryChoiceBox.getSelectionModel().getSelectedItem();
            try {
                if (Objects.equals(selectedCategory.getName(), "All movies")) {
                    editCategoryButton.setDisable(true);
                    deleteCategoryButton.setDisable(true);
                    movieObservableList.setAll(movieManager.getAllMovies());
                } else {
                    editCategoryButton.setDisable(false);
                    deleteCategoryButton.setDisable(false);
                    movieObservableList.clear();

                    for (Movie m : movieManager.getAllMovies())
                        if (m.getCategories().size() != 0)
                            for (int i = 0; i < m.getCategories().size(); i++)
                                if(m.getCategories().get(i).getId() == selectedCategory.getId())
                                    movieObservableList.add(m);
                }
            } catch (NullPointerException ex) {
                System.out.println("NullPointerException ignored, program works just fine :)");
            }
        });
    }

    protected static MovieManager getMovieManager() {
        return movieManager;
    }

    protected static CategoryManager getCategoryManager() {
        return categoryManager;
    }

    private void showPlayerWindow() {
        try {
            movieManager.updateMovieLastview(selectedMovie, Date.valueOf(java.time.LocalDate.now()));
            movieObservableList.setAll(movieManager.getAllMovies());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mymovies/views/PlayerTab.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("MyMovies Player");
            stage.setResizable(true);
            stage.initModality(Modality.NONE);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showNewEditMovieWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mymovies/views/NewEditMovie.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            if (isNewPressed) {
                stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/add.png"));
                stage.setTitle("New Movie");
            } else {
                stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/edit.png"));
                stage.setTitle("Edit Movie");
            }
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private ContextMenu getMovieContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");
        MenuItem like = new MenuItem("Like");

        edit.setOnAction((event) -> editMovie());
        delete.setOnAction((event) -> deleteMovie());
        like.setOnAction((event) -> {
            movieManager.updateMovieLike(selectedMovie, selectedMovie.getLike().isSelected() ? 0 : 1);
            movieObservableList.setAll(movieManager.getAllMovies());
        });

        contextMenu.getItems().addAll(edit, delete, like);
        return contextMenu;
    }

    private void showNewEditCategoryWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mymovies/views/NewEditCategory.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            if (isNewPressed) {
                stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/add.png"));
                stage.setTitle("New Category");
            } else {
                stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/edit.png"));
                stage.setTitle("Edit Category");
            }
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Opens an alert window that can be customized.
    //Returns the button type that was pressed in the alert window.
    private Optional<ButtonType> showAlertWindow(String text) {
        Alert alert = new Alert(Alert.AlertType.NONE, text, ButtonType.NO, ButtonType.YES);
        alert.setTitle("Confirmation");
        alert.initOwner(searchTextField.getScene().getWindow()); //Retrieves the title bar icon from the main window by setting the alerts owner to that window.
        return alert.showAndWait();
    }

    private void editMovie() {
        isNewPressed = false;
        showNewEditMovieWindow();
        movieObservableList.setAll(movieManager.getAllMovies());
    }

    private void deleteMovie() {
        Optional<ButtonType> result = showAlertWindow("Are you sure you wish to delete this movie?");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            movieManager.removeMovie(selectedMovie);
            movieObservableList.setAll(movieManager.getAllMovies());
            //Loops through and deletes all copies of the deleted song from each playlist.
            for (int i = 0; i < categoryManager.getAllCategories().size(); i++) {
                categoryManager.getAllCategories().get(i).getMovies().removeIf(s -> s.getId() == selectedMovie.getId());
            }

            //Updates Playlist and SongsInPlaylist lists.
            int index = categoryChoiceBox.getSelectionModel().getSelectedIndex();
            categoryObservableList.clear();
            categoryObservableList.add(new Category(0, "All movies"));
            categoryObservableList.addAll(categoryManager.getAllCategories());
            categoryChoiceBox.getSelectionModel().select(index);
        }
    }

    private void deleteMovieInCategory() {
        Optional<ButtonType> result = showAlertWindow("Are you sure you wish to delete this movie from the category?");

        /*if (result.isPresent() && result.get() == ButtonType.YES) {
            selectedCategory.removeMovie(selectedMovieInPlaylist);
            movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
            categoryObservableList.setAll(categoryManager.getAllPlaylists());
            player.updateCurrentPlaylist(selectedCategory);
            if (player.getCurrentPlaylist() == selectedCategory && player.getListStatus() == Player.ListStatus.PLAYLIST && selectedCategory.getMovies().size() == 0) {
                player.load(movieObservableList, movieObservableList.get(0));
            }
        }*/
    }

    private void editCategory() {
        isNewPressed = false;
        showNewEditCategoryWindow();
        int index = categoryChoiceBox.getSelectionModel().getSelectedIndex();
        categoryObservableList.clear();
        categoryObservableList.add(new Category(0, "All movies"));
        categoryObservableList.addAll(categoryManager.getAllCategories());
        categoryChoiceBox.getSelectionModel().select(index);
    }

    private void deleteCategory() {
        Optional<ButtonType> result = showAlertWindow("Are you sure you wish to delete this playlist?");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            categoryManager.removeCategory(selectedCategory);
            selectedCategory.getMovies().clear();
            categoryObservableList.clear();
            categoryObservableList.add(new Category(0, "All movies"));
            categoryObservableList.addAll(categoryManager.getAllCategories());
            categoryChoiceBox.getSelectionModel().select(0);
        }
    }
}