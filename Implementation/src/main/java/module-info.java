module Implementation
{
    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX;
    requires java.sql;
    requires org.apache.commons.io;
    requires json.simple;

    opens Application.ClavarChatGUI to javafx.fxml;
    exports Application.ClavarChatGUI;

    opens GUI.GUIControllers to javafx.fxml;
    exports GUI.GUIControllers;

    opens GUI.GUIControllers.Controllers to javafx.fxml;
    exports GUI.GUIControllers.Controllers;
}