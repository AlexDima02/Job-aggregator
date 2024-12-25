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

        try {

            String url = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?user=postgres.npwvjtzjdlqntviahmvq&password=y2jVFtwoz2mytgvg";
            Connection conn = DriverManager.getConnection(url);

            if (conn != null) {

                for (jobCard job : jobs) {

                    String checkQuery = "SELECT COUNT(*) FROM jobs WHERE job_title = ? AND job_company = ? AND job_location = ? AND job_link = ?";

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
                            String insertQuery = "INSERT INTO jobs (job_title, job_company, job_location, job_link) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                insertStmt.setString(1, job.getTitle());
                                insertStmt.setString(2, job.getCompany());
                                insertStmt.setString(3, job.getLocation());
                                insertStmt.setString(4, job.getLink());
                                insertStmt.executeUpdate();
                                System.out.println("Data inserted successfully.");
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }

            }

        } catch (SQLException e) {
           e.printStackTrace();
        }
    }
}
