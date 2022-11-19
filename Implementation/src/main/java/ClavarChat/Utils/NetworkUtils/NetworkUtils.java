package ClavarChat.Utils.NetworkUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class NetworkUtils
{

    public static String inetAddressToString(InetAddress addr)
    {
        return addr.toString().split("/")[1];
    }

    public static String getNetworkMask(String ip)
    {
        String mask = "";

        try
        {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
            short prflen = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
            int shft = 0xffffffff<<(32-prflen);
            int oct1 = ((byte) ((shft&0xff000000)>>24)) & 0xff;
            int oct2 = ((byte) ((shft&0x00ff0000)>>16)) & 0xff;
            int oct3 = ((byte) ((shft&0x0000ff00)>>8)) & 0xff;
            int oct4 = ((byte) (shft&0x000000ff)) & 0xff;
            mask = oct1+"."+oct2+"."+oct3+"."+oct4;
        }
        catch (SocketException | UnknownHostException e) { e.printStackTrace(); }

        return mask;
    }

    public static ArrayList<String> getAllIp()
    {
        ArrayList<String> ip = new ArrayList<String>();

        try
        {
            for (NetworkInterface netint : Collections.list(NetworkInterface.getNetworkInterfaces()))
            {
                for (InetAddress inetAddress : Collections.list(netint.getInetAddresses()))
                {
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress())
                    {
                        ip.add(inetAddressToString(inetAddress));
                    }
                }
            }

        }
        catch (SocketException e) { e.printStackTrace(); }

        return ip;
    }

    public static String getNetwork(String ip, String mask)
    {
        return ipToString(stringAddressToInt(ip) & stringAddressToInt(mask));
    }

    public static String getBroadcastAddress(String ip)
    {
        String broadcast = "";

        try
        {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
            InetAddress broadcastAddr = networkInterface.getInterfaceAddresses().get(0).getBroadcast();
            broadcast = inetAddressToString(broadcastAddr);

        } catch (SocketException | UnknownHostException e) { e.printStackTrace(); }

        return broadcast;
    }

    private static int stringAddressToInt(String ip)
    {
        int ipInt = 0;

        try
        {
            InetAddress ipAddr = InetAddress.getByName(ip);
            byte[] adr = ipAddr.getAddress();
            int[] i = new int[4];
            for (int j = 0; j < 4; j++) i[j] = (int) ((adr[j] < 0) ? (256 + adr[j]) : adr[j]);
            ipInt =  i[3] + (i[2] << 8) + (i[1] << 16) + (i[0] << 24);
        } catch (UnknownHostException e) { e.printStackTrace(); }

        return ipInt;
    }

    private static String ipToString(int address)
    {
        StringBuffer sa = new StringBuffer();
        for (int i = 0; i < 4; i++)
        {
            sa.append(0xff & address >> 24);
            address <<= 8;
            if (i != 4 - 1) sa.append('.');
        }
        return sa.toString();
    }
}