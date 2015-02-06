package noveo.school.android.newsapp.retrofit.interfaces;

import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;

import java.util.List;

/**
 * Created by Arseniy Nazarov on 01.02.2015.
 */
public interface RestClientCallbackForNewsOverview {

    void onLoadFinished(List<ShortNewsEntry> news);

    void onLoadFailed(RestClient.Error reason);

    void onLoadStart();
}
