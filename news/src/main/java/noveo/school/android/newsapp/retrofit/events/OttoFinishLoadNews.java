package noveo.school.android.newsapp.retrofit.events;

import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;

import java.util.List;

public class OttoFinishLoadNews {

    private List<ShortNewsEntry> mNews;

    public OttoFinishLoadNews(List<ShortNewsEntry> news) {
        mNews = news;
    }

    public List<ShortNewsEntry> getNews() {
        return mNews;
    }
}
