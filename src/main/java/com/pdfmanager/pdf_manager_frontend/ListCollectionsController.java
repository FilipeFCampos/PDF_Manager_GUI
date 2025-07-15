package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import com.pdfmanager.pdf_manager_backend.files.Collection;
import com.pdfmanager.pdf_manager_backend.files.DocumentType;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ListCollectionsController {

    @FXML private TableView<Collection> collectionsTable;
    @FXML private TableColumn<Collection, String> nameColumn;
    @FXML private TableColumn<Collection, String> authorColumn;
    @FXML private TableColumn<Collection, DocumentType> typeColumn;
    @FXML private TableColumn<Collection, String> sizeColumn;

    // --- ADICIONADO ---
    @FXML private ListView<String> filesListView;

    private DatabaseManager db;

    @FXML
    public void initialize() {
        db = new DatabaseManager();
        setupTableColumns();
        loadCollections();

        collectionsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        showCollectionFiles(newValue); // Mostra os arquivos da nova coleção selecionada
                    } else {
                        filesListView.getItems().clear(); // Limpa a lista se nada estiver selecionado
                    }
                }
        );
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

        sizeColumn.setCellValueFactory(cellData -> {
            Collection collection = cellData.getValue();
            String sizeInfo = collection.getEntryTitles().size() + " / " + collection.getMaxSize();
            return new SimpleStringProperty(sizeInfo);
        });
    }

    private void loadCollections() {
        try {
            List<Collection> collections = db.getAllCollections();
            collectionsTable.setItems(FXCollections.observableArrayList(collections));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Preenche a ListView com os títulos dos arquivos da coleção selecionada.
     * @param collection A coleção que foi selecionada na tabela.
     */
    private void showCollectionFiles(Collection collection) {
        if (collection == null || collection.getEntryTitles() == null) {
            filesListView.getItems().clear();
            return;
        }
        // Pega a lista de títulos da coleção e a exibe
        List<String> fileTitles = collection.getEntryTitles();
        filesListView.setItems(FXCollections.observableArrayList(fileTitles));
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("collectionsMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 480, 480));
        stage.show();
    }
}