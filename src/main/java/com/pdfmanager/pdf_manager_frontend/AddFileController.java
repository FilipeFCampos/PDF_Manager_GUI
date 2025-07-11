package com.pdfmanager.pdf_manager_frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    Map<String, String> buffer = new HashMap<>();
    @FXML public Text output;

    @FXML
    private void submit(ActionEvent event) {
        buffer.clear();
        String title = titlefield.getText();
        String authors = authorsfield.getText();
        String path = pathfield.getText();
        String type;
        if (bookrb.isSelected()) {
            type = "book";
        } else if (classnoterb.isSelected()) {
            type = "classnote";
        } else if (sliderb.isSelected()) {
            type = "slide";
        } else {
            type = null;
        }

        if (title.isEmpty() || authors.isEmpty() || path.isEmpty() || type == null) {
            showAlert("Please fill all fields.");
            return;
        }

        buffer.put("title", title);
        buffer.put("authors", authors);
        buffer.put("path", path);
        buffer.put("type", type);

        printToGUI("deu bom");
        try {
            disablePane("addFileGlobal");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (type.equals("book")) {

        }
        // Send to backend logic here
    }

    private String addBook() {
        String subtitle = subtitlefield.getText();
        String fieldofknowledge = fieldofknowledgefield.getText();
        String publisher = publisherfield.getText();
        String publishyear = publishyearfield.getText();

        if (subtitle.isEmpty() || fieldofknowledge.isEmpty() || publishyear.isEmpty() || publisher.isEmpty()) {
            showAlert("Please fill all fields.");
            return null;
        }

        return null;
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void printToGUI(String content) {
        output.setText(content);
    }
}
