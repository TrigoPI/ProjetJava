package ClavarChat.Utils.Audio;

import ClavarChat.Utils.Audio.TinySound.src.kuusisto.tinysound.Sound;
import ClavarChat.Utils.Audio.TinySound.src.kuusisto.tinysound.TinySound;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Audio
{
    Sound sound;
    public Audio(URL url)
    {
        this.sound = TinySound.loadSound(url);
    }

    public void play()
    {
        this.sound.play();
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
