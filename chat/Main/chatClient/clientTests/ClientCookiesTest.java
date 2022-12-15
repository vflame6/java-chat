package chat.Main.chatClient.clientTests;

import chat.Main.chatClient.util.ClientCookies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientCookiesTest {
    ClientCookies test = new ClientCookies();

    @Test
    void getCookieTest123() {
        String cookie = "123";
        test.createCookie(cookie);
        assertEquals("123", test.getCookie());
    }

    @Test
    void getCookieTest567() {
        String cookie = "567";
        test.createCookie(cookie);
        assertEquals("567", test.getCookie());
    }

    @Test
    void getCookieTestString() {
        String cookie = "String";
        test.createCookie(cookie);
        assertEquals("String", test.getCookie());
    }

    @Test
    void getCookieTest() {
        String cookie = "String\n123";
        test.createCookie(cookie);
        assertEquals("String\n123", test.getCookie());
    }

    @Test
    void deleteCookieTest() {
        String cookie = "123";
        test.createCookie(cookie);
        test.deleteCookie();
        assertThrows(RuntimeException.class,() -> test.getCookie());
    }

    @Test
    void cookieExistsTest() {
        test.createCookie("test");
        assertTrue(test.isCookieExists());
    }

    @Test
    void cookieNotExistsTest() {
        test.deleteCookie();
        assertFalse(test.isCookieExists());
    }
}
