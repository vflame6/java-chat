package chat.Main.chatServer;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.TimeZone;

public class Server {
    public static void launchServer(int port, String timezone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));

        System.setProperty("javax.net.ssl.keyStore", "encryption/KeyStore1.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            System.out.println("Server Started & Ready to accept Client Connection");
            printServerInet4Addresses();

            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("New connection from: " + sslSocket.getRemoteSocketAddress());
                new ChatThread(sslSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printServerInet4Addresses() throws SocketException {
        System.out.println("Listing server IPv4 addresses:");
        System.out.println("-".repeat(33));
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface networkInterface : Collections.list(networkInterfaceEnumeration)) {
            Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddressEnumeration)) {
                if (inetAddress instanceof Inet4Address) {
                    System.out.printf("| %-6s | %-20s |\n", networkInterface.getName(), inetAddress.getHostAddress() + ":" + 9000);
                }
            }
        }
        System.out.println("-".repeat(33));
    }
}
