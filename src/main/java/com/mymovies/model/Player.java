package com.mymovies.model;

import javafx.beans.property.*;
import javafx.scene.control.CheckBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.nio.file.Path;
import java.sql.Date;
import java.util.*;

/**
 * A class that is responsible for manipulating a MediaPlayer object and load Song objects from playlists/all-songs.
 * Also provides methods such as next(), previous() and shuffle().
 */

public class Player {

    private Path path;
    private Media media;
    private MediaPlayer mediaPlayer;

    private final SimpleBooleanProperty hasLoadedProperty = new SimpleBooleanProperty();
    private Movie currentMovie;
    private Category currentCategory;
    private List<Movie> allMovies;

    private boolean isShuffling;
    private int shuffleCounter = 0;
    private final List<Integer> shuffleNumbers = new ArrayList<>();

    private ListStatus listStatus;

    //Enum list that are used to specify whether the player is playing from a playlist or the all-songs list.
    public enum ListStatus {
        ALL_MOVIES,
        CATEGORY,
    }

    //Default constructor for when there are no songs.
    public Player() {
        path = Path.of("src/main/resources/com/mymovies/trailers/The Dark Knight Trailer.mp4");
        load(path);

        CheckBox like = new CheckBox();
        like.setSelected(Boolean.TRUE);
        currentMovie = new Movie(2, "Test", "Test", 9.0F, Date.valueOf("2009-12-12"), "src/main/resources/com/mymovies/trailers/The Dark Knight Trailer.mp4", "src/main/resources/com/mymovies/trailers/The Dark Knight Trailer.mp4", 0, 0.0F, like);
    }

    //Constructor for when songs list is not empty.
    public Player(List<Movie> movies, Movie movie) {
        this(); //Initialize the base MediaPlayer object, so that the next line can utilize its functions.
        load(movies, movie);
        pause();
    }

    //Base loader for passing in a file or initializing the MediaPlayer object.
    private void load(Path path) {
        media = new Media(path.toUri().toString());
        mediaPlayer = new MediaPlayer(media);
    }

    //Used in prev/next methods.
    private void load(Movie movie) {
        //Stores player values in temporary variables so that they can be reapplied on the new media-player object.
        double volumeBeforeMediaChange = mediaPlayer.getVolume();
        boolean isMutedBeforeMediaChange = isMuted();
        boolean isRepeatingBeforeMediaChange = isRepeating();

        mediaPlayer.dispose(); //Removes the previous MediaPlayer object, as a new one will be created from the next load.

        path = Path.of(movie.getMoviePath());
        load(path);

        //Reapplies player values from before.
        setVolume(volumeBeforeMediaChange);
        mute(isMutedBeforeMediaChange);
        repeat(isRepeatingBeforeMediaChange);
        play();

        currentMovie = movie;

        //Toggles to either true or false when a song has been loaded.
        //It is used to detect a change to run the update() method in MyTunesController.
        hasLoadedProperty.set(!hasLoadedProperty.get());
    }

    //Used when clicking a song on the all songs list.
    public void load(List<Movie> movies, Movie movie) {
        setListStatus(ListStatus.ALL_MOVIES);
        load(movie);

        //Resets shuffle algorithm.
        shuffleCounter = 0;
        shuffleNumbers.clear();
        while (shuffleCounter < movies.size()) {
            shuffleNumbers.add(shuffleCounter++);
        }
        Collections.shuffle(shuffleNumbers);
        shuffleNumbers.remove((Integer) movies.indexOf(movie));
        shuffleNumbers.add(movies.indexOf(movie));

        allMovies = movies;
    }

    //Used when clicking a song on a playlist.
    public void load(Category category, Movie movie) {
        setListStatus(ListStatus.CATEGORY);
        load(movie);

        //Resets shuffle algorithm.
        shuffleCounter = 0;
        shuffleNumbers.clear();
        while (shuffleCounter < category.getMovies().size()) {
            shuffleNumbers.add(shuffleCounter++);
        }
        Collections.shuffle(shuffleNumbers);
        shuffleNumbers.remove((Integer) category.getMovies().indexOf(movie));
        shuffleNumbers.add(category.getMovies().indexOf(movie));

        currentCategory = category;
    }

