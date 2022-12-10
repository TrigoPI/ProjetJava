package ClavarChat.Models.ByteImage;

import org.apache.commons.io.FilenameUtils;
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

    private ByteImage(String path, String extension) throws IOException
    {
        this.byteArrayOutputStream = new ByteArrayOutputStream();

        BufferedImage image = ImageIO.read(new File(path));
        ImageIO.write(image, extension, byteArrayOutputStream);

        this.size = this.byteArrayOutputStream.size();
    }

    public static ByteImage createByteImage(String path)
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
}
