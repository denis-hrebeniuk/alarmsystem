module com.hrebeniuk.alarmsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
    requires org.json;
    requires java.prefs;


    opens com.hrebeniuk.alarmsystem to javafx.fxml;
    exports com.hrebeniuk.alarmsystem;
    exports com.hrebeniuk.alarmsystem.controllers;
    opens com.hrebeniuk.alarmsystem.controllers to javafx.fxml;
}