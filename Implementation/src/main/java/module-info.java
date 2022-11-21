module Implementation
{
    requires javafx.controls;
    requires javafx.fxml;

    opens Application.ClavarChatGUI to javafx.fxml;
    exports Application.ClavarChatGUI;
}