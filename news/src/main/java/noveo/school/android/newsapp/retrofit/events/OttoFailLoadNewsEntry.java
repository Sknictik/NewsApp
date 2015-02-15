package noveo.school.android.newsapp.retrofit.events;

import noveo.school.android.newsapp.retrofit.service.RestClient;

public class OttoFailLoadNewsEntry {

    private RestClient.Error mError;

    public OttoFailLoadNewsEntry(RestClient.Error error) {
        mError = error;
    }

    public RestClient.Error getError() {
        return mError;
    }
}
