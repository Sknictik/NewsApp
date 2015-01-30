package noveo.school.android.newsapp.retrofit.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;
import retrofit.converter.GsonConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Arseniy Nazarov on 08.01.2015.
 */
public class RestClient {

    private static NewsAPI REST_CLIENT;
    private static ExecutorService mExecutorService;
    //private static RestClient instance;

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

    public static NewsAPI get() {
        if (REST_CLIENT == null) {
            setupRestClient();
        }
        return REST_CLIENT;
    }
}

