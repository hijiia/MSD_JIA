import assignment02.Book;
import assignment02.Library;
import assignment02.LibraryBook;
import assignment02.PhoneNumber;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    @Test
    public void testEmpty() {
        Library<String> lib = new Library<>();

        // by ISBN
        assertNull(lib.lookup(978037429279L));

        // checked out by a holder
        List<LibraryBook<String>> booksCheckedOut = lib.lookup("Jane Doe");
        assertEquals(((List<?>) booksCheckedOut).size(), 0);

        // checkout and checkin with an empty library
        assertFalse(lib.checkout(978037429279L, "Jane Doe", 1, 1, 2008));
        assertFalse(lib.checkin(978037429279L));
        assertFalse(lib.checkin("Jane Doe"));
    }

    @Test
    public void testNonEmpty() {
        var lib = new Library<String>();
        // test a small library
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        //  without checking out should return an empty list
        assertEquals(lib.lookup("Jane Doe").size(), 0);

        // Checkout a book
        var res = lib.checkout(9780330351690L, "Jane Doe", 1, 1, 2008);
        assertTrue(res);

        // Now, lookup should return the book checked out by "Jane Doe"
        var booksCheckedOut = lib.lookup("Jane Doe");
        assertEquals(booksCheckedOut.size(), 1);
        assertEquals(booksCheckedOut.get(0).getHolder(), "Jane Doe");

        // Create an expected due date for comparison (January 1st, 2008)
        GregorianCalendar expectedDueDate = new GregorianCalendar(2008, 0, 1);
        // Set the expected due date to match the format of the actual due date
        assertEquals(booksCheckedOut.get(0).getDueDate().get(Calendar.YEAR), expectedDueDate.get(Calendar.YEAR));
        assertEquals(booksCheckedOut.get(0).getDueDate().get(Calendar.MONTH), expectedDueDate.get(Calendar.MONTH));
        assertEquals(booksCheckedOut.get(0).getDueDate().get(Calendar.DAY_OF_MONTH), expectedDueDate.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testLargeLibrary() {
        Library<String> lib = new Library<>();

        // Simulate adding books from a file or other large data source
        lib.add(9780553804360L, "George Orwell", "1984");
        lib.add(9780679783272L, "Jane Austen", "Pride and Prejudice");
        lib.add(9780451524935L, "Harper Lee", "To Kill a Mockingbird");
        lib.add(9781400079988L, "J.K. Rowling", "Harry Potter and the Sorcerer's Stone");
        lib.add(9780375826697L, "J.R.R. Tolkien", "The Hobbit");

        // Test checkout for some of the books
        boolean res1 = lib.checkout(9780679783272L, "Alice", 2, 15, 2024);
        assertTrue(res1);
        boolean res2 = lib.checkout(9780451524935L, "Bob", 3, 10, 2024);
        assertTrue(res2);

        // Test lookup for a holder (should return books checked out by "Alice")
        ArrayList<LibraryBook<String>> booksAlice = (ArrayList<LibraryBook<String>>) lib.lookup("Alice");
        assertEquals(booksAlice.size(), 1);
        assertEquals(booksAlice.get(0).getHolder(), "Alice");

        // Create expected due date for "Alice" with fixed time zone (UTC)
        GregorianCalendar expectedDueDate = new GregorianCalendar(2024, 1, 15);
        expectedDueDate.setTimeZone(TimeZone.getTimeZone("UTC"));  // Set to UTC to avoid local time zone issues
        expectedDueDate.setLenient(false);  // Strict parsing

        // Ensure time is set to 00:00:00 (midnight)
        expectedDueDate.set(Calendar.HOUR_OF_DAY, 0);
        expectedDueDate.set(Calendar.MINUTE, 0);
        expectedDueDate.set(Calendar.SECOND, 0);
        expectedDueDate.set(Calendar.MILLISECOND, 0);

        // Compare the due date using getTime() to avoid internal state issues
        GregorianCalendar actualDueDate = booksAlice.get(0).getDueDate();
        actualDueDate.setTimeZone(TimeZone.getTimeZone("UTC"));  // Ensure actual due date is in UTC
        actualDueDate.set(Calendar.HOUR_OF_DAY, 0);  // Set time to 00:00:00 to ignore time portion

        assertEquals(actualDueDate.getTime(), expectedDueDate.getTime());

        // Test checkin for "Alice"
        boolean checkinResult = lib.checkin("Alice");
        assertTrue(checkinResult);

        // Test the status after checkin
        booksAlice = (ArrayList<LibraryBook<String>>) lib.lookup("Alice");
        assertEquals(booksAlice.size(), 0); // No books should be checked out by "Alice"
    }
    @Test
    public void stringLibraryTest() {
        // test a library that uses names (String) to id patrons
        Library<String> lib = new Library<>();
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        String patron1 = "Jane Doe";

        assertTrue(lib.checkout(9780330351690L, patron1, 1, 1, 2008));
        assertTrue(lib.checkout(9780374292799L, patron1, 1, 1, 2008));

        var booksCheckedOut1 = lib.lookup(patron1);
        assertEquals(booksCheckedOut1.size(), 2);
        assertTrue(booksCheckedOut1.contains(new Book(9780330351690L, "Jon Krakauer", "Into the Wild")));
        assertTrue(booksCheckedOut1.contains(new Book(9780374292799L, "Thomas L. Friedman", "The World is Flat")));
        assertEquals(booksCheckedOut1.get(0).getHolder(), patron1);

        // Set expected due date in the same time zone as the actual due date (America/Denver)
        TimeZone denverTimeZone = TimeZone.getTimeZone("America/Denver");
        GregorianCalendar expectedDueDate = new GregorianCalendar(2008, 0, 1); // January 1st, 2008
        expectedDueDate.setTimeZone(denverTimeZone);
        expectedDueDate.set(Calendar.HOUR_OF_DAY, 0);  // Set time to midnight
        expectedDueDate.set(Calendar.MINUTE, 0);
        expectedDueDate.set(Calendar.SECOND, 0);
        expectedDueDate.set(Calendar.MILLISECOND, 0);

        // Get actual due date from the booksCheckedOut list
        GregorianCalendar actualDueDate = booksCheckedOut1.get(0).getDueDate();
        actualDueDate.setTimeZone(denverTimeZone);
        actualDueDate.set(Calendar.HOUR_OF_DAY, 0);  // Set time to midnight
        actualDueDate.set(Calendar.MINUTE, 0);
        actualDueDate.set(Calendar.SECOND, 0);
        actualDueDate.set(Calendar.MILLISECOND, 0);

        // Compare the expected and actual due dates
        assertEquals(expectedDueDate.getTime(), actualDueDate.getTime());

        assertEquals(booksCheckedOut1.get(1).getHolder(), patron1);
        assertEquals(booksCheckedOut1.get(1).getDueDate(), expectedDueDate);

        assertTrue(lib.checkin(patron1));
    }

    @Test
    public void phoneNumberTest(){
        // test a library that uses phone numbers (PhoneNumber) to id patrons
        var lib = new Library<PhoneNumber>();
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        PhoneNumber patron2 = new PhoneNumber("801.555.1234");

        assertTrue(lib.checkout(9780330351690L, patron2, 1, 1, 2008));
        assertTrue(lib.checkout(9780374292799L, patron2, 1, 1, 2008));

        ArrayList<LibraryBook<PhoneNumber>> booksCheckedOut2 = (ArrayList<LibraryBook<PhoneNumber>>) (ArrayList<LibraryBook<PhoneNumber>>) lib.lookup(patron2);


        assertEquals(booksCheckedOut2.size(), 2);
        assertTrue(booksCheckedOut2.contains(new Book(9780330351690L, "Jon Krakauer", "Into the Wild")));
        assertTrue(booksCheckedOut2.contains(new Book(9780374292799L, "Thomas L. Friedman", "The World is Flat")));

        assertEquals(booksCheckedOut2.get(0).getHolder(), patron2);
        assertEquals(booksCheckedOut2.get(0).getDueDate().getTime(), new GregorianCalendar(2008, 0, 1).getTime());
        assertEquals(booksCheckedOut2.get(1).getHolder(), patron2);
        assertEquals(booksCheckedOut2.get(1).getDueDate().getTime(), new GregorianCalendar(2008, 0, 1).getTime());

        assertTrue(lib.checkin(patron2));

    }
}