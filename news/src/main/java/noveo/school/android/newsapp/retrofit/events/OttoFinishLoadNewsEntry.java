package noveo.school.android.newsapp.retrofit.events;

import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;

public class OttoFinishLoadNewsEntry {

    private FullNewsEntry mNewsEntry;

    public OttoFinishLoadNewsEntry(FullNewsEntry newsEntry) {
        mNewsEntry = newsEntry;
    }

    public FullNewsEntry getNewsEntry() {
        return mNewsEntry;
    }
}
