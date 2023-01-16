package com.mymovies.controllers;

import com.mymovies.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewEditMovieController {

    private final ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();
    private final ObservableList<Category> categoryInMovieObservableList = FXCollections.observableArrayList();
    private final List<Category> categories = new ArrayList<>();

    @FXML private GridPane gridPane;

    @FXML private TextField titleTextField, directorTextField, ratingTextField, moviePathTextField, trailerPathTextField, yearTextField, imdbTextField;

    @FXML private ListView<Category> categoryListView;

    @FXML private Button cancelButton, saveButton, categoryDeleteButton;

    @FXML private ChoiceBox<Category> categoryChoiceBox;

    @FXML void handleCategoryAdd() {
        categories.add(categoryChoiceBox.getSelectionModel().getSelectedItem());
        categoryInMovieObservableList.add(categoryChoiceBox.getSelectionModel().getSelectedItem());
    }

    @FXML void handleCategoryDelete() {
        categories.remove(categoryListView.getSelectionModel().getSelectedItem());
        categoryInMovieObservableList.remove(categoryListView.getSelectionModel().getSelectedItem());
    }

    @FXML void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML void handleChooseMovie() {
        Stage stage = (Stage) gridPane.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Video files", "*.mp4");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File("src/main/resources/com/mymovies/movies/"));
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            moviePathTextField.setText(file.getPath());
        }
    }

    @FXML void handleChooseTrailer() {
        Stage stage = (Stage) gridPane.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Video files", "*.mp4");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File("src/main/resources/com/mymovies/trailers/"));
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            trailerPathTextField.setText(file.getPath());
        }
    }

    @FXML void handleSave() {
        if (ListController.isNewPressed) {
            CheckBox like = new CheckBox();
            like.setSelected(false);

            ListController.getMovieManager().addMovie(
                    titleTextField.getText(),
                    directorTextField.getText(),
                    Float.valueOf(ratingTextField.getText()),
                    null,
                    moviePathTextField.getText(),
                    trailerPathTextField.getText(),
                    Integer.parseInt(yearTextField.getText()),
                    Float.parseFloat(imdbTextField.getText()),
                    like);

            for (Category c : categories) {
                c.addMovie(ListController.getMovieManager().getAllMovies().get(ListController.getMovieManager().getAllMovies().size() - 1));
            }
            ListController.getMovieManager().getAllMovies().get(ListController.getMovieManager().getAllMovies().size() - 1).setCategories(categories);
        } else {
            ListController.getMovieManager().updateMovie(
                    ListController.selectedMovie,
                    titleTextField.getText(),
                    directorTextField.getText(),
                    Float.valueOf(ratingTextField.getText()),
                    moviePathTextField.getText(),
                    trailerPathTextField.getText(),
                    Integer.parseInt(yearTextField.getText()),
                    Float.parseFloat(imdbTextField.getText()));

            ListController.getMovieManager().removeMovieInAllCategories(ListController.selectedMovie);
            for (Category c : categories) {
                c.addMovie(ListController.selectedMovie);
            }
            ListController.selectedMovie.setCategories(categories);
        }

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        categoryObservableList.addAll(ListController.getCategoryManager().getAllCategories());
        categoryChoiceBox.setItems(categoryObservableList);
        categoryListView.setItems(categoryInMovieObservableList);

        if (!ListController.isNewPressed) {
            titleTextField.setText(ListController.selectedMovie.getTitle());
            directorTextField.setText(ListController.selectedMovie.getDirector());
            ratingTextField.setText(String.valueOf(ListController.selectedMovie.getRating()));
            moviePathTextField.setText(ListController.selectedMovie.getMoviePath());
            trailerPathTextField.setText(ListController.selectedMovie.getTrailerPath());
            yearTextField.setText(String.valueOf(ListController.selectedMovie.getYear()));
            imdbTextField.setText(String.valueOf(ListController.selectedMovie.getImdbScore()));

            categories.addAll(ListController.selectedMovie.getCategories());
            categoryInMovieObservableList.addAll(ListController.selectedMovie.getCategories());
        }

        categoryListView.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue))
                categoryDeleteButton.setDisable(false);
        });
    }
}