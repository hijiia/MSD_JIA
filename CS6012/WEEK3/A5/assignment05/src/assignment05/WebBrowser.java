package assignment05;

import java.net.URL;
import java.util.NoSuchElementException;

public class WebBrowser {
    private LinkedListStack<URL> back;
    private LinkedListStack<URL> forward;
    private URL current;

    /**
     * This constructor creates a new web browser with no previously-visited webpages
     * and no webpages to visit next.
     */
    public WebBrowser() {
        back = new LinkedListStack<>();
        forward = new LinkedListStack<>();
        current = null;
    }

    /**
     * creates a new web brower with a preloaded history of visited webpage
     *
     * @param history -- a list of URLLinks to an external site
     *                the 1st page is the "current"
     *                remaining are previous
     */
    public WebBrowser(SinglyLinkedList<URL> history) {
        back = new LinkedListStack<>();
        forward = new LinkedListStack<>();
        if (history != null && !history.isEmpty()) {
            current = history.get(0);
            for (int i = 1; i < history.size(); i++) {
                back.push(history.get(i));
            }
        } else {
            current = null;
        }
    }

    /**
     * Simulates visiting a webpage
     * Calling this method clears the forward button stack
     *
     * @param webpage - the URL of the webpage to visit
     */
    public void visit(URL webpage) {
        if (current != null) {
            back.push(current);
        }
        current = webpage;
        forward.clear();
    }

    /**
     * Simulates using the back button
     * Throws NoSuchElementException if there is no previously-visited URL.
     *
     * @return the URL visited after pressing the back button
     * @throws NoSuchElementException if there is no previously-visited URL
     */
    public URL back() throws NoSuchElementException {
        if (back.isEmpty()) {
            throw new NoSuchElementException("No previously-visited URL.");
        }
        forward.push(current);
        current = back.pop();
        return current;
    }

    /**
     * Simulates using the forward button
     * Throws NoSuchElementException if there is no URL to visit next.
     *
     * @return the URL visited after pressing the forward button
     * @throws NoSuchElementException if there is no URL to visit next
     */
    public URL forward() throws NoSuchElementException {
        if (forward.isEmpty()) {
            throw new NoSuchElementException("No URL to visit next.");
        }
        back.push(current);
        current = forward.pop();
        return current;
    }

    /**
     * Generates a history of URLs visited
     * as a list of URL objects ordered from most recently visited to least recently visited
     * (including the "current" webpage visited).
     * The "forward" URLs are not included.
     * This method must have a time complexity of O(N)
     *
     * @return a SinglyLinkedList of URLs representing the browser history
     */
    public SinglyLinkedList<URL> history() {
        SinglyLinkedList<URL> historyList = new SinglyLinkedList<>();

        // Add the current page if it's not null
        if (current != null) {
            historyList.insertFirst(current);  // Most recent page goes first
        }

        // Use a temporary stack to reverse the order of the back stack
        LinkedListStack<URL> temp = new LinkedListStack<>();
        while (!back.isEmpty()) {
            temp.push(back.pop());  // Transfer all URLs from the back stack to temp
        }

        // pop from the temp stack and insert at the front of historyList
        while (!temp.isEmpty()) {
            URL url = temp.pop();
            historyList.insertFirst(url);  // Insert each URL at the front
        }

        return historyList;
    }
}