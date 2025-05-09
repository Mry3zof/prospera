module fcai.prospera {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens fcai.prospera.controller to javafx.fxml;
    opens fcai.prospera to javafx.fxml;
    exports fcai.prospera;
    exports fcai.prospera.controller;
    exports fcai.prospera.model;
    exports fcai.prospera.service;
    exports fcai.prospera.view;
}