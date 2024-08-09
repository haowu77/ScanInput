package cool.wuhao.scaninputlib;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Util {

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces == null) {
                System.out.println("No network interfaces found");
                return "Cannot get IP address!";
            }

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                if (inetAddresses == null) {
                    System.out.println("No IP addresses found for network interface: " + networkInterface.getName());
                    continue;
                }

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        String ipAddress = inetAddress.getHostAddress();
                        System.out.println("Local IP Address: " + ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("SocketException occurred while getting IP address");
        }

        System.out.println("Cannot get IP address!");
        return "Cannot get IP address!";
    }
}
