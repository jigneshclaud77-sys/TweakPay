package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Standalone JDBC utility (not a JUnit test) invoked by the /seed-user command.
 * Run via: mvnw.cmd test-compile
 *          mvnw.cmd org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass=com.example.demo.SeedUserRunner -Dexec.classpathScope=test
 */
public class SeedUserRunner {

    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3307/taskdb?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "secret";
    private static final String PLAIN_PASSWORD = "Password@1234";
    private static final String ROLE = "CUSTOMER";

    private static final String[] FIRST_NAMES = {
            "Rohan", "Amit", "Vikram", "Rajesh", "Sanjay", "Anil", "Deepak",
            "Jignesh", "Kiran", "Chirag", "Mihir", "Nikunj",
            "Karthik", "Arjun", "Venkatesh", "Naveen", "Praveen", "Suresh",
            "Debashish", "Anirban", "Sourav", "Partha",
            "Harpreet", "Gurpreet", "Manpreet"
    };

    private static final String[] LAST_NAMES = {
            "Parmar", "Patel", "Shah", "Mehta", "Trivedi",
            "Sharma", "Verma", "Gupta", "Kapoor", "Malhotra",
            "Iyer", "Nair", "Reddy", "Rao", "Menon", "Krishnan", "Pillai",
            "Banerjee", "Chatterjee", "Das", "Mukherjee",
            "Singh", "Joshi", "Kulkarni", "Deshpande"
    };

    public static void main(String[] args) throws Exception {
        Random rnd = new Random();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            String first = FIRST_NAMES[rnd.nextInt(FIRST_NAMES.length)];
            String last = LAST_NAMES[rnd.nextInt(LAST_NAMES.length)];
            String fullName = first + " " + last;

            String email = generateUniqueEmail(conn, rnd, first, last);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPassword = encoder.encode(PLAIN_PASSWORD);

            java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

            int newId = nextUserId(conn);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (id, full_name, email, password, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, newId);
                ps.setString(2, fullName);
                ps.setString(3, email);
                ps.setString(4, hashedPassword);
                ps.setDate(5, now);
                ps.setDate(6, now);
                ps.executeUpdate();
            }

            try (PreparedStatement rps = conn.prepareStatement(
                    "INSERT INTO user_roles (user_id, roles) VALUES (?, ?)")) {
                rps.setInt(1, newId);
                rps.setString(2, ROLE);
                rps.executeUpdate();
            }

            conn.commit();

            System.out.println("Seeded user successfully:");
            System.out.println("  ID:    " + newId);
            System.out.println("  Name:  " + fullName);
            System.out.println("  Email: " + email);
        }
    }

    private static String generateUniqueEmail(Connection conn, Random rnd, String first, String last) throws SQLException {
        for (int attempt = 0; attempt < 25; attempt++) {
            int suffix = 10 + rnd.nextInt(990);
            String email = (first + "." + last + suffix + "@gmail.com").toLowerCase();
            try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")) {
                check.setString(1, email);
                try (ResultSet rs = check.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        return email;
                    }
                }
            }
        }
        throw new IllegalStateException("Could not find a unique email after 25 attempts");
    }

    /**
     * users.id has no AUTO_INCREMENT (Hibernate manages it via the users_seq
     * table generator), so this mirrors that table-based hi/lo generator:
     * read the current value, claim it, and advance the counter by 1 so a
     * later Hibernate-driven insert never reuses an id claimed here.
     */
    private static int nextUserId(Connection conn) throws SQLException {
        try (PreparedStatement select = conn.prepareStatement("SELECT next_val FROM users_seq FOR UPDATE")) {
            try (ResultSet rs = select.executeQuery()) {
                rs.next();
                int nextId = rs.getInt(1);
                try (PreparedStatement update = conn.prepareStatement("UPDATE users_seq SET next_val = ?")) {
                    update.setInt(1, nextId + 1);
                    update.executeUpdate();
                }
                return nextId;
            }
        }
    }
}
