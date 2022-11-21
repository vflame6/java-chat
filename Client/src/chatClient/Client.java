package chatClient;
import chatServer.Message;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.io.*;
import java.net.Socket;

public class Client {
    InetAddress ip = null;
    static int port = 9000;
    String cookieFileName;
    static String username;
    static String password;
    static String telephone;
    static int isAdmin;
    static String sessionCookie;
    List<Message> messages;
    static int logged = 0;
    //static String message=Message(idCounter,username,value,);

    private static Socket clientSocket; //сокет для общения
    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public static void main(String[] args) {
        try {
            try {
                // подключение по данным сервера
                clientSocket = new Socket("localhost", port); // этой строкой мы запрашиваем
                //  у сервера доступ на соединение
                reader = new BufferedReader(new InputStreamReader(System.in));
                // поток, принимаемый с сервера
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // поток, который отдаем на сервер
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                loginCookie();
                while (true) {

                    if (logged == 0) {
                        System.out.println("1-войти в систему " +
                                "2-зарегерстрироваться");
                        String cmd = reader.readLine();
                        switch (cmd) {
                            case ("1"):
                                login();
                                break;
                            case ("2"):
                                register();
                                break;
                        }
                    } else {
                        String cmd = reader.readLine();
                        System.out.println("1-Отправить сообщение" +
                                "2-Вывести список сообщений" +
                                "3-Удалить сообщение(только админ)");
                        switch (cmd) {
                            case ("1"):
                                break;
                        }
                    }
                }
            } finally { // в любом случае необходимо закрыть сокет и потоки
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    private static void login() throws IOException {
        System.out.println("Введите имя пользователя");
        String username = reader.readLine();
        System.out.println("Введите пароль");
        String password = reader.readLine();
        out.write("LOGIN" + " " + username + " " + password + "\n");
        out.flush();
        String serverResonse = in.readLine();

        String[] serverCommand = serverResonse.split(" ");
        if (serverCommand[0].equals("OK")) {
            System.out.println("Вы успешно авторизовались");
            logged = 1;
            createCookie(serverCommand[4]);
        } else {
            System.out.println("Данные введены неверно, попробуйте ещё раз");
        }
    }

    private static void register() throws IOException {
        System.out.println("Введите номер телефона");
        String telephone = reader.readLine();
        System.out.println("Введите имя пользователя");
        String username = reader.readLine();
        System.out.println("Введите пароль");
        String password = reader.readLine();
        out.write("REGISTER" + " " + telephone + " " + username + " " + password + "\n");
        out.flush();
        String serverResonse = in.readLine();

        System.out.println(serverResonse);


//
    }

    private static void loginCookie() {


    }

    private static void getMessages() throws IOException {
        String serverResonse = in.readLine();
    }

    private static void deleteMessage(int isadmin, int id) throws IOException {
        if (isadmin == 1) {

        } else {
        }
    }

    private static void createCookie(String cookie) throws IOException {
        File cookieFile = new File("cookie.txt");
        FileWriter cookieWriter = new FileWriter("cookie.txt");
        cookieWriter.write(cookie);
        cookieWriter.close();
        System.out.println("File created: " + cookieFile.getName());

    }


}




