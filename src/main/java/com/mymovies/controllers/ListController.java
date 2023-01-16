package com.mymovies.controllers;

import com.mymovies.model.*;
import javafx.application.Platform;
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

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.awt.Desktop;

/**
 * Responsible for controlling various elements in the GUI by utilizing the model classes.
 */

public class ListController {

    //Initialize all the images that will be used when toggling different button states.
    private final Image searchImage = new Image("file:src/main/resources/com/mymovies/images/search.png");
    private final Image unsearchImage = new Image("file:src/main/resources/com/mymovies/images/cancel.png");
    private final Image markedImage = new Image("file:src/main/resources/com/mymovies/images/marked.png");
    private final Image unmarkedImage = new Image("file:src/main/resources/com/mymovies/images/unmarked.png");

    protected static MovieManager movieManager;
    protected static CategoryManager categoryManager;
    protected static boolean isNewPressed;
    protected static boolean isMoviePlayPressed;
    private boolean isMoviesToDelete = false;
    private boolean isSearching = false;

    private final ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();
    private final ObservableList<Movie> movieObservableList = FXCollections.observableArrayList();
    protected static Category selectedCategory;
    protected static Movie selectedMovie;

    @FXML private TableView<Movie> movieTableView;
    @FXML private TableColumn<Movie, String> titleCol, directorCol, ratingCol, lastviewCol, imdbCol, yearCol, categoryCol;
    @FXML private TableColumn<Movie, Boolean> likeCol;
    @FXML private ChoiceBox<Category> categoryChoiceBox;

    @FXML private CheckBox mediaPlayerCheckBox;

    @FXML private Label selectedMovieLabel;

    @FXML private TextField searchTextField;

    @FXML private ImageView searchUnsearchImage, markUnmarkImage;

    @FXML private Button editCategoryButton, deleteCategoryButton, likeButton, playMovieButton, playTrailerButton, editMovieButton, deleteMovieButton;

    @FXML void handleMovieClick(MouseEvent e) throws IOException {
        if (selectedMovie != null && e.getClickCount() == 2) {
            if (mediaPlayerCheckBox.isSelected()) {
                isMoviePlayPressed = true;
                showPlayerWindow();
            } else {
                Desktop.getDesktop().open(new File(selectedMovie.getMoviePath()));
            }
            movieManager.updateMovieLastview(selectedMovie, Date.valueOf(java.time.LocalDate.now()));
            movieObservableList.setAll(movieManager.getAllMovies());
        }
    }

    //Toggles between search and unsearch states.
    @FXML void handleSearch() {
        if (!isSearching && !searchTextField.getText().isEmpty()) {
            movieObservableList.clear();
            for (Movie movie : movieManager.searchMovies(searchTextField.getText())) {
                if (movie.getCategories().size() != 0 && !Objects.equals(categoryChoiceBox.getSelectionModel().getSelectedItem().getName(), "All movies"))
                    for (int i = 0; i < movie.getCategories().size(); i++)
                        if (movie.getCategories().get(i).getId() == selectedCategory.getId())
                            movieObservableList.add(movie);
                if (Objects.equals(categoryChoiceBox.getSelectionModel().getSelectedItem().getName(), "All movies"))
                    movieObservableList.add(movie);
            }
            searchUnsearchImage.setImage(unsearchImage);
            isSearching = true;
        } else {
            searchTextField.clear();
            movieObservableList.setAll(movieManager.getAllMovies());
            categoryChoiceBox.getSelectionModel().select(0);
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

    @FXML void handlePlayMovie() throws IOException {
        if (mediaPlayerCheckBox.isSelected()) {
            isMoviePlayPressed = true;
            showPlayerWindow();
        } else {
            Desktop.getDesktop().open(new File(selectedMovie.getMoviePath()));
        }
        movieManager.updateMovieLastview(selectedMovie, Date.valueOf(java.time.LocalDate.now()));
        movieObservableList.setAll(movieManager.getAllMovies());
    }

    @FXML void handlePlayTrailer() throws IOException {
        if (mediaPlayerCheckBox.isSelected()) {
            isMoviePlayPressed = false;
            showPlayerWindow();
        } else {
            Desktop.getDesktop().open(new File(selectedMovie.getTrailerPath()));
        }
    }

    @FXML void handleLike() {
        editMovieLike();
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

    public void initialize() {
        //Initialize movie and category managers.
        movieManager = new MovieManager();
        categoryManager = new CategoryManager();

        //Set up the table columns and cells for the movie table.
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

        movieTableView.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && movieTableView.getSelectionModel().getSelectedItem() == null) {
                selectedMovie = null;
                playMovieButton.setDisable(true);
                playTrailerButton.setDisable(true);
                editMovieButton.setDisable(true);
                deleteMovieButton.setDisable(true);
                likeButton.setDisable(true);
                selectedMovieLabel.setText("");
            } else if (!Objects.equals(oldValue, newValue)) {
                selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
                markUnmarkImage.setImage(selectedMovie.getLike().isSelected() ? markedImage : unmarkedImage);
                playMovieButton.setDisable(false);
                playTrailerButton.setDisable(false);
                editMovieButton.setDisable(false);
                deleteMovieButton.setDisable(false);
                likeButton.setDisable(false);
                selectedMovieLabel.setText(selectedMovie.getTitle());
            }
        });

        categoryObservableList.add(new Category(0, "All movies"));
        categoryObservableList.addAll(categoryManager.getAllCategories());
        categoryChoiceBox.setItems(categoryObservableList);
        categoryChoiceBox.getSelectionModel().select(0);

        categoryChoiceBox.setOnAction(e -> {
            selectedCategory = categoryChoiceBox.getSelectionModel().getSelectedItem();
            try {
                if (!isSearching) {
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
                                    if (m.getCategories().get(i).getId() == selectedCategory.getId())
                                        movieObservableList.add(m);
                    }
                } else {
                    if (Objects.equals(selectedCategory.getName(), "All movies")) {
                        editCategoryButton.setDisable(true);
                        deleteCategoryButton.setDisable(true);
                        movieObservableList.setAll(movieManager.searchMovies(searchTextField.getText()));
                    } else {
                        editCategoryButton.setDisable(false);
                        deleteCategoryButton.setDisable(false);
                        movieObservableList.clear();

                        for (Movie m : movieManager.searchMovies(searchTextField.getText()))
                            if (m.getCategories().size() != 0)
                                for (int i = 0; i < m.getCategories().size(); i++)
                                    if (m.getCategories().get(i).getId() == selectedCategory.getId())
                                        movieObservableList.add(m);
                    }
                }
            } catch (NullPointerException ex) {
                System.out.println("NullPointerException ignored, program works just fine :)");
            }
        });

