package noveo.school.android.newsapp.retrofit.service;

import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Arseniy Nazarov on 08.01.2015.
 */
public interface NewsAPI {
    @GET("/getAll")
    void getAllNews(Callback<ShortNewsEntry[]> news);

    @GET("/getById/{id}")
    void getNewsById(@Path("id") String newsId, Callback<FullNewsEntry> news);
}
