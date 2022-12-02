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

public class Client implements ChatCommands {
    private static final int port = 9000;
    public static final ClientCookies clientCookies = new ClientCookies();
    private InetAddress ip = null;
    private SSLSocket sslSocket = null;
    private InputStream SSLSocketInputStream = null;
    private BufferedReader inp = null;
    private DataOutputStream out = null;
    public String username;
    public boolean isAdmin;
    public List<Message> messageList;

    public Client(String address) {
        System.setProperty("javax.net.ssl.trustStore", "encryption/TrustStore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
        String command = "LOGIN;" + username + " " + password + "\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("INVALID_CREDENTIALS;")) {
                throw new InvalidCredentialsException("Invalid credentials");
            } else {
                String[] userData = result.split(";")[1]
                                .split(" ");
                this.username = userData[0];
                isAdmin = Integer.parseInt(userData[1]) == 1;
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
                isAdmin = Integer.parseInt(userData[1]) == 1;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean register(String username, String password, String telephone) {
        String command = "REGISTER;" + username + " " + password + " " + telephone + "\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("INVALID_USERNAME;")) {
                // INVALID_USERNAME;
                throw new InvalidCredentialsException(username);
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

    public boolean getMessages() {
        String command ="GET_MESSAGES;" + "\n";
        try {
            out.write(command.getBytes());
            String[] results = inp.readLine().split(";");
            String state = results[0];
            if (state.equals("OK")) {
                // OK
                String encodedMessages = results[1];
                messageList = Message.decodeMessages(encodedMessages);
                return true;
            } else if (state.equals("AUTHENTICATION_REQUIRED")) {
                // AUTHENTICATION_REQUIRED;
                throw new AuthenticationRequiredException("No authentication");
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean sendMessage(String content) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Message message = new Message(0, username, content, date);
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
}

