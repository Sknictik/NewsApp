package noveo.school.android.newsapp.retrofit.entities;

import java.util.Date;

/**
 * Created by Arseniy Nazarov on 08.01.2015.
 */
public class NewsEntry {
    private String id;
    private String html;
    private String[] images;
    private Date pubDate;
    private String title;
    private String[] topics;

    public NewsEntry(String id, String html, String[] images, Date pubDate, String title, String[] topics) {
        this.id = id;
        this.html = html;
        this.images = images;
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

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
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
