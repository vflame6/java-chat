package chat.Main.chatClient.clientTests;

import chat.Main.chatClient.util.ClientCookies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientCookiesTest {
    // Создание объекта клиент-куки для тестирования
    ClientCookies test = new ClientCookies();

    // Тестирование метода createCookie
    @Test
    void getCookieTest123() {
        String cookie = "123";
        test.createCookie(cookie);
        assertEquals("123", test.getCookie());
    }

    // Тестирование метода createCookie с другим значением
    @Test
    void getCookieTest567() {
        String cookie = "567";
        test.createCookie(cookie);
        assertEquals("567", test.getCookie());
    }

    // Тестирование метода getCookie
    @Test
    void getCookieTestString() {
        String cookie = "String";
        test.createCookie(cookie);
        assertEquals("String", test.getCookie());
    }

    // Тестирование метода getCookie с другим значением
    @Test
    void getCookieTest() {
        String cookie = "String\n123";
        test.createCookie(cookie);
        assertEquals("String\n123", test.getCookie());
    }

    // Тестирование метода deleteCookie
    @Test
    void deleteCookieTest() {
        String cookie = "123";
        test.createCookie(cookie);
        test.deleteCookie();
        assertThrows(RuntimeException.class, () -> test.getCookie());
    }

    // Тестирование метода isCookieExists
    @Test
    void cookieExistsTest() {
        test.createCookie("test");
        assertTrue(test.isCookieExists());
    }

    // Тестирование метода isCookieExists
    @Test
    void cookieNotExistsTest() {
        test.deleteCookie();
        assertFalse(test.isCookieExists());
    }
}
