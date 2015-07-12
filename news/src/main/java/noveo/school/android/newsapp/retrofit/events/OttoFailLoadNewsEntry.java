package noveo.school.android.newsapp.retrofit.events;

import noveo.school.android.newsapp.retrofit.service.RestClient;

public class OttoFailLoadNewsEntry {

    private final RestClient.Error error;

    public OttoFailLoadNewsEntry(RestClient.Error error) {
        this.error = error;
    }

    public RestClient.Error getError() {
        return error;
    }
}
