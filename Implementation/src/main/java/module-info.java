module Implementation
{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens Application.ClavarChatGUI to javafx.fxml;
    exports Application.ClavarChatGUI;
}