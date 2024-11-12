package assigment02;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Library<Type> {

  private List<LibraryBook<Type>> libraryBooks = new ArrayList<>();  // Use List instead of ArrayList

  // Add a book to the library
  public void add(long isbn, String author, String title) {
    libraryBooks.add(new LibraryBook<>(isbn, author, title));
  }


  // Lookup by ISBN
  public LibraryBook<Type> lookup(long isbn) {
    for (LibraryBook<Type> book : libraryBooks) {
      if (book.getIsbn() == isbn) {
        return book;
      }
    }
    return null;  // Return null if the book is not found
  }


public List<LibraryBook<Type>> lookup(Type holder) {
  List<LibraryBook<Type>> result = new ArrayList<>();
  for (LibraryBook<Type> book : libraryBooks) {
    if (book.getHolder() != null && book.getHolder().equals(holder)) {
      result.add(book);
    }
  }
  return result;  // Return empty if no books are checked out by the holder
}
  // Checkout a book by ISBN and assign it to a holder
  public boolean checkout(long isbn, Type holder, int month, int day, int year) {
    LibraryBook<Type> book = lookup(isbn);
    if (book != null && book.getHolder() == null) {
      book.setHolder(holder);
      book.setDueDate(new GregorianCalendar(year, month - 1, day));  // Month is 0-based in Calendar
      return true;
    }
    return false;
  }

  // Checkin a book by ISBN
  public boolean checkin(long isbn) {
    LibraryBook<Type> book = lookup(isbn);
    if (book != null && book.getHolder() != null) {
      book.setHolder(null);
      book.setDueDate(null);
      return true;
    }
    return false;
  }

  // Checkin all books by a specific holder
  public boolean checkin(Type holder) {
    List<LibraryBook<Type>> books = lookup(holder);
    if (books.isEmpty()) {
      return false;
    }
    for (LibraryBook<Type> book : books) {
      book.setHolder(null);
      book.setDueDate(null);
    }
    return true;
  }
}