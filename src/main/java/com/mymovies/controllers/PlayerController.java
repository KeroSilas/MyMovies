package com.mymovies.controllers;

import com.mymovies.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;

import java.util.Objects;

public class PlayerController {

    private Player player;

    private final Image playImage = new Image("file:src/main/resources/com/mymovies/images/play.png");
    private final Image pauseImage = new Image("file:src/main/resources/com/mymovies/images/pause.png");
    private final Image muteImage = new Image("file:src/main/resources/com/mymovies/images/muted.png");
    private final Image unmuteImage = new Image("file:src/main/resources/com/mymovies/images/unmuted.png");
    private final Image repeatImage = new Image("file:src/main/resources/com/mymovies/images/repeat.png");
    private final Image unrepeatImage = new Image("file:src/main/resources/com/mymovies/images/repeating.png");
    private final Image shuffleImage = new Image("file:src/main/resources/com/mymovies/images/shuffle.png");
    private final Image unshuffleImage = new Image("file:src/main/resources/com/mymovies/images/shuffling.png");

    @FXML private Slider volumeSlider, progressSlider;

    @FXML private ProgressBar progressBar, volumeBar;

    @FXML private Label currentMovieTitleLabel, currentSecondaryLabel, currentTimeLabel, totalDurationLabel, volumeLabel;

    @FXML private ImageView playPauseImage, muteUnmuteImage, repeatUnrepeatImage, shuffleUnshuffleImage;

    @FXML private MediaView mediaView;

    @FXML private VBox mediaViewPane;
    private ListController myController;

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

    @FXML void handleShuffle() {
        if (player.isShuffling()) {
            player.shuffle(false);
            shuffleUnshuffleImage.setImage(shuffleImage);
        } else {
            player.shuffle(true);
            shuffleUnshuffleImage.setImage(unshuffleImage);
        }
    }

    @FXML void handleNextSong() {
        player.next();
    }

    @FXML void handlePreviousSong() {
        player.previous();
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
        player = new Player();
        mediaView.fitHeightProperty().bind(mediaViewPane.heightProperty());
        mediaView.fitWidthProperty().bind(mediaViewPane.widthProperty());
        mediaView.setMediaPlayer(player.getMediaPlayer());

        //Add listener to volumeSlider.
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

        //Set default volume to 50%.
        volumeSlider.setValue(50);

        update();
        //Calls on the update method whenever a new song is loaded.
        player.hasLoadedProperty().addListener((ov, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue))
                update();
        });
    }

    //Refreshes some listeners and runnables: this is necessary because when a new media file is loaded, the listeners don't get updated with the new media-player object.
    private void update() {
        //Automatically moves progress slider with current time on song.
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

        //If repeat button isn't toggled on, automatically loads next song on end of media.
        player.setOnEndOfMedia(() -> {
            if (!player.isRepeating()) {
                player.next();
            }
        });

        //Changes play button icon whenever a song is playing (from either pressing play, clicking on a song or pressing next/prev).
        player.setOnPlaying(() -> playPauseImage.setImage(pauseImage));

        //Updates all relevant labels for currently playing song.
        player.setOnReady(() -> {
            int duration = (int) player.getTotalTime().toSeconds();
            int minutes = (duration % 3600) / 60;
            int seconds = duration % 60;
            totalDurationLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });
        currentMovieTitleLabel.setText(player.getCurrentMovie().getTitle());
        currentSecondaryLabel.setText(player.getCurrentMovie().getDirector());
    }
}