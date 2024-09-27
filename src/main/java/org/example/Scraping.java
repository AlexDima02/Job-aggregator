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


public class Scraping {

    WebDriver driver = new ChromeDriver();


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
        // Wait 10 seconds, collect current jobs that browser sees, click on the next button
        while (true){
            try {
                TimeUnit.SECONDS.sleep(5);
//                checkScrollStatus();
                clickBtns();
                collectJobs();

            }catch (InterruptedException ignored){

            }

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

        }
        catch (Exception e) {
        }







    }

    public void collectJobs(){

        List<WebElement> jobCard = driver.findElements(By.cssSelector("div.slider_item"));
        int totalCards = jobCard.size();

        List<WebElement> jobName = driver.findElements(By.cssSelector("a.jcs-JobTitle span"));
        List<WebElement> jobCompany = driver.findElements(By.cssSelector("span.css-63koeb"));
        List<WebElement> jobLink =driver.findElements(By.cssSelector("a.jcs-JobTitle"));
        List<WebElement> jobDescription = driver.findElements(By.id("div#jobDescriptionText"));

        System.out.println(jobCard.size());
        System.out.println(jobName.size());
        System.out.println(jobCompany.size());
        System.out.println(jobLink.size());

        // Extract from each job card all of its descriptions
        for (int i = 0; i < jobName.size(); i++){

            System.out.println(jobName.get(i).getAttribute("innerHTML"));

        }





    }

    public void checkScrollStatus(){
        JavascriptExecutor js = (JavascriptExecutor) driver;

        boolean reached = false;
        Object last_height = js.executeScript("return document.body.scrollHeight");

        while (!reached){

            WebElement element = driver.findElement(By.xpath("//body"));
            new Actions(driver)
                    .sendKeys(element, Keys.END)
                    .perform();

            Object new_height = js.executeScript("return document.body.scrollHeight");
            if (last_height.equals(new_height)){
                reached = true;
            }else {
                last_height = new_height;
            }

        }




    }

}
