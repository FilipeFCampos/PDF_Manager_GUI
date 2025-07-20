package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import com.pdfmanager.pdf_manager_backend.files.Book;
import com.pdfmanager.pdf_manager_backend.files.Collection;
import com.pdfmanager.pdf_manager_backend.files.DocumentType;
import com.pdfmanager.pdf_manager_backend.utils.BibTexGenerator;
import com.pdfmanager.pdf_manager_backend.utils.DataLoader;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GenerateBibtexController {

    @FXML private ComboBox<Collection> collectionComboBox;
    @FXML private Label selectedFileLabel;

    private DatabaseManager db;
    private File outputFile;

    @FXML
    public void initialize() {
        db = new DatabaseManager();
        loadBookCollections();

        // Faz o ComboBox mostrar o nome da coleção
        collectionComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Collection collection) {
                return collection == null ? "" : collection.getName();
            }

            @Override
            public Collection fromString(String string) {
                return null; // Não é necessário para um ComboBox não editável
            }
        });
    }

    private void loadBookCollections() {
        try {
            List<Collection> bookCollections = db.getAllCollections().stream()
                    .filter(c -> c.getType() == DocumentType.BOOK)
                    .collect(Collectors.toList());
            collectionComboBox.setItems(FXCollections.observableArrayList(bookCollections));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar as coleções de livros.");
        }
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Arquivo BibTeX");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BibTeX Files", "*.bib"));

        Collection selectedCollection = collectionComboBox.getSelectionModel().getSelectedItem();
        if (selectedCollection != null) {
            fileChooser.setInitialFileName(selectedCollection.getName().replaceAll("\\s+", "_") + ".bib");
        }

        Stage stage = (Stage) collectionComboBox.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            this.outputFile = file;
            selectedFileLabel.setText(outputFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        Collection selectedCollection = collectionComboBox.getSelectionModel().getSelectedItem();

        if (selectedCollection == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Necessária", "Por favor, selecione uma coleção.");
            return;
        }
        if (outputFile == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Necessária", "Por favor, escolha um local para salvar o arquivo.");
            return;
        }

        try {
            // Carrega todos os livros do DB
            List<Book> allBooks = DataLoader.loadBooks();

            // Filtra para obter apenas os livros que estão na coleção selecionada
            List<String> titlesInCollection = selectedCollection.getEntryTitles();
            List<Book> collectionBooks = allBooks.stream()
                    .filter(book -> titlesInCollection.contains(book.getTitle()))
                    .collect(Collectors.toList());

            if (collectionBooks.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Nenhum Livro", "Nenhum dos livros desta coleção foi encontrado no banco de dados.");
                return;
            }

            // Gera o arquivo BibTeX
            BibTexGenerator.generate(selectedCollection, collectionBooks, outputFile.toPath());
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Arquivo BibTeX gerado com sucesso em:\n" + outputFile.getAbsolutePath());
            handleBackButton(event);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Gerar", "Ocorreu um erro ao gerar o arquivo: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("collectionsMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 480, 480));
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}