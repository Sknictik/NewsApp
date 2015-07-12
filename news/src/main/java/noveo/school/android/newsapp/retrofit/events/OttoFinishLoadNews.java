package noveo.school.android.newsapp.retrofit.events;

import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;

import java.util.List;

public class OttoFinishLoadNews {

    private final List<ShortNewsEntry> news;

    public OttoFinishLoadNews(List<ShortNewsEntry> news) {
        this.news = news;
    }

    public List<ShortNewsEntry> getNews() {
        return news;
    }
}
