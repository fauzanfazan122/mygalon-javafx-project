module com.fauzan.mygalon {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.sql.rowset;
    requires com.github.librepdf.openpdf;
    requires org.xerial.sqlitejdbc;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.desktop;
    requires java.compiler;

    opens com.fauzan.mygalon to javafx.fxml;
    opens com.fauzan.mygalon.controller to javafx.fxml;
    opens com.fauzan.mygalon.model to javafx.base;

    exports com.fauzan.mygalon;
    exports com.fauzan.mygalon.controller;



}