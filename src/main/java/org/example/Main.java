package org.example;

import org.openqa.selenium.WebElement;

import java.util.AbstractList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Scraping browser = new Scraping();
        browser.start();


        System.out.println("Hello world!");
    }
}