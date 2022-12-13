package chat.Main.chatServer;

import java.io.IOException;
import java.sql.Time;
import java.util.TimeZone;
import javax.net.ssl.*;

public class Server {
    public static void launchServer(int port, String timezone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));

        System.setProperty("javax.net.ssl.keyStore","encryption/KeyStore1.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");

        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            System.out.println("Server Started & Ready to accept Client Connection");

            while (true) {
                SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();
                System.out.println("New connection from: " + sslSocket.getRemoteSocketAddress());
                new ChatThread(sslSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
