package ClavarChat.Models.ByteImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class ByteImage implements Serializable
{
    public int size;
    public ByteArrayOutputStream byteArrayOutputStream;

    public ByteImage(String path, String extension) throws IOException
    {
        this.byteArrayOutputStream = new ByteArrayOutputStream();

        BufferedImage image = ImageIO.read(new File("path"));
        ImageIO.write(image, "jpg", byteArrayOutputStream);

        this.size = this.byteArrayOutputStream.size();
    }
}
