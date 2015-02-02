package noveo.school.android.newsapp.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android_news.newsapp.R;

/**
 * Created by Arseniy Nazarov on 21.01.2015.
 */
public class NewsEmptyFragment extends Fragment {

    private boolean isBackgroundWhite;

    public NewsEmptyFragment() {}

    public static NewsEmptyFragment newInstance(boolean isBackgroundWhite) {
        NewsEmptyFragment instance = new NewsEmptyFragment();
        instance.isBackgroundWhite = isBackgroundWhite;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_news_empty_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (isBackgroundWhite) {
            getView().setBackgroundColor(Color.WHITE);
        }
    }

}
