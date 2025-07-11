package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.cli.UserInterface;
import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class AddFileController {
    @FXML RadioButton bookrb;
    @FXML RadioButton classnoterb;
    @FXML RadioButton sliderb;
    @FXML TextField titlefield;
    @FXML TextField pathfield;
    @FXML TextField authorsfield;
    @FXML StackPane stackPane;
    @FXML TextField fieldofknowledgefield;
    @FXML TextField subtitlefield;
    @FXML TextField publisherfield;
    @FXML TextField publishyearfield;
    Map<String, Object> buffer = new HashMap<>();
    @FXML public Text outputField;
    private String outputData;
    private UserInterface ui;

    public AddFileController() {
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
    private void submit(ActionEvent event) {
        this.buffer.clear();
        String title = titlefield.getText();
        String authors = authorsfield.getText();
        String path = pathfield.getText();
        String type;
        if (bookrb.isSelected()) {
            type = "Book";
        } else if (classnoterb.isSelected()) {
            type = "ClassNote";
        } else if (sliderb.isSelected()) {
            type = "Slide";
        } else {
            type = null;
        }

        if (title.isEmpty() || authors.isEmpty() || path.isEmpty() || type == null) {
            showAlert("Please fill all fields.");
            return;
        }

        this.buffer.put("title", title);
        List<String> authorsList = Arrays.asList(authors.split("\\s*,\\s*"));
        this.buffer.put("authors", authorsList);
        this.buffer.put("path", path);
        this.buffer.put("type", type);

        if (type.equals("Book")) {
            try {
                disablePane("#addFileGlobalPane");
                enablePane("#addBookPane");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Send to backend logic here
    }

    private void switchToMenuScene(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent root = loader.load();

        showAlert(this.outputData);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 480, 480);
        stage.setScene(scene);
        stage.show();
    }

    private void disablePane(String id) throws IOException {
        Node node = stackPane.lookup(id);
        if (node != null) {
            node.setDisable(true);
            node.setVisible(false);
        }
    }

    @FXML
    private void addBook(ActionEvent event) {
        String subtitle = subtitlefield.getText();
        String fieldofknowledge = fieldofknowledgefield.getText();
        String publisher = publisherfield.getText();
        String publishyear = publishyearfield.getText();

        if (subtitle.isEmpty() || fieldofknowledge.isEmpty() || publishyear.isEmpty() || publisher.isEmpty()) {
            showAlert("Please fill all fields.");
            return;
        }

        this.buffer.put("subTitle", subtitle);
        this.buffer.put("fieldOfKnowledge", fieldofknowledge);
        this.buffer.put("publisher", publisher);
        this.buffer.put("publishYear", publishyear);
        try {
            disablePane("#addBookPane");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.outputData = ui.addToDbFromGUI(this.buffer);
        try {
            switchToMenuScene(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void enablePane(String id) throws IOException {
        Node node = stackPane.lookup(id);
        if (node != null) {
            node.setDisable(false);
            node.setVisible(true);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //public void printToGUI(String content) {
    //    outputField.setText(content);
    //}
}
