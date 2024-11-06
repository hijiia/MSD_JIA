package assign02;

import java.util.GregorianCalendar;

public class LibraryBook extends Book {
    private String holder;
    private GregorianCalendar dueDate;

    public LibraryBook(long isbn, String author, String title) {
        super(isbn, author, title);
        this.holder = null;
        this.dueDate = null;
    }

    // Returns the ISBN of this book
    public long getIsbn() {
        return super.getIsbn();
    }

    // Returns the holder of the book
    public String getHolder() {
        return holder;
    }

    // Returns the due date of the book
    public GregorianCalendar getDueDate() {
        return dueDate;
    }

    // Method to check out the book to a holder with a due date
    public void checkout(String holder, GregorianCalendar dueDate) {
        this.holder = holder;
        this.dueDate = dueDate;
    }

    // Method to check in the book (clears holder and due date)
    public void checkin() {
        this.holder = null;
        this.dueDate = null;
    }
}