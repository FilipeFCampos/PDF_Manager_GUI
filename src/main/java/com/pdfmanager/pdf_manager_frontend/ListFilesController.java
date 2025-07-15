package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.files.Book;
import com.pdfmanager.pdf_manager_backend.files.ClassNote;
import com.pdfmanager.pdf_manager_backend.files.Slide;
import com.pdfmanager.pdf_manager_backend.utils.DataLoader;
import javafx.event.ActionEvent; // Adicionado
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Adicionado
import javafx.scene.Node; // Adicionado
import javafx.scene.Parent; // Adicionado
import javafx.scene.Scene; // Adicionado
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage; // Adicionado

import java.io.IOException; // Adicionado
import java.util.List;
import java.util.Objects;

public class ListFilesController {

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> bookTitleColumn;
    @FXML private TableColumn<Book, String> bookAuthorColumn;
    @FXML private TableColumn<Book, Integer> bookYearColumn;

    @FXML private TableView<ClassNote> notesTable;
    @FXML private TableColumn<ClassNote, String> noteTitleColumn;
    @FXML private TableColumn<ClassNote, String> noteSubjectColumn;
    @FXML private TableColumn<ClassNote, String> noteDateColumn;

    @FXML private TableView<Slide> slidesTable;
    @FXML private TableColumn<Slide, String> slideTitleColumn;
    @FXML private TableColumn<Slide, String> slideTopicColumn;
    @FXML private TableColumn<Slide, String> slideDateColumn;

    @FXML
    public void initialize() {
        // As propriedades ("author", "year", "subject", etc.) devem corresponder
        // exatamente aos nomes dos métodos getter na sua classe de modelo.
        // Ex: para "author", a classe Book deve ter um método getAuthor().

        // Configurar colunas dos livros
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("authors")); // Corrigido para "authors" se for uma lista
        bookYearColumn.setCellValueFactory(new PropertyValueFactory<>("publishYear")); // Corrigido para "publishYear"

        // Configurar colunas das anotações
        noteTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        noteSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("lectureName")); // Corrigido para "lectureName"
        noteDateColumn.setCellValueFactory(new PropertyValueFactory<>("institutionName")); // Corrigido para "institutionName"

        // Configurar colunas dos slides
        slideTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        slideTopicColumn.setCellValueFactory(new PropertyValueFactory<>("lectureName")); // Corrigido para "lectureName"
        slideDateColumn.setCellValueFactory(new PropertyValueFactory<>("institutionName")); // Corrigido para "institutionName"

        // Carregar dados
        List<Book> books = DataLoader.loadBooks();
        List<ClassNote> notes = DataLoader.loadClassNotes();
        List<Slide> slides = DataLoader.loadSlides();

        booksTable.getItems().setAll(books);
        notesTable.getItems().setAll(notes);
        slidesTable.getItems().setAll(slides);
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        Parent menuRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("menu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(menuRoot, 480, 480));
        stage.show();
    }
}