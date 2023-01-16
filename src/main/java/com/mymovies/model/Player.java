package com.mymovies.model;

import javafx.beans.property.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.nio.file.Path;

/**
 * A class that is responsible for manipulating a MediaPlayer object.
 */

public class Player {

    private final Media media;
    private final MediaPlayer mediaPlayer;

    public Player(Path path) {
        media = new Media(path.toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        play();
    }

    public void play() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime()); //Without this line, a small stutter would occur when resuming after pause.
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void mute(boolean mute) {
        mediaPlayer.setMute(mute);
    }

    //Repeats the currently playing movie when it reaches end of media.
    public void repeat(boolean repeat) {
        if (repeat) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } else {
            mediaPlayer.setCycleCount(1);
        }
    }

    public void dispose() {
        mediaPlayer.dispose();
    }

    public double getVolume() {
        return mediaPlayer.getVolume();
    }

    public Duration getCurrentTime() {
        return mediaPlayer.getCurrentTime();
    }

    public Duration getTotalTime() {
        return mediaPlayer.getTotalDuration();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    //Volume value must be between 0 and 1.
    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }

    //Progress value must be between 0 and 1.
    public void setProgress(double progress) {
        mediaPlayer.seek(media.getDuration().multiply(progress));
    }

    public boolean isPlaying() {
        return mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public boolean isRepeating() {
        return mediaPlayer.getCycleCount() == MediaPlayer.INDEFINITE;
    }

    public boolean isMuted() {
        return mediaPlayer.isMute();
    }

    public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
        return mediaPlayer.currentTimeProperty();
    }

    public void setOnEndOfMedia(Runnable runnable) {
        mediaPlayer.setOnEndOfMedia(runnable);
    }

    public void setOnPlaying(Runnable runnable) {
        mediaPlayer.setOnPlaying(runnable);
    }

    public void setOnReady(Runnable runnable) {
        mediaPlayer.setOnReady(runnable);
    }
}