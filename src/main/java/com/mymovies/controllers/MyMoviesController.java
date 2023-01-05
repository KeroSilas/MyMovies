package com.mymovies.controllers;

import com.mymovies.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Responsible for controlling various elements in the GUI by utilizing the model classes.
 */

public class MyMoviesController {

    private Player player;

    //Initialize all the images that will be used when toggling different button states.
    private final Image playImage = new Image("file:src/main/resources/com/mymovies/images/play.png");
    private final Image pauseImage = new Image("file:src/main/resources/com/mymovies/images/pause.png");
    private final Image muteImage = new Image("file:src/main/resources/com/mymovies/images/muted.png");
    private final Image unmuteImage = new Image("file:src/main/resources/com/mymovies/images/unmuted.png");
    private final Image repeatImage = new Image("file:src/main/resources/com/mymovies/images/repeat.png");
    private final Image unrepeatImage = new Image("file:src/main/resources/com/mymovies/images/repeating.png");
    private final Image shuffleImage = new Image("file:src/main/resources/com/mymovies/images/shuffle.png");
    private final Image unshuffleImage = new Image("file:src/main/resources/com/mymovies/images/shuffling.png");
    private final Image searchImage = new Image("file:src/main/resources/com/mymovies/images/search.png");
    private final Image unsearchImage = new Image("file:src/main/resources/com/mymovies/images/cancel.png");
    private final Image defaultAlbumImage = new Image("file:src/main/resources/com/mymovies/images/default-album-art.png");

    private MovieManager movieManager;
    private CategoryManager categoryManager;
    private boolean isSearching = false;
    protected static boolean isNewPressed;

    private final ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();
    private final ObservableList<Movie> movieObservableList = FXCollections.observableArrayList();
    private final ObservableList<Movie> movieInPlaylistObservableList = FXCollections.observableArrayList();
    protected static Category selectedCategory;
    protected static Movie selectedMovie;
    private Movie selectedMovieInPlaylist;

    @FXML private TableView<Category> categoryTableView;
    @FXML private TableColumn<Category, String> nameColumn, totalDurationColumn;
    @FXML private TableColumn<Category, Integer> songsColumn;
    @FXML private TableView<Movie> movieTableView;
    @FXML private TableColumn<Movie, String> titleColumn, artistColumn, categoryColumn, durationColumn;
    @FXML private ListView<Movie> catMovieListView;

    @FXML private TextField searchTextField;

    @FXML private Slider volumeSlider, progressSlider;

    @FXML private ProgressBar progressBar, volumeBar;

    @FXML private Button editPlaylistButton, deletePlaylistButton, editSongButton, deleteSongButton, moveSongUpButton, moveSongDownButton, deleteSongFromPlaylistButton, addSongToPlaylistButton;

    @FXML private Label currentSongTitleLabel, currentSongArtistLabel, currentTimeLabel, totalDurationLabel, volumeLabel;

    @FXML private ImageView playPauseImage, muteUnmuteImage, repeatUnrepeatImage, shuffleUnshuffleImage, searchUnsearchImage, albumCoverImage;

    ///// --- LIST AND SELECTION METHODS --- /////

