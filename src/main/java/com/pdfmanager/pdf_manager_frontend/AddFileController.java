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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class AddFileController {
    // Radio buttons for selecting the type of file
    @FXML RadioButton bookrb;
    @FXML RadioButton classnoterb;
    @FXML RadioButton sliderb;
    // Common fields for all types
    @FXML TextField titlefield;
    @FXML TextField pathfield;
    @FXML TextField authorsfield;
    // StackPane to manage different panes
    @FXML StackPane stackPane;
    // Fields specific to Book
    @FXML TextField fieldofknowledgefield;
    @FXML TextField subtitlefieldB;
    @FXML TextField publisherfield;
    @FXML TextField publishyearfield;
    // Fields specific to ClassNote
    @FXML TextField subtitlefieldCN;
    @FXML TextField lecturenamefieldCN;
    @FXML TextField institutionnamefieldCN;
    // Fields specific to Slide
    @FXML TextField lecturenamefieldS;
    @FXML TextField institutionnamefieldS;
    // Buffer to hold data before submission
    Map<String, Object> buffer = new HashMap<>();
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
    public void voltarGlobal(ActionEvent event) {
        this.buffer.clear();
        try {
            // Disables the specific pane and enables the global pane
            disablePane("#addBookPane");
            disablePane("#addClassNotePane");
            disablePane("#addSlidePane");
            enablePane("#addFileGlobalPane");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the submission of the form, collecting data about the file to be added.
     * @param event
     */
    @FXML
    private void submit(ActionEvent event) {
        // Cleans the buffer to ensure fresh data is collected
        this.buffer.clear();
        // Collects data from the common fields
        String title = titlefield.getText();
        String authors = authorsfield.getText();
        String path = pathfield.getText();
        String type;
        // Determines the type of file based on the selected radio button
        if (bookrb.isSelected()) {
            type = "Book";
        } else if (classnoterb.isSelected()) {
            type = "ClassNote";
        } else if (sliderb.isSelected()) {
            type = "Slide";
        } else {
            type = null;
        }

        // Validates that all required fields are filled
        if (title.isEmpty() || authors.isEmpty() || path.isEmpty() || type == null) {
            // Shows an alert and aborts execution if any field is empty
            showAlert("Please fill all fields.");
            return;
        }

        // Records the collected data into the buffer
        this.buffer.put("title", title);
        // Splits the authors string into a list, trimming commas and the whitespace around them
        List<String> authorsList = Arrays.asList(authors.split("\\s*,\\s*"));
        this.buffer.put("authors", authorsList);
        this.buffer.put("path", path);
        this.buffer.put("type", type);

        // Switches to the appropriate pane based on the selected type
        if (type.equals("Book")) {
            try {
                // Disables the global pane and enables the Book-specific pane
                disablePane("#addFileGlobalPane");
                enablePane("#addBookPane");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (type.equals("ClassNote")) {
            try {
                // Disables the global pane and enables the ClassNote-specific pane
                disablePane("#addFileGlobalPane");
                enablePane("#addClassNotePane");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                // Disables the global pane and enables the Slide-specific pane
                disablePane("#addFileGlobalPane");
                enablePane("#addSlidePane");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Switches to the main menu scene after adding a file.
     * @param event
     * @throws IOException
     */@FXML

    private void switchToMenuScene(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent root = loader.load();

        // If outputData is NOT null or empty, show an alert with the output data
        if (!(this.outputData == null) && !this.outputData.isEmpty()) {
            showAlert(this.outputData);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 480, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Disables a specific pane in the StackPane by its ID.
     * @param id
     * @throws IOException
     */
    private void disablePane(String id) throws IOException {
        Node node = stackPane.lookup(id);
        if (node != null) {
            node.setDisable(true);
            node.setVisible(false);
        }
    }

    /**
     * Gathers specific data for a Book and adds it to the buffer which is then submitted to the database.
     * @param event
     */
    @FXML
    private void addBook(ActionEvent event) {
        // Collects data from the Book-specific fields
        String subtitle = subtitlefieldB.getText();
        String fieldofknowledge = fieldofknowledgefield.getText();
        String publisher = publisherfield.getText();
        String publishyear = publishyearfield.getText();

        // Validates that all required fields are filled
        if (subtitle.isEmpty() || fieldofknowledge.isEmpty() || publishyear.isEmpty() || publisher.isEmpty()) {
            showAlert("Please fill all fields.");
            return;
        }

        // Records the collected data into the buffer
        this.buffer.put("subTitle", subtitle);
        this.buffer.put("fieldOfKnowledge", fieldofknowledge);
        this.buffer.put("publisher", publisher);
        this.buffer.put("publishYear", publishyear);
        // Tries to add the Book to the database
        try {
            this.outputData = ui.addToDbFromGUI(this.buffer);
        } catch (GUIException e) {
            showAlert(e.getMessage());
        }
        // Switches back to the main menu scene
        try {
            switchToMenuScene(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gathers specific data for a ClassNote and adds it to the buffer which is then submitted to the database.
     * @param event
     */
    @FXML
    private void addClassNote(ActionEvent event) {
        // Collects data from the ClassNote-specific fields
        String subtitle = subtitlefieldCN.getText();
        String lectureName = lecturenamefieldCN.getText();
        String institutionName = institutionnamefieldCN.getText();

        if (subtitle.isEmpty() || institutionName.isEmpty() || lectureName.isEmpty()) {
            showAlert("Please fill all fields.");
            return;
        }

        // Records the collected data into the buffer
        this.buffer.put("subTitle", subtitle);
        this.buffer.put("lectureName", lectureName);
        this.buffer.put("institutionName", institutionName);
        // Tries to add the ClassNote to the database
        try {
            this.outputData = ui.addToDbFromGUI(this.buffer);
        } catch (GUIException e) {
            showAlert(e.getMessage());
        }
        // Switches back to the main menu scene
        try {
            switchToMenuScene(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gathers specific data for a Slide and adds it to the buffer which is then submitted to the database.
     * @param event
     */
    @FXML
    private void addSlide(ActionEvent event) {
        // Collects data from the Slide-specific fields
        String lectureName = lecturenamefieldS.getText();
        String institutionName = institutionnamefieldS.getText();

        // Validates that all required fields are filled
        if (lectureName.isEmpty() || institutionName.isEmpty()) {
            showAlert("Please fill all fields.");
            return;
        }

        // Records the collected data into the buffer
        this.buffer.put("lectureName", lectureName);
        this.buffer.put("institutionName", institutionName);
        // Tries to add the Slide to the database
        try {
            this.outputData = ui.addToDbFromGUI(this.buffer);
        } catch (GUIException e) {
            showAlert(e.getMessage());
        }
        // Switches back to the main menu scene
        try {
            switchToMenuScene(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enables a specific pane in the StackPane by its ID.
     * @param id
     * @throws IOException
     */
    private void enablePane(String id) throws IOException {
        Node node = stackPane.lookup(id);
        if (node != null) {
            node.setDisable(false);
            node.setVisible(true);
        }
    }

    /**
     * Displays an alert dialog with a warning message.
     * @param message The message to be displayed in the alert dialog.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
