package com.mymovies.controllers;

import com.mymovies.model.Category;
import com.mymovies.model.Movie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewEditMovieController {

    private String categoriesString;
    private final ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();
    private final List<Category> categories = new ArrayList<>();

    @FXML private GridPane gridPane;

    @FXML private TextField titleTextField, directorTextField, ratingTextField, moviePathTextField, trailerPathTextField, yearTextField, imdbTextField, categoryTextField;

    @FXML private Button cancelButton, saveButton;

    @FXML private ChoiceBox<Category> categoryChoiceBox;

    @FXML void handleCategoryAdd() {
        if (categories.size() == 0)
            categoriesString = categoryChoiceBox.getSelectionModel().getSelectedItem().getName();
        else
            categoriesString = categoriesString.concat(", " + categoryChoiceBox.getSelectionModel().getSelectedItem().getName());

        categories.add(categoryChoiceBox.getSelectionModel().getSelectedItem());
        categoryTextField.setText(categoriesString);
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

            int movieId = ListController.getMovieManager().addMovie(
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
                Movie movie = new Movie(movieId,
                        titleTextField.getText(),
                        directorTextField.getText(),
                        Float.valueOf(ratingTextField.getText()),
                        null,
                        moviePathTextField.getText(),
                        trailerPathTextField.getText(),
                        Integer.parseInt(yearTextField.getText()),
                        Float.parseFloat(imdbTextField.getText()),
                        like);
                c.addMovie(movie);
            }
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

            for (Category c : categories) {
                ListController.selectedMovie.setCategories(categories);
                c.removeMovie(ListController.selectedMovie);
                c.addMovie(ListController.selectedMovie);
            }
        }

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        categoryObservableList.addAll(ListController.getCategoryManager().getAllCategories());
        categoryChoiceBox.setItems(categoryObservableList);

        if (!ListController.isNewPressed) {
            titleTextField.setText(ListController.selectedMovie.getTitle());
            directorTextField.setText(ListController.selectedMovie.getDirector());
            ratingTextField.setText(String.valueOf(ListController.selectedMovie.getRating()));
            moviePathTextField.setText(ListController.selectedMovie.getMoviePath());
            trailerPathTextField.setText(ListController.selectedMovie.getTrailerPath());
            yearTextField.setText(String.valueOf(ListController.selectedMovie.getYear()));
            imdbTextField.setText(String.valueOf(ListController.selectedMovie.getImdbScore()));

            if (ListController.selectedMovie.getCategories().size() != 0) {
                for (int i = 0; i < ListController.selectedMovie.getCategories().size(); i++) {
                    categories.add(ListController.selectedMovie.getCategories().get(i));
                    if (i == 0)
                        categoriesString = ListController.selectedMovie.getCategories().get(0).getName();
                    else
                        categoriesString = categoriesString.concat(", " + ListController.selectedMovie.getCategories().get(i).getName());
                }
                categoryTextField.setText(categoriesString);
            }
        }
    }
}