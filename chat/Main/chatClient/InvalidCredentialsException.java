package chat.Main.chatClient;

class InvalidCredentialsException extends RuntimeException {
    InvalidCredentialsException(String _credentials) {
        super(_credentials + " is incorrect!");
    }
}
