module Implementation
{
    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX;

    opens Application.ClavarChatGUI to javafx.fxml;
    exports Application.ClavarChatGUI;

    opens ClavarChat.Controllers.GUIControllers to javafx.fxml;
    exports ClavarChat.Controllers.GUIControllers;
}