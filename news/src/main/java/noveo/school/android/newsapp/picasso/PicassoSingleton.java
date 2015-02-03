package noveo.school.android.newsapp.picasso;

import android.content.Context;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Arseniy Nazarov on 03.02.2015.
 */
public class PicassoSingleton {
    private static Picasso picasso;

    private PicassoSingleton() {}

    private static void setupPicasso(Context activity) {
        OkHttpClient okHttpClient = new OkHttpClient();

        picasso = new Picasso.Builder(activity)
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();
    }

    public static Picasso get(Context callerActivity) {
        if (picasso == null) {
            setupPicasso(callerActivity);
        }
        return picasso;
    }
}
