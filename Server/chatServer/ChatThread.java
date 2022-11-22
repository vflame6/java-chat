package chatServer;

import java.io.*;
import javax.net.ssl.*;

import static chatServer.Cookies.getCookie;

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
                String[] command = line.split(" ");
                switch (command[0]) {
                    case ("LOGIN"):
                        String usernameLogin = command[1];
                        String passwordLogin = command[2];
                        if (usernameLogin.equals("admin")) {
                            out.write(("OK " + usernameLogin + " 9999999999" + " 1 "
                                    + getCookie() + '\n').getBytes());
                        } else {
                            out.write(("INVALID_CREDENTIALS" + '\n').getBytes());
                        }
                        break;
                    case ("REGISTER"):
                        String usernameRegistration = command[1];
                        String telephone = command[2];
                        String password = command[3];
                        if (usernameRegistration.equals("admin")) {
                            out.write(("OK" + '\n').getBytes());
                            // add to bd
                        } else {
                            out.write(("INVALID_USERNAME OR INCORRECT_TELEPHONE " +
                                    +'\n').getBytes());
                        }
                        break;
                }

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
