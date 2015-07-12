package noveo.school.android.newsapp;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;

/**
 * Stores application's current global states and objects
 */
public final class ApplicationState {

    private static NewsTopic heading = NewsTopic.MAIN;
    private static List<ShortNewsEntry> newsList = new ArrayList<>();
    private static Bus busInstance;

    private ApplicationState() { }

    public static NewsTopic getCurrentTopic() {
        return heading;
    }

    public static void setCurrentTopic(NewsTopic newHeading) {
        ApplicationState.heading = newHeading;
    }

    public static List<ShortNewsEntry> getNews() {
        return newsList;
    }

    public static void setNews(List<ShortNewsEntry> freshNewsList) {
        ApplicationState.newsList = freshNewsList;
    }

    public static synchronized Bus getBusInstance() {
        if (busInstance == null) {
            busInstance = new Bus();
        }
        return busInstance;
    }


}
