package chatServer;

import java.io.*;
import javax.net.ssl.*;

public class ChatThread extends Thread {
    private SSLSocket sslSocket;

    public ChatThread(SSLSocket sslSocket) {
        this.sslSocket = sslSocket;
    }

    public void run() {
        InputStream SSLSocketInputStream = null;
        BufferedReader inp = null;
        DataOutputStream out = null;

        try {
            SSLSocketInputStream = sslSocket.getInputStream();
            inp = new BufferedReader(new InputStreamReader(SSLSocketInputStream));
            out = new DataOutputStream(sslSocket.getOutputStream());
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

                // If command not exists
                // INVALID_COMMAND

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
