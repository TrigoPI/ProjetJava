package Resources;

import javafx.fxml.FXMLLoader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Resources
{
    public static class IMG
    {
        public static final String LOGO   = getUri("IMG/Logo.png").getPath();
        public static final String USER_1 = getUri("IMG/user1.jpg").getPath();
    }

    public static class CONFIG
    {
        public static final String CONF_FILE = getUri("CONFIG/Conf.json").getPath();
    }

    public static class BDD
    {
        public static final String BDD_FILE = getUri("BDD/ClavarDataBase.db").getPath();
    }

    public static class SOUND
    {
        public static final String NOTIFICATION = getUri("SOUND/notification.wav").getPath();
        public static final String ARGH         = getUri("SOUND/notification2.wav").getPath();
    }

    public static class FXML
    {
        public static final String LOGIN_FXML      = getUri("FXML/LoginGUI.fxml").getPath();
        public static final String SETTINGS_FXML   = getUri("FXML/SettingsGUI.fxml").getPath();
        public static final String CLAVARCHAT_FXML = getUri("FXML/ClavarChatGUI.fxml").getPath();

        public static class LOADER
        {
            private static final URL LOGIN_URL        = getUrl("file://" + LOGIN_FXML);
            private static final URL CLAVARCHAT_URL   = getUrl("file://" + CLAVARCHAT_FXML);
            private static final URL SETTINGS_URL     = getUrl("file://" + SETTINGS_FXML);

            public static FXMLLoader LOGIN_LOADER      = new FXMLLoader(LOGIN_URL);
            public static FXMLLoader CLAVARCHAT_LOADER = new FXMLLoader(CLAVARCHAT_URL);
            public static FXMLLoader SETTINGS_LOADER   = new FXMLLoader(SETTINGS_URL);
        }
    }

    private static URI getUri(String path)
    {
        URI uri;
        try
        {
            uri = Objects.requireNonNull(Resources.class.getResource(path)).toURI();
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }

        return uri;
    }

    private static URL getUrl(String path)
    {
        URL url;

        try
        {
            url = new URL(path);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }

        return url;
    }
}
