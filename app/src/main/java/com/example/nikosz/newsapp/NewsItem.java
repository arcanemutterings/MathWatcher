package com.example.nikosz.newsapp;

import java.util.Date;

public class NewsItem {
    // TODO
    private String mTitle;
    private String mSection;
    private Date mPublishDate;
    private String mAuthor;
    private String mUrl;

    public NewsItem(String mTitle, String mSection, Date mPublishDate, String mAuthor, String mUrl) {
        this.mTitle = mTitle;
        this.mSection = mSection;
        this.mPublishDate = mPublishDate;
        this.mAuthor = mAuthor;
        this.mUrl = mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public Date getPublishDate() {
        return mPublishDate;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getStringUrl() {
        return mUrl;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "mTitle='" + mTitle + '\'' +
                ", mSection='" + mSection + '\'' +
                ", mPublishDate=" + mPublishDate +
                ", mAuthor='" + mAuthor + '\'' +
                ", mUrl='" + mUrl + '\'' +
                '}';
    }
}
