module Implementation
{
    requires javafx.controls;
    requires javafx.fxml;

    opens Application to javafx.fxml;
    exports Application;
    exports Application.GUI;
    opens Application.GUI to javafx.fxml;
}