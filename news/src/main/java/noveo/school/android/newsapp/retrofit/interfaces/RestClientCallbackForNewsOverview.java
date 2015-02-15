package noveo.school.android.newsapp.retrofit.interfaces;

import noveo.school.android.newsapp.retrofit.events.OttoFailLoadNews;
import noveo.school.android.newsapp.retrofit.events.OttoFinishLoadNews;
import noveo.school.android.newsapp.retrofit.events.OttoStartLoadNews;

/**
 * Interface for the entity which wants an array of news entry descriptions downloaded from server.
 */
public interface RestClientCallbackForNewsOverview {

    void onLoadFinished(OttoFinishLoadNews event);

    void onLoadFailed(OttoFailLoadNews event);

    void onLoadStart(OttoStartLoadNews event);
}
