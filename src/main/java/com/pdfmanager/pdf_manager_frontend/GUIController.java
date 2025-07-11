package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.cli.UserInterface;
import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GUIController {

    private UserInterface ui;
    private Parent root;
    private String buffer;
    @FXML private StackPane stackPane;
    @FXML public Text output;

    public GUIController() {
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
    protected void switchToAddFileScene(ActionEvent event) throws IOException {
        this.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("addFile.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 480, 480);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void addFile(ActionEvent event) throws IOException {
        //buffer = ui.test();
        //printToGUI(buffer);
        //disablePane("#options");
        //enablePane("#addFileGlobal");
        switchToAddFileScene(event);
    }

    private void disablePane(String id) throws IOException {
        Node node = stackPane.lookup(id);
        if (node != null) {
            node.setDisable(true);
            node.setVisible(false);
        }
    }

    private void enablePane(String id) throws IOException {
        Node node = stackPane.lookup(id);
        if (node != null) {
            node.setDisable(false);
            node.setVisible(true);
        }
    }

    public void printToGUI(String content) {
        output.setText(content);
    }
}