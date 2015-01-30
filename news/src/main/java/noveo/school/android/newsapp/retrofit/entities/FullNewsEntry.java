package noveo.school.android.newsapp.retrofit.entities;

import java.util.Date;

/**
 * Created by Arseniy Nazarov on 23.01.2015.
 */
public class FullNewsEntry extends NewsEntry {

    private String[] images;
    private String html;

    public FullNewsEntry(String id, String html, String[] images, Date pubDate, String title, String[] topics) {
        super(id, pubDate, title, topics);
        this.images = images;
        this.html = html;
    }

    public String[] getImages() {
        if (images == null)
            return new String[]{};
        else {
            return images;
        }
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

}
