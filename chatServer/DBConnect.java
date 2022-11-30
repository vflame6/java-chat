package chatServer;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBConnect {
    private static final String DB_URL = "jdbc:sqlite:chatDatabase.db";
    private static final String DRIVER = "org.sqlite.JDBC";

    private DBConnect() {}

    public static Connection getConnection() throws ClassNotFoundException {
        Class.forName(DRIVER);
        Connection connection = null;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(DB_URL,config.toProperties());
        } catch (SQLException ex) {}
        return connection;
    }

    public static void createChatTables() {
        createUsersTable();
        createMailsTable();
        createSessionsTable();
        createAdminUser();
    }

    private static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n" +
                "id integer PRIMARY KEY,\n" +
                "username text NOT NULL,\n" +
                "telephone text NOT NULL,\n" +
                "passwordHash text,\n" +
                "isAdmin integer DEFAULT 0\n" +
                ");";
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createMailsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS mails (\n" +
                "id INTEGER PRIMARY KEY,\n" +
                "fromUser text NOT NULL,\n" +
                "content text NOT NULL,\n" +
                "time DATETIME DEFAULT CURRENT_TIMESTAMP\n" +
                ");";
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createSessionsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS sessions (\n" +
                "id INTEGER PRIMARY KEY,\n" +
                "user_id integer,\n" +
                "value text NOT NULL,\n" +
                "time DATETIME DEFAULT CURRENT_TIMESTAMP,\n" +
                "FOREIGN KEY (user_id) REFERENCES users (id)\n" +
                ");";
        try(Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createAdminUser() {
        String sql = "INSERT INTO users(username, telephone, passwordHash, isAdmin) VALUES('admin','+7 (999) 999-99-99', '21232f297a57a5a743894a0e4a801fc3', 1)";

        try (Connection connection = getConnection()){
            Statement statement = connection.createStatement();

            statement.executeUpdate(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createUser(String username, String telephone, String passwordHash) {
        String sql = "INSERT INTO users(username, telephone, passwordHash) VALUES(?,?,?)";

        try (Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, telephone);
            preparedStatement.setString(3, passwordHash);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static User getUser(int searchId) {
        String sql = "SELECT * FROM users WHERE id = ? LIMIT 1";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, searchId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String username = resultSet.getString(2);
                String telephone = resultSet.getString(3);
                String passwordHash = resultSet.getString(4);
                int isAdmin = resultSet.getInt(5);

                return new User(id, username, telephone, passwordHash, isAdmin);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUser(String searchUsername) {
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchUsername);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String username = resultSet.getString(2);
                String telephone = resultSet.getString(3);
                String passwordHash = resultSet.getString(4);
                int isAdmin = resultSet.getInt(5);

                return new User(id, username, telephone, passwordHash, isAdmin);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUser(String searchUsername, String searchPasswordHash) {
        String sql = "SELECT * FROM users WHERE username = ? AND passwordHash = ? LIMIT 1";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchUsername);
            preparedStatement.setString(2, searchPasswordHash);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String username = resultSet.getString(2);
                String telephone = resultSet.getString(3);
                String passwordHash = resultSet.getString(4);
                int isAdmin = resultSet.getInt(5);

                return new User(id, username, telephone, passwordHash, isAdmin);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> getAllUsers() {
        String sql = "SELECT * FROM users;";
        List<User> result = new ArrayList<>();

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String username = resultSet.getString(2);
                String telephone = resultSet.getString(3);
                String passwordHash = resultSet.getString(4);
                int isAdmin = resultSet.getInt(5);

                User user = new User(id, username, telephone, passwordHash, isAdmin);
                result.add(user);
            }
        } catch (ClassNotFoundException | SQLException e) {}
        return result;
    }

    public static void createMessage(String from, String content, Timestamp date) {
        String sql = "INSERT INTO mails(fromUser, content, time) VALUES (?,?,?)";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, from);
            preparedStatement.setString(2, content);
            preparedStatement.setTimestamp(3, date);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Message> getAllMessages() {
        String sql = "SELECT * FROM mails;";
        List<Message> result = new ArrayList<>();

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String from = resultSet.getString(2);
                String content = resultSet.getString(3);
                Timestamp date = resultSet.getTimestamp(4);

                Message message = new Message(id, from, content, date);
                result.add(message);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void createSession(int userId, String value) {
        String sql = "INSERT INTO sessions(user_id, value) VALUES (?,?)";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, value);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static User checkSession(String value) {
        String sql = "SELECT * FROM sessions WHERE value = ? LIMIT 1";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt(2);
                Timestamp time = resultSet.getTimestamp(4);
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());

                if ((now.getTime() - time.getTime()) / 1000 < 86400) {
                    return getUser(userId);
                } else {
                    deleteSession(value);
                    return null;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteSession(String value) {
        String sql = "DELETE FROM sessions WHERE value = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, value);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
