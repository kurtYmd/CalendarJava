module com.calendar.calendar {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires ormlite.jdbc;
    requires java.sql;
    requires java.desktop;

    exports com.calendar.app to javafx.graphics;
    exports com.calendar.controllers to javafx.fxml;
    opens com.calendar.app to javafx.fxml, ormlite.jdbc;
    opens com.calendar.controllers to javafx.fxml;
    opens com.calendar.models to javafx.base;
}