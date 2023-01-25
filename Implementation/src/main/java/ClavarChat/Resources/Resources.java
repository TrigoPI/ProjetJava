package ClavarChat.Resources;

import javafx.fxml.FXMLLoader;

import java.io.InputStream;
import java.net.URL;

public class Resources
{
    public static class BDD
    {
        public static final String BDD_FILE = getJarPath("ClavarChat/Resources/BDD/ClavarDataBase.db");
    }

    public static class IMG
    {
        public static final InputStream LOGO   = getFileAsStream("IMG/Logo.png");
        public static final InputStream USER_1 = getFileAsStream("IMG/DefaultUserAvatar.jpg");

    }
    public static class CONFIG
    {
        public static final String CONF_FILE = getJarPath("ClavarChat/Resources/CONFIG/Conf.json");
    }

    public static class SOUND
    {
        public static final URL NOTIFICATION = getUrl("SOUND/notification1.wav");
    }

    public static class FXML
    {
        private static final URL LOGIN_FXML      = getUrl("FXML/LoginGUI.fxml");
        private static final URL CLAVARCHAT_FXML = getUrl("FXML/ClavarChatGUI.fxml");
        private static final URL SETTINGS_FXML   = getUrl("FXML/SettingsGUI.fxml");
        private static final URL PASSWORD_FXML = getUrl("FXML/PasswordGUI.fxml");

        public static class LOADER
        {
            public static FXMLLoader LOGIN_LOADER      = new FXMLLoader(LOGIN_FXML);
            public static FXMLLoader CLAVARCHAT_LOADER = new FXMLLoader(CLAVARCHAT_FXML);
            public static FXMLLoader SETTINGS_LOADER   = new FXMLLoader(SETTINGS_FXML);
            public static FXMLLoader PASSWORD_LOADER   = new FXMLLoader(PASSWORD_FXML);
        }
    }

    private static URL getUrl(String path)
    {
        return Resources.class.getResource(path);
    }

    private static InputStream getFileAsStream(String path)
    {
        return Resources.class.getResourceAsStream(path);
    }

    private static String getJarPath(String filePath)
    {
        String path = Resources.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String[] filesName = path.split("/");
        String lastElement = filesName[filesName.length - 1];
        StringBuilder finalPath = new StringBuilder();

        if (lastElement.contains(".jar"))
        {
            for (int i = 0; i < filesName.length - 1; i++) finalPath.append(filesName[i]).append("/");
        }
        else
        {
            for (int i = 0; i < filesName.length; i++) finalPath.append(filesName[i]).append("/");
        }

        return finalPath + filePath;
    }
}
