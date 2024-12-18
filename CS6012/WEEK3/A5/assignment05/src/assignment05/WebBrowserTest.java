package assignment05;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebBrowserTest {

    @org.junit.jupiter.api.Test
    void testvisit() throws Exception {
        WebBrowser browser = new WebBrowser();
        browser.visit(new URL("https://a.com"));
        browser.visit(new URL("https://b.com"));
        browser.visit(new URL("https://c.com"));

        assertEquals("https://c.com", browser.history().getFirst().toString());
    }

    @org.junit.jupiter.api.Test
    void backAndForward() throws MalformedURLException {
        WebBrowser browser = new WebBrowser();
        browser.visit(new URL("https://a.com"));
        browser.visit(new URL("https://b.com"));
        browser.visit(new URL("https://c.com"));
        assertEquals("https://b.com", browser.back().toString());
        assertEquals("https://c.com", browser.forward().toString());

    }


    @org.junit.jupiter.api.Test
    void history() throws MalformedURLException {
        WebBrowser browser = new WebBrowser();
        browser.visit(new URL("https://a.com"));
        browser.visit(new URL("https://b.com"));
        browser.visit(new URL("https://c.com"));

        SinglyLinkedList<URL> history = browser.history();
        assertEquals("https://c.com", history.getFirst().toString());
        history.deleteFirst();
        assertEquals("https://b.com", history.getFirst().toString());
        assertEquals("https://a.com", history.delete(1).toString());
    }

}