package noveo.school.android.newsapp.retrofit.entities;

import java.util.Date;

/**
 * Created by Arseniy Nazarov on 15.01.2015.
 */
public class NewsEntry {

    private String id;
    private Date pubDate;
    private String title;
    private String[] topics;
    private boolean isFavouriteNews;

    protected NewsEntry(String id, Date pubDate, String title, String[] topics) {
        this.id = id;
        this.pubDate = pubDate;
        this.title = title;
        this.topics = topics;
    }

    protected NewsEntry(String id, Date pubDate, String title, String[] topics, boolean isFavouriteNews) {
        this.id = id;
        this.pubDate = pubDate;
        this.title = title;
        this.topics = topics;
        this.isFavouriteNews = isFavouriteNews;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public boolean isFavourite() {
        return isFavouriteNews;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavouriteNews = isFavourite;
    }

}
