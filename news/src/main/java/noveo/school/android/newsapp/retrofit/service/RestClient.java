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

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Arseniy Nazarov on 08.01.2015.
 */
public final class RestClient {

    private static final Logger REST_CLIENT_LOGGER = LoggerFactory.getLogger(RestClient.class);
    private static NewsAPI apiInstance;
    private static ExecutorService mExecutorService;

    private RestClient() {
    }

    private static void setupRestClient() {

        Gson gson = new GsonBuilder()
                .setDateFormat("dd MMM yyyy HH:mm:ss Z")
                .create();

        String root = "http://androidtraining.noveogroup.com/news/";
        mExecutorService = Executors.newCachedThreadPool();
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(root)
                .setConverter(new GsonConverter(gson))
                .setExecutors(mExecutorService, new MainThreadExecutor())
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();


        apiInstance = restAdapter.create(NewsAPI.class);
    }

    public static void shutdownAll() {
        mExecutorService.shutdownNow();
        apiInstance = null;
    }

    private static NewsAPI get() {
        if (apiInstance == null) {
            setupRestClient();
        }
        return apiInstance;
    }

    public static void downloadNews(final RestClientCallbackForNewsOverview caller) {
        caller.onLoadStart();

        RestClient.get().getAllNews(new Callback<ShortNewsEntry[]>() {
            @Override
            public void success(ShortNewsEntry[] news, Response response) {
                caller.onLoadFinished(Arrays.asList(news));
                REST_CLIENT_LOGGER.trace("News list downloaded from server");
            }

            @Override
            public void failure(RetrofitError error) {
                REST_CLIENT_LOGGER.trace("Error occurred while downloading list of news", error);
                Error restClientError = inspectError(error);
                caller.onLoadFailed(restClientError);
            }
        });
    }

    public static void downloadNewsEntry(final RestClientCallbackForNewsEntry caller, String newsId) {
        caller.onLoadStart();

        RestClient.get().getNewsById(newsId, new Callback<FullNewsEntry>() {
            @Override
            public void success(FullNewsEntry news, Response response) {

                caller.onLoadFinished(news);
                REST_CLIENT_LOGGER.trace("News entry " + news.getId() + " downloaded from server");
            }

            @Override
            public void failure(RetrofitError error) {
                REST_CLIENT_LOGGER.trace("Error occurred while downloading news by Id", error);
                Error restClientError = inspectError(error);
                caller.onLoadFailed(restClientError);
            }
        });
    }

    private static Error inspectError(RetrofitError error) {
        switch (error.getKind()) {
            case NETWORK:
                if (error.getCause() instanceof SocketTimeoutException) {
                    REST_CLIENT_LOGGER.error("Timeout error", error);
                    return Error.CONNECTION_TIMEOUT;
                } else {
                    REST_CLIENT_LOGGER.error("No connection error", error);
                    return Error.NO_CONNECTION;
                }
            case HTTP:
            case CONVERSION:
            case UNEXPECTED:
            default:
                REST_CLIENT_LOGGER.error("Unknown error occurred while loading news from server", error);
                return Error.UNKNOWN_ERROR;
        }
    }

    public enum Error {
        NO_CONNECTION,
        CONNECTION_TIMEOUT, UNKNOWN_ERROR
    }

}

