package ClavarChat.Utils.Audio;

import ClavarChat.Utils.Audio.TinySound.src.kuusisto.tinysound.Sound;
import ClavarChat.Utils.Audio.TinySound.src.kuusisto.tinysound.TinySound;

import java.net.MalformedURLException;
import java.net.URL;

public class Audio
{
    Sound sound;
    public Audio(String path)
    {
        URL url = this.getURL(path);
        this.sound = TinySound.loadSound(url);
    }

    public void play()
    {
        this.sound.play();
    }

    private URL getURL(String path)
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

    public static void init()
    {
        TinySound.init();
    }

    public static void shutdown()
    {
        TinySound.shutdown();
    }
}
