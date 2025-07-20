package com.pdfmanager.pdf_manager_frontend;

import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;
import com.pdfmanager.pdf_manager_backend.files.Collection;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackageCollectionController {

    @FXML private ComboBox<Collection> collectionComboBox;
    @FXML private Label selectedFileLabel;

    private DatabaseManager db;
    private File outputFile;

    @FXML
    public void initialize() {
        db = new DatabaseManager();
        loadCollections();

        // Faz o ComboBox mostrar o nome da coleção
        collectionComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Collection collection) {
                return collection == null ? "" : collection.getName();
            }

            @Override
            public Collection fromString(String string) {
                return null; // Não é necessário
            }
        });
    }

    private void loadCollections() {
        try {
            List<Collection> collections = db.getAllCollections();
            collectionComboBox.setItems(FXCollections.observableArrayList(collections));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar as coleções.");
        }
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Pacote da Coleção");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));

        Collection selectedCollection = collectionComboBox.getSelectionModel().getSelectedItem();
        if (selectedCollection != null) {
            fileChooser.setInitialFileName(selectedCollection.getName().replaceAll("\\s+", "_") + ".zip");
        }

        Stage stage = (Stage) collectionComboBox.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            this.outputFile = file;
            selectedFileLabel.setText(outputFile.getAbsolutePath());
        }
    }

    @FXML
    private void handlePackage(ActionEvent event) {
        Collection selectedCollection = collectionComboBox.getSelectionModel().getSelectedItem();

        if (selectedCollection == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Necessária", "Por favor, selecione uma coleção.");
            return;
        }
        if (outputFile == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Necessária", "Por favor, escolha um local para salvar o arquivo .zip.");
            return;
        }

        try {
            String libraryPath = db.getLibraryPath();
            String author = selectedCollection.getAuthor();
            List<String> titles = selectedCollection.getEntryTitles();

            // Constrói a lista de caminhos completos dos arquivos a serem compactados
            List<File> filesToZip = titles.stream()
                    .map(title -> new File(libraryPath + File.separator + author + File.separator + title))
                    .filter(File::exists) // Garante que o arquivo realmente existe na biblioteca
                    .collect(Collectors.toList());

            if (filesToZip.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Nenhum Arquivo", "Nenhum dos arquivos desta coleção foi encontrado na biblioteca.");
                return;
            }

            // Lógica de compactação
            zipFiles(filesToZip, outputFile);

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Coleção empacotada com sucesso em:\n" + outputFile.getAbsolutePath());
            handleBackButton(event);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Empacotar", "Ocorreu um erro: " + e.getMessage());
        }
    }

    private void zipFiles(List<File> filesToZip, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (File file : filesToZip) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }
                    zos.closeEntry();
                }
            }
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