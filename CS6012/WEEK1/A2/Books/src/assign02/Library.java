package assign02;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Scanner;


public class Library{

  private ArrayList<LibraryBook> library;

  public ArrayList<LibraryBook> getLibrary() {
    return library;
  }

  public Library() {
    library = new ArrayList<LibraryBook>();
  }


  public void add(long isbn, String author, String title) {
    library.add(new LibraryBook(isbn, author, title));
  }


  public void addAll(ArrayList<LibraryBook> list) {
    library.addAll(list);
  }


  public void addAll(String filename) {
    ArrayList<LibraryBook> toBeAdded = new ArrayList<LibraryBook>();

    try (Scanner fileIn = new Scanner(new File(filename))) {

      int lineNum = 1;

      while (fileIn.hasNextLine()) {
        String line = fileIn.nextLine();

        try (Scanner lineIn = new Scanner(line)) {
          lineIn.useDelimiter("\\t");

          if (!lineIn.hasNextLong()) {
            throw new ParseException("ISBN", lineNum);
          }
          long isbn = lineIn.nextLong();

          if (!lineIn.hasNext()) {
            throw new ParseException("Author", lineNum);
          }
          String author = lineIn.next();

          if (!lineIn.hasNext()) {
            throw new ParseException("Title", lineNum);
          }
          String title = lineIn.next();
          toBeAdded.add(new LibraryBook(isbn, author, title));
        }
        lineNum++;
      }
    } catch (FileNotFoundException e) {
      System.err.println(e.getMessage() + " Nothing added to the library.");
      return;
    } catch (ParseException e) {
      System.err.println(e.getLocalizedMessage() + " formatted incorrectly at line " + e.getErrorOffset()
          + ". Nothing added to the library.");
      return;
    }

    library.addAll(toBeAdded);
  }


  // Looks up the holder of a specific book by ISBN
  public String lookup(long isbn) {
    for (LibraryBook book : library) {
      if (book.getIsbn() == isbn) {
        return book.getHolder();
      }
    }
    return null; // Book not found
  }

  // Looks up all books checked out to a specific holder
  public ArrayList<LibraryBook> lookup(String holder) {
    ArrayList<LibraryBook> checkedOutBooks = new ArrayList<>();
    for (LibraryBook book : library) {
      if (holder.equals(book.getHolder())) {
        checkedOutBooks.add(book);
      }
    }
    return checkedOutBooks;
  }

  // Checks out a book to a holder with a specific due date
  public boolean checkout(long isbn, String holder, int month, int day, int year) {
    for (LibraryBook book : library) {
      if (book.getIsbn() == isbn && book.getHolder() == null) {
        book.checkout(holder, new GregorianCalendar(year, month - 1, day));
        return true;
      }
    }
    return false; // Book not found or already checked out
  }

  // Checks in a book by ISBN
  public boolean checkin(long isbn) {
    for (LibraryBook book : library) {
      if (book.getIsbn() == isbn && book.getHolder() != null) {
        book.checkin();
        return true;
      }
    }
    return false; // Book not found or already checked in
  }

  // Checks in all books checked out to a specific holder
  public boolean checkin(String holder) {
    boolean anyCheckedIn = false;
    for (LibraryBook book : library) {
      if (holder.equals(book.getHolder())) {
        book.checkin();
        anyCheckedIn = true;
      }
    }
    return anyCheckedIn;
  }
}