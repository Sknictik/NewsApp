package noveo.school.android.newsapp.retrofit.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * This class used for objects containing small description of news entry.
 * Only arrays of this objects can be downloaded from server.
 */
public class ShortNewsEntry extends NewsEntry implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ShortNewsEntry> CREATOR = new Parcelable.Creator<ShortNewsEntry>() {
        @Override
        public ShortNewsEntry createFromParcel(Parcel in) {
            return new ShortNewsEntry(in);
        }

        @Override
        public ShortNewsEntry[] newArray(int size) {
            return new ShortNewsEntry[size];
        }
    };
    private String image;

    public ShortNewsEntry(String id, Date pubDate, String title, String image, String[] topics) {
        super(id, pubDate, title, topics);
        this.image = image;
    }

    protected ShortNewsEntry(Parcel in) {
        super(in);
        image = in.readString();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(image);
    }
}
