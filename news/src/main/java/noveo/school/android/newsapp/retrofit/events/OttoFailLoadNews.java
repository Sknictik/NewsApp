package noveo.school.android.newsapp.retrofit.events;

import noveo.school.android.newsapp.retrofit.service.RestClient;

public class OttoFailLoadNews {

    private RestClient.Error mError;

    public OttoFailLoadNews(RestClient.Error error) {
        mError = error;
    }

    public RestClient.Error getError() {
        return mError;
    }
}
