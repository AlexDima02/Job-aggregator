package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class JobExtractionTest {
    public  void start(){


        // Initialize the WebDriver (Chrome in this case)
        WebDriver driver = new ChromeDriver();

        try {
            // Navigate to the Indeed job search results page
            String baseURL = "https://ro.indeed.com/jobs?q=&l=Romania&from=searchOnHP&vjk=eab90bbb32dee1d2&advn=7558139012226704";
            driver.get(baseURL);

            // Create a wait object to ensure the page and elements are fully loaded before interactions
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("onetrust-accept-btn-handler")));
            WebElement cookiePopup = driver.findElement(By.id("onetrust-accept-btn-handler"));

            if (cookiePopup.isDisplayed()) {
                cookiePopup.click();
            }



            // Wait until job postings are visible on the page
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.jobTitle")));

            // Find all job listings on the page
            List<WebElement> jobListings = driver.findElements(By.xpath("//h2[contains(@class, 'jobTitle')]"));

            // Loop through each job listing and click it to load the job description in the right panel
            for (WebElement job : jobListings) {
                // Click on the job title to open the description in the right-side panel
                job.click();

                // Wait for the job description to load in the right panel
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("jobDescriptionText")));

                // Extract the job description from the right-side panel
                WebElement jobDescription = driver.findElement(By.id("jobDescriptionText"));
                System.out.println("Job Description: ");
                System.out.println(jobDescription.getText());

                // Optionally, you can add a small delay to avoid being flagged as a bot
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }

    }
}
