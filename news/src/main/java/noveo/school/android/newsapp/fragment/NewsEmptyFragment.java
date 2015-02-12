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

    // CR#1 use Fragment.setArguments(Bundle) for passing args into fragment
    public static NewsEmptyFragment newInstance(boolean isBackgroundWhite) {
        NewsEmptyFragment instance = new NewsEmptyFragment();
        instance.isBackgroundWhite = isBackgroundWhite;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View root = inflater.inflate(R.layout.fragment_news_empty_layout, container, false);
        if (isBackgroundWhite) {
            root.setBackgroundColor(Color.WHITE);
        } else {
            root.setBackgroundResource(R.drawable.bg_empty);
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

}
