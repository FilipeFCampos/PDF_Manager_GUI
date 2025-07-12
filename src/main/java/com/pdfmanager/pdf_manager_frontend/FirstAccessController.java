package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.cli.UserInterface;
import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import com.pdfmanager.pdf_manager_backend.utils.GUIException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FirstAccessController {

    @FXML TextField librarypathfield;
    @FXML TextField librarynamefield;
    @FXML CheckBox existinglibrarycb ;
    @FXML Text warningfield;
    @FXML Button savebutton;

    private UserInterface ui;

    public FirstAccessController() {
        DatabaseManager db;
        try {
            db = new DatabaseManager();
        } catch (Exception e) {
            System.err.println("ERROR: Invalid database path");
            return;
        }
        this.ui = new UserInterface(db);
    }

    @FXML
    private void saveButtonAction(ActionEvent event) {
        String libraryPath = librarypathfield.getText();
        String libraryName = librarynamefield.getText();

        if (libraryPath.isEmpty() || libraryName.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try {
            ui.editLibraryPathFromGUI(libraryPath, libraryName, existinglibrarycb.isSelected());
        } catch (GUIException e) {
            showAlert(e.getMessage());
            return;
        }

        showAlert("Library added successfully!! Path: " + libraryPath + File.separator + libraryName);
        try {
            switchToMenuScene(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void existingPathWarning() {
        if (existinglibrarycb.isSelected() && !librarynamefield.getText().isEmpty()) {
            warningfield.setText("WARNING: Keep in mind that any data present in'"
                    + librarynamefield.getText() + "' might be altered.");
        } else {
            warningfield.setText("");
        }

    }

    private void switchToMenuScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 480, 480);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
