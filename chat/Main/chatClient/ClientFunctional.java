package chat.Main.chatClient;

import chat.Main.ChatCommands;
import chat.Main.InvalidTelephoneException;
import chat.Main.Message;
import chat.Main.chatClient.util.AuthenticationRequiredException;
import chat.Main.chatClient.util.ClientCookies;
import chat.Main.chatClient.util.NoAdminRightsException;

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
    public String chatName;
    public Timestamp lastMessageTimestamp;
    public List<Message> messageList;

    public ClientFunctional(String address) throws UnknownHostException {
        System.setProperty("javax.net.ssl.trustStore", "encryption/TrustStore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            if (address.equals("")){
                throw new UnknownHostException();
            }
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

    // Тестовый метод для проверки связи с сервером
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

    // Метод авторизации, принимает логин и пароль, передает их на сервер
    public boolean login(String username, String password) {
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


    // Метод реализующий вход по куки, сравнивает переданную куку с сохранненой в БД.
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

    // Метод регистрации, принимает данные пользователя и отправляет на сервер,
    // выкидывает ошибку при неверном формате телефона
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

    public boolean getConfig(int id) {
        String command = "GET_CONFIG;" + id + "\n";

        try {
            out.write(command.getBytes());
            String[] result = inp.readLine().split(";");

            if (result[0].equals("OK")) {
                // OK;<CHAT NAME>
                chatName = result[1];
                return true;
            } else if (result[0].equals("AUTHENTICATION_REQUIRED")) {
                // AUTHENTICATION_REQUIRED;
                throw new AuthenticationRequiredException("No authentication");
            } else {
                // NO_SUCH_CONFIG;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean updateConfig(int id, String chatName) {
        String command = "UPDATE_CONFIG;" + id + " " + chatName + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();

            if (result.equals("OK;")) {
                // OK;
                this.chatName = chatName;
                return true;
            } else if (result.equals("AUTHENTICATION_REQUIRED;")) {
                // AUTHENTICATION_REQUIRED;
                throw new AuthenticationRequiredException("No authentication");
            } else {
                // NO_SUCH_CONFIG;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Метод прекращает сессию пользователя
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

    // Запрос на сервер о времени последнего сообщения
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

    // Запрос с сервера списка всех сообщений
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

    // Метод отпрвки сообщения
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
            } else if (result.equals("AUTHENTICATION_REQUIRED;")){
                // AUTHENTICATION_REQUIRED;
                throw new AuthenticationRequiredException("No authentication");
            } else {
                // TOO_LONG_MESSAGE;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Удалить сообщение по id(только admin-user)
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
    // Удаление пользователя по username
    public boolean deleteUser(String username){
        String command = "DELETE_USER;" + username.trim() + "\n";

        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            return switch (result){
                case "OK;" ->
                    // OK;
                        true;
                case "INVALID_USERNAME;" ->
                    //INVALID_USERNAME
                        false;
                case "NO_ADMIN_RIGHTS;" ->
                    // NO_ADMIN_RIGHTS;
                        throw new NoAdminRightsException("No admin rights");
                default -> false;
            };
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    // Неверная команда
    public boolean invalidCommand() {
        return true;
    }

    // Закрыть соеденение с сервером
    public void closeConnection() {
        try {
            inp.close();
            out.close();
            sslSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

