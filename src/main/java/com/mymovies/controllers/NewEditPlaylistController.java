package com.mymovies.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewEditPlaylistController {

    private MyMoviesController myTunesController;

    @FXML private Button cancelButton, saveButton;

    @FXML private TextField nameTextField;

    @FXML void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML void handleSave() {
        if (MyMoviesController.isNewPressed)
            myTunesController.getPlaylistsManager().addPlaylist(nameTextField.getText());
        else
            myTunesController.getPlaylistsManager().updatePlaylist(MyMoviesController.selectedPlaylist, nameTextField.getText());

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        if (!MyMoviesController.isNewPressed) {
            nameTextField.setText(MyMoviesController.selectedPlaylist.getName());
        }
    }

    public void setMyTunesController(MyMoviesController myTunesController) {
        this.myTunesController = myTunesController;
    }
}