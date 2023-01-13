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

    opens Application.FXMLControllers to javafx.fxml;
    exports Application.FXMLControllers;

    opens Application.FXMLControllers.Controllers to javafx.fxml;
    exports Application.FXMLControllers.Controllers;
}