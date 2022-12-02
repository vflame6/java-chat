package chatClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;
import java.sql.Timestamp;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {
    private static final int port = 9000;
    private static ClientCookies clientCookies = new ClientCookies();
    private InetAddress ip = null;
    private SSLSocket sslSocket = null;
    private InputStream SSLSocketInputStream = null;
    private BufferedReader inp = null;
    private DataOutputStream out = null;

    private String from = "";

    public Client(String address) {
        System.setProperty("javax.net.ssl.trustStore", "encryption/TrustStore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            this.sslSocket = (SSLSocket) sslsocketfactory.createSocket(ip, port);
            this.SSLSocketInputStream = sslSocket.getInputStream();
            // поток, принимаемый с сервера
            this.inp = new BufferedReader(new InputStreamReader(SSLSocketInputStream));
            // поток, который отдаем на сервер
            this.out = new DataOutputStream(sslSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public boolean ping() {
        String command = "PING;" + "\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            System.out.println(result);
            if (result.equals("PONG;")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) throws InvalidCredentials {
        String command = "LOGIN;" + username + " " + password + "\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("INVALID_CREDENTIALS;")) {
                throw new InvalidCredentials(username + password);
            } else {
                String cookie = (result.split(" ")[-1]);
                clientCookies.createCookie(cookie);
                from = username;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String password, String telephone) throws InvalidTelephone, InvalidCredentials {
        String command = "REGISTER;" + username + " " +
                password + " " + telephone + "\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("INVALID_USERNAME;")) {
                throw new InvalidCredentials(username);
            }
            if (result.equals("INCORRECT_TELEPHONE;")) {
                throw new InvalidTelephone(telephone);
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginCookie() {
        String command = "";
        String cookie = clientCookies.getCookie();

        try {
            command = "LOGIN_COOKIE;" + cookie + "\n";
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("INCORRECT_COOKIE;")) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean logout() {
        String cookie = clientCookies.getCookie();
        String command ="LOGOUT;"+cookie+"\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("OK;\n")){
                return true;
            }
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean getMessages() {
        String command ="GET_MESSAGES;"+"\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("OK;\n")){
                return true;
            }
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    public boolean deleteMessage(String id) {
        String command ="DELETE_MESSAGE;"+id+" "+"\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("OK;\n")){
                return true;
            }
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

    }
    }

    public boolean sendMessage(String value) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Message message=new Message(0,from,value,date);
        String encoded_message=message.encodeMessage();
        String command = "SEND_MESSAGE;"+encoded_message+"\n";
        try {
            out.write(command.getBytes());
            String result = inp.readLine();
            if (result.equals("OK;\n")){
                return true;
            }
            else {return false;}
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }
}

