package chatServer;

import java.io.IOException;

public interface ChatCommands {
    boolean ping() throws IOException;
    boolean login(String username, String password) throws IOException;
    boolean loginCookie(String cookieValue) throws IOException;
    boolean register(String username, String telephone, String password) throws IOException;
    boolean logout(String cookieValue) throws IOException;
    boolean getMessages() throws IOException;
    boolean sendMessage(String from, String content) throws IOException;
    boolean deleteMessage(int id) throws IOException;
    boolean invalidCommand() throws IOException;
}
