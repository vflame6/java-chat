package chatServer;

import java.io.*;
import java.net.Socket;

public class ChatThread extends Thread {
    private Socket socket;

    public ChatThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        InputStream socketInputStream = null;
        BufferedReader inp = null;
        DataOutputStream out = null;

        try {
            socketInputStream = socket.getInputStream();
            inp = new BufferedReader(new InputStreamReader(socketInputStream));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String line;
        while (true) {
            try {
                line = inp.readLine();
                System.out.println(line);

                // LOGIN;USERNAME PASSWORD
                // OK;USERNAME TELEPHONE IS_ADMIN SESSION_COOKIE
                // INVALID_CREDENTIALS;

                // REGISTER;USERNAME TELEPHONE PASSWORD
                // OK;USERNAME TELEPHONE IS_ADMIN
                // INVALID_USERNAME;
                // INCORRECT_TELEPHONE;

                // LOGIN_COOKIE;VALUE
                // OK; USERNAME TELEPHONE IS_ADMIN SESSION_COOKIE
                // INCORRECT_COOKIE;

                // AUTHENTICATED ONLY FUNCTIONS:
                // without auth returns AUTHENTICATION_REQUIRED;

                // GET_MESSAGES;
                // OK;[MESSAGE1, MESSAGE2, ...]

                // SEND_MESSAGE;MESSAGE
                // OK;MESSAGE_ID

                // DELETE_MESSAGE;ID
                // OK;
                // NO_ADMIN_RIGHTS;
                // NO_SUCH_MESSAGE;

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
