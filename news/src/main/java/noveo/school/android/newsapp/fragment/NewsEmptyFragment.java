package noveo.school.android.newsapp.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android_news.newsapp.R;

/**
 * Empty fragment is shown when there is nothing to display
 * to user for whatever reason. It can be used in
 * either MainActivity or ReadNewsEntryActivity.
 */
public class NewsEmptyFragment extends Fragment {

    public static final String IS_BACKGROUND_WHITE_PARAM =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.IS_BACKGROUND_WHITE_PARAM";

    private boolean isBackgroundWhite;

    //  CR#1 (DONE) use Fragment.setArguments(Bundle) for passing args into fragment
    public static NewsEmptyFragment newInstance(boolean isBackgroundWhite) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(NewsEmptyFragment.IS_BACKGROUND_WHITE_PARAM, isBackgroundWhite);
        NewsEmptyFragment emptyFragment = new NewsEmptyFragment();
        emptyFragment.setArguments(bundle);

        return emptyFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View root = inflater.inflate(R.layout.fragment_news_empty_layout, container, false);
        if (!isBackgroundWhite) {
            isBackgroundWhite = getArguments().getBoolean(IS_BACKGROUND_WHITE_PARAM);
        }
        if (isBackgroundWhite) {
            root.setBackgroundColor(Color.WHITE);
        } else {
            root.setBackgroundResource(R.drawable.bg_empty);
        }
        return root;
    }

}
