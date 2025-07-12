package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.cli.UserInterface;
import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends javafx.application.Application {
    /**
     * Starts the JavaFX application.
     * Initializes the database manager and loads the user interface based on whether it's the user's first access.
     * @param stage The primary stage for this application, onto which the application scene can be set.
     * @throws IOException If an I/O error occurs during loading of FXML files.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize the database manager
        DatabaseManager db;
        try {
            db = new DatabaseManager();
        } catch (Exception e) {
            System.err.println("ERROR: Invalid database path");
            return;
        }
        // Loads the user interface to check whether it's the user's first access or not
        UserInterface ui = new UserInterface(db);
        FXMLLoader fxmlLoader;
        if (ui.checkFirstAccess()) {
            // If it's the user's first access, load the first access scene
            fxmlLoader = new FXMLLoader(GUI.class.getResource("firstAccess.fxml"));
        } else {
            // If it's not the user's first access, load the main menu scene
            fxmlLoader = new FXMLLoader(GUI.class.getResource("menu.fxml"));
        }
        // Load the scene with the FXML loader and set the stage
        loadScene(fxmlLoader, stage);
    }

    /**
     * Loads the scene for the given FXML loader and stage.
     * @param fxmlLoader
     * @param stage
     * @throws IOException
     */
    private void loadScene(FXMLLoader fxmlLoader, Stage stage) throws IOException {
        // Load the FXML file and set the scene within a 480x480 window
        Scene scene = new Scene(fxmlLoader.load(), 480, 480);
        // By design, the window is not resizable
        stage.setResizable(false);
        // Set the title of the window and the scene
        stage.setTitle("PDF Manager");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method to launch the JavaFX application.
     * @param args
     */
    public static void main(String[] args) { launch(); }
}