        Platform.runLater(() -> {
            for (Movie m : ListController.getMovieManager().getAllMovies())
                if (LocalDate.now().minusYears(2).isBefore(m.getLastview().toLocalDate())) {
                    isMoviesToDelete = true;
                    break;
                }
            if (isMoviesToDelete)
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mymovies/views/DeleteSuggestion.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/delete.png"));
                    stage.setTitle("Delete Movies");
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            movieObservableList.setAll(movieManager.getAllMovies());
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mymovies/views/PlayerTab.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/logo.png"));
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
        MenuItem playMovie = new MenuItem("Play movie");
        MenuItem playTrailer = new MenuItem("Play trailer");
        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");
        MenuItem like = new MenuItem("Like");

        playMovie.setOnAction((event) -> {
            if (mediaPlayerCheckBox.isSelected()) {
                isMoviePlayPressed = true;
                showPlayerWindow();
            } else {
                try {
                    Desktop.getDesktop().open(new File(selectedMovie.getMoviePath()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            movieManager.updateMovieLastview(selectedMovie, Date.valueOf(java.time.LocalDate.now()));
            movieObservableList.setAll(movieManager.getAllMovies());
        });
        playTrailer.setOnAction((event) -> {
            isMoviePlayPressed = false;
            showPlayerWindow();
        });
        edit.setOnAction((event) -> editMovie());
        delete.setOnAction((event) -> deleteMovie());
        like.setOnAction((event) -> editMovieLike());

        contextMenu.getItems().addAll(playMovie, playTrailer, edit, delete, like);
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

    private void editMovieLike() {
        movieManager.updateMovieLike(selectedMovie, selectedMovie.getLike().isSelected() ? 0 : 1);
        movieObservableList.setAll(movieManager.getAllMovies());

        markUnmarkImage.setImage(selectedMovie.getLike().isSelected() ? markedImage : unmarkedImage);
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
            //Loops through and deletes all copies of the deleted movie from each category.
            for (int i = 0; i < categoryManager.getAllCategories().size(); i++) {
                categoryManager.getAllCategories().get(i).getMovies().removeIf(s -> s.getId() == selectedMovie.getId());
            }

            //Updates Category list.
            int index = categoryChoiceBox.getSelectionModel().getSelectedIndex();
            categoryObservableList.clear();
            categoryObservableList.add(new Category(0, "All movies"));
            categoryObservableList.addAll(categoryManager.getAllCategories());
            categoryChoiceBox.getSelectionModel().select(index);
        }
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