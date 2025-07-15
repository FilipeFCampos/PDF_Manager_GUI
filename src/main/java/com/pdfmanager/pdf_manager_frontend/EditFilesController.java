package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.files.Book;
import com.pdfmanager.pdf_manager_backend.files.ClassNote;
import com.pdfmanager.pdf_manager_backend.files.Document;
import com.pdfmanager.pdf_manager_backend.files.Slide;
import com.pdfmanager.pdf_manager_backend.utils.DataLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class EditFilesController {

    @FXML private TabPane tabPane;
    @FXML private TableView<Book> booksTable;
    @FXML private TableView<ClassNote> notesTable;
    @FXML private TableView<Slide> slidesTable;

    @FXML
    public void initialize() {
        setupTables();
        loadData();
    }

    private void setupTables() {
        TableColumn<Book, String> bookTitleCol = (TableColumn<Book, String>) booksTable.getColumns().get(0);
        TableColumn<Book, String> bookPublisherCol = (TableColumn<Book, String>) booksTable.getColumns().get(1);
        TableColumn<Book, Integer> bookYearCol = (TableColumn<Book, Integer>) booksTable.getColumns().get(2);
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookPublisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        bookYearCol.setCellValueFactory(new PropertyValueFactory<>("publishYear"));

        TableColumn<ClassNote, String> noteTitleCol = (TableColumn<ClassNote, String>) notesTable.getColumns().get(0);
        TableColumn<ClassNote, String> noteLectureCol = (TableColumn<ClassNote, String>) notesTable.getColumns().get(1);
        TableColumn<ClassNote, String> noteInstCol = (TableColumn<ClassNote, String>) notesTable.getColumns().get(2);
        noteTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        noteLectureCol.setCellValueFactory(new PropertyValueFactory<>("lectureName"));
        noteInstCol.setCellValueFactory(new PropertyValueFactory<>("institutionName"));

        TableColumn<Slide, String> slideTitleCol = (TableColumn<Slide, String>) slidesTable.getColumns().get(0);
        TableColumn<Slide, String> slideLectureCol = (TableColumn<Slide, String>) slidesTable.getColumns().get(1);
        TableColumn<Slide, String> slideInstCol = (TableColumn<Slide, String>) slidesTable.getColumns().get(2);
        slideTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        slideLectureCol.setCellValueFactory(new PropertyValueFactory<>("lectureName"));
        slideInstCol.setCellValueFactory(new PropertyValueFactory<>("institutionName"));
    }

    private void loadData() {
        booksTable.getItems().setAll(DataLoader.loadBooks());
        notesTable.getItems().setAll(DataLoader.loadClassNotes());
        slidesTable.getItems().setAll(DataLoader.loadSlides());
    }

    @FXML
    private void handleEditButtonAction() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) return;

        TableView<?> selectedTableView = (TableView<?>) selectedTab.getContent().lookup("TableView");
        Document selectedDocument = (Document) selectedTableView.getSelectionModel().getSelectedItem();

        if (selectedDocument == null) {
            showAlert("Nenhum item selecionado", "Por favor, selecione um item na tabela para editar.");
            return;
        }

        try {
            openEditForm(selectedDocument);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro ao abrir formulário", "Não foi possível carregar a tela de edição.");
        }
    }

    private void openEditForm(Document document) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("editItemForm.fxml"));
        Parent root = loader.load();

        EditItemFormController controller = loader.getController();
        controller.setDocumentToEdit(document);

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Editar Item");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(((Node)tabPane).getScene().getWindow());
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        dialogStage.showAndWait();

        loadData();
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        Parent menuRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("menu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(new Scene(menuRoot, 480, 480));
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}