package chat.Main.chatClient.util;

public class NoAdminRightsException extends RuntimeException {
    public NoAdminRightsException(String message) {
        super(message);
    }
}
