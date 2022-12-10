package ClavarChat.Models.ByteImage;

import ClavarChat.Utils.Path.Path;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ByteImage implements Serializable
{
    public int size;
    public byte[] buffer;

    private ByteImage(String path, String extension) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedImage image = ImageIO.read(new File(path));
        ImageIO.write(image, extension, byteArrayOutputStream);

        this.size = byteArrayOutputStream.size();
        this.buffer = byteArrayOutputStream.toByteArray();
    }

    public static ByteImage encode(String path)
    {
        String extension = FilenameUtils.getExtension(path);
        ByteImage img = null;

        try
        {
            img = new ByteImage(path, extension);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return img;
    }

    public static Image decode(ByteImage img)
    {
        byte[] buffer = new byte[img.size];

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer));
            ImageIO.write(image, "jpg", new File(Path.getWorkingPath() + "src\\main\\resources\\test.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Image(new ByteArrayInputStream(buffer));
    }
}
