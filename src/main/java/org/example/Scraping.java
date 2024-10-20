package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import java.util.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import org.jsoup.*;

public class Scraping {

    WebDriver driver = new ChromeDriver();
    ArrayList<jobCard> jobCollection = new ArrayList<jobCard>();
    int pages = 0;

    public void start(){


        // Start browser
        driver.get("https://ro.indeed.com/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait till you see search box on Indeed
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("text-input-where")));

        // Click on it and type the location where we want to get jobs and press ENTER
        element.click();
        new Actions(driver)
                .sendKeys(element, "brasov,romania")
                .sendKeys(Keys.ENTER)
                .perform();


        // Repeat
        // Wait 10 seconds, collect current jobs that browser sees
        while (pages < 5){
            try {

                Thread.sleep(2000);
                clickBtns();
                collectJobs();
                pages++;

            }catch (InterruptedException ignored){}

        }



    }

    public void clickBtns(){


        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));


        // Unable to locate close button of email
        // Overlaping elements prevent clicking the button

        try {

            if (driver.findElements(By.xpath("//button[contains(@aria-label, \"închidere\")]")).size() > 0 ) {

                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@aria-label, \"închidere\")]"))).click();
            }

            if (driver.findElements(By.xpath("//*[contains(@aria-label, 'Next Page')]")).size() > 0){
                WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(@aria-label, 'Next Page')]")));

                js.executeScript("arguments[0].click();", nextBtn);
            }

        }catch (Exception e) {}


    }

    public void collectJobs(){


//        List<WebElement> jobName = driver.findElements(By.cssSelector("h2.jobTitle a span"));
//        List<WebElement> jobCompany = driver.findElements(By.cssSelector("div.company_location span"));
//        List<WebElement> jobLink =driver.findElements(By.cssSelector("h2.jobTitle a"));
//        List<WebElement> jobDescription = driver.findElements(By.cssSelector("div.jobsearch-embeddedBody div.jobsearch-JobComponent-description div#jobDescriptionText div"));
//        WebElement jobTitle = driver.findElement(By.cssSelector("h2.jobTitle"));
//        //List<WebElement> jobDesc = driver.findElements(By.cssSelector("div#jobDescriptionTitle h2#jobDescriptionTitleHeading"));
//        // Extract from each job card all of its descriptions
//        for (int i =0; i < jobName.size(); i++){
//
//        }

        try{

            // Create a wait object to ensure the page and elements are fully loaded before interactions
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait until job postings are visible on the page
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".job_seen_beacon")));

            // Find all job listings on the page
            List<WebElement> jobListings = driver.findElements(By.cssSelector(".slider_container"));

            // Loop through each job listing and click it to load the job description in the right panel
            for (WebElement job : jobListings) {
                // Click on the job title to open the description in the right-side panel
                job.click();

                // Wait for the job description to load in the right panel
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("jobDescriptionText")));
                //myJobsStateDate

                // Extract the job info from the right-side panel or left panel
                System.out.println("Job Title: ");
                WebElement jobTitle = driver.findElement(By.cssSelector("h2.jobTitle a span"));
                System.out.println(jobTitle.getText());

                System.out.println("Job Company: ");
                WebElement jobCompany = driver.findElement(By.cssSelector("div.company_location span"));
                System.out.println(jobCompany.getText());

                System.out.println("Job Description: ");
                WebElement jobDescription = driver.findElement(By.id("jobDescriptionText"));
                System.out.println(jobDescription.getText());

                System.out.println("Job Link: ");
                WebElement jobLink = driver.findElement(By.cssSelector("h2.jobTitle a"));
                System.out.println(jobLink.getText());

                System.out.println("Job Location: ");
                WebElement jobLocation = driver.findElement(By.className("company_location"));
                System.out.println(jobLocation.getText());

//                System.out.println("Job Posting Date: ");
//                WebElement postingDate = driver.findElement(By.xpath("//*[@id='mosaic-provider-jobcards']/ul/li[2]/div/div/div/div/div/div/div[1]/div[2]/div/span[3]/span"));
//                System.out.println(postingDate.getText());

                // Optionally, you can add a small delay to avoid being flagged as a bot
                Thread.sleep(2000);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            driver.quit();
        }

    }

}
