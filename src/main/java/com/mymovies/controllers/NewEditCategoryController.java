package com.mymovies.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewEditCategoryController {

    @FXML private Button cancelButton, saveButton;

    @FXML private TextField nameTextField;

    @FXML void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML void handleSave() {
        if (ListController.isNewPressed)
            ListController.getCategoryManager().addCategory(nameTextField.getText());
        else
            ListController.getCategoryManager().updateCategory(ListController.selectedCategory, nameTextField.getText());

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        if (!ListController.isNewPressed) {
            nameTextField.setText(ListController.selectedCategory.getName());
        }
    }
}