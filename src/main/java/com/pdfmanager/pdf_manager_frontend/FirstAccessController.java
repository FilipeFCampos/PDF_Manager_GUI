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

    /**
     * Handles the action of the save button, i.e. Validates the input fields and
     * attempts to set the library path and name.
     * @param event
     */
    @FXML
    private void saveButtonAction(ActionEvent event) {
        // Validate input fields
        String libraryPath = librarypathfield.getText();
        String libraryName = librarynamefield.getText();

        // Check if fields are empty
        if (libraryPath.isEmpty() || libraryName.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try {
            // Tries to update the config file with the provided library path and name
            ui.editLibraryPathFromGUI(libraryPath, libraryName, existinglibrarycb.isSelected());
        } catch (GUIException e) {
            // If unable to perform operation, show an alert with the error message
            showAlert(e.getMessage());
            return;
        }

        // If successful, show a success message and switch to the menu scene
        showAlert("Library added successfully!! Path: " + libraryPath + File.separator + libraryName);
        try {
            switchToMenuScene(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Warns the user of the potential consequences of using an existing library.
     * Shows up only if the checkbox is selected and the library name field is not empty.
     */
    @FXML
    private void existingPathWarning() {
        if (existinglibrarycb.isSelected() && !librarynamefield.getText().isEmpty()) {
            warningfield.setText("WARNING: Keep in mind that any data present in'"
                    + librarynamefield.getText() + "' might be altered.");
        } else {
            warningfield.setText("");
        }

    }

    /**
     * Switches to the menu scene after successfully saving the library path and name.
     * @param event
     * @throws IOException
     */
    private void switchToMenuScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 480, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays an alert with the provided message.
     * @param message Message to be displayed in the alert.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
