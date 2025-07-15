package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import com.pdfmanager.pdf_manager_backend.files.Book;
import com.pdfmanager.pdf_manager_backend.files.ClassNote;
import com.pdfmanager.pdf_manager_backend.files.Document;
import com.pdfmanager.pdf_manager_backend.files.Slide;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class EditItemFormController {

    @FXML private Label titleLabel;
    @FXML private VBox bookFields;
    @FXML private VBox noteFields;
    @FXML private VBox slideFields;

    // Campos do Livro
    @FXML private TextField publisherField;
    @FXML private TextField bookSubtitleField;
    @FXML private TextField publishYearField;

    // Campos da Anotação
    @FXML private TextField noteSubtitleField;
    @FXML private TextField noteLectureField;
    @FXML private TextField noteInstitutionField;

    // Campos do Slide
    @FXML private TextField slideLectureField;
    @FXML private TextField slideInstitutionField;

    private Document document;
    private final DatabaseManager db = new DatabaseManager();

    public void setDocumentToEdit(Document document) {
        this.document = document;
        titleLabel.setText("Editando: " + document.getTitle());

        bookFields.setVisible(false);
        noteFields.setVisible(false);
        slideFields.setVisible(false);

        if (document instanceof Book book) {
            bookFields.setVisible(true);
            publisherField.setText(book.getPublisher());
            bookSubtitleField.setText(book.getSubTitle());
            publishYearField.setText(String.valueOf(book.getPublishYear()));
        } else if (document instanceof ClassNote note) {
            noteFields.setVisible(true);
            noteSubtitleField.setText(note.getSubTitle());
            noteLectureField.setText(note.getLectureName());
            noteInstitutionField.setText(note.getInstitutionName());
        } else if (document instanceof Slide slide) {
            slideFields.setVisible(true);
            slideLectureField.setText(slide.getLectureName());
            slideInstitutionField.setText(slide.getInstitutionName());
        }
    }

    @FXML
    private void handleSaveChanges(ActionEvent event) {
        try {
            if (document instanceof Book) {
                updateField(db.getBooksPath(), "publisher", publisherField.getText());
                updateField(db.getBooksPath(), "subTitle", bookSubtitleField.getText());
                updateField(db.getBooksPath(), "publishYear", publishYearField.getText());
            } else if (document instanceof ClassNote) {
                updateField(db.getClassNotesPath(), "subTitle", noteSubtitleField.getText());
                updateField(db.getClassNotesPath(), "lectureName", noteLectureField.getText());
                updateField(db.getClassNotesPath(), "institutionName", noteInstitutionField.getText());
            } else if (document instanceof Slide) {
                updateField(db.getSlidesPath(), "lectureName", slideLectureField.getText());
                updateField(db.getSlidesPath(), "institutionName", slideInstitutionField.getText());
            }
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Arquivo atualizado com sucesso.");
            closeWindow(event);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", e.getMessage());
        }
    }

    private void updateField(File dbPath, String field, String value) throws IOException {
        db.updateEntryField(dbPath, document.getTitle(), field, value);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}