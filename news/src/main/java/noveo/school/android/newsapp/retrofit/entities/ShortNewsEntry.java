package noveo.school.android.newsapp.retrofit.entities;

import java.util.Date;

/**
 * Created by Arseniy Nazarov on 08.01.2015.
 */
public class ShortNewsEntry extends NewsEntry {

    private String image;

    public ShortNewsEntry(String id, Date pubDate, String title, String image, String[] topics)
    {
        super(id, pubDate, title, topics);
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
