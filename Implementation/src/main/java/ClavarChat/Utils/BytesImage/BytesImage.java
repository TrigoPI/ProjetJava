package ClavarChat.Utils.BytesImage;

import ClavarChat.Utils.Path.Path;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

public class BytesImage implements Serializable
{
    private final byte[] buffer;
    public BytesImage(String path)
    {
        this.buffer = Path.getBytes(path);
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
