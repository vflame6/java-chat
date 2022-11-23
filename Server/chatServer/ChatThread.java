package chatServer;

import java.io.*;
import java.util.Objects;
import javax.net.ssl.*;

public class ChatThread extends Thread {
    private SSLSocket sslSocket;
    private boolean isAuthenticated = false; // Authenticated state
    private boolean isAdmin = false; // Admin state

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
                String[] command = line.split(";");
                String output = "";

                switch (command[0]) {
                    // testConnection()
                    case ("TEST_CONNECTION"):
                        output = testConnection();
                        break;
                    // login(String username, String password)
                    case ("LOGIN"):
                        String[] credentials = command[1].split(" ");
                        String username = credentials[1];
                        String password = credentials[2];

                        output = login(username, password);
                        break;
                    // loginCookie(String value)
                    case ("LOGIN_COOKIE"):
                        break;
                    // register(String username, String telephone, String password)
                    case ("REGISTER"):
                        break;
                    // getMessages()
                    case ("GET_MESSAGES"):
                        break;
                    // sendMessage(String from, String content)
                    case ("SEND_MESSAGE"):
                        break;
                    // deleteMessage(int id)
                    case ("DELETE_MESSAGE"):
                        break;
                    // Command not exists:
                    default:
                        output = "INVALID_COMMAND";
                        break;
                }
                output += "\n";
                out.write(output.getBytes());
            } catch (IOException e) {
                System.out.println("Connection closed from: " +
                        sslSocket.getRemoteSocketAddress());
                return;
            }
        }
    }

    // Input:
    // TEST_CONNECTION;
    // Returns:
    // OK;
    private String testConnection() {
        return "OK;";
    }

    // Input:
    // LOGIN;USERNAME PASSWORD
    // Returns:
    // OK;USERNAME TELEPHONE IS_ADMIN SESSION_COOKIE
    // INVALID_CREDENTIALS;
    private String login(String username, String password) {
        String passwordHash = PasswordHash.getPasswordHash(password);
        User user = DBConnect.getUser(username, passwordHash);

        if (Objects.isNull(user)) {
            return "INVALID_CREDENTIALS;";
        }
        isAuthenticated = true;
        if (user.getIsAdmin() == 1) {
            isAdmin = true;
        }

        String sessionCookie = Cookies.getCookie();
        DBConnect.createSession(user.getId(), sessionCookie);
        return "OK;" + user.getUsername() + " " +
                user.getTelephone() + " " +
                sessionCookie;
    }

    // Input:
    // LOGIN_COOKIE;VALUE
    // Output:
    // OK; USERNAME TELEPHONE IS_ADMIN SESSION_COOKIE
    // INCORRECT_COOKIE;
    private String loginCookie(String value) {
        return null;
    }

    // Input:
    // REGISTER;USERNAME TELEPHONE PASSWORD
    // Output:
    // OK;USERNAME TELEPHONE IS_ADMIN
    // INVALID_USERNAME;
    // INCORRECT_TELEPHONE;
    private String register(String username, String telephone, String password) {
        return null;
    }

    // AUTHENTICATED only functions:
    // without AUTHENTICATED returns AUTHENTICATION_REQUIRED;

    // Input:
    // GET_MESSAGES;
    // Output:
    // OK;[MESSAGE1, MESSAGE2, ...]
    private String getMessages() {
        return null;
    }

    // Input:
    // SEND_MESSAGE;MESSAGE
    // Output:
    // OK;MESSAGE_ID
    private String sendMessage(String from, String content) {
        return null;
    }

    // AUTHENTICATED and ADMIN only functions:

    // Input:
    // DELETE_MESSAGE;ID
    // Output:
    // OK;
    // NO_ADMIN_RIGHTS;
    // NO_SUCH_MESSAGE;
    private String deleteMessage(int id) {
        return null;
    }
}
