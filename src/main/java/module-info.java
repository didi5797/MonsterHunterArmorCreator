module com.example.monsterhunter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.jsoup;
    requires com.fasterxml.jackson.databind;
    requires org.json;
    requires com.google.gson;
    requires okhttp3;
    requires org.apache.commons.lang3;

    opens com.example.monsterhunter.armorpieces to com.fasterxml.jackson.databind; // Export für Jackson
    opens com.example.monsterhunter to javafx.fxml;
    exports com.example.monsterhunter;
}