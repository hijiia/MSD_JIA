module com.example.synthesizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    exports com.example.synthesizer; // Export your main package to allow access from JavaFX
    opens com.example.synthesizer to javafx.fxml; // Allow reflection from javafx.fxml
}