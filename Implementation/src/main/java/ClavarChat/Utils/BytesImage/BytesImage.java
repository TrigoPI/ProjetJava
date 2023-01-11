package ClavarChat.Utils.BytesImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class BytesImage implements Serializable
{
    private final byte[] buffer;

    public BytesImage(InputStream in)
    {
        try
        {
            this.buffer = in.readAllBytes();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public BytesImage(byte[] buffer)
    {
        this.buffer = buffer;
    }

    public InputStream toInputStream()
    {
        return new ByteArrayInputStream(this.buffer);
    }

    public byte[] getBytes()
    {
        return this.buffer;
    }
}
