package noveo.school.android.newsapp.retrofit.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Common ancestor class for ShortNewsEntry and FullNewsEntry
 * used to store data downloaded from server.
 */
public class NewsEntry implements Parcelable {

    public static final Parcelable.Creator<NewsEntry> CREATOR = new Parcelable.Creator<NewsEntry>() {
        @Override
        public NewsEntry createFromParcel(Parcel in) {
            return new NewsEntry(in);
        }

        @Override
        public NewsEntry[] newArray(int size) {
            return new NewsEntry[size];
        }
    };
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

    protected NewsEntry(Parcel in) {
        id = in.readString();
        long tmpPubDate = in.readLong();
        pubDate = tmpPubDate != -1 ? new Date(tmpPubDate) : null;
        title = in.readString();
        topics = new String[in.readInt()];
        in.readStringArray(topics);
        isFavouriteNews = in.readByte() != 0x00;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(pubDate != null ? pubDate.getTime() : -1L);
        dest.writeString(title);
        dest.writeInt(topics.length);
        dest.writeStringArray(topics);
        dest.writeByte((byte) (isFavouriteNews ? 0x01 : 0x00));
    }
}
