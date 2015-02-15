package noveo.school.android.newsapp.retrofit.interfaces;

import noveo.school.android.newsapp.retrofit.events.OttoFailLoadNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoFinishLoadNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoStartLoadNewsEntry;

/**
 * Interface for the entity which wants a certain FullNewsEntry downloaded from server.
 */
public interface RestClientCallbackForNewsEntry {

    void onLoadFinished(OttoFinishLoadNewsEntry event);

    void onLoadFailed(OttoFailLoadNewsEntry event);

    void onLoadStart(OttoStartLoadNewsEntry event);

}
