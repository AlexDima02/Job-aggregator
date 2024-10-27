package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

        // Insert your location
        sendKeys(element,"brasov",500);


        // Repeat
        // Wait 10 seconds, collect current jobs that browser sees
        while (pages < 5){
            try {

                Thread.sleep(2500);
                respondToEvents();
                collectJobs();
                pages++;

            }catch (InterruptedException ignored){
                respondToEvents();
            }

        }

        // Export data somewhere
        File csvOutputFile = new File("test");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            jobCollection.stream()
                    .map(this::con)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        driver.quit();

    }

    public void sendKeys(WebElement element, String keys, Integer delay) {

        try {
            for(String chr : keys.split(",")){
                Thread.sleep(delay);
                element.sendKeys(chr);
            }
            element.sendKeys(Keys.ENTER);
        }catch (Exception e) {
            // catching the exception
            System.out.println(e);
        }
    }

    public void respondToEvents(){


        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));


        // Unable to locate close button of email
        // Overlaping elements prevent clicking the button

        try {


            if (driver.findElements(By.xpath("//*[@id=\"onetrust-accept-btn-handler\"]")).size() > 0 ) {

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("onetrust-accept-btn-handler")));
                WebElement cookiePopup = driver.findElement(By.id("onetrust-accept-btn-handler"));
                cookiePopup.click();

            }

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

        try{

            // Create a wait object to ensure the page and elements are fully loaded before interactions
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

            // Wait until job postings are visible on the page
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.jobTitle")));


            // Find all job listings on the page
            List<WebElement> jobListings = driver.findElements(By.cssSelector("h2.jobTitle"));

            // Loop through each job listing and click it to load the job description in the right panel
            for (WebElement job : jobListings) {
                    // Click on the job title to open the description in the right-side panel
                    job.click();


                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.jobsearch-JobInfoHeader-title span")));
                    System.out.println(" ");
                     // Extract the job info from the right-side panel or left panel
                    System.out.println("Job Title: ");
                    WebElement jobTitle = driver.findElement(By.cssSelector("h2.jobsearch-JobInfoHeader-title span"));
                    System.out.println(jobTitle.getText());


                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.css-1saizt3 a")));
                    System.out.println(" ");
                    System.out.println("Job Company: ");
                    WebElement jobCompany = driver.findElement(By.cssSelector("span.css-1saizt3 a"));
                    System.out.println(jobCompany.getText());


                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("jobDescriptionText")));
                    System.out.println(" ");
                    System.out.println("Job Description: ");
                    WebElement jobDescription = driver.findElement(By.id("jobDescriptionText"));
                    System.out.println(jobDescription.getText());


                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.jobTitle a")));
                    System.out.println(" ");
                    System.out.println("Job Link: ");
                    WebElement jobLink = job.findElement(By.cssSelector("h2.jobTitle a"));
                    System.out.println(jobLink.getAttribute("href"));


                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#jobLocationText span")));
                    System.out.println(" ");
                    System.out.println("Job Location: ");
                    WebElement jobLocation = driver.findElement(By.cssSelector("div#jobLocationText span"));
                    System.out.println(jobLocation.getText());


                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"mosaic-provider-jobcards\"]/ul/li[1]/div/div/div/div/div/div/div[1]/div/div[1]/span[1]")));
                    System.out.println(" ");
                    System.out.println("Job Posting Date: ");
                    WebElement postingDate = driver.findElement(By.xpath("//*[@id=\"mosaic-provider-jobcards\"]/ul/li[1]/div/div/div/div/div/div/div[1]/div/div[1]/span[1]"));
                    System.out.println(postingDate.getText());

                    jobCollection.add(new jobCard(jobTitle.getText(), jobCompany.getText(), jobLink.getText(), jobCompany.getText()));

                    Thread.sleep(3000);

            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
