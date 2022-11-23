package ClavarChat.Utils.Clock;

public class Clock
{
    private long start;

    public Clock()
    {
        this.start = System.currentTimeMillis();
    }

    public float timeSecond()
    {
        return (System.currentTimeMillis() - this.start) / 1000;
    }

    public float resetSecond()
    {
        float time = this.timeSecond();
        this.start = System.currentTimeMillis();
        return  time;
    }
}
