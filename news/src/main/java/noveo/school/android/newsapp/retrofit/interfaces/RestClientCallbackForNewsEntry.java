package noveo.school.android.newsapp.retrofit.interfaces;

import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;

/**
 * Created by Arseniy Nazarov on 01.02.2015.
 */
public interface RestClientCallbackForNewsEntry {

    public void onLoadFinished(FullNewsEntry news);

    public void onLoadFailed(RestClient.Error reason);

    public void onLoadStart();

}
