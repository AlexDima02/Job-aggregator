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

            // Connection to our database


            // Class that scrapes
//            browser.scrapeBestJobs(10);
            browser.scrapeEJobs(10);
//            db.processDatabaseChanges(browser.jobCollection);

//            browser.scrapeEJobs(2);
//            db.processDatabaseChanges(browser.jobCollection);

        }catch(Exception e){}





    }
}