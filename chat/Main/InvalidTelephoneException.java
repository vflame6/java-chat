package chat.Main;

public class InvalidTelephoneException extends RuntimeException {
    public InvalidTelephoneException(String errorMessage) {
        super(errorMessage);
    }
}
