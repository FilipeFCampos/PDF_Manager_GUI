package com.pdfmanager.pdf_manager_backend;

import com.pdfmanager.pdf_manager_backend.cli.UserInterface;
import com.pdfmanager.pdf_manager_backend.db.DatabaseManager;

public class App {

    public static void main(String[] args) {
        DatabaseManager db;
        try {
            db = new DatabaseManager();
        } catch (Exception e) {
            System.err.println("ERROR: Invalid database path");
            return;
        }
        UserInterface CLI = new UserInterface(db);

        CLI.printWelcomeMessage();
        CLI.printOptions();
    }
}