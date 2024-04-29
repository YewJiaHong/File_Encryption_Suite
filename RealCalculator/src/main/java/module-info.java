module com.example.realcalculator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.scripting;
    requires java.logging;

    opens com.example.realcalculator to javafx.fxml;
    exports com.example.realcalculator;
}