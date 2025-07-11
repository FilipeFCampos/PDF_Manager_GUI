package com.pdfmanager.pdf_manager_frontend.utils;

import com.pdfmanager.pdf_manager_frontend.GUI;
import com.pdfmanager.pdf_manager_frontend.GUIController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class Communicator {

    Parent root;
    GUIController controller;

    public Communicator() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pdfmanager/pdf_manager_frontend/menu.fxml"));
        try {
            this.root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printToGUI(String content) {
        Text output = (Text) root.lookup("#outputField");
        if (output != null) {
            Platform.runLater(() -> output.setText(content));
        }
        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.show();
        //controller.outputField.setText(content);
    }
}
