package noveo.school.android.newsapp.retrofit.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This object is an entity containing full information
 * about single news entry downloaded from server.
 */
public class FullNewsEntry extends NewsEntry implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FullNewsEntry> CREATOR = new Parcelable.Creator<FullNewsEntry>() {
        @Override
        public FullNewsEntry createFromParcel(Parcel in) {
            return new FullNewsEntry(in);
        }

        @Override
        public FullNewsEntry[] newArray(int size) {
            return new FullNewsEntry[size];
        }
    };
    private String[] images;
    private String html;

    protected FullNewsEntry(Parcel in) {
        super(in);
        images = new String[in.readInt()];
        in.readStringArray(images);
        html = in.readString();

    }

    public String[] getImages() {
        if (images == null) {
            return new String[]{};
        } else {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(images.length);
        dest.writeStringArray(images);
        dest.writeString(html);
    }
}
