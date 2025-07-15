package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.cli.UserInterface;
import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MenuController {

    private Parent root;

    public MenuController() {}

    /**
     * Switches to the add file scene. This method is called when the user clicks on the "Add File" button.
     * @param event
     * @throws IOException
     */
    @FXML
    protected void switchToAddFileScene(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("addFile.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 480, 480);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void switchToListFilesScene(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("listFiles.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 640, 480); // pode ajustar as dimensões
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void switchToRemoveFilesScene(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("removeFiles.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 640, 480));
        stage.show();
    }

    @FXML
    protected void switchToChangeLibraryScene(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("changeLibrary.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 480, 480);
        stage.setScene(scene);
        stage.show();
    }
}