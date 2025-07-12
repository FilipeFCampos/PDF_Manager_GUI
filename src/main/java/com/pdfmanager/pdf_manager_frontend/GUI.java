package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.cli.UserInterface;
import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager db;
        try {
            db = new DatabaseManager();
        } catch (Exception e) {
            System.err.println("ERROR: Invalid database path");
            return;
        }
        UserInterface ui = new UserInterface(db);
        FXMLLoader fxmlLoader;
        if (ui.checkFirstAccess()) {
            fxmlLoader = new FXMLLoader(GUI.class.getResource("firstAccess.fxml"));
        } else {
            fxmlLoader = new FXMLLoader(GUI.class.getResource("menu.fxml"));
        }
        loadScene(fxmlLoader, stage);
    }

    private void loadScene(FXMLLoader fxmlLoader, Stage stage) throws IOException {
        Scene scene = new Scene(fxmlLoader.load(), 480, 480);
        stage.setResizable(false);
        stage.setTitle("PDF Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}