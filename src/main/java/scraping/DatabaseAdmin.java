package scraping;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseAdmin {

    public void createJobsTable() throws SQLException {
            String createJobTable = """
                CREATE TABLE IF NOT EXISTS job_table (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title VARCHAR(100) NOT NULL,
                    company TEXT NOT NULL,
                    location VARCHAR(100) NOT NULL,
                    link TEXT NOT NULL,
                    UNIQUE(title, company, location)
                );
            """;

            String url = "jdbc:sqlite:src/main/java/database/test.db";
            Connection conn = DriverManager.getConnection(url);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createJobTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void processDatabaseChanges(ArrayList<jobCard> jobs) throws SQLException {

        String url = "jdbc:sqlite:src/main/java/database/test.db";
        Connection conn = DriverManager.getConnection(url);

        try{
            if (conn != null) {
                for (jobCard job : jobs) {

                    String checkQuery = "SELECT COUNT(*) FROM job_table WHERE title = ? AND company = ? AND location = ? AND link = ?";

                    try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                        checkStmt.setString(1, job.getTitle());
                        checkStmt.setString(2, job.getCompany());
                        checkStmt.setString(3, job.getLocation());
                        checkStmt.setString(4, job.getLink());

                        ResultSet rs = checkStmt.executeQuery();

                        if (rs.next() && rs.getInt(1) > 0) {
                            System.out.println("Duplicate found: " + job.getTitle() + " (" + job.getCompany() + ")");
                        } else {
                            System.out.println("No duplicate for: " + job.getTitle() + " (" + job.getCompany() + ")");
                            String insertQuery = "INSERT INTO job_table (title, company, location, link) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                insertStmt.setString(1, job.getTitle());
                                insertStmt.setString(2, job.getCompany());
                                insertStmt.setString(3, job.getLocation());
                                insertStmt.setString(4, job.getLink());
                                insertStmt.executeUpdate();
                                System.out.println("Data inserted successfully.");
                            }
                        }
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();

        }


    }

    public void clearJobTable() throws SQLException {

        String url = "jdbc:sqlite:src/main/java/database/test.db";
        Connection conn = DriverManager.getConnection(url);

        String deleteJobTable = """
                DELETE FROM `job_table`;
                DELETE FROM sqlite_sequence where name='job_table';
        """;


        try (Statement stmt = conn.createStatement()) {
            stmt.execute(deleteJobTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
