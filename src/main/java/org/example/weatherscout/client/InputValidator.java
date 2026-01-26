package org.example.weatherscout.client;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class InputValidator {
    private static final String ERROR_MSG = "Fehler: Nur Buchstaben, Leerzeichen und Bindestriche erlaubt.";

    public static void attachTo(TextField field, Label errorLabel) {
        field.setTextFormatter(new TextFormatter<String>(change -> {
            if (!change.getText().matches("[a-zA-ZäöüßÄÖÜ\\s-]*")) {
                showError(field, errorLabel);
                return null;
            }
            clearError(field, errorLabel);
            return change;
        }));
    }

    public static boolean isValid(String text) {
        return text != null && text.matches("[a-zA-ZäöüßÄÖÜ\\s-]*") && text.matches(".*[a-zA-ZäöüßÄÖÜ].*");
    }

    public static void showError(TextField field, Label label) {
        label.setText(ERROR_MSG);
        field.setStyle("-fx-border-color: #b83535; -fx-border-width: 1;");
    }

    public static void clearError(TextField field, Label label) {
        if (ERROR_MSG.equals(label.getText())) label.setText("");
        field.setStyle("");
    }
}