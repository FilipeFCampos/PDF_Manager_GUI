package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import com.pdfmanager.pdf_manager_backend.files.Book;
import com.pdfmanager.pdf_manager_backend.files.ClassNote;
import com.pdfmanager.pdf_manager_backend.files.Slide;
import com.pdfmanager.pdf_manager_backend.utils.DataLoader;
import com.pdfmanager.pdf_manager_backend.utils.FileManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class RemoveFilesController {

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> bookTitleColumn;
    @FXML private TableColumn<Book, String> bookPublisherColumn;
    @FXML private TableColumn<Book, Integer> bookYearColumn;

    @FXML private TableView<ClassNote> notesTable;
    @FXML private TableColumn<ClassNote, String> noteTitleColumn;
    @FXML private TableColumn<ClassNote, String> noteLectureColumn;
    @FXML private TableColumn<ClassNote, String> noteInstitutionColumn;

    @FXML private TableView<Slide> slidesTable;
    @FXML private TableColumn<Slide, String> slideTitleColumn;
    @FXML private TableColumn<Slide, String> slideLectureColumn;
    @FXML private TableColumn<Slide, String> slideInstitutionColumn;

    private final DatabaseManager db = new DatabaseManager();
    private final FileManager fileManager = new FileManager();

    @FXML
    public void initialize() {
        // Books
        bookTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        bookPublisherColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPublisher()));
        bookYearColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPublishYear()).asObject());
        booksTable.getItems().setAll(DataLoader.loadBooks());

        // Notes
        noteTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        noteLectureColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLectureName()));
        noteInstitutionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInstitutionName()));
        notesTable.getItems().setAll(DataLoader.loadClassNotes());

        // Slides
        slideTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        slideLectureColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLectureName()));
        slideInstitutionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInstitutionName()));
        slidesTable.getItems().setAll(DataLoader.loadSlides());
    }

    @FXML
    private void handleRemoveBook() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Nenhum livro selecionado", "Por favor, selecione um livro na tabela para remover.");
            return;
        }

        try {
            // Etapa 1: Remover do JSON e obter o primeiro autor para encontrar o diretório do arquivo.
            String author = db.removeEntry(db.getBooksPath(), selected.getTitle(), "authors[0]");

            if (author != null) {
                // Etapa 2: Montar o caminho do arquivo físico e deletá-lo.
                String filePath = db.getLibraryPath() + File.separator + author + File.separator + selected.getTitle();
                fileManager.removeFile(new File(filePath));

                // Etapa 3: Remover o item da TableView (interface).
                booksTable.getItems().remove(selected);
            }
        } catch (IOException e) {
            showAlert("Erro de E/S", "Ocorreu um erro ao tentar remover o arquivo do banco de dados ou do sistema de arquivos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoveNote() {
        ClassNote selected = notesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Nenhuma nota de aula selecionada", "Por favor, selecione uma nota de aula na tabela para remover.");
            return;
        }

        try {
            String author = db.removeEntry(db.getClassNotesPath(), selected.getTitle(), "authors[0]");
            if (author != null) {
                String filePath = db.getLibraryPath() + File.separator + author + File.separator + selected.getTitle();
                fileManager.removeFile(new File(filePath));
                notesTable.getItems().remove(selected);
            }
        } catch (IOException e) {
            showAlert("Erro de E/S", "Ocorreu um erro ao tentar remover o arquivo do banco de dados ou do sistema de arquivos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoveSlide() {
        Slide selected = slidesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Nenhum slide selecionado", "Por favor, selecione um slide na tabela para remover.");
            return;
        }

        try {
            String author = db.removeEntry(db.getSlidesPath(), selected.getTitle(), "authors[0]");
            if (author != null) {
                String filePath = db.getLibraryPath() + File.separator + author + File.separator + selected.getTitle();
                fileManager.removeFile(new File(filePath));
                slidesTable.getItems().remove(selected);
            }
        } catch (IOException e) {
            showAlert("Erro de E/S", "Ocorreu um erro ao tentar remover o arquivo do banco de dados.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        Parent menuRoot = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(menuRoot, 480, 480));
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}