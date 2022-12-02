package chat.Main.chatServer;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.*;

import chat.Main.ChatCommands;
import chat.Main.InvalidTelephoneException;
import chat.Main.Message;

public class ChatThread extends Thread implements ChatCommands {

    private final SSLSocket sslSocket;
    private InputStream SSLSocketInputStream = null;
    private BufferedReader inp = null;
    private DataOutputStream out = null;
    private boolean isAuthenticated = false; // Authenticated state
    private boolean isAdmin = false; // Admin state
    private static final ServerCookies serverCookies = new ServerCookies();
    private static final Encryptor encryptor = new Encryptor();

    public ChatThread(SSLSocket sslSocket) {
        this.sslSocket = sslSocket;
    }

    public void run() {
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
                ChatLogger.logAccess(sslSocket.getInetAddress(), line);
                String[] command = line.split(";");

                switch (command[0]) {
                    // ping()
                    case ("PING") -> ping();

                    // login(String username, String password)
                    case ("LOGIN") -> {
                        String[] credentials = command[1].split(" ");
                        String username = credentials[0];
                        String password = credentials[1];
                        login(username, password);
                    }
                    // loginCookie(String value)
                    case ("LOGIN_COOKIE") -> {
                        String[] loginCredentials = command[1].split(" ");
                        String cookie = loginCredentials[0];
                        loginCookie(cookie);
                    }
                    // register(String username, String telephone, String password)
                    case ("REGISTER") -> {
                        String[] registerCredentials = command[1].split(" ");
                        String registerUserName = registerCredentials[0];
                        String registerPassword = registerCredentials[1];
                        String registerTelephone = registerCredentials[2];
                        register(registerUserName, registerPassword, registerTelephone);
                    }
                    // logout("String cookieValue")
                    case ("LOGOUT") -> {
                        String logoutCookie = command[1];
                        logout(logoutCookie);
                    }
                    // getMessages()
                    case ("GET_MESSAGES") -> getMessages();

                    // sendMessage(String from, String content)
                    case ("SEND_MESSAGE") -> {
                        String encodedMessage = command[1];
                        sendMessage(encodedMessage);
                    }
                    // deleteMessage(int id)
                    case ("DELETE_MESSAGE") -> {
                        String id = command[1];
                        int messageId = Integer.parseInt(id);
                        deleteMessage(messageId);
                    }
                    // Command not exists:
                    // invalidCommand()
                    default -> invalidCommand();
                }
            } catch (IOException e) {
                System.out.println("Connection closed from: " +
                        sslSocket.getRemoteSocketAddress());
                return;
            }
        }
    }

    // Input:
    // PING;
    // Returns:
    // PONG;
    public boolean ping() throws IOException {
        String output = "PONG;\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // LOGIN;USERNAME PASSWORD
    // Returns:
    // OK;USERNAME IS_ADMIN SESSION_COOKIE
    // INVALID_CREDENTIALS;
    public boolean login(String username, String password) throws IOException {
        String output;
        String passwordHash = PasswordHash.getPasswordHash(password);
        User user = DBConnect.getUser(username, passwordHash);

        if (Objects.isNull(user)) {
            output = "INVALID_CREDENTIALS;\n";
            out.write(output.getBytes());
            return false;
        }
        isAuthenticated = true;
        if (user.getIsAdmin() == 1) {
            isAdmin = true;
        }

        String sessionCookie = serverCookies.getCookie();
        DBConnect.createSession(user.getId(), sessionCookie);
        output = "OK;" + user.getUsername() + " " + user.getIsAdmin() + " " + sessionCookie + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // LOGIN_COOKIE;VALUE
    // Output:
    // OK; USERNAME IS_ADMIN
    // INVALID_COOKIE;
    public boolean loginCookie(String value) throws IOException {
        String output;
        User user = DBConnect.checkSession(value);
        if (Objects.isNull(user)) {
            output = "INVALID_COOKIE;\n";
            out.write(output.getBytes());
            return false;
        }
        isAuthenticated = true;
        if (user.getIsAdmin() == 1) {
            isAdmin = true;
        }
        output = "OK;" + user.getUsername() + " " + isAdmin + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // REGISTER;USERNAME TELEPHONE PASSWORD
    // Output:
    // OK;
    // INVALID_USERNAME;
    // INCORRECT_TELEPHONE;
    public boolean register(String username, String telephone, String password) throws IOException {
        String output;
        String passwordHash = PasswordHash.getPasswordHash(password);
        String correctTelephone;

        try {
            correctTelephone = Telephone.processTelephone(telephone);
        } catch (InvalidTelephoneException ex) {
            output = "INCORRECT_TELEPHONE;\n";
            out.write(output.getBytes());
            return false;
        }

        User user = DBConnect.getUser(username);
        if (!Objects.isNull(user)) {
            output = "INVALID_USERNAME;\n";
            out.write(output.getBytes());
            return false;
        }

        DBConnect.createUser(username, correctTelephone, passwordHash);
        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // AUTHENTICATED only functions:
    // without AUTHENTICATED returns AUTHENTICATION_REQUIRED;

    // Input:
    // LOGOUT;COOKIE_VALUE
    // OUTPUT:
    // OK;
    // AUTHENTICATION_REQUIRED;
    // Удалить из базы значение сессии
    public boolean logout(String cookieValue) throws IOException {
        String output;
        if(!isAuthenticated) {
            output = "AUTHENTICATION_REQUIRED;\n";
            out.write(output.getBytes());
            return false;
        }
        User user = DBConnect.checkSession(cookieValue);
        if (Objects.isNull(user)) {
            output = "INVALID_COOKIE;\n";
            out.write(output.getBytes());
            return false;
        }
        DBConnect.deleteSession(cookieValue);
        isAuthenticated = false;
        isAdmin = false;

        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // GET_MESSAGES;
    // Output:
    // OK;BASE64_ENCODED_LIST<MESSAGE>
    // AUTHENTICATION_REQUIRED;
    // NO_MESSAGES;
    // Перед отправкой нужно расшифровать сообщения из базы
    public boolean getMessages() throws IOException {
        String output;
        if(!isAuthenticated) {
            output = "AUTHENTICATION_REQUIRED;\n";
            out.write(output.getBytes());
            return false;
        }

        List<Message> messageList = DBConnect.getAllMessages();
        int messageListSize = messageList.size();
        if(messageListSize == 0) {
            output = "NO_MESSAGES;\n";
            out.write(output.getBytes());
            return false;
        }

        List<Message> decodedMessages = new ArrayList<>();
        for(Message encryptedMessage : messageList) {
            int id = encryptedMessage.getId();
            String from = encryptedMessage.getFrom();
            String encryptedContent = encryptedMessage.getContent();
            Timestamp date = encryptedMessage.getDate();
            String decryptedContent = encryptor.decryptMessage(encryptedContent);
            Message message = new Message(id, from, decryptedContent, date);
            decodedMessages.add(message);
        }
        String base64String = Message.encodeMessages(decodedMessages);
        output = "OK;" + base64String + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // SEND_MESSAGE;BASE64_ENCODED_MESSAGE
    // Output:
    // OK;
    // AUTHENTICATION_REQUIRED;
    // Сообщения должны передаваться в базу в зашифрованном виде
    public boolean sendMessage(String encodedMessage) throws IOException {
        String output;
        if (!isAuthenticated) {
            output = "AUTHENTICATION_REQUIRED;\n";
            out.write(output.getBytes());
            return false;
        }
        Message message = Message.decodeMessage(encodedMessage);
        String from = message.getFrom();
        String content = message.getContent();
        Timestamp date = message.getDate();

        String encryptedContent = encryptor.encryptMessage(content);
        ChatLogger.logMessage(from, encryptedContent);
        DBConnect.createMessage(from, encryptedContent, date);
        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // AUTHENTICATED and ADMIN only functions:

    // Input:
    // DELETE_MESSAGE;ID
    // Output:
    // OK;
    // AUTHENTICATION_REQUIRED;
    // NO_ADMIN_RIGHTS;
    // NO_SUCH_MESSAGE;
    public boolean deleteMessage(int id) throws IOException {
        String output;
        if (!isAuthenticated) {
            output = "AUTHENTICATION_REQUIRED;\n";
            out.write(output.getBytes());
            return false;
        }
        if (!isAdmin) {
            output = "NO_ADMIN_RIGHTS;\n";
            out.write(output.getBytes());
            return false;
        }
        Message message = DBConnect.getMessage(id);
        if (Objects.isNull(message)) {
            output = "NO_SUCH_MESSAGE;\n";
            out.write(output.getBytes());
            return false;
        }
        DBConnect.deleteMessage(message.getId());
        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // Any invalid command:
    public boolean invalidCommand() throws IOException {
        String output = "INVALID_COMMAND;\n";
        out.write(output.getBytes());
        return true;
    }
}
