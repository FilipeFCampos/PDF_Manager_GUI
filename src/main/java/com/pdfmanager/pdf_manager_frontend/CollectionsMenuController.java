package com.pdfmanager.pdf_manager_frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CollectionsMenuController {

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        Parent menuRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("menu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(menuRoot, 480, 480));
        stage.show();
    }

    @FXML
    private void handleCreateCollection(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("createCollection.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 500, 600)); // Ajuste o tamanho conforme necessário
        stage.show();
    }
}