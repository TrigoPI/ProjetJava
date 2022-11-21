module Implementation
{
    requires javafx.controls;
    requires javafx.fxml;

    opens Application.GUI to javafx.fxml;
    exports Application.GUI;
}