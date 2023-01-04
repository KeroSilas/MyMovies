module com.mymovies {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.bind;
    requires java.sql;
    requires java.naming;
    requires com.microsoft.sqlserver.jdbc;
    requires javafx.media;

    exports com.mymovies.controllers;
    opens com.mymovies.controllers to javafx.fxml;
    exports com.mymovies.dao;
    opens com.mymovies.dao to javafx.fxml;
    exports com.mymovies.model;
    opens com.mymovies.model to javafx.fxml;
    exports com.mymovies.services;
    opens com.mymovies.services to javafx.fxml;
}