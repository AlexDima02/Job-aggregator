package scraping;

public class jobCard {
    private String title;
    private String company;
    private String link;
    private String location;


    public jobCard(final String title, final String company, final String location, final String link) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.link = link;
    }

    public String getTitle() { return this.title; }
    public String getCompany() { return this.company; }
    public String getLocation() { return this.location; }
    public String getLink() { return this.link; }
}
