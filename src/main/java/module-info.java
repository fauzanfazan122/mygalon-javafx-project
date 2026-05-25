module com.fauzan.mygalon {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.fauzan.mygalon to javafx.fxml;
    exports com.fauzan.mygalon;
    exports com.fauzan.mygalon.controller;
    opens com.fauzan.mygalon.controller to javafx.fxml;

    requires java.sql;
    requires java.sql.rowset;
    opens com.fauzan.mygalon.model to javafx.base;

    requires org.xerial.sqlitejdbc;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.desktop;
    requires java.compiler;
}