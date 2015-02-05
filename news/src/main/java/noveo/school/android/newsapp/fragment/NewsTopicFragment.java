package noveo.school.android.newsapp.fragment;

/**
 * Created by Arseniy Nazarov on 21.01.2015.
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
import noveo.school.android.newsapp.activity.MainActivity;
import noveo.school.android.newsapp.activity.ReadNewsEntryActivity;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.interfaces.RestClientCallbackForNewsOverview;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.adapter.ArrayAdapterForNewsGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class NewsTopicFragment extends Fragment implements RestClientCallbackForNewsOverview{

    final int READ_NEWS_ENTRY_REQUEST = 1;  // The request code

    private List<ShortNewsEntry> topicNews = new ArrayList<>();
    private GridView gridView;
    private SwipeRefreshLayout mPullToRefreshLayout;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestClient.downloadNews((MainActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View root = inflater.inflate(R.layout.fragment_news_grid, container, false);;
        if (gridView != null) {
            GridView newGrid = (GridView) root.findViewById(R.id.news_grid);
            newGrid.setAdapter(gridView.getAdapter());
            newGrid.setOnItemClickListener(gridView.getOnItemClickListener());
            gridView = newGrid;
        }
        else {
            gridView = (GridView) root.findViewById(R.id.news_grid);
            gridView.setBackgroundResource(R.drawable.bg_empty);
        }

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (SwipeRefreshLayout) root;
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RestClient.downloadNews(getThisInstance());
            }
        });

        return root;
    }

    public void fillNewsGrid(final MainActivity.NewsTopic heading, List<ShortNewsEntry> newsList) {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.shared_preference_name),
                Context.MODE_PRIVATE);

        topicNews.clear();

        for (ShortNewsEntry entry : newsList) {
            String[] topics = entry.getTopics();
            for (String topic : topics) {
                if (heading.name().toLowerCase().equals(topic)) {
                    //Check if news is stored in local database if so mark them as favourite
                    if (mPrefs.getString(entry.getId(), null) != null)
                    {
                        entry.setFavourite(true);
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
        TypedArray icons = res.obtainTypedArray(R.array.faveIcons);
        TypedArray colors = res.obtainTypedArray(R.array.newsHighlightColorsArray);

        Drawable faveIcon = icons.getDrawable(heading.ordinal());
        final int topicColor = colors.getColor(heading.ordinal(), 0);

        gridView.setAdapter(new ArrayAdapterForNewsGrid(getActivity(), R.layout.news_cell,
                topicNews,
                faveIcon,
                topicColor));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent readNewsIntent = new Intent(getActivity(), ReadNewsEntryActivity.class);
                readNewsIntent.putExtra(getString(R.string.news_topic_fragment_topic_num_key), heading.ordinal());
                readNewsIntent.putExtra(getString(R.string.news_topic_fragment_topic_key), getActivity().getActionBar().getTitle());

                readNewsIntent.putExtra(getString(R.string.news_topic_fragment_news_entry_id_key), topicNews.get(position).getId());
                readNewsIntent.putExtra(getString(R.string.news_topic_fragment_news_entry_date_key), topicNews.get(position).getPubDate());
                readNewsIntent.putExtra(getString(R.string.news_topic_fragment_news_entry_title_key), topicNews.get(position).getTitle());
                readNewsIntent.putExtra(getString(R.string.news_topic_fragment_news_is_fave_key), topicNews.get(position).isFavourite());
                startActivityForResult(readNewsIntent, READ_NEWS_ENTRY_REQUEST);
            }
        });
        gridView.setBackgroundColor(Color.WHITE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == READ_NEWS_ENTRY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                String id = data.getStringExtra(getString(R.string.read_news_entry_activity_id_result_key));
                boolean isFave = data.getBooleanExtra(getString(R.string.read_news_entry_activity_news_is_fave_result_key), false);
                for (ShortNewsEntry news : topicNews) {
                    if (news.getId().equals(id)) {
                        news.setFavourite(isFave);
                        ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onLoadFinished(List<ShortNewsEntry> news) {
        if (mPullToRefreshLayout.isRefreshing()) {
            mPullToRefreshLayout.setRefreshing(false);
        }
        ((MainActivity) getActivity()).onLoadFinished(news);
    }

    @Override
    public void onLoadFailed(RestClient.Error reason) {
        if (mPullToRefreshLayout.isRefreshing()) {
            mPullToRefreshLayout.setRefreshing(false);
        }
        ((MainActivity) getActivity()).onLoadFailed(reason);
    }

    @Override
    public void onLoadStart() {
        ((MainActivity) getActivity()).onLoadStart();
    }

    private NewsTopicFragment getThisInstance() {
        return NewsTopicFragment.this;
    }
}
