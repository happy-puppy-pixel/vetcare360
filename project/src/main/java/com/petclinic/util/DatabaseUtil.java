package com.petclinic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vetcare360";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

// Create veterinarians table
            stmt.execute("CREATE TABLE IF NOT EXISTS veterinarians (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "first_name VARCHAR(100) NOT NULL, " +
                    "last_name VARCHAR(100) NOT NULL, " +
                    "specialization VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "schedule VARCHAR(100), " +
                    "is_available BOOLEAN DEFAULT TRUE, " +
                    "notes VARCHAR(500)" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS owners (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "first_name VARCHAR(100) NOT NULL, " +
                        "last_name VARCHAR(100) NOT NULL, " +
                        "phone VARCHAR(20) NOT NULL, " +
                        "email VARCHAR(100), " +
                        "address VARCHAR(255)" +
                        ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS veterinarians (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "first_name VARCHAR(100) NOT NULL, " +
                    "last_name VARCHAR(100) NOT NULL, " +
                    "specialization VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "schedule VARCHAR(100), " +
                    "is_available BOOLEAN DEFAULT TRUE, " +
                    "notes VARCHAR(500)" +
                    ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS pets (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "species VARCHAR(50) NOT NULL, " +
                        "breed VARCHAR(100), " +
                        "birth_date DATE, " +
                        "owner_id INT NOT NULL, " +
                        "notes VARCHAR(500), " +
                        "FOREIGN KEY (owner_id) REFERENCES owners(id)" +
                        ")");


            stmt.execute("CREATE TABLE IF NOT EXISTS appointments (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "pet_id INT NOT NULL, " +
                        "date_time TIMESTAMP NOT NULL, " +
                        "reason VARCHAR(255), " +
                        "diagnosis VARCHAR(500), " +
                        "treatment VARCHAR(500), " +
                        "notes VARCHAR(500), " +
                        "status VARCHAR(20) NOT NULL, " +
                        "FOREIGN KEY (pet_id) REFERENCES pets(id)" +
                        ")");


            stmt.execute("CREATE TABLE IF NOT EXISTS medical_records (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "pet_id INT NOT NULL, " +
                        "appointment_id INT, " +
                        "date_time TIMESTAMP NOT NULL, " +
                        "type VARCHAR(50) NOT NULL, " +
                        "description VARCHAR(500), " +
                        "treatment VARCHAR(500), " +
                        "notes VARCHAR(500), " +
                        "FOREIGN KEY (pet_id) REFERENCES pets(id), " +
                        "FOREIGN KEY (appointment_id) REFERENCES appointments(id)" +
                        ")");


            insertSampleData(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertSampleData(Connection conn) throws SQLException {

        if (!hasData(conn, "owners")) {

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("INSERT INTO owners (first_name, last_name, phone, email, address) VALUES " +
                            "('John', 'Doe', '555-123-4567', 'john.doe@email.com', '123 Main St'), " +
                            "('Jane', 'Smith', '555-987-6543', 'jane.smith@email.com', '456 Oak Ave'), " +
                            "('Robert', 'Johnson', '555-456-7890', 'robert.johnson@email.com', '789 Pine Rd')");
            }


            try (Statement stmt = conn.createStatement()) {
                stmt.execute("INSERT INTO pets (name, species, breed, birth_date, owner_id, notes) VALUES " +
                            "('Max', 'Dog', 'Labrador', '2018-06-15', 1, 'Friendly and energetic'), " +
                            "('Bella', 'Cat', 'Siamese', '2019-02-20', 2, 'Quiet and independent'), " +
                            "('Charlie', 'Dog', 'Beagle', '2020-04-10', 1, 'Loves to play fetch'), " +
                            "('Luna', 'Cat', 'Persian', '2017-11-05', 3, 'Requires regular grooming')");
            }


            try (Statement stmt = conn.createStatement()) {
                stmt.execute("INSERT INTO appointments (pet_id, date_time, reason, diagnosis, treatment, notes, status) VALUES " +
                            "(1, DATEADD('DAY', 1, CURRENT_DATE), 'Annual checkup', '', '', 'Routine examination', 'Scheduled'), " +
                            "(2, DATEADD('DAY', 2, CURRENT_DATE), 'Vaccination', '', '', 'Booster shots due', 'Scheduled'), " +
                            "(3, DATEADD('DAY', -10, CURRENT_DATE), 'Limping', 'Mild sprain', 'Rest and anti-inflammatory medication', 'Minor injury from playing', 'Completed'), " +
                            "(4, DATEADD('DAY', 5, CURRENT_DATE), 'Dental cleaning', '', '', 'Preventive dental care', 'Scheduled')");
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("INSERT INTO medical_records (pet_id, appointment_id, date_time, type, description, treatment, notes) VALUES " +
                            "(1, NULL, DATEADD('MONTH', -6, CURRENT_DATE), 'Vaccination', 'Annual vaccinations', 'DHPP, Rabies, Bordetella', 'All vaccines up to date'), " +
                            "(2, NULL, DATEADD('MONTH', -2, CURRENT_DATE), 'Examination', 'Routine checkup', 'None required', 'Healthy condition'), " +
                            "(3, 3, DATEADD('DAY', -10, CURRENT_DATE), 'Treatment', 'Sprained paw', 'Carprofen 25mg twice daily for 5 days', 'Follow-up in two weeks')");
            }
        }
    }

    private static boolean hasData(Connection conn, String table) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}