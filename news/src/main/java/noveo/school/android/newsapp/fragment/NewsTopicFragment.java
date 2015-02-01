package noveo.school.android.newsapp.fragment;

/**
 * Created by Arseniy Nazarov on 21.01.2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android_news.newsapp.R;
import noveo.school.android.newsapp.activity.MainActivity;
import noveo.school.android.newsapp.activity.ReadNewsEntryActivity;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.adapter.ArrayAdapterForNewsGrid;

import java.util.ArrayList;
import java.util.List;


public class NewsTopicFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //ReadNewsEntryActivity keys
    public final static String TOPIC_NUM_KEY = "noveo.school.android.newsapp.TOPIC_NUM";
    public final static String TOPIC_KEY = "noveo.school.android.newsapp.TOPIC";

    public final static String NEWS_ENTRY_ID_KEY = "noveo.school.android.newsapp.NEWS_ID";
    public final static String NEWS_ENTRY_DATE_KEY = "noveo.school.android.newsapp.DATE";
    public final static String NEWS_ENTRY_TITLE_KEY = "noveo.school.android.newsapp.TITLE";
    public final static String NEWS_IS_FAVE_KEY = "noveo.school.android.newsapp.IS_FAVE";


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NewsTopicFragment newInstance() {
        NewsTopicFragment fragment = new NewsTopicFragment();
        return fragment;
    }

    public NewsTopicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_news_grid, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RestClient.downloadNews((MainActivity) getActivity());
    }

    public void fillNewsGrid(final MainActivity.NewsTopic heading, List<ShortNewsEntry> newsList) {
        final List<ShortNewsEntry> topicNews = new ArrayList<>();

        for (ShortNewsEntry entry : newsList) {
            String[] topics = entry.getTopics();
            for (String topic : topics) {
                if (heading.name().toLowerCase().equals(topic)) {
                    topicNews.add(entry);
                    break;
                }
            }
        }

        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.faveIcons);
        TypedArray colors = res.obtainTypedArray(R.array.newsHighlightColorsArray);

        Drawable faveIcon = icons.getDrawable(heading.ordinal());
        final int topicColor = colors.getColor(heading.ordinal(), 0);

        GridView gridView = (GridView) getView().findViewById(R.id.news_grid);

        gridView.setAdapter(new ArrayAdapterForNewsGrid(getActivity(), R.layout.news_cell,
                topicNews,
                faveIcon,
                topicColor));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent readNewsIntent = new Intent(getActivity(), ReadNewsEntryActivity.class);
                readNewsIntent.putExtra(TOPIC_NUM_KEY, heading.ordinal());
                readNewsIntent.putExtra(TOPIC_KEY, getActivity().getActionBar().getTitle());

                readNewsIntent.putExtra(NEWS_ENTRY_ID_KEY, topicNews.get(position).getId());
                readNewsIntent.putExtra(NEWS_ENTRY_DATE_KEY, topicNews.get(position).getPubDate());
                readNewsIntent.putExtra(NEWS_ENTRY_TITLE_KEY, topicNews.get(position).getTitle());
                readNewsIntent.putExtra(NEWS_IS_FAVE_KEY, topicNews.get(position).isFavourite());
                startActivity(readNewsIntent);
            }
        });
        getView().setBackgroundColor(Color.WHITE);
    }
}