    @FXML void handlePlaylistClick(MouseEvent e) {
        selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
            if (selectedCategory.getNumberOfMovies() > 0 && e.getClickCount() == 2) {
                movieTableView.getSelectionModel().clearSelection();
                player.load(selectedCategory, selectedCategory.getMovies().get(0));
                catMovieListView.getSelectionModel().select(player.getCurrentSong());
            }
        }
    }

    @FXML void handleSongClick(MouseEvent e) {
        selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null && e.getClickCount() == 2) {
            catMovieListView.getSelectionModel().clearSelection();
            player.load(movieObservableList, selectedMovie);
        }
    }

    @FXML void handleSongInPlaylistClick(MouseEvent e) {
        selectedMovieInPlaylist = catMovieListView.getSelectionModel().getSelectedItem();
        if (selectedMovieInPlaylist != null && e.getClickCount() == 2) {
            movieTableView.getSelectionModel().clearSelection();
            player.load(selectedCategory, selectedMovieInPlaylist);
        }
    }

    @FXML void handleMoveSongUp() {
        Collections.swap(selectedCategory.getMovies(), selectedCategory.getMovies().indexOf(selectedMovieInPlaylist), selectedCategory.getMovies().indexOf(selectedMovieInPlaylist) - 1);
        movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
        catMovieListView.getSelectionModel().select(selectedMovieInPlaylist);
        player.updateCurrentPlaylist(selectedCategory);
    }

    @FXML void handleMoveSongDown() {
        Collections.swap(selectedCategory.getMovies(), selectedCategory.getMovies().indexOf(selectedMovieInPlaylist), selectedCategory.getMovies().indexOf(selectedMovieInPlaylist) + 1);
        movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
        catMovieListView.getSelectionModel().select(selectedMovieInPlaylist);
        player.updateCurrentPlaylist(selectedCategory);
    }

    ///// --- MANAGER OBJECTS CONTROLS --- /////

    //searches for songs in the database
    @FXML void handleSearch() {
        if (!isSearching && !searchTextField.getText().isEmpty()) {
            movieObservableList.setAll(movieManager.searchSongs(searchTextField.getText()));
            searchUnsearchImage.setImage(unsearchImage);
            isSearching = true;
        } else {
            searchTextField.clear();
            movieObservableList.setAll(movieManager.getAllSongs());
            searchUnsearchImage.setImage(searchImage);
            isSearching = false;
        }
    }

    @FXML void handleAddPlaylist() {
        isNewPressed = true;
        showNewEditPlaylistWindow();
        categoryObservableList.setAll(categoryManager.getAllPlaylists());
    }

    @FXML void handleAddSong() {
        isNewPressed = true;
        showNewEditSongWindow();
        movieObservableList.setAll(movieManager.getAllSongs());
        player.updateCurrentAllSongs(movieManager.getAllSongs());
    }

    @FXML void handleAddSongToPlaylist() {
        boolean containsDuplicate = false;
        if (selectedCategory != null && selectedMovie != null) {
            for (Movie s : selectedCategory.getMovies()) {
                if (s.getId() == selectedMovie.getId()) {
                    containsDuplicate = true;
                    Optional<ButtonType> result = showAlertWindow("You already have this song in the selected playlist.\nDo you want to add a duplicate?");
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        selectedCategory.addMovie(selectedMovie);
                        movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
                        categoryObservableList.setAll(categoryManager.getAllPlaylists());
                        player.updateCurrentPlaylist(selectedCategory);
                    }
                    break;
                }
            }
        }
        if (selectedCategory != null && selectedMovie != null && !containsDuplicate) {
            selectedCategory.addMovie(selectedMovie);
            movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
            categoryObservableList.setAll(categoryManager.getAllPlaylists());
            player.updateCurrentPlaylist(selectedCategory);
        }
    }

    @FXML void handleEditPlaylist() {
        editPlaylist();
    }

    @FXML void handleEditSong() {
        editSong();
    }

    @FXML void handleDeleteSong() {
        deleteSong();
    }

    @FXML void handleDeletePlaylist() {
        deletePlaylist();
    }

    @FXML void handleDeleteSongFromPlaylist() {
        deleteSongInPlaylist();
    }

    ///// --- PLAYER CONTROLS --- /////

    //Determines whether the player is playing or paused and changes the buttons action accordingly.
    @FXML void handlePlayPause() {
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
        //Initialize song and playlist managers.
        movieManager = new MovieManager();
        categoryManager = new CategoryManager();

        //Set up the table columns and cells for the song table.
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("Title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("Artist"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("Category"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("DurationInString"));
        durationColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");
        movieObservableList.addAll(movieManager.getAllSongs());
        movieTableView.setItems(movieObservableList);
        movieTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        movieTableView.setContextMenu(getSongsContextMenu());

        //Set up the table columns and cells for the playlist table.
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        songsColumn.setCellValueFactory(new PropertyValueFactory<>("NumberOfSongs"));
        songsColumn.setStyle( "-fx-alignment: CENTER;");
        totalDurationColumn.setCellValueFactory(new PropertyValueFactory<>("DurationInString"));
        totalDurationColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");
        categoryObservableList.addAll(categoryManager.getAllPlaylists());
        categoryTableView.setItems(categoryObservableList);
        categoryTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        categoryTableView.setContextMenu(getPlaylistsContextMenu());

        catMovieListView.setItems(movieInPlaylistObservableList);
        catMovieListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        catMovieListView.setContextMenu(getSongsInPlaylistContextMenu());

        //Initialize player with first song on Songs list if there is any.
        if (movieObservableList.isEmpty()) {
            player = new Player();
        } else {
            player = new Player(movieManager.getAllSongs(), movieManager.getAllSongs().get(0));
            movieTableView.getSelectionModel().select(player.getCurrentSong());
            Platform.runLater(() -> movieTableView.requestFocus());
        }

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

        //Next three sets of listeners are responsible for disabling or enabling buttons that have no valid selection input.
        categoryTableView.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && categoryTableView.getSelectionModel().getSelectedItem() == null) {
                editPlaylistButton.setDisable(true);
                deletePlaylistButton.setDisable(true);
            } else if (!Objects.equals(oldValue, newValue)) {
                editPlaylistButton.setDisable(false);
                deletePlaylistButton.setDisable(false);
            }
        });

        movieTableView.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && movieTableView.getSelectionModel().getSelectedItem() == null) {
                editSongButton.setDisable(true);
                deleteSongButton.setDisable(true);
                addSongToPlaylistButton.setDisable(true);
            } else if (!Objects.equals(oldValue, newValue)) {
                editSongButton.setDisable(false);
                deleteSongButton.setDisable(false);
                addSongToPlaylistButton.setDisable(false);
            }
        });

        catMovieListView.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && catMovieListView.getSelectionModel().getSelectedItem() == null) {
                moveSongUpButton.setDisable(true);
                moveSongDownButton.setDisable(true);
                deleteSongFromPlaylistButton.setDisable(true);
            } else if (!Objects.equals(oldValue, newValue)) {
                moveSongUpButton.setDisable(false);
                moveSongDownButton.setDisable(false);
                deleteSongFromPlaylistButton.setDisable(false);
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
            double percentage = newValue.toSeconds() / player.getCurrentSong().getDurationInInteger();
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

        //Updates selection and focus toward currently playing song.
        if (player.getListStatus() == Player.ListStatus.ALL_SONGS) {
            movieTableView.getSelectionModel().select(player.getCurrentSong());
            movieTableView.requestFocus();
            movieTableView.scrollTo(player.getCurrentSong());
            selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        } else if (player.getListStatus() == Player.ListStatus.PLAYLIST) {
            catMovieListView.getSelectionModel().select(player.getCurrentSong());
            catMovieListView.requestFocus();
            catMovieListView.scrollTo(player.getCurrentSong());
            selectedMovieInPlaylist = catMovieListView.getSelectionModel().getSelectedItem();
        }

        //Updates all relevant labels for currently playing song.
        totalDurationLabel.setText(player.getCurrentSong().getDurationInString());
        currentSongTitleLabel.setText(player.getCurrentSong().getTitle());
        currentSongArtistLabel.setText(player.getCurrentSong().getArtist());

        //Retrieves album cover on currently playing song.
        albumCoverImage.setImage(defaultAlbumImage);
        player.getMedia().getMetadata().addListener((MapChangeListener.Change<? extends String, ?> c) -> {
            if (c.wasAdded()) {
                if ("image".equals(c.getKey())) {
                    albumCoverImage.setImage((Image) c.getValueAdded());
                }
            }
        });
    }

    public MovieManager getSongsManager() {
        return movieManager;
    }

    public CategoryManager getPlaylistsManager() {
        return categoryManager;
    }

    private void showNewEditSongWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mymovies/views/NewEditMovie.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            NewEditMovieController newEditMovieController = fxmlLoader.getController();
            newEditMovieController.setMyController(this);
            Stage stage = new Stage();
            if (isNewPressed) {
                stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/add.png"));
                stage.setTitle("New Song");
            } else {
                stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/edit.png"));
                stage.setTitle("Edit Song");
            }
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Next three methods are responsible for adding right-lick context menus for editing or deleting items.
    private ContextMenu getPlaylistsContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");

        edit.setOnAction((event) -> editPlaylist());
        delete.setOnAction((event) -> deletePlaylist());

        contextMenu.getItems().addAll(edit,delete);
        return contextMenu;
    }

    private ContextMenu getSongsContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");

        edit.setOnAction((event) -> editSong());
        delete.setOnAction((event) -> deleteSong());

        contextMenu.getItems().addAll(edit,delete);
        return contextMenu;
    }

    private ContextMenu getSongsInPlaylistContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");

        delete.setOnAction((event) -> deleteSongInPlaylist());

        contextMenu.getItems().addAll(delete);
        return contextMenu;
    }

    private void showNewEditPlaylistWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mymovies/views/NewEditCategory.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            NewEditCategoryController newEditCategoryController = fxmlLoader.getController();
            newEditCategoryController.setMyController(this);
            Stage stage = new Stage();
            if (isNewPressed) {
                stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/add.png"));
                stage.setTitle("New Playlist");
            } else {
                stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/edit.png"));
                stage.setTitle("Edit Playlist");
            }
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Opens an alert window that can be customized.
    //Returns the button type that was pressed in the alert window.
    private Optional<ButtonType> showAlertWindow(String text) {
        Alert alert = new Alert(Alert.AlertType.NONE, text, ButtonType.NO, ButtonType.YES);
        alert.setTitle("Confirmation");
        alert.initOwner(searchTextField.getScene().getWindow()); //Retrieves the title bar icon from the main window by setting the alerts owner to that window.
        return alert.showAndWait();
    }

    private void editSong() {
        isNewPressed = false;
        showNewEditSongWindow();
        movieObservableList.setAll(movieManager.getAllSongs());
    }

    private void deleteSong() {
        Optional<ButtonType> result = showAlertWindow("Are you sure you wish to delete this song?");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            movieManager.removeSong(selectedMovie);
            movieObservableList.setAll(movieManager.getAllSongs());
            //Loops through and deletes all copies of the deleted song from each playlist.
            for (int i = 0; i < categoryManager.getAllPlaylists().size(); i++) {
                categoryManager.getAllPlaylists().get(i).getMovies().removeIf(s -> s.getId() == selectedMovie.getId());
            }

            //Updates Playlist and SongsInPlaylist lists.
            categoryObservableList.setAll(categoryManager.getAllPlaylists());
            if (selectedCategory != null) {
                movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
            }

            //Sends new versions of the lists to the Player.
            player.updateCurrentPlaylist(selectedCategory);
            player.updateCurrentAllSongs(movieObservableList);
        }
    }

    private void deleteSongInPlaylist() {
        Optional<ButtonType> result = showAlertWindow("Are you sure you wish to delete this song from the playlist?");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            selectedCategory.removeMovie(selectedMovieInPlaylist);
            movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
            categoryObservableList.setAll(categoryManager.getAllPlaylists());
            player.updateCurrentPlaylist(selectedCategory);
            if (player.getCurrentPlaylist() == selectedCategory && player.getListStatus() == Player.ListStatus.PLAYLIST && selectedCategory.getMovies().size() == 0) {
                player.load(movieObservableList, movieObservableList.get(0));
            }
        }
    }

    private void editPlaylist() {
        isNewPressed = false;
        showNewEditPlaylistWindow();
        categoryObservableList.setAll(categoryManager.getAllPlaylists());
    }

    private void deletePlaylist() {
        Optional<ButtonType> result = showAlertWindow("Are you sure you wish to delete this playlist?");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            categoryManager.removePlaylist(selectedCategory);
            categoryObservableList.setAll(categoryManager.getAllPlaylists());
            selectedCategory.getMovies().clear();
            movieInPlaylistObservableList.setAll(selectedCategory.getMovies());
            //If the playlist that is getting deleted is currently loaded, then switch the player to load the first song on the all songs list.
            if (player.getCurrentPlaylist() == selectedCategory && player.getListStatus() == Player.ListStatus.PLAYLIST) {
                player.load(movieObservableList, movieObservableList.get(0));
            }
        }
    }
}