module com.pdfmanager.pdf_manager_frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.path;
    requires com.fasterxml.jackson.databind;


    opens com.pdfmanager.pdf_manager_frontend to javafx.fxml;
    exports com.pdfmanager.pdf_manager_frontend;
}