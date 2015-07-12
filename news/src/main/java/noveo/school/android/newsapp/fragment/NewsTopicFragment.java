package noveo.school.android.newsapp.fragment;

/**
 * This fragment contains all news entries related to
 * current topic chosen by user. This fragment used in MainActivity
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android_news.newsapp.R;

import noveo.school.android.newsapp.ApplicationState;
import noveo.school.android.newsapp.NewsTopic;
import noveo.school.android.newsapp.NewsUtils;
import noveo.school.android.newsapp.activity.MainActivity;
import noveo.school.android.newsapp.activity.ReadNewsEntryActivity;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.adapter.ArrayAdapterForNewsGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class NewsTopicFragment extends Fragment {

    private static final int READ_NEWS_ENTRY_REQUEST = 1;  // The request code
    private final List<ShortNewsEntry> topicNews = new ArrayList<>();
    private GridView gridView;
    private SwipeRefreshLayout pullToRefreshLayout;

    /**
     * Returns a new instance of this fragment.
     */
    public static NewsTopicFragment newInstance() {
        return new NewsTopicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestClient.downloadNews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View root = inflater.inflate(R.layout.fragment_news_grid, container, false);
        if (gridView != null) {
            GridView newGrid = (GridView) root.findViewById(R.id.news_grid);
            newGrid.setAdapter(gridView.getAdapter());
            newGrid.setOnItemClickListener(gridView.getOnItemClickListener());
            gridView = newGrid;
        } else {
            gridView = (GridView) root.findViewById(R.id.news_grid);
            gridView.setBackgroundResource(R.drawable.bg_empty);
        }

        // Now find the PullToRefreshLayout to setup
        pullToRefreshLayout = (SwipeRefreshLayout) root;
        pullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RestClient.downloadNews();
            }
        });

        return root;
    }

    public void fillNewsGrid(List<ShortNewsEntry> newsList) {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);

        topicNews.clear();

        final NewsTopic heading = ApplicationState.getCurrentTopic();

        for (ShortNewsEntry entry : newsList) {
            String[] topics = entry.getTopics();
            for (String topic : topics) {
                if (heading.name().equalsIgnoreCase(topic)) {
                    //Check if news is stored in local database if so mark them as favourite
                    if (mPrefs.getString(entry.getId(), null) != null) {
                        entry.setIsFavourite(true);
                    }
                    topicNews.add(entry);
                    break;
                }
            }
        }

        Collections.sort(topicNews, new Comparator<ShortNewsEntry>() {
            //If newsEntry is more late then it should be lesser one
            @Override
            public int compare(ShortNewsEntry o1, ShortNewsEntry o2) {
                Long time1 = o1.getPubDate().getTime();
                Long time2 = o2.getPubDate().getTime();
                return (int) (time2 - time1);
            }
        });

        Resources res = getResources();
        TypedArray icons = NewsUtils.getTypedArray(res, R.array.faveIcons);
        TypedArray colors = NewsUtils.getTypedArray(res, R.array.newsHighlightColorsArray);

        Drawable faveIcon = icons.getDrawable(heading.ordinal());
        final int topicColor = colors.getColor(heading.ordinal(), 0);

        icons.recycle();
        colors.recycle();

        gridView.setAdapter(new ArrayAdapterForNewsGrid(getActivity(), R.layout.news_cell,
                topicNews,
                faveIcon,
                topicColor));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent readNewsIntent = ReadNewsEntryActivity.createIntent(getActivity(), topicNews.get(position));
                //  CR#1 (DONE) Good practice when you build
                // Intent in Activity which you need to show.
                // It makes the code more readable.
                // You should create static method in ReadNewsEntryActivity which create Intent
                // (like newInstance method in fragments)


                //  CR#1 (DONE) too enough parameters. please make
                // topicNews as Serializable or Parcelable
                // and pass just one object to the activity
                startActivityForResult(readNewsIntent, READ_NEWS_ENTRY_REQUEST);
            }
        });
        gridView.setBackgroundColor(Color.WHITE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == READ_NEWS_ENTRY_REQUEST && resultCode == Activity.RESULT_OK) {
            // Make sure the request was successful
            String id = data.getStringExtra(ReadNewsEntryActivity.ID_RESULT_KEY);
            boolean isFave = data.getBooleanExtra(ReadNewsEntryActivity.IS_FAVE_RESULT_KEY, false);
            for (ShortNewsEntry news : topicNews) {
                if (news.getId().equals(id)) {
                    news.setIsFavourite(isFave);
                    ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void cancelPullToRefreshLayoutRefresh() {
        if (pullToRefreshLayout.isRefreshing()) {
            pullToRefreshLayout.setRefreshing(false);
        }
    }
}
