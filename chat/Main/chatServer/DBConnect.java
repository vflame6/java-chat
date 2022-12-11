package chat.Main.chatServer;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import chat.Main.Message;

public class DBConnect {
    private static final String DB_URL = "jdbc:sqlite:chatDatabase.db";
    private static final String DRIVER = "org.sqlite.JDBC";

    // Конструктор закрыт, нельзя создать экземпляр класса
    private DBConnect() {}

    // Получить соединение для работы с базой
    public static Connection getConnection() throws ClassNotFoundException {
        Class.forName(DRIVER);
        Connection connection = null;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(DB_URL,config.toProperties());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Создаёт все нужные таблицы в базе, использовать один раз
    public static void createChatTables() {
        createUsersTable();
        createConfigTable();
        createMailsTable();
        createSessionsTable();

        createDefaultConfig();
        createAdminUser();
    }

    // Создаёт таблицу users в базе. Нужна для хранения пользователей
    private static void createUsersTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                id integer PRIMARY KEY,
                username text NOT NULL,
                telephone text NOT NULL,
                passwordHash text,
                isAdmin integer DEFAULT 0
                );""";
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createConfigTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS config (
                id INTEGER PRIMARY KEY,
                chat_name TEXT NOT NULL
                );""";
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Создаёт таблицу mails в базе. Нужна для хранения сообщений
    private static void createMailsTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS mails (
                id INTEGER PRIMARY KEY,
                fromUser text NOT NULL,
                content text NOT NULL,
                time DATETIME DEFAULT CURRENT_TIMESTAMP
                );""";
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Создаёт таблицу sessions в базе. Нужна для хранения сессий
    private static void createSessionsTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS sessions (
                id INTEGER PRIMARY KEY,
                user_id integer,
                value text NOT NULL,
                time DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users (id)
                );""";
        try(Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Создаёт конфигурацию по умолчанию в таблице config
    private static void createDefaultConfig() {
        String sql = "INSERT INTO config(chat_name) VALUES('Chatik!')";

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Создаёт пользователя admin в таблице users
    private static void createAdminUser() {
        String sql = "INSERT INTO users(username, telephone, passwordHash, isAdmin) VALUES('admin','+7 (999) 999-99-99', '21232f297a57a5a743894a0e4a801fc3', 1)";

        try (Connection connection = getConnection()){
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getConfig(int searchId) {
        String sql = "SELECT * FROM config WHERE id = ? LIMIT 1";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, searchId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(2);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Метод для обновления имени чата в базе config
    public static void updateConfig(int configId, String chatName) {
        String sql = "UPDATE config SET chat_name = ? WHERE id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, chatName);
            preparedStatement.setInt(2, configId);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для создания пользователя в таблице users
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

    // Метод для поиска пользователя в таблице users по его id
    public static User getUser(int searchId) {
        String sql = "SELECT * FROM users WHERE id = ? LIMIT 1";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, searchId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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

    // Метод для поиска пользователя в таблице users по имени пользователя
    public static User getUser(String searchUsername) {
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchUsername);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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

    // Метод для поиска пользователя в таблице users по имени пользователя и паролю.
    // Нужен для авторизации
    public static User getUser(String searchUsername, String searchPasswordHash) {
        String sql = "SELECT * FROM users WHERE username = ? AND passwordHash = ? LIMIT 1";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchUsername);
            preparedStatement.setString(2, searchPasswordHash);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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

    // Метод для получения списка всех пользователей
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
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Метод для создания сообщения в таблице mails
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

    // Метод для поиска сообщения в таблице mails по его id
    public static Message getMessage(int searchId) {
        String sql = "SELECT * FROM mails WHERE id = ? LIMIT 1";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, searchId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String from = resultSet.getString(2);
                String content = resultSet.getString(3);
                Timestamp date = resultSet.getTimestamp(4);
                return new Message(id, from, content, date);
            }
        }   catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Метод для получения последнего сообщения в таблице mails
    public static Message getLastMessage() {
        String sql = "SELECT * FROM mails ORDER BY id DESC LIMIT 1";

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String from = resultSet.getString(2);
                String content = resultSet.getString(3);
                Timestamp date = resultSet.getTimestamp(4);

                return new Message(id, from, content, date);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Метод для получения списка всех сообщений
    public static List<Message> getAllMessages() {
        String sql = "SELECT * FROM mails";
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

    // Метод для удаления сообщений из базы mails
    public static void deleteMessage(int id) {
        String sql = "DELETE FROM mails WHERE id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для создания сессии в таблице sessions
    public static void createSession(int userId, String value) {
        String sql = "INSERT INTO sessions(user_id, value) VALUES (?,?)";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, value);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для поиска сессии в таблице sessions по значению cookie.
    // Нужен для авторизации
    public static User checkSession(String value) {
        String sql = "SELECT * FROM sessions WHERE value = ? LIMIT 1";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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

    // Метод для удаления сессии из таблицы sessions
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
