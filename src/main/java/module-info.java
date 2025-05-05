module fcai.prospera {
    requires javafx.controls;
    requires javafx.fxml;


    opens fcai.prospera to javafx.fxml;
    exports fcai.prospera;
}