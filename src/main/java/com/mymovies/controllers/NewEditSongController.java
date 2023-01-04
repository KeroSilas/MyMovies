package com.mymovies.controllers;

import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class NewEditSongController {

    private MyMoviesController myTunesController;
    private int duration;

    @FXML private GridPane gridPane;

    @FXML private TextField titleTextField, artistTextField, categoryTextField, durationTextField, fileTextField;

    @FXML private Button cancelButton, saveButton;

    @FXML void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML void handleChooseSong() {
        Stage stage = (Stage) gridPane.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Audio files", "*.mp3", "*.wav");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File("src/main/resources/com/mytunes/music/"));
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            fileTextField.setText(file.getName());

            media.getMetadata().addListener((MapChangeListener.Change<? extends String, ?> c) -> {
                if (c.wasAdded()) {
                    if ("title".equals(c.getKey())) {
                        titleTextField.setText(c.getValueAdded().toString());
                    } else if ("artist".equals(c.getKey())) {
                        artistTextField.setText(c.getValueAdded().toString());
                    } else if ("genre".equals(c.getKey())) {
                        categoryTextField.setText(c.getValueAdded().toString());
                    }
                }
            });

            mediaPlayer.setOnReady(() -> {
                duration = (int) mediaPlayer.getTotalDuration().toSeconds();
                int minutes = (duration % 3600) / 60;
                int seconds = duration % 60;
                durationTextField.setText(String.format("%02d:%02d", minutes, seconds));
            });
        }
    }

    @FXML void handleSave() {
        if (MyMoviesController.isNewPressed)
            myTunesController.getSongsManager().addSong(titleTextField.getText(), artistTextField.getText(), categoryTextField.getText(), duration, fileTextField.getText());
        else
            myTunesController.getSongsManager().updateSong(MyMoviesController.selectedSong, titleTextField.getText(), artistTextField.getText(), categoryTextField.getText(), duration, fileTextField.getText());

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        if (!MyMoviesController.isNewPressed) {
            titleTextField.setText(MyMoviesController.selectedSong.getTitle());
            artistTextField.setText(MyMoviesController.selectedSong.getArtist());
            categoryTextField.setText(MyMoviesController.selectedSong.getCategory());
            durationTextField.setText(MyMoviesController.selectedSong.getDurationInString());
            duration = MyMoviesController.selectedSong.getDurationInInteger();
            fileTextField.setText(MyMoviesController.selectedSong.getPath());
        }
    }

    public void setMyTunesController(MyMoviesController myTunesController) {
        this.myTunesController = myTunesController;
    }
}