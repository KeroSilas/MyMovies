package com.mymovies.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewEditCategoryController {

    private MyMoviesController myController;

    @FXML private Button cancelButton, saveButton;

    @FXML private TextField nameTextField;

    @FXML void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML void handleSave() {
        if (MyMoviesController.isNewPressed)
            myController.getPlaylistsManager().addPlaylist(nameTextField.getText());
        else
            myController.getPlaylistsManager().updatePlaylist(MyMoviesController.selectedCategory, nameTextField.getText());

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        if (!MyMoviesController.isNewPressed) {
            nameTextField.setText(MyMoviesController.selectedCategory.getName());
        }
    }

    public void setMyController(MyMoviesController myController) {
        this.myController = myController;
    }
}