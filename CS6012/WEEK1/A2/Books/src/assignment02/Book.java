package assigment02;


public class Book {

    private long isbn;

    private String author;

    private String title;

    public Book(long isbn, String author, String title) {
        this.isbn = isbn;
        this.author = author;
        this.title = title;
    }


    public String getAuthor() {
        return this.author;
    }


    public long getIsbn() {
        return this.isbn;
    }


    public String getTitle() {
        return this.title;
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true; // Same instance, no need for further checks
        }

        if (!(other instanceof Book)) {
            return false; // Not the same type, so not equal
        }

        Book otherBook = (Book) other;

        // Compare the fields: isbn, author, and title
        return this.isbn == otherBook.isbn &&
                this.author.equals(otherBook.author) &&
                this.title.equals(otherBook.title);
    }

    public String toString() {
        return isbn + ", " + author + ", \"" + title + "\"";
    }

    @Override
    public int hashCode() {
        return (int) isbn + author.hashCode() + title.hashCode();
    }
}