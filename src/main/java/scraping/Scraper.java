package scraping;
import com.knifelish.tool.util.FakeUa;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


// Scraper class
    // jobCollection list that represents a collection of objects in the form of a job info
    // Function that resolve and check for eventual anti bot blockers
    // Scraping jobs from Indeed function
        // Navigate to the chosen source
        // Control how many pages do we want to scrape
        // How much should we scroll down or click the button to show more
    // Scraping jobs from Ejobs function
    // Scraping jobs from Bestjobs function

public class Scraper {


    ArrayList<jobCard> jobCollection = new ArrayList<jobCard> ();
    ArrayList<String> jobLinks = new ArrayList<String> ();
    final WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void utilityScroll(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions action = new Actions(driver);

        try{

            while (true) {

                action.sendKeys(Keys.PAGE_DOWN).perform();
                Thread.sleep(1500);
                long current_scroll_position = (long) js.executeScript("return window.pageYOffset + window.innerHeight;");
                long total_page_height = (long) js.executeScript("return document.body.scrollHeight;");

                if (current_scroll_position >= total_page_height) {
                    return;
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void scrapeIndeedJobs(String location) {

        //  Job-card: #mosaic-provider-jobcards
        //  Job title: ".jcs-JobTitle"
        //  Job link: ".jcs-JobTitle"[href]
        //  Job location: "text-location"[data-testid]
        //  Job company: "company-name"[data-testid]
        int pages = 1;
        ChromeOptions settings = new ChromeOptions();
        settings.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://ro.indeed.com/jobs?q=&l="+location);

        try {
            // Close cookie popup if present
            try {
                WebElement cookiePopup = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button#onetrust-accept-btn-handler")));
                if (cookiePopup.isDisplayed()) {
                    cookiePopup.click();
                }
            } catch (TimeoutException e) {
                System.out.println("Cookie popup not found or already closed.");
            }

            while (pages < 10) {
                System.out.println("Scraping page " + pages);


                // Wait until job postings are visible on the page
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.jobTitle")));


                // Find all job listings on the page
                List<WebElement> jobListings = driver.findElements(By.cssSelector("h2.jobTitle"));

                // Extract details from each job listing
                for (WebElement job : jobListings) {
                    System.out.println("Scraping page " + pages);
                    try{
                        job.click();
                        Thread.sleep(4500);
                    }catch (Exception e) {}



                    if(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.jobsearch-JobInfoHeader-title span"))).isDisplayed()){
                        System.out.println(" ");
                        // Extract the job info from the right-side panel or left panel
                        System.out.println("Job Title: ");
                        WebElement jobTitle = driver.findElement(By.cssSelector("h2.jobsearch-JobInfoHeader-title span"));
                        System.out.println(jobTitle.getText());
                    }


                    if(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.css-1saizt3 a"))).isDisplayed()){
                        System.out.println(" ");
                        System.out.println("Job Company: ");
                        WebElement jobCompany = driver.findElement(By.cssSelector("span.css-1saizt3 a"));
                        System.out.println(jobCompany.getText());
                    }


                    if(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2.jobTitle a"))).isDisplayed()){
                        System.out.println(" ");
                        System.out.println("Job Link: ");
                        WebElement jobLink = job.findElement(By.cssSelector("h2.jobTitle a"));
                        System.out.println(jobLink.getAttribute("href"));
                    }


                    if(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#jobLocationText span"))).isDisplayed()){
                        System.out.println(" ");
                        System.out.println("Job Location: ");
                        WebElement jobLocation = driver.findElement(By.cssSelector("div#jobLocationText span"));
                        System.out.println(jobLocation.getText());
                    }



                }

                // Check for and click the "Next" button, if it exists
                try {
                    WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='jobsearch-JapanPage']/div/div[5]/div/div[1]/nav/ul/li[6]/a")));
                    if (nextButton != null && nextButton.isDisplayed()) {
                        nextButton.click();
                        pages++; // Increment page count
                        Thread.sleep(2000);  // Wait for the next page to load
                    } else {
                        break; // No "Next" button found, exit the loop
                    }
                } catch (TimeoutException e) {
                    System.out.println("No more pages to scrape.");
                    break; // Exit the loop if there's no "Next" button
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the driver after scraping
//            driver.quit();
        }


    }


    // Scrape a custom number of pages
    public void scrapeEJobs(int options){


        int pages = 1;
        ChromeOptions settings = new ChromeOptions();
        settings.addArguments("--headless=new");
        settings.addArguments("--user-agent="+FakeUa.generateWindowsChromeUa());
        WebDriver driver = new ChromeDriver();
//        driver.get("https://www.ejobs.ro/locuri-de-munca/sort-publish/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        try {

            while (pages < options) {

                System.out.println("Scraping page " + pages);
                driver.get("https://www.ejobs.ro/locuri-de-munca/sort-publish/pagina"+pages);

                // Close cookie popup if present
                try {
                    WebElement cookiePopup = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"__nuxt\"]/div/div[6]/div/div[3]/button[2]")));
                    if (cookiePopup.isDisplayed()) {
                        cookiePopup.click();
                    }
                } catch (TimeoutException e) {
                    System.out.println("Cookie popup not found or already closed.");
                }

                WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"__nuxt\"]/div/div[4]/div[1]/div/div[2]/div[3]/a")));

                utilityScroll(driver);

                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("h2 a")));

                // Find all job listings on the page
                List<WebElement> jobs = driver.findElements(By.cssSelector("li.job-card-wrapper .job-card-content-middle__title a"));

                for (int i = 0; i < jobs.size(); i++) {
                    jobLinks.add(jobs.get(i).getAttribute("href"));
                }

                System.out.println(jobLinks.size());

                // Extract details from each job listing
                for (int i = 0; i < jobLinks.size(); i++) {
                            try {

                                Thread.sleep(500);

                                // Navigate to the link
                                driver.get(jobLinks.get(i));


                                // Get info from the current page
                                WebElement jobName = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"__nuxt\"]/div/div[4]/section/div[2]/aside/div[2]/div/div[1]/div/div[2]/h1")));
                                WebElement jobCompany = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"__nuxt\"]/div/div[4]/section/div[2]/aside/div[2]/div/div[1]/div/div[2]/div[1]/a")));
                                WebElement jobLocation = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"__nuxt\"]/div/div[4]/section/div[2]/aside/div[2]/div/div[2]/div[2]/div[2]/div/div/a")));
                                String jobLink = jobLinks.get(i);

                                System.out.println("Title of " + jobLinks.get(i) + ": " + jobName.getText());
                                System.out.println("Company of " + jobLinks.get(i) + ": " + jobCompany.getText());
                                System.out.println("Location of " + jobLinks.get(i) + ": " + jobLocation.getText());
                                System.out.println("Link of " + jobLinks.get(i) + ": " + jobLink);

                                jobCollection.add(new jobCard(jobName.getText(), jobCompany.getText(), jobLocation.getText(), jobLink));

                            } catch (Exception e) {}

                }

                driver.get("https://www.ejobs.ro/locuri-de-munca/sort-publish/pagina"+pages);
                jobLinks.clear();
                utilityScroll(driver);

                // Check for and click the "Next" button, if it exists
                try {
                    WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"__nuxt\"]/div/div[4]/div[1]/div/div[2]/div[3]/a")));
                    if (nextButton != null && nextButton.isDisplayed()) {
                        nextButton.click();
                        pages++; // Increment page count
                        Thread.sleep(2000);  // Wait for the next page to load
                    } else {
                        break; // No "Next" button found, exit the loop
                    }
                } catch (TimeoutException e) {
                    System.out.println("No more pages to scrape.");
                    break; // Exit the loop if there's no "Next" button
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the driver after scraping
            driver.quit();
        }
    }

    public void scrapeBestJobs(int options) throws Exception{
        int pages = 1;
        ChromeOptions settings = new ChromeOptions();
        settings.addArguments("--headless=new");
        settings.addArguments("--user-agent="+FakeUa.generateWindowsChromeUa());
        WebDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {

            while (pages < options) {

                System.out.println("Scraping page " + pages);
                driver.get("https://www.bestjobs.eu/locuri-de-munca/recent");

                // Close cookie popup if present
                try {
                    WebElement cookiePopup = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='headlessui-dialog-panel-:re:']/div/button[2]")));
                    if (cookiePopup.isDisplayed()) {
                        cookiePopup.click();
                    }
                } catch (TimeoutException e) {
                    System.out.println("Cookie popup not found or already closed.");
                }



                utilityScroll(driver);

                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.relative a.absolute")));

                // Find all job listings on the page
                List<WebElement> jobs = driver.findElements(By.cssSelector("div.relative a.absolute"));

                for (int i = 0; i < jobs.size(); i++) {
                    jobLinks.add(jobs.get(i).getAttribute("href"));
                }

                System.out.println(jobLinks.size());

                // Extract details from each job listing
                for (int i = 0; i <= jobLinks.size(); i++) {
                    try {

                        Thread.sleep(700);

                        // Navigate to the link
                        driver.get(jobLinks.get(i));


                        // Get info from the current page
                        WebElement jobName = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div/div[1]/div[1]/h2/a"));
                        WebElement jobCompany = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div/div[1]/div[1]/div[2]/div[2]/div[1]/h3/a"));
                        WebElement jobLocation = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div/div[1]/div[1]/div[1]/div[3]/div/span[1]/span/span/a"));
                        String jobLink = jobLinks.get(i);

                        System.out.println("Title of " + jobLinks.get(i) + ": " + jobName.getText());
                        System.out.println("Company of " + jobLinks.get(i) + ": " + jobCompany.getText());
                        System.out.println("Location of " + jobLinks.get(i) + ": " + jobLocation.getText());
                        System.out.println("Link of " + jobLinks.get(i) + ": " + jobLink);

                        jobCollection.add(new jobCard(jobName.getText(), jobCompany.getText(), jobLocation.getText(), jobLink));

                    } catch (Exception e) {}

                }

                driver.get("https://www.bestjobs.eu/locuri-de-munca/recent");
                utilityScroll(driver);
                jobLinks.clear();

                // Check for and click the "Next" button, if it exists
                try {
                    WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.my-8.text-center button")));
                    if (nextButton != null && nextButton.isDisplayed()) {
//                      nextButton.click();
                        js.executeScript("arguments[0].click();",nextButton);
                        pages++; // Increment page count
                        Thread.sleep(2000);  // Wait for the next page to load
                    } else {
                        break; // No "Next" button found, exit the loop
                    }
                } catch (TimeoutException e) {
                    System.out.println("No more pages to scrape.");
                    break; // Exit the loop if there's no "Next" button
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the driver after scraping
            driver.quit();
        }

    }

}




