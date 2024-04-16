package com.example.bbcfeedrss;

public class NewsItem {
    private int id;
    private String title;
    private String description;
    private String pubDate;
    private String mediaUrl;
    private String link;

    public NewsItem(int id, String title, String description, String pubDate, String mediaUrl, String link) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.mediaUrl = mediaUrl;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return title; // Return the title as the string representation of the NewsItem
    }
}
