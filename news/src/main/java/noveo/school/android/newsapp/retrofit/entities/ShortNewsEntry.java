package noveo.school.android.newsapp.retrofit.entities;

import java.util.Date;

/**
 * Created by Arseniy Nazarov on 08.01.2015.
 */
public class ShortNewsEntry {
    protected String id;
    protected String image;
    protected Date pubDate;
    protected String title;
    protected String[] topics;

    public ShortNewsEntry(String id, String image, Date pubDate, String title, String[] topics) {
        this.id = id;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
