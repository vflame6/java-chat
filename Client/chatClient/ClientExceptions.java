package chatClient;

class InvalidCredentials extends Exception {
    private String credentials;

    InvalidCredentials(String _credentials) {
        credentials = _credentials;
    }

    public String toString() {
        String msg = "Exception: " + credentials + " is not found in database!!!";
        return msg;
    }
}
class InvalidTelephone extends Exception {
    private String telephone;

    InvalidTelephone(String _telephone) {
        telephone = _telephone;
    }

    public String toString() {
        String msg = "Exception: " + telephone + " is incorrect!!!";
        return msg;
    }
}
class IncorrectUsername extends Exception {
        private String username;

        IncorrectUsername(String _username) {
            username = _username;
        }

        public String toString() {
            String msg = "Exception: " + username + " is already used!!!";
            return msg;
        }

    }

