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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ChangeLibraryController {

    @FXML private TextField librarypathfield;
    @FXML private TextField librarynamefield;
    @FXML private CheckBox existinglibrarycb;
    @FXML private Text warningfield;

    private final UserInterface ui;

    public ChangeLibraryController() {
        DatabaseManager db;
        try {
            db = new DatabaseManager();
        } catch (Exception e) {
            System.err.println("ERROR: Invalid database path");
            throw new RuntimeException("Failed to initialize DatabaseManager", e);
        }
        this.ui = new UserInterface(db);
    }

    @FXML
    private void saveButtonAction(ActionEvent event) {
        String libraryPath = librarypathfield.getText();
        String libraryName = librarynamefield.getText();

        if (libraryPath.isEmpty() || libraryName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            // Reutiliza a mesma lógica do backend que o FirstAccessController usa
            ui.editLibraryPathFromGUI(libraryPath, libraryName, existinglibrarycb.isSelected());
        } catch (GUIException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Biblioteca alterada com sucesso!\nNovo caminho: " + libraryPath + File.separator + libraryName);
        switchToMenuScene(event);
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        switchToMenuScene(event);
    }

    @FXML
    private void existingPathWarning() {
        if (existinglibrarycb.isSelected() && !librarynamefield.getText().isEmpty()) {
            warningfield.setText("AVISO: Lembre-se que qualquer dado presente em '"
                    + librarynamefield.getText() + "' pode ser alterado.");
        } else {
            warningfield.setText("");
        }
    }

    private void switchToMenuScene(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("menu.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 480, 480));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro Crítico", "Não foi possível voltar ao menu principal.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}