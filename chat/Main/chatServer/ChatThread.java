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
import chat.Main.chatServer.auth.Telephone;
import chat.Main.chatServer.auth.User;
import chat.Main.chatServer.util.ChatLogger;
import chat.Main.chatServer.util.DBConnect;
import chat.Main.chatServer.util.Encryptor;
import chat.Main.chatServer.auth.PasswordHash;
import chat.Main.chatServer.auth.ServerCookies;

public class ChatThread extends Thread implements ChatCommands {
    private final SSLSocket sslSocket;
    private InputStream SSLSocketInputStream = null;
    private BufferedReader inp = null;
    private DataOutputStream out = null;
    private String username;
    private boolean authenticatedState = false;
    private boolean adminState = false;
    private Timestamp lastChangeTimestamp;
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

        updateLastChangeTimestamp();

        String line;
        while (true) {
            try {
                line = inp.readLine();
                System.out.println(sslSocket.getInetAddress() + " " + line);
                ChatLogger.logAccess(sslSocket.getInetAddress(), line);
                parseCommand(line);
            } catch (IOException e) {
                System.out.println("Connection closed from: " +
                        sslSocket.getRemoteSocketAddress());
                return;
            }
        }
    }

    private void updateLastChangeTimestamp() {
        lastChangeTimestamp = new Timestamp(System.currentTimeMillis());
    }

    private void parseCommand(String line) throws IOException {
        if (Objects.isNull(line)) {
            throw new IOException();
        }
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
            // register(String username, String password, String telephone)
            case ("REGISTER") -> {
                String[] registerCredentials = command[1].split(" ");
                String registerUserName = registerCredentials[0];
                String registerPassword = registerCredentials[1];
                String registerTelephone = registerCredentials[2];
                register(registerUserName, registerPassword, registerTelephone);
            }
            // getConfig(int id)
            case ("GET_CONFIG") -> {
                int id = Integer.parseInt(command[1]);
                getConfig(id);
            }
            // updateConfig(int id, String chatName)
            case("UPDATE_CONFIG") -> {
                String[] inputs = command[1].split(" ");
                int id = Integer.parseInt(inputs[0]);
                String chatName = inputs[1];
                for (int i = 2; i < inputs.length; i++) {
                    chatName += " " + inputs[i];
                }
                updateConfig(id, chatName);
            }
            // logout("String cookieValue")
            case ("LOGOUT") -> {
                String logoutCookie = command[1];
                logout(logoutCookie);
            }
            // getLastMessageTimestamp()
            case ("GET_LAST_MESSAGE_TIMESTAMP") -> getLastMessageTimestamp();
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
            case("DELETE_USER") -> {
                String username = command[1];
                deleteUser(username);
            }
            // Command not exists:
            // invalidCommand()
            default -> invalidCommand();
        }
    }

    // Input:
    // PING;
    // Returns:
    // PONG;
    public boolean ping() throws IOException {
        String output;

        output = "PONG;\n";
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

        username = user.getUsername();
        authenticatedState = true;
        adminState = user.getIsAdmin() == 1;

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

        username = user.getUsername();
        authenticatedState = true;
        adminState = user.getIsAdmin() == 1;

        output = "OK;" + user.getUsername() + " " + adminState + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // REGISTER;USERNAME TELEPHONE PASSWORD
    // Output:
    // OK;
    // INVALID_USERNAME;
    // INCORRECT_TELEPHONE;
    public boolean register(String username, String password, String telephone) throws IOException {
        String output;

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

        String passwordHash = PasswordHash.getPasswordHash(password);
        DBConnect.createUser(username, correctTelephone, passwordHash);

        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // AUTHENTICATED only functions:
    // without AUTHENTICATED returns AUTHENTICATION_REQUIRED;

    // Input:
    // GET_CONFIG;<ID>
    // Output:
    // OK;<CHAT NAME>
    // AUTHENTICATION_REQUIRED;
    // NO_SUCH_CONFIG;
    public boolean getConfig(int id) throws IOException {
        String output;
        if(!isAuthenticated()) {
            return false;
        }

        String config = DBConnect.getConfig(id);
        if (Objects.isNull(config)) {
            output = "NO_SUCH_CONFIG;\n";
            out.write(output.getBytes());
            return false;
        }

        output = "OK;" + config + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // UPDATE_CONFIG;<ID> <CHAT_NAME>
    // Output:
    // OK;
    // AUTHENTICATION_REQUIRED;
    // NO_SUCH_CONFIG;
    public boolean updateConfig(int id, String chatName) throws IOException {
        String output;
        if(!isAuthenticated()) {
            return false;
        }

        String config = DBConnect.getConfig(id);
        if (Objects.isNull(config)) {
            output = "NO_SUCH_CONFIG;\n";
            out.write(output.getBytes());
            return false;
        }

        DBConnect.updateConfig(id, chatName);
        updateLastChangeTimestamp();

        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // LOGOUT;COOKIE_VALUE
    // OUTPUT:
    // OK;
    // AUTHENTICATION_REQUIRED;
    // Удалить из базы значение сессии
    public boolean logout(String cookieValue) throws IOException {
        String output;
        if(!isAuthenticated()) {
            return false;
        }

        User user = DBConnect.checkSession(cookieValue);
        if (Objects.isNull(user)) {
            output = "INVALID_COOKIE;\n";
            out.write(output.getBytes());
            return false;
        }

        DBConnect.deleteSession(cookieValue);
        authenticatedState = false;
        adminState = false;

        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // GET_LAST_MESSAGE_TIMESTAMP;
    // OUTPUT:
    // OK;<TIMESTAMP>
    // AUTHENTICATION_REQUIRED;
    // NO_MESSAGES;
    public boolean getLastMessageTimestamp() throws IOException {
        String output;
        if (!isAuthenticated()) {
            return false;
        }

        output = "OK;" + lastChangeTimestamp + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // GET_MESSAGES;
    // Output:
    // OK;<BASE64_ENCODED_LIST<MESSAGE>>
    // AUTHENTICATION_REQUIRED;
    // NO_MESSAGES;
    // Перед отправкой нужно расшифровать сообщения из базы
    public boolean getMessages() throws IOException {
        String output;
        if (!isAuthenticated()) {
            return false;
        }

        List<Message> messageList = DBConnect.getAllMessages();
        if (messageList.size() == 0) {
            output = "NO_MESSAGES;\n";
            out.write(output.getBytes());
            return false;
        }

        List<Message> decodedMessages = new ArrayList<>();
        for (Message encryptedMessage : messageList) {
            decodedMessages.add(new Message(encryptedMessage.getId(),
                    encryptedMessage.getFrom(),
                    encryptor.decryptMessage(encryptedMessage.getContent()),
                    encryptedMessage.getTimestamp()));
        }

        output = "OK;" + Message.encodeMessages(decodedMessages) + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // SEND_MESSAGE;BASE64_ENCODED_MESSAGE
    // Output:
    // OK;
    // TOO_LONG_MESSAGE;
    // AUTHENTICATION_REQUIRED;
    // Сообщения должны передаваться в базу в зашифрованном виде
    public boolean sendMessage(String encodedMessage) throws IOException {
        String output;
        if (!isAuthenticated()) {
            return false;
        }

        Message message = Message.decodeMessage(encodedMessage);
        if (message.getContent().length() > 100) {
            output = "TOO_LONG_MESSAGE;\n";
            out.write(output.getBytes());
            return false;
        }

        String encryptedContent = encryptor.encryptMessage(message.getContent());
        DBConnect.createMessage(message.getFrom(),
                encryptedContent,
                new Timestamp(System.currentTimeMillis()));

        output = "OK;\n";
        out.write(output.getBytes());
        updateLastChangeTimestamp();
        ChatLogger.logMessage(message.getFrom(), encryptedContent);
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
        if (!isAuthenticated() && !isAdmin()) {
            return false;
        }

        Message message = DBConnect.getMessage(id);
        if (Objects.isNull(message)) {
            output = "NO_SUCH_MESSAGE;\n";
            out.write(output.getBytes());
            return false;
        }

        DBConnect.deleteMessage(message.getId());
        updateLastChangeTimestamp();

        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // DELETE_USER;<USERNAME>
    // OUTPUT:
    // OK;
    // INVALID_USERNAME
    // Удаление пользователя по username
    public boolean deleteUser(String username) throws IOException {
        if (!isAuthenticated() && !isAdmin()) {
            return false;
        }

        String output;
        User user = DBConnect.getUser(username);

        if (Objects.isNull(user)) {
            output = "INVALID_USERNAME;\n";
            out.write(output.getBytes());
            return false;
        }

        DBConnect.deleteUserSessions(user.getId());
        DBConnect.deleteUser(username);

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

    private boolean isAuthenticated() throws IOException {
        if (!authenticatedState && !Objects.isNull(DBConnect.getUser(username))) {
            String output = "AUTHENTICATION_REQUIRED;\n";
            out.write(output.getBytes());
            return false;
        } else {
            return true;
        }
    }

    private boolean isAdmin() throws IOException {
        if (!adminState) {
            String output = "NO_ADMIN_RIGHTS;\n";
            out.write(output.getBytes());
            return false;
        } else {
            return true;
        }
    }
}
