package assigment02;

import java.util.GregorianCalendar;

public class LibraryBook<Type> extends Book {

    private Type holder;
    private GregorianCalendar dueDate;

    public LibraryBook(long isbn, String author, String title) {
        super(isbn, author, title);
        this.holder = null;
        this.dueDate = null;
    }

    public Type getHolder() {
        return holder;
    }

    public GregorianCalendar getDueDate() {
        return dueDate;
    }

    public void setHolder(Type holder) {
        this.holder = holder;
    }

    public void setDueDate(GregorianCalendar dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LibraryBook)) {
            return false;
        }
        LibraryBook<?> otherBook = (LibraryBook<?>) other;
        return this.getIsbn() == otherBook.getIsbn();
    }

    @Override
    public String toString() {
        return super.toString() + " Holder: " + holder + " Due: " + dueDate;
    }
}