package noveo.school.android.newsapp.retrofit.entities;

import java.util.Date;

/**
 * Created by Arseniy Nazarov on 15.01.2015.
 */
public abstract class NewsEntry {

    protected String id;
    protected Date pubDate;
    protected String title;
    protected String[] topics;
    protected boolean isFavourite = false;

    public NewsEntry(String id, Date pubDate, String title, String[] topics) {
        this.id = id;
        this.pubDate = pubDate;
        this.title = title;
        this.topics = topics;
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

    public boolean isFavourite() { return isFavourite; }

    public void setFavourite(boolean isFavourite) { this.isFavourite = isFavourite; }

}
