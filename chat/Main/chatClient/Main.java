package chat.Main.chatClient;

public class Main {
    public static void main(String[] args) {
        Client client = new Client("localhost");
        System.out.println(client.ping());
        System.out.println(client.login("admin", "admin"));
        System.out.println(Client.clientCookies.isCookieExists());
        String cookie = Client.clientCookies.getCookie();
        System.out.println(cookie);
        System.out.println(client.sendMessage("It works!!!"));
        System.out.println(client.getMessages());
        System.out.println(client.messageList);
        System.out.println(client.logout(cookie));
        System.out.println(client.loginCookie("BAD_COOKIE"));
    }
}
