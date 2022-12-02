package chatServer;

import chatServer.Message;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.*;

public class ChatThread extends Thread implements ChatCommands {

    private final SSLSocket sslSocket;
    private InputStream SSLSocketInputStream = null;
    private BufferedReader inp = null;
    private DataOutputStream out = null;
    private boolean isAuthenticated = false; // Authenticated state
    private boolean isAdmin = false; // Admin state
    private static final ServerCookies serverCookies = new ServerCookies();

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
                    case ("PING"):
                        ping();
                        break;
                    // login(String username, String password)
                    case ("LOGIN"):
                        String[] credentials = command[1].split(" ");
                        String username = credentials[0];
                        String password = credentials[1];
                        login(username, password);
                        break;
                    // loginCookie(String value)
                    case ("LOGIN_COOKIE"):
                        String[] loginCredentials = command[1].split(" ");
                        String cookie = loginCredentials[0];
                        loginCookie(cookie);
                        break;
                    // register(String username, String telephone, String password)
                    case ("REGISTER"):
                        String[] registerCredentials = command[1].split(" ");
                        String registerUserName = registerCredentials[0];
                        String registerPassword = registerCredentials[1];
                        String registerTelephone = registerCredentials[2];
                        register(registerUserName, registerPassword, registerTelephone );
                        break;
                    // logout("String cookieValue")
                    case ("LOGOUT"):
                        String[] logoutCredentials = command[1].split(" ");
                        String logoutCookie = logoutCredentials[0];
                        logout(logoutCookie);
                        break;
                    // getMessages()
                    case ("GET_MESSAGES"):
                        getMessages();
                        break;
                    // sendMessage(String from, String content)
                    case ("SEND_MESSAGE"):
//                        var base64String = command[1];
//                        var decodedMessage = Message.decodeMessage(base64String);
//                        String from = decodedMessage.getFrom();
//                        String content = decodedMessage.getContent();
//                        sendMessage(from, content);
                        break;
                    // deleteMessage(int id)
                    case ("DELETE_MESSAGE"):
                        String[] deleteMessagesCredentials = command[1].split(" ");
                        int Id = Integer.parseInt(deleteMessagesCredentials[0]);
                        deleteMessage(Id);
                        break;
                    // Command not exists:
                    // invalidCommand()
                    default:
                        invalidCommand();
                        break;
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
    // OK;USERNAME TELEPHONE IS_ADMIN SESSION_COOKIE
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
        output = "OK;" + user.getUsername() + " " + user.getTelephone() + " " + sessionCookie + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // LOGIN_COOKIE;VALUE
    // Output:
    // OK; USERNAME TELEPHONE IS_ADMIN SESSION_COOKIE
    // INVALID_COOKIE;
    public boolean loginCookie(String value) throws IOException {
        String output;
        User user = DBConnect.checkSession(value);
        if (Objects.isNull(user)) {
            output = "INVALID_COOKIE;\n";
            out.write(output.getBytes());
            return false;
        }

        if (user.getIsAdmin() == 1) {
            isAdmin = true;
        }
        output = "OK; " + user.getUsername() + " " + user.getTelephone()+ " " + isAdmin + " " + value + "\n";
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
        try {
            String correctTelephone = Telephone.processTelephone(telephone);
        } catch (IncorrectTelephoneException ex) {
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

        DBConnect.createUser(username, telephone, passwordHash);
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
        if(!isAuthenticated)
        {
            System.out.println("AUTHENTICATION_REQUIRED");
            return false;
        }
        String output;
        User user = DBConnect.checkSession(cookieValue);
        if (Objects.isNull(user)) {
            output = "INVALID_COOKIE;\n";
            out.write(output.getBytes());
            return false;
        }
        DBConnect.deleteSession(cookieValue);
        output = "OK;\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // GET_MESSAGES;
    // Output:
    // OK;BASE64_ENCODED_LIST<MESSAGE>
    // AUTHENTICATION_REQUIRED;
    // Перед отправкой нужно расшифровать сообщения из базы
    public boolean getMessages() throws IOException {
        if(!isAuthenticated)
        {
            System.out.println("AUTHENTICATION_REQUIRED");
            return false;
        }
        String output;
        Timestamp date = new Timestamp(System.currentTimeMillis());
        var messages = DBConnect.getAllMessages();
        if(messages.size() == 0)
        {
            return false;
        }
        List<Message> decodedMessages = null;
        for(int i =0; i < messages.size(); i++)
        {
            var message = messages.get(i);
            var content = message.getContent();
            Encryptor enc = new Encryptor();
            var decryptContent = enc.decodeMessage(content);
            decodedMessages.add(new Message(message.getId(), message.getFrom(), decryptContent, date));
        }

        var base64String = Message.encodeMessages(decodedMessages);
        output = "OK; " + base64String + "\n";
        out.write(output.getBytes());
        return true;
    }

    // Input:
    // SEND_MESSAGE;BASE64_ENCODED_MESSAGE
    // Output:
    // OK;
    // AUTHENTICATION_REQUIRED;
    // Сообщения должны передаваться в базу в зашифрованном виде
    public boolean sendMessage(String from, String content) throws IOException {
        if(!isAuthenticated)
        {
            System.out.println("AUTHENTICATION_REQUIRED");
            return false;
        }
        String output;
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Encryptor enc = new Encryptor();
        var encodedMessage = enc.encryptMessage(content);
        DBConnect.createMessage(from, encodedMessage, date);
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
        if(!isAuthenticated)
        {
            System.out.println("AUTHENTICATION_REQUIRED");
            return false;
        }
        if(!isAdmin)
        {
            System.out.println("NO_ADMIN_RIGHTS");
            return false;
        }
        String output;
        var message = DBConnect.getMessage(id);
        if (message == null)
        {
            System.out.println("NO_SUCH_MESSAGE");
            return false;
        }
        String command = String.format("DELETE FROM mails WHERE id=%d;", id);
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
