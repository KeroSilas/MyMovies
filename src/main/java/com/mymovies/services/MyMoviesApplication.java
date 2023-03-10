package com.mymovies.services;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MyMoviesApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mymovies/views/ListTab.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MyMovies Library");
        stage.getIcons().add(new Image("file:src/main/resources/com/mymovies/images/logo.png")); //Adds an icon to the title-bar.
        stage.setMinWidth(600); //Restricts resizing to a minimum size.
        stage.setMinHeight(200);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}