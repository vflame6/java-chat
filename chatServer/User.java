package chatServer;

public class User {
    private final int id;
    private String username;
    private String telephone;
    private String passwordHash;
    private int isAdmin;

    public User(int id, String name, String telephone, String passwordHash, int isAdmin) {
        this.id = id;
        this.username = name;
        this.telephone = telephone;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
    }

    public int getId() { return id; }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getTelephone() {
        return telephone;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return String.format("User(%d, %s, %s, %s, %d)", id, username, telephone, passwordHash, isAdmin);
    }
}
