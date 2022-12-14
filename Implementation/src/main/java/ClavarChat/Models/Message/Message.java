package ClavarChat.Models.Message;

public class Message
{
    public final String srcPseudo;
    public final String dstPseudo;
    public final String text;

    public Message(String srcPseudo, String dstPseudo, String text)
    {
        this.srcPseudo = srcPseudo;
        this.dstPseudo = dstPseudo;
        this.text = text;
    }
}
