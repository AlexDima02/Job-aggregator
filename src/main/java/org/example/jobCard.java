package org.example;

public class jobCard {
    private String title;
    private String company;
    private String link;
    private String description;

    public jobCard(final String title, final String company, final String link, final String description) {
        this.title = title;
        this.company = company;
        this.link = link;
        this.description = description;
    }

    public String getTitle() { return this.title; }
    public String getCompany() { return this.company; }
    public String getLink() { return this.link; }
    public String getDescription() { return this.description; }
}
