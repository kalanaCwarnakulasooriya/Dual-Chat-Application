module lk.ijse.dualchatapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.dualchatapplication to javafx.fxml;
    opens lk.ijse.dualchatapplication.controller to javafx.fxml;
    exports lk.ijse.dualchatapplication;
}