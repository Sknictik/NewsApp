package noveo.school.android.newsapp.retrofit.events;

import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;

public class OttoFinishLoadNewsEntry {

    private final FullNewsEntry newsEntry;

    public OttoFinishLoadNewsEntry(FullNewsEntry newsEntry) {
        this.newsEntry = newsEntry;
    }

    public FullNewsEntry getNewsEntry() {
        return newsEntry;
    }
}
