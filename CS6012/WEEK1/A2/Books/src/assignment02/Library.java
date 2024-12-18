package assignment02;
import java.util.*;

import static java.util.Collections.sort;

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
  protected class OrderByAuthor implements Comparator<LibraryBook<Type>> {
    @Override
    public int compare(LibraryBook<Type> lhs, LibraryBook<Type> rhs) {
      int authorComparison = lhs.getAuthor().compareTo(rhs.getAuthor());
      if (authorComparison != 0) {
        return authorComparison; // 如果作者不同，直接返回结果
      }
      // 如果作者相同，则按书名排序
      return lhs.getTitle().compareTo(rhs.getTitle());
    }
  }
  protected class OrderByDueDate implements Comparator<LibraryBook<Type>> {
    @Override
    public int compare(LibraryBook<Type> lhs, LibraryBook<Type> rhs) {
      if (lhs.getDueDate() == null && rhs.getDueDate() != null) {
        return 1; // 无到期日期的书籍被视为在有到期日期的书籍之后
      } else if (lhs.getDueDate() != null && rhs.getDueDate() == null) {
        return -1; // 有到期日期的书籍排在无到期日期的书籍之前
      } else if (lhs.getDueDate() == null && rhs.getDueDate() == null) {
        return 0; // 两本书都没有到期日期
      }
      return lhs.getDueDate().compareTo(rhs.getDueDate()); // 比较到期日期
    }
  }

  public ArrayList<LibraryBook<Type>> getOrderedByAuthor() {
    ArrayList<LibraryBook<Type>> libraryCopy = new ArrayList<>();
    libraryCopy.addAll(libraryBooks);
    OrderByAuthor comparator = new OrderByAuthor();
    sort(libraryCopy, comparator);
    return libraryCopy;
  }
  public ArrayList<LibraryBook<Type>> getOverdueList(int month, int day, int year) {
    ArrayList<LibraryBook<Type>> overdueList = new ArrayList<>();
    Date inputDate = new GregorianCalendar(year, month - 1, day).getTime();

    for (LibraryBook<Type> book : libraryBooks) {
      if (book.getDueDate() != null && book.getDueDate().before(inputDate)) {
        overdueList.add(book);
      }
    }

    OrderByDueDate comparator = new OrderByDueDate();
    sort(overdueList, comparator);
    return overdueList;
  }


}