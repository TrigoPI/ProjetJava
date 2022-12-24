package ClavarChat.Utils.Clock;

public class Clock
{
    private long start;

    public Clock()
    {
        this.start = System.currentTimeMillis();
    }

    public double timeSecond()
    {
        return (System.currentTimeMillis() - this.start) / 1000.0;
    }

    public double resetSecond()
    {
        double time = this.timeSecond();
        this.start = System.currentTimeMillis();
        return time;
    }
}