    public void play() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime()); //Without this line, a small stutter would occur when resuming after pause.
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    //Loads next song on either the all songs list or the currently playing playlist.
    public void next() {
        if (getListStatus() == Player.ListStatus.ALL_MOVIES) {
            if (isShuffling()) {
                load(allMovies.get(shuffleNumbers.get(0)));
                shuffleNumbers.add(shuffleNumbers.get(0));
                shuffleNumbers.remove(0);
            } else if (allMovies.indexOf(getCurrentMovie()) == allMovies.size() - 1) { //Checks if current song is at the end of the list.
                load(allMovies.get(0)); //Returns to first song on the list.
            } else {
                Movie nextMovie = allMovies.get(allMovies.indexOf(getCurrentMovie()) + 1); //Gets next song on the list.
                load(nextMovie);
            }
        } else if (getListStatus() == Player.ListStatus.CATEGORY) {
            if (isShuffling()) {
                load(currentCategory.getMovies().get(shuffleNumbers.get(0)));
                shuffleNumbers.add(shuffleNumbers.get(0));
                shuffleNumbers.remove(0);
            } else if (currentCategory.getMovies().indexOf(getCurrentMovie()) == currentCategory.getMovies().size() - 1) {
                load(currentCategory.getMovies().get(0));
            } else {
                Movie nextMovie = currentCategory.getMovies().get(currentCategory.getMovies().indexOf(getCurrentMovie()) + 1);
                load(nextMovie);
            }
        }
    }

    //Loads previous song on either the all songs list or the currently playing playlist.
    public void previous() {
        if (getCurrentTime().toSeconds() > 3) { //Checks if less than 3 seconds have passed.
            reset(); //Resets currently playing song.
        } else if (getListStatus() == Player.ListStatus.ALL_MOVIES) {
            if (allMovies.indexOf(getCurrentMovie()) == 0) { //Checks if current song is at the start of the list.
                reset();
            } else {
                Movie previousMovie = allMovies.get(allMovies.indexOf(getCurrentMovie()) - 1); //Gets previous song on the list.
                load(previousMovie);
            }
        } else if (getListStatus() == Player.ListStatus.CATEGORY) {
            if (currentCategory.getMovies().indexOf(getCurrentMovie()) == 0) {
                reset();
            } else {
                Movie previousMovie = currentCategory.getMovies().get(currentCategory.getMovies().indexOf(getCurrentMovie()) - 1);
                load(previousMovie);
            }
        }
    }

    public void reset() {
        mediaPlayer.seek(mediaPlayer.getStartTime()); //Seeks the media-player back to its start time.
    }

    public void mute(boolean mute) {
        mediaPlayer.setMute(mute);
    }

    //Repeats the currently playing song when it reaches end of media.
    public void repeat(boolean repeat) {
        if (repeat) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } else {
            mediaPlayer.setCycleCount(1);
        }
    }

    public void shuffle(boolean shuffle) {
        isShuffling = shuffle;
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

    public Movie getCurrentMovie() {
        return currentMovie;
    }

    public Category getCurrentCategory() {
        return currentCategory;
    }

    //Used to retrieve metadata in MyTunesController.
    public Media getMedia() {
        return mediaPlayer.getMedia();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public ListStatus getListStatus() {
        return listStatus;
    }

    //List status must be either ALL_SONGS or PLAYLIST.
    public void setListStatus(ListStatus listStatus) {
        this.listStatus = listStatus;
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

    public boolean isShuffling() {
        return isShuffling;
    }

    public boolean isMuted() {
        return mediaPlayer.isMute();
    }

    public void updateCurrentCategory(Category category) {
        currentCategory = category;
    }

    public void updateCurrentAllMovies(List<Movie> allMovies) {
        this.allMovies = allMovies;
    }

    public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
        return mediaPlayer.currentTimeProperty();
    }

    public ReadOnlyBooleanProperty hasLoadedProperty() {
        return hasLoadedProperty;
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