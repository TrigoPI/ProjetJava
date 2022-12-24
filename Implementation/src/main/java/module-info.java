module Implementation
{
    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX;
    requires java.sql;
    requires org.apache.commons.io;

    opens Application.ClavarChatGUI to javafx.fxml;
    exports Application.ClavarChatGUI;

    opens GUI.GUIControllers to javafx.fxml;
    exports GUI.GUIControllers;
    exports GUI.GUIControllers.Controllers;
    opens GUI.GUIControllers.Controllers to javafx.fxml;
}