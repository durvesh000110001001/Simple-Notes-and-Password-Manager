import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NotesPasswordManager {

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:notes_passwords.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String notesTable = "CREATE TABLE IF NOT EXISTS notes (title TEXT PRIMARY KEY, content TEXT);";
        String passwordsTable = "CREATE TABLE IF NOT EXISTS passwords (service_name TEXT PRIMARY KEY, password TEXT);";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // Create tables if they do not exist
            stmt.execute(notesTable);
            stmt.execute(passwordsTable);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private static int getUserChoice(Scanner scanner) {
        int choice = -1;
        while (choice < 1 || choice > 5) {
            System.out.println("1. Add Note");
            System.out.println("2. Retrieve Note");
            System.out.println("3. Add Password");
            System.out.println("4. Retrieve Password");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
            }
        }
        return choice;
    }

    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int choice = getUserChoice(scanner);

            switch (choice) {
                case 1:
                    System.out.print("Enter the note title: ");
                    String noteTitle = scanner.nextLine();
                    System.out.print("Enter the note content: ");
                    String noteContent = scanner.nextLine();
                    addNote(noteTitle, noteContent);
                    break;
                case 2:
                    System.out.print("Enter the note title to retrieve: ");
                    noteTitle = scanner.nextLine();
                    retrieveNote(noteTitle);
                    break;
                case 3:
                    System.out.print("Enter the password service name: ");
                    String serviceName = scanner.nextLine();
                    System.out.print("Enter the password: ");
                    String password = scanner.nextLine();
                    addPassword(serviceName, hashPassword(password));
                    break;
                case 4:
                    System.out.print("Enter the password service name to retrieve: ");
                    serviceName = scanner.nextLine();
                    retrievePassword(serviceName);
                    break;
                case 5:
                    System.out.println("Exiting the program.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addNote(String title, String content) {
        if (title.isEmpty() || content.isEmpty()) {
            System.out.println("Note title and content cannot be empty.");
            return;
        }

        String sql = "INSERT INTO notes(title, content) VALUES(?, ?) ON CONFLICT(title) DO UPDATE SET content = excluded.content;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.executeUpdate();
            System.out.println("Note added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding note: " + e.getMessage());
        }
    }

    private static void retrieveNote(String title) {
        if (title.isEmpty()) {
            System.out.println("Note title cannot be empty.");
            return;
        }

        String sql = "SELECT content FROM notes WHERE title = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Note Content: " + rs.getString("content"));
            } else {
                System.out.println("Note not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving note: " + e.getMessage());
        }
    }

    private static void addPassword(String serviceName, String password) {
        if (serviceName.isEmpty() || password.isEmpty()) {
            System.out.println("Service name and password cannot be empty.");
            return;
        }

        String sql = "INSERT INTO passwords(service_name, password) VALUES(?, ?) ON CONFLICT(service_name) DO UPDATE SET password = excluded.password;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Password added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding password: " + e.getMessage());
        }
    }

    private static void retrievePassword(String serviceName) {
        if (serviceName.isEmpty()) {
            System.out.println("Service name cannot be empty.");
            return;
        }

        String sql = "SELECT password FROM passwords WHERE service_name = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Password: " + rs.getString("password"));
            } else {
                System.out.println("Password not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving password: " + e.getMessage());
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage());
        }
    }
}
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NotesPasswordManager {

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:notes_passwords.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String notesTable = "CREATE TABLE IF NOT EXISTS notes (title TEXT PRIMARY KEY, content TEXT);";
        String passwordsTable = "CREATE TABLE IF NOT EXISTS passwords (service_name TEXT PRIMARY KEY, password TEXT);";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // Create tables if they do not exist
            stmt.execute(notesTable);
            stmt.execute(passwordsTable);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private static int getUserChoice(Scanner scanner) {
        int choice = -1;
        while (choice < 1 || choice > 5) {
            System.out.println("1. Add Note");
            System.out.println("2. Retrieve Note");
            System.out.println("3. Add Password");
            System.out.println("4. Retrieve Password");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
            }
        }
        return choice;
    }

    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int choice = getUserChoice(scanner);

            switch (choice) {
                case 1:
                    System.out.print("Enter the note title: ");
                    String noteTitle = scanner.nextLine();
                    System.out.print("Enter the note content: ");
                    String noteContent = scanner.nextLine();
                    addNote(noteTitle, noteContent);
                    break;
                case 2:
                    System.out.print("Enter the note title to retrieve: ");
                    noteTitle = scanner.nextLine();
                    retrieveNote(noteTitle);
                    break;
                case 3:
                    System.out.print("Enter the password service name: ");
                    String serviceName = scanner.nextLine();
                    System.out.print("Enter the password: ");
                    String password = scanner.nextLine();
                    addPassword(serviceName, hashPassword(password));
                    break;
                case 4:
                    System.out.print("Enter the password service name to retrieve: ");
                    serviceName = scanner.nextLine();
                    retrievePassword(serviceName);
                    break;
                case 5:
                    System.out.println("Exiting the program.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addNote(String title, String content) {
        if (title.isEmpty() || content.isEmpty()) {
            System.out.println("Note title and content cannot be empty.");
            return;
        }

        String sql = "INSERT INTO notes(title, content) VALUES(?, ?) ON CONFLICT(title) DO UPDATE SET content = excluded.content;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.executeUpdate();
            System.out.println("Note added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding note: " + e.getMessage());
        }
    }

    private static void retrieveNote(String title) {
        if (title.isEmpty()) {
            System.out.println("Note title cannot be empty.");
            return;
        }

        String sql = "SELECT content FROM notes WHERE title = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Note Content: " + rs.getString("content"));
            } else {
                System.out.println("Note not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving note: " + e.getMessage());
        }
    }

    private static void addPassword(String serviceName, String password) {
        if (serviceName.isEmpty() || password.isEmpty()) {
            System.out.println("Service name and password cannot be empty.");
            return;
        }

        String sql = "INSERT INTO passwords(service_name, password) VALUES(?, ?) ON CONFLICT(service_name) DO UPDATE SET password = excluded.password;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Password added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding password: " + e.getMessage());
        }
    }

    private static void retrievePassword(String serviceName) {
        if (serviceName.isEmpty()) {
            System.out.println("Service name cannot be empty.");
            return;
        }

        String sql = "SELECT password FROM passwords WHERE service_name = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Password: " + rs.getString("password"));
            } else {
                System.out.println("Password not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving password: " + e.getMessage());
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage());
        }
    }
}
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NotesPasswordManager {

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:notes_passwords.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String notesTable = "CREATE TABLE IF NOT EXISTS notes (title TEXT PRIMARY KEY, content TEXT);";
        String passwordsTable = "CREATE TABLE IF NOT EXISTS passwords (service_name TEXT PRIMARY KEY, password TEXT);";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // Create tables if they do not exist
            stmt.execute(notesTable);
            stmt.execute(passwordsTable);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private static int getUserChoice(Scanner scanner) {
        int choice = -1;
        while (choice < 1 || choice > 5) {
            System.out.println("1. Add Note");
            System.out.println("2. Retrieve Note");
            System.out.println("3. Add Password");
            System.out.println("4. Retrieve Password");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
            }
        }
        return choice;
    }

    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int choice = getUserChoice(scanner);

            switch (choice) {
                case 1:
                    System.out.print("Enter the note title: ");
                    String noteTitle = scanner.nextLine();
                    System.out.print("Enter the note content: ");
                    String noteContent = scanner.nextLine();
                    addNote(noteTitle, noteContent);
                    break;
                case 2:
                    System.out.print("Enter the note title to retrieve: ");
                    noteTitle = scanner.nextLine();
                    retrieveNote(noteTitle);
                    break;
                case 3:
                    System.out.print("Enter the password service name: ");
                    String serviceName = scanner.nextLine();
                    System.out.print("Enter the password: ");
                    String password = scanner.nextLine();
                    addPassword(serviceName, hashPassword(password));
                    break;
                case 4:
                    System.out.print("Enter the password service name to retrieve: ");
                    serviceName = scanner.nextLine();
                    retrievePassword(serviceName);
                    break;
                case 5:
                    System.out.println("Exiting the program.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addNote(String title, String content) {
        if (title.isEmpty() || content.isEmpty()) {
            System.out.println("Note title and content cannot be empty.");
            return;
        }

        String sql = "INSERT INTO notes(title, content) VALUES(?, ?) ON CONFLICT(title) DO UPDATE SET content = excluded.content;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.executeUpdate();
            System.out.println("Note added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding note: " + e.getMessage());
        }
    }

    private static void retrieveNote(String title) {
        if (title.isEmpty()) {
            System.out.println("Note title cannot be empty.");
            return;
        }

        String sql = "SELECT content FROM notes WHERE title = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Note Content: " + rs.getString("content"));
            } else {
                System.out.println("Note not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving note: " + e.getMessage());
        }
    }

    private static void addPassword(String serviceName, String password) {
        if (serviceName.isEmpty() || password.isEmpty()) {
            System.out.println("Service name and password cannot be empty.");
            return;
        }

        String sql = "INSERT INTO passwords(service_name, password) VALUES(?, ?) ON CONFLICT(service_name) DO UPDATE SET password = excluded.password;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Password added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding password: " + e.getMessage());
        }
    }

    private static void retrievePassword(String serviceName) {
        if (serviceName.isEmpty()) {
            System.out.println("Service name cannot be empty.");
            return;
        }

        String sql = "SELECT password FROM passwords WHERE service_name = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Password: " + rs.getString("password"));
            } else {
                System.out.println("Password not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving password: " + e.getMessage());
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage());
        }
    }
}
