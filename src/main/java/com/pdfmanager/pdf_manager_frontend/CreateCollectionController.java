package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import com.pdfmanager.pdf_manager_backend.files.Collection;
import com.pdfmanager.pdf_manager_backend.files.Document;
import com.pdfmanager.pdf_manager_backend.files.DocumentType;
import com.pdfmanager.pdf_manager_backend.utils.DataLoader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateCollectionController {

    // --- Parte 1: Formulário de Dados ---
    @FXML private VBox step1Pane;
    @FXML private TextField nameField;
    @FXML private TextField authorField;
    @FXML private TextField maxSizeField;
    @FXML private ToggleGroup typeToggleGroup;

    // --- Parte 2: Seleção de Documentos ---
    @FXML private VBox step2Pane;
    @FXML private Label selectionLabel;
    @FXML private ListView<SelectableItem> documentsListView; // Alterado para usar o Wrapper

    private DatabaseManager db;

    /**
     * Classe interna (wrapper) para adicionar a funcionalidade de seleção
     * aos objetos Document sem modificar a classe original.
     */
    public static class SelectableItem {
        private final Document document;
        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        public SelectableItem(Document document) {
            this.document = document;
        }

        public Document getDocument() {
            return document;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }
    }


    public void initialize() {
        db = new DatabaseManager();

        // Configura a ListView para usar CheckBoxes e mostrar o título do documento
        documentsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(SelectableItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox(item.getDocument().getTitle());
                    checkBox.selectedProperty().bindBidirectional(item.selectedProperty());
                    setGraphic(checkBox);
                }
            }
        });

        step2Pane.setVisible(false);
        step2Pane.setManaged(false);
    }

    @FXML
    private void handleFindDocuments() {
        String name = nameField.getText();
        String author = authorField.getText();
        String maxSizeStr = maxSizeField.getText();
        RadioButton selectedRadio = (RadioButton) typeToggleGroup.getSelectedToggle();

        if (name.isEmpty() || author.isEmpty() || maxSizeStr.isEmpty() || selectedRadio == null) {
            showAlert(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            DocumentType type = DocumentType.valueOf(selectedRadio.getText().toUpperCase());
            List<SelectableItem> eligibleItems = findEligibleDocuments(author, type);

            if (eligibleItems.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Nenhum Documento", "Nenhum documento encontrado para este autor e tipo.");
                return;
            }

            selectionLabel.setText("Selecione os documentos para a coleção '" + name + "':");
            documentsListView.setItems(FXCollections.observableArrayList(eligibleItems));

            step1Pane.setVisible(false);
            step1Pane.setManaged(false);
            step2Pane.setVisible(true);
            step2Pane.setManaged(true);

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Tipo Inválido", "O tipo de documento selecionado é inválido.");
        }
    }

    @FXML
    private void handleCreateCollection(ActionEvent event) {
        List<String> selectedTitles = documentsListView.getItems().stream()
                .filter(SelectableItem::isSelected)
                .map(item -> item.getDocument().getTitle())
                .collect(Collectors.toList());

        int maxSize = Integer.parseInt(maxSizeField.getText());

        if (selectedTitles.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Nenhum Item Selecionado", "Você deve selecionar pelo menos um documento para criar a coleção.");
            return;
        }

        if (selectedTitles.size() > maxSize) {
            showAlert(Alert.AlertType.ERROR, "Tamanho Excedido", "O número de itens selecionados (" + selectedTitles.size() + ") excede o tamanho máximo da coleção (" + maxSize + ").");
            return;
        }

        String name = nameField.getText();
        String author = authorField.getText();
        RadioButton selectedRadio = (RadioButton) typeToggleGroup.getSelectedToggle();
        DocumentType type = DocumentType.valueOf(selectedRadio.getText().toUpperCase());

        Collection newCollection = new Collection(name, author, type, maxSize, selectedTitles);

        try {
            db.saveCollection(newCollection);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Coleção '" + name + "' criada com sucesso!");
            handleBackButton(event);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Não foi possível salvar a coleção no banco de dados.");
        }
    }

    private List<SelectableItem> findEligibleDocuments(String author, DocumentType type) {
        List<Document> documents = new ArrayList<>();
        switch (type) {
            case BOOK:
                DataLoader.loadBooks().stream()
                        .filter(doc -> doc.getAuthors().contains(author))
                        .forEach(documents::add);
                break;
            case SLIDE:
                DataLoader.loadSlides().stream()
                        .filter(doc -> doc.getAuthors().contains(author))
                        .forEach(documents::add);
                break;
            case CLASS_NOTE:
                DataLoader.loadClassNotes().stream()
                        .filter(doc -> doc.getAuthors().contains(author))
                        .forEach(documents::add);
                break;
        }
        // Converte a lista de Document para uma lista de SelectableItem
        return documents.stream()
                .map(SelectableItem::new)
                .collect(Collectors.toList());
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