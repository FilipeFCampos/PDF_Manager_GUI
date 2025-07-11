package com.pdfmanager.pdf_manager_frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class AddFileController {
    @FXML RadioButton bookrb;
    @FXML RadioButton classnoterb;
    @FXML RadioButton sliderb;
    @FXML TextField titlefield;
    @FXML TextField pathfield;
    @FXML TextField authorsfield;
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
        buffer.put("type", type);
        printToGUI("deu bom");
        // Send to backend logic here
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
