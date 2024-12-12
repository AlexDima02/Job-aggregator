package scraping;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {





    public static void main(String[] args) {

        Scraper browser = new Scraper();
        DatabaseAdmin db = new DatabaseAdmin();

        try{

            // Function that initialize the table
            // db.createJobsTable();
            // db.clearJobTable();
            // Class that scrapes
            browser.scrapeBestJobs(2);
            // Function that process data from the web scraper class
            // db.processDatabaseChanges(browser.jobCollection);
            //browser.scrapeBestJobs();
            //browser.scrapeIndeedJobs("Iasi");

        }catch(Exception e){}





    }
}