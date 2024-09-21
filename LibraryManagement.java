import java.sql.*;
import java.util.*;

public class LibraryManagement {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/library_mry";
    static final String USER = "root";
    static final String PASS = "YA11**sh";

    static Connection conn = null;
    static Statement stmt = null;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            createDatabaseAndTables();
            mainMenu();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private static void createDatabaseAndTables() throws SQLException {
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS library_mry");
        stmt.executeUpdate("USE library_mry");
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS books (Book_Code VARCHAR(100) PRIMARY KEY, Book_Name VARCHAR(100), Shelf_No VARCHAR(100), Genre VARCHAR(1000), Author_Name VARCHAR(100), No_Copies INT)");
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS issue (Name VARCHAR(100), Reg_No VARCHAR(100), Book_Code VARCHAR(100), Issue_Date DATE)");
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS back (Name VARCHAR(100), Reg_No VARCHAR(100), Book_Code VARCHAR(100), return_Date DATE)");
    }

    private static void mainMenu() {
        System.out.println("----------------------LIBRARY MANAGEMENT APPLICATION----------------------");
        System.out.println("1.ADD BOOK");
        System.out.println("2.ISSUE A BOOK");
        System.out.println("3.RETURN A BOOK");
        System.out.println("4.DISPLAY BOOKS");
        System.out.println("5.REPORT MENU");
        System.out.println("6.GENRE SEARCH");
        System.out.println("7.AUTHOR SEARCH");
        System.out.println("8.BOOK SEARCH");
        System.out.println("9.NO. OF COPIES");
        System.out.println("10.EXIT PROGRAM");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addBook();
                break;
            case 2:
                issueBook();
                break;
            case 3:
                returnBook();
                break;
            case 4:
                displayBooks();
                break;
            case 5:
                reportMenu();
                break;
            case 6:
                searchByGenre();
                break;
            case 7:
                searchByAuthor();
                break;
            case 8:
                searchByBookName();
                break;
            case 9:
                searchByCopies();
                break;
            case 10:
                System.out.println(
                        "------------------------------THANK YOU HAVE A GREAT DAY AHEAD-------------------------------");
                break;
            default:
                System.err.println(
                        "---------------------------------PLEASE TRY AGAIN-----------------------------------");
                mainMenu();
        }
    }

    private static void addBook() {
        try {
            System.out.print("Enter the genre of the book: ");
            String genre = scanner.nextLine();
            System.out.print("Enter Book Name: ");
            String bookName = scanner.nextLine();
            System.out.print("Enter Author's Name: ");
            String authorName = scanner.nextLine();
            System.out.print("Enter Book Code: ");
            String bookCode = scanner.nextLine();
            System.out.print("Enter Shelf No.: ");
            String shelfNo = scanner.nextLine();
            System.out.print("Enter number of copies: ");
            int noCopies = scanner.nextInt();
            scanner.nextLine();

            String sql = "INSERT INTO books VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookCode);
            pstmt.setString(2, bookName);
            pstmt.setString(3, shelfNo);
            pstmt.setString(4, genre);
            pstmt.setString(5, authorName);
            pstmt.setInt(6, noCopies);
            pstmt.executeUpdate();
            System.out.println("Book Added Successfully.......");

        } catch (SQLException e) {
            System.err.println("=====================BOOK CODE ALREADY EXISTS====================");
        }
        mainMenu();
    }

    private static void issueBook() {
        try {
            System.out.print("Enter Student Name: ");
            String studentName = scanner.nextLine();
            System.out.print("Enter Reg No: ");
            String regNo = scanner.nextLine();
            System.out.print("Enter Book Code: ");
            String bookCode = scanner.nextLine();
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String issueDate = scanner.nextLine();

            String sql = "SELECT no_copies FROM books WHERE Book_Code = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt("no_copies") < 1) {
                System.err.println("====================INSUFFICIENT BOOKS======================");
            } else {
                String updateSql = "UPDATE books SET no_copies = no_copies - 1 WHERE Book_Code = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, bookCode);
                pstmt.executeUpdate();

                String insertSql = "INSERT INTO issue VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, studentName);
                pstmt.setString(2, regNo);
                pstmt.setString(3, bookCode);
                pstmt.setDate(4, java.sql.Date.valueOf(issueDate));
                pstmt.executeUpdate();

                displayBooks();
                System.out.println("Book issued successfully to: " + studentName);
            }

        } catch (SQLException e) {
            System.err.println("========================BOOK DOESN'T EXIST==========================");
            System.err.println("=========================BOOK IS NOT ISSUED======================");
        }
        mainMenu();
    }

    private static void returnBook() {
        try {
            System.out.print("Enter Student Name: ");
            String studentName = scanner.nextLine();
            System.out.print("Enter Reg No.: ");
            String regNo = scanner.nextLine();
            System.out.print("Enter Book Code: ");
            String bookCode = scanner.nextLine();
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String returnDate = scanner.nextLine();

            String insertSql = "INSERT INTO back VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, studentName);
            pstmt.setString(2, regNo);
            pstmt.setString(3, bookCode);
            pstmt.setDate(4, java.sql.Date.valueOf(returnDate));
            pstmt.executeUpdate();

            String updateSql = "UPDATE books SET no_copies = no_copies + 1 WHERE Book_Code = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, bookCode);
            pstmt.executeUpdate();

            System.out.println("Book returned by: " + studentName);

        } catch (SQLException e) {
            System.err.println("=================PLEASE ENTER THE CORRECT DATE=====================");
            System.err.println("=======================BOOK IS NOT RETURNED======================");
        }
        mainMenu();
    }

    private static void displayBooks() {
        try {
            String sql = "SELECT * FROM books";
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.isBeforeFirst()) {
                System.err.println("=====================DATABASE IS EMPTY====================");
                System.err.println("================ENTER RECORDS IN DATABASE===================");
            } else {
                System.out.println(String.format("%-15s %-25s %-15s %-25s %-25s %-10s", "Book Code", "Book Name",
                        "Shelf No", "Genre", "Author Name", "No. Copies"));
                while (rs.next()) {
                    System.out.println(String.format("%-15s %-25s %-15s %-25s %-25s %-10d", rs.getString("Book_Code"),
                            rs.getString("Book_Name"), rs.getString("Shelf_No"), rs.getString("Genre"),
                            rs.getString("Author_Name"), rs.getInt("No_Copies")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        mainMenu();
    }

    private static void reportMenu() {
        System.out.println("=====================REPORT MENU=====================");
        System.out.println("1. ISSUED BOOKS");
        System.out.println("2. RETURNED BOOKS");
        System.out.println("3. GO BACK TO MAIN MENU");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                reportIssuedBooks();
                break;
            case 2:
                reportReturnedBooks();
                break;
            case 3:
                mainMenu();
                break;
            default:
                System.err.println("====================Please try again====================");
                reportMenu();
        }
    }

    private static void reportIssuedBooks() {
        try {
            String sql = "SELECT * FROM issue";
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.isBeforeFirst()) {
                System.err.println("======================DATABASE IS EMPTY====================");
                System.err.println("===================ENTER RECORDS IN DATABASE===================");
            } else {
                System.out
                        .println(String.format("%-25s %-25s %-25s %-25s", "Name", "Reg No", "Book Code", "Issue Date"));
                while (rs.next()) {
                    System.out.println(String.format("%-25s %-25s %-25s %-25s", rs.getString("Name"),
                            rs.getString("Reg_No"), rs.getString("Book_Code"), rs.getDate("Issue_Date").toString()));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        reportMenu();
    }

    private static void reportReturnedBooks() {
        try {
            String sql = "SELECT * FROM back";
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.isBeforeFirst()) {
                System.err.println("=======================DATABASE IS EMPTY====================");
                System.err.println("===================ENTER RECORDS IN DATABASE===================");
            } else {
                System.out.println(
                        String.format("%-25s %-25s %-25s %-25s", "Name", "Reg No", "Book Code", "Return Date"));
                while (rs.next()) {
                    System.out.println(String.format("%-25s %-25s %-25s %-25s", rs.getString("Name"),
                            rs.getString("Reg_No"), rs.getString("Book_Code"), rs.getDate("Return_Date").toString()));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        reportMenu();
    }

    private static void searchByGenre() {
        try {
            System.out.print("Enter the book genre you want to search: ");
            String genre = scanner.nextLine();

            String sql = "SELECT book_name, genre, author_name FROM books WHERE genre = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, genre);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.err.println("===================NO SUCH GENRE EXIST====================");
            } else {
                System.out.println(String.format("%-25s %-25s %-25s", "Book Name", "Genre", "Author Name"));
                while (rs.next()) {
                    System.out.println(String.format("%-25s %-25s %-25s", rs.getString("book_name"),
                            rs.getString("genre"), rs.getString("author_name")));
                }
            }

        } catch (SQLException e) {
            System.err.println("===================INVALID ENTRY====================");
        }
        mainMenu();
    }

    private static void searchByAuthor() {
        try {
            System.out.print("Enter the book author you want to search: ");
            String authorName = scanner.nextLine();

            String sql = "SELECT book_name, genre, author_name FROM books WHERE author_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, authorName);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.err.println("===================NO SUCH AUTHOR EXIST====================");
            } else {
                System.out.println(String.format("%-25s %-25s %-25s", "Book Name", "Genre", "Author Name"));
                while (rs.next()) {
                    System.out.println(String.format("%-25s %-25s %-25s", rs.getString("book_name"),
                            rs.getString("genre"), rs.getString("author_name")));
                }
            }

        } catch (SQLException e) {
            System.err.println("===================INVALID ENTRY====================");
        }
        mainMenu();
    }

    private static void searchByBookName() {
        try {
            System.out.print("Enter the book name you want to search: ");
            String bookName = scanner.nextLine();

            String sql = "SELECT book_name, genre, author_name FROM books WHERE book_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookName);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.err.println("===================NO SUCH BOOK EXIST====================");
            } else {
                System.out.println(String.format("%-25s %-25s %-25s", "Book Name", "Genre", "Author Name"));
                while (rs.next()) {
                    System.out.println(String.format("%-25s %-25s %-25s", rs.getString("book_name"),
                            rs.getString("genre"), rs.getString("author_name")));
                }
            }

        } catch (SQLException e) {
            System.err.println("===================INVALID ENTRY====================");
        }
        mainMenu();
    }

    private static void searchByCopies() {
        try {
            System.out.print("Enter the book name you want to search: ");
            String bookName = scanner.nextLine();

            String sql = "SELECT book_name, no_copies FROM books WHERE book_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookName);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.err.println("===================NO SUCH BOOK EXIST====================");
            } else {
                while (rs.next()) {
                    System.out.println("BOOK NAME: " + rs.getString("book_name"));
                    System.out.println("NUMBER OF COPIES: " + rs.getInt("no_copies"));
                }
            }

        } catch (SQLException e) {
            System.err.println("=======================INVALID ENTRY======================");
        }
        mainMenu();
    }
}
