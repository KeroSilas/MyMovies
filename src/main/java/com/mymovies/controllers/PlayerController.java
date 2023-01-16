package com.mymovies.controllers;

import com.mymovies.model.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.Objects;

public class PlayerController {

    private Player player;

    private final Image playImage = new Image("file:src/main/resources/com/mymovies/images/play.png");
    private final Image pauseImage = new Image("file:src/main/resources/com/mymovies/images/pause.png");
    private final Image muteImage = new Image("file:src/main/resources/com/mymovies/images/muted.png");
    private final Image unmuteImage = new Image("file:src/main/resources/com/mymovies/images/unmuted.png");
    private final Image repeatImage = new Image("file:src/main/resources/com/mymovies/images/repeat.png");
    private final Image unrepeatImage = new Image("file:src/main/resources/com/mymovies/images/repeating.png");

    @FXML private Slider volumeSlider, progressSlider;

    @FXML private ProgressBar progressBar, volumeBar;

    @FXML private Label currentTimeLabel, totalDurationLabel, volumeLabel;

    @FXML private ImageView playPauseImage, muteUnmuteImage, repeatUnrepeatImage;

    @FXML private MediaView mediaView;

    @FXML private VBox mediaViewPane;

    //Determines whether the player is playing or paused and changes the buttons action accordingly.
    @FXML
    void handlePlayPause() {
        if (player.isPlaying()) {
            player.pause();
            playPauseImage.setImage(playImage);
        } else {
            player.play();
        }
    }

    @FXML void handleRepeat() {
        if (player.isRepeating()) {
            player.repeat(false);
            repeatUnrepeatImage.setImage(repeatImage);
        } else {
            player.repeat(true);
            repeatUnrepeatImage.setImage(unrepeatImage);
        }
    }

    @FXML void handleMuteUnmute() {
        if (player.isMuted()) {
            player.mute(false);
            volumeBar.lookup(".bar").setStyle("-fx-background-color: linear-gradient(to right, #376ef8, #295cdc);");
            muteUnmuteImage.setImage(unmuteImage);
        } else {
            player.mute(true);
            volumeBar.lookup(".bar").setStyle("-fx-background-color: linear-gradient(to right, #939393, #757575);");
            muteUnmuteImage.setImage(muteImage);
        }
        if (player.getVolume() == 0) {
            player.mute(false);
            volumeBar.lookup(".bar").setStyle("-fx-background-color: linear-gradient(to right, #376ef8, #295cdc);");
            volumeSlider.setValue(20);
            muteUnmuteImage.setImage(unmuteImage);
        }
    }

    //Changes progress of player when mouse click is released.
    @FXML void handleProgressSlider() {
        player.setProgress(progressSlider.getValue() / 100);
    }

    public void initialize() {
        Path path;
        if (ListController.isMoviePlayPressed)
            path = Path.of(ListController.selectedMovie.getMoviePath());
        else
            path = Path.of(ListController.selectedMovie.getTrailerPath());

        player = new Player(path);
        mediaView.fitHeightProperty().bind(mediaViewPane.heightProperty());
        mediaView.fitWidthProperty().bind(mediaViewPane.widthProperty());

        player.setOnReady(() -> {
            player.currentTimeProperty().addListener((ov, oldValue, newValue) -> {
                double percentage = newValue.toSeconds() / player.getTotalTime().toSeconds();
                if (!progressSlider.isPressed())
                    progressSlider.setValue(percentage * 100);
                progressBar.setProgress(percentage);

                //Keeps current time label updated.
                int currentTime = (int) newValue.toSeconds();
                int minutes = (currentTime % 3600) / 60;
                int seconds = currentTime % 60;
                currentTimeLabel.setText(String.format("%02d:%02d", minutes, seconds));
            });

            player.setOnEndOfMedia(() -> {
                if (!player.isRepeating()) {
                    player.dispose();
                    Stage stage = (Stage) mediaViewPane.getScene().getWindow();
                    stage.close();
                }
            });

            mediaView.setMediaPlayer(player.getMediaPlayer());

            int duration = (int) player.getTotalTime().toSeconds();
            int minutes = (duration % 3600) / 60;
            int seconds = duration % 60;
            totalDurationLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });

        volumeSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            double percentage = newValue.doubleValue();
            player.setVolume(percentage / 100);
            volumeBar.setProgress(percentage / 100);
            volumeLabel.setText(String.format("%s%%", newValue.intValue()));
            if (!Objects.equals(oldValue, newValue) && player.getVolume() == 0) {
                muteUnmuteImage.setImage(muteImage);
            } else if (!Objects.equals(oldValue, newValue) && player.getVolume() > 0) {
                player.mute(false);
                if (volumeBar.lookup(".bar") != null) //would give an error on launch, presumably since the bar hadn't been loaded yet
                    volumeBar.lookup(".bar").setStyle("-fx-background-color: linear-gradient(to right, #376ef8, #295cdc);");
                muteUnmuteImage.setImage(unmuteImage);
            }
        });

        volumeSlider.setValue(50);

        player.setOnPlaying(() -> playPauseImage.setImage(pauseImage));

        Platform.runLater(() -> {
            mediaViewPane.getScene().getWindow().setOnCloseRequest(t -> {
                player.dispose();
            });
        });
    }
}