package noveo.school.android.newsapp.retrofit.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.interfaces.RestClientCallbackForNewsEntry;
import noveo.school.android.newsapp.retrofit.interfaces.RestClientCallbackForNewsOverview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Arseniy Nazarov on 08.01.2015.
 */
public class RestClient {

    private static NewsAPI REST_CLIENT;
    private static ExecutorService mExecutorService;
    private static final Logger restClientLogger = LoggerFactory.getLogger(RestClient.class);

    public enum Error {NO_CONNECTION,
        CONNECTION_TIMEOUT, UNKNOWN_ERROR};

    private RestClient() {}

    private static void setupRestClient() {

        Gson gson = new GsonBuilder()
                .setDateFormat("dd MMM yyyy HH:mm:ss Z")
                .create();

        String ROOT = "http://androidtraining.noveogroup.com/news/";
        mExecutorService = Executors.newCachedThreadPool();
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setConverter(new GsonConverter(gson))
                .setExecutors(mExecutorService, new MainThreadExecutor())
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();


        REST_CLIENT = restAdapter.create(NewsAPI.class);
    }

    public static void shutdownAll() {
        mExecutorService.shutdownNow();
        REST_CLIENT = null;
    }

    private static NewsAPI get() {
        if (REST_CLIENT == null) {
            setupRestClient();
        }
        return REST_CLIENT;
    }

    public static void downloadNews(final RestClientCallbackForNewsOverview caller) {
       caller.onLoadStart();

       RestClient.get().getAllNews(new Callback<ShortNewsEntry[]>() {
            @Override
            public void success(ShortNewsEntry[] news, Response response) {
                caller.onLoadFinished(Arrays.asList(news));
                restClientLogger.trace("News list downloaded from server");
            }

            @Override
            public void failure(RetrofitError error) {
                restClientLogger.trace("Error occurred while downloading list of news");
                Error restClientError = inspectError(error);
                caller.onLoadFailed(restClientError);
                error.printStackTrace();
            }
        });
    }

    public static void downloadNewsEntry(final RestClientCallbackForNewsEntry caller, String newsId) {
        caller.onLoadStart();

        RestClient.get().getNewsById(newsId, new Callback<FullNewsEntry>() {
            @Override
            public void success(FullNewsEntry news, Response response) {

                caller.onLoadFinished(news);
                restClientLogger.trace("News entry " + news.getId() + " downloaded from server");
            }

            @Override
            public void failure(RetrofitError error) {
                restClientLogger.trace("Error occurred while downloading news by Id");
                Error restClientError = inspectError(error);
                caller.onLoadFailed(restClientError);
                error.printStackTrace();
            }
        });
    }

    private static Error inspectError(RetrofitError error) {
        switch (error.getKind()) {
            case HTTP: {
                // TODO get message from getResponse()'s body or HTTP status
                //String msg = error.getResponse().getBody().mimeType();
                restClientLogger.error("Connection timeout error");
                return Error.CONNECTION_TIMEOUT;
            }

            case NETWORK: {
                // TODO get message from getCause()'s message or just declare "network problem"
                //String msg = error.getResponse().getBody().mimeType();
                restClientLogger.error("No connection error");
                return Error.NO_CONNECTION;
            }

            case CONVERSION:
            case UNEXPECTED:
            default: {
                restClientLogger.error("Unknown error occurred while loading news from server");
                return Error.UNKNOWN_ERROR;
            }
        }
    }

}

