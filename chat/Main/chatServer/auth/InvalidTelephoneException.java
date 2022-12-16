package chat.Main.chatServer.auth;

public class InvalidTelephoneException extends RuntimeException {
    public InvalidTelephoneException(String errorMessage) {
        super(errorMessage);
    }
}
