package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import com.pdfmanager.pdf_manager_backend.files.Collection;
import com.pdfmanager.pdf_manager_backend.files.Document;
import com.pdfmanager.pdf_manager_backend.files.DocumentType;
import com.pdfmanager.pdf_manager_backend.utils.DataLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ManageCollectionController {

    @FXML private ComboBox<String> collectionComboBox;
    @FXML private ListView<String> filesInCollectionListView;
    @FXML private ListView<String> availableFilesListView;

    private DatabaseManager db;
    private Collection currentCollection;
    private ObservableList<String> filesInCollection = FXCollections.observableArrayList();
    private ObservableList<String> availableFiles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        db = new DatabaseManager();
        filesInCollectionListView.setItems(filesInCollection);
        availableFilesListView.setItems(availableFiles);
        loadCollectionNames();

        // Adiciona um "ouvinte" para quando uma coleção for selecionada no ComboBox
        collectionComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadCollectionDetails(newValue);
                    }
                }
        );
    }

    private void loadCollectionNames() {
        try {
            List<String> collectionNames = db.getAllCollections().stream()
                    .map(Collection::getName)
                    .collect(Collectors.toList());
            collectionComboBox.setItems(FXCollections.observableArrayList(collectionNames));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar as coleções.");
        }
    }

    private void loadCollectionDetails(String collectionName) {
        try {
            currentCollection = db.getCollectionByName(collectionName);
            if (currentCollection == null) return;

            // Carrega os arquivos que já estão na coleção
            filesInCollection.setAll(currentCollection.getEntryTitles());

            // Carrega os arquivos disponíveis para adicionar
            List<Document> allEligibleDocs = findEligibleDocuments(currentCollection.getAuthor(), currentCollection.getType());
            List<String> availableTitles = allEligibleDocs.stream()
                    .map(Document::getTitle)
                    .filter(title -> !filesInCollection.contains(title)) // Exclui os que já estão na coleção
                    .collect(Collectors.toList());
            availableFiles.setAll(availableTitles);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os detalhes da coleção.");
        }
    }

    @FXML
    private void addFileToCollection() {
        String selected = availableFilesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (filesInCollection.size() < currentCollection.getMaxSize()) {
                availableFiles.remove(selected);
                filesInCollection.add(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Tamanho Máximo Atingido", "A coleção já atingiu seu tamanho máximo.");
            }
        }
    }

    @FXML
    private void removeFileFromCollection() {
        String selected = filesInCollectionListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            filesInCollection.remove(selected);
            availableFiles.add(selected);
        }
    }

    @FXML
    private void handleSaveChanges(ActionEvent event) {
        if (currentCollection == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Coleção", "Nenhuma coleção selecionada para salvar.");
            return;
        }

        currentCollection.setEntryTitles(new ArrayList<>(filesInCollection));

        try {
            // Se a coleção ficar vazia, ela é deletada
            if (currentCollection.getEntryTitles().isEmpty()) {
                db.deleteCollection(currentCollection.getName());
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Coleção esvaziada e removida com sucesso.");
            } else {
                db.saveCollection(currentCollection);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Alterações na coleção salvas com sucesso.");
            }
            handleBackButton(event); // Volta ao menu de coleções
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Não foi possível salvar as alterações.");
        }
    }

    private List<Document> findEligibleDocuments(String author, DocumentType type) {
        List<Document> documents = new ArrayList<>();
        switch (type) {
            case BOOK:
                DataLoader.loadBooks().stream().filter(doc -> doc.getAuthors().contains(author)).forEach(documents::add);
                break;
            case SLIDE:
                DataLoader.loadSlides().stream().filter(doc -> doc.getAuthors().contains(author)).forEach(documents::add);
                break;
            case CLASS_NOTE:
                DataLoader.loadClassNotes().stream().filter(doc -> doc.getAuthors().contains(author)).forEach(documents::add);
                break;
        }
        return documents;
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