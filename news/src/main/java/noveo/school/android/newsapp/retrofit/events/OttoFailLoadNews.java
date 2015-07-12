package noveo.school.android.newsapp.retrofit.events;

import noveo.school.android.newsapp.retrofit.service.RestClient;

public class OttoFailLoadNews {

    private final RestClient.Error error;

    public OttoFailLoadNews(RestClient.Error error) {
        this.error = error;
    }

    public RestClient.Error getError() {
        return error;
    }
}
