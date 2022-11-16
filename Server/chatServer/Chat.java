package chatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Chat {
    public static void main(String[] args) {
        int port = 9000;
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new ChatThread(socket).start();
        }
    }
}
