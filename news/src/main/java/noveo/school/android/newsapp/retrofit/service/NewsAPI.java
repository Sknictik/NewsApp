package noveo.school.android.newsapp.retrofit.service;

import noveo.school.android.newsapp.retrofit.entities.NewsEntry;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Arseniy Nazarov on 08.01.2015.
 */
public interface NewsAPI {
    @GET("/getAll")
    void getAllNews(Callback<NewsEntry[]> news);

    @GET("/getById")
    void getNewsById(@Query("id") String id, Callback<NewsEntry> news);
}
