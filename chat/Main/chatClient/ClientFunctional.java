package chat.Main.chatClient;

import chat.Main.ChatCommands;
import chat.Main.InvalidTelephoneException;
import chat.Main.Message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;
import java.sql.Timestamp;
import java.util.List;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ClientFunctional implements ChatCommands {
    private static final int port = 9000;
    public final ClientCookies clientCookies = new ClientCookies();
    private InetAddress ip;
    private SSLSocket sslSocket = null;
    private InputStream SSLSocketInputStream = null;
    private BufferedReader inp = null;
    private DataOutputStream out = null;
    public String username;
    public boolean isAdmin;
    public Timestamp lastMessageTimestamp;
    public List<Message> messageList;

    public ClientFunctional(String address) throws UnknownHostException {
        System.setProperty("javax.net.ssl.trustStore", "encryption/TrustStore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            ip = InetAddress.getByName(address.trim());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw e;
        }

        try {
            this.sslSocket = (SSLSocket) sslsocketfactory.createSocket(ip, port);
            this.SSLSocketInputStream = sslSocket.getInputStream();
            // поток, принимаемый с сервера
            this.inp = new BufferedReader(new InputStreamReader(SSLSocketInputStream));
            // поток, который отдаем на сервер
            this.out = new DataOutputStream(sslSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "ip=" + ip +
                ", sslSocket=" + sslSocket +
                ", SSLSocketInputStream=" + SSLSocketInputStream +
                ", inp=" + inp +
                ", out=" + out +
                ", username='" + username + '\'' +
                ", isAdmin=" + isAdmin +
                ", messageList=" + messageList +
                '}';
    }

    public boolean ping() {
        String command = "PING;" + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            return result.equals("PONG;");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) throws InvalidCredentialsException {
        String command = "LOGIN;" + username.trim() + " " + password.trim() + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("INVALID_CREDENTIALS;")) {
                return false;
            } else {
                String[] userData = result.split(";")[1]
                                .split(" ");
                this.username = userData[0];
                isAdmin = Boolean.parseBoolean(userData[1]);
                String cookie = userData[2];
                clientCookies.createCookie(cookie);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean loginCookie(String cookieValue) {
        String command = "LOGIN_COOKIE;" + cookieValue + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("INVALID_COOKIE;")) {
                // INVALID_COOKIE;
                return false;
            } else {
                // OK;USERNAME TELEPHONE IS_ADMIN
                String[] userData = result.split(";")[1]
                        .split(" ");
                username = userData[0];
                isAdmin = Boolean.parseBoolean(userData[1]);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean register(String username, String password, String telephone) {
        String command = "REGISTER;" + username.trim() + " " + password.trim() + " " + telephone.trim() + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("INVALID_USERNAME;")) {
                // INVALID_USERNAME;
                return false;
            }
            if (result.equals("INCORRECT_TELEPHONE;")) {
                // INCORRECT_TELEPHONE;
                throw new InvalidTelephoneException(telephone);
            } else {
                // OK;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean logout(String cookieValue) {
        String command ="LOGOUT;" + cookieValue + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();

            if (result.equals("OK;")) {
                // OK;
                clientCookies.deleteCookie();
                return true;
            } else if (result.equals("AUTHENTICATION_REQUIRED;")) {
                // AUTHENTICATION_REQUIRED;
                throw new AuthenticationRequiredException("No authentication");
            } else {
                // INVALID_COOKIE;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean getLastMessageTimestamp() {
        String command = "GET_LAST_MESSAGE_TIMESTAMP;" + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            String[] results = result.split(";");
            String state = results[0];
            if (state.equals("OK")) {
                // OK;<LAST_MESSAGE_TIMESTAMP_STRING>
                lastMessageTimestamp = Timestamp.valueOf(results[1]);
                return true;
            } else if (state.equals("AUTHENTICATION_REQUIRED")) {
                // AUTHENTICATION_REQUIRED;
                throw new AuthenticationRequiredException("No authentication");
            } else {
                // NO_MESSAGES;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean getMessages() {
        String command = "GET_MESSAGES;" + "\n";

        try {
            out.write(command.getBytes());
            String[] results = inp.readLine().split(";");
            String state = results[0];
            if (state.equals("OK")) {
                // OK;<BASE64_ENCODED_LIST<MESSAGE>>
                String encodedMessages = results[1];
                messageList = Message.decodeMessages(encodedMessages);
                return true;
            } else if (state.equals("AUTHENTICATION_REQUIRED")) {
                // AUTHENTICATION_REQUIRED;
                throw new AuthenticationRequiredException("No authentication");
            } else {
                // NO_MESSAGES;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean sendMessage(String content) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Message message = new Message(0, username, content.trim(), date);
        String encoded_message = message.encodeMessage();
        String command = "SEND_MESSAGE;" + encoded_message + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("OK;")){
                // OK;
                return true;
            } else {
                // AUTHENTICATION_REQUIRED;
                throw new AuthenticationRequiredException("No authentication");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean deleteMessage(int id) {
        String command ="DELETE_MESSAGE;" + id + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            return switch (result) {
                case "OK;" ->
                    // OK;
                        true;
                case "AUTHENTICATION_REQUIRED;" ->
                    // AUTHENTICATION_REQUIRED;
                        throw new AuthenticationRequiredException("No authentication");
                case "NO_ADMIN_RIGHTS;" ->
                    // NO_ADMIN_RIGHTS;
                        throw new NoAdminRightsException("No admin rights");
                default ->
                    // NO_SUCH_MESSAGE;
                        false;
            };
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean invalidCommand() {
        return true;
    }

    public void closeConnection() {
        try{
            inp.close();
            out.close();
            sslSocket.close();
            System.out.println("The Client is disconnected...");
        } catch (IOException e) {
            System.err.println("Socket not closed...");
            e.printStackTrace();
        }
    }
}

