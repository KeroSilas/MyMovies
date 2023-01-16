package com.mymovies.controllers;

import com.mymovies.model.Movie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeleteSuggestionController {

    private final ObservableList<Movie> movieObservableList = FXCollections.observableArrayList();
    private final List<Movie> moviesLastSeen = new ArrayList<>();
    private final List<Movie> moviesToDelete = new ArrayList<>();

    @FXML private TableView<Movie> movieTableView;
    @FXML private TableColumn<Movie, String> titleCol, lastviewCol, ratingCol;

    @FXML private Button cancelButton, saveButton, movieDeleteButton;

    @FXML private CheckBox showAllCheckBox;

    @FXML void handleMovieDelete() {
        moviesToDelete.add(movieTableView.getSelectionModel().getSelectedItem());
        movieObservableList.remove(movieTableView.getSelectionModel().getSelectedItem());
    }

    @FXML void handleMovieDeleteAll() {
        moviesToDelete.clear();
        moviesToDelete.addAll(movieObservableList);
        movieObservableList.clear();
    }

    @FXML void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML void handleSave() {
        for (Movie m : moviesToDelete)
            ListController.getMovieManager().removeMovie(m);

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        lastviewCol.setCellValueFactory(new PropertyValueFactory<>("Lastview"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("Rating"));

        for (Movie m : ListController.getMovieManager().getAllMovies())
            if (LocalDate.now().minusYears(2).isAfter(m.getLastview().toLocalDate()))
                moviesLastSeen.add(m);

        movieObservableList.addAll(moviesLastSeen);
        movieTableView.setItems(movieObservableList);
        movieTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ratingCol.setSortType(TableColumn.SortType.ASCENDING);
        movieTableView.getSortOrder().setAll(ratingCol);

        movieTableView.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue))
                movieDeleteButton.setDisable(false);
        });

        showAllCheckBox.selectedProperty().addListener((ov, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && showAllCheckBox.isSelected()) {
                moviesToDelete.clear();
                movieObservableList.setAll(ListController.getMovieManager().getAllMovies());
            }
            if (!Objects.equals(oldValue, newValue) && !showAllCheckBox.isSelected()) {
                moviesToDelete.clear();
                movieObservableList.setAll(moviesLastSeen);
            }
        });
    }
}