package noveo.school.android.newsapp.fragment;

/**
 * Created by Arseniy Nazarov on 21.01.2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewsTopicFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static boolean loadFromNet;
    private static List<ShortNewsEntry> newsList;
    private static MainActivity.NewsTopic heading;
    //ReadNewsEntryActivity keys
    public final static String TOPIC_COLOR_KEY = "noveo.school.android.newsapp.TOPIC_COLOR";
    public final static String NEWS_ID_KEY = "noveo.school.android.newsapp.NEWS_ID";

    private static final Logger newsOverviewFragmentLogger = LoggerFactory.getLogger(NewsTopicFragment.class);

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NewsTopicFragment newInstance(MainActivity.NewsTopic heading,
                                                List<ShortNewsEntry> newsList, boolean loadFromNet) {
        NewsTopicFragment fragment = new NewsTopicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, heading.ordinal());
        fragment.setArguments(args);
        NewsTopicFragment.loadFromNet = loadFromNet;
        NewsTopicFragment.newsList = newsList;
        NewsTopicFragment.heading = heading;
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
        //If news list is empty - load from site
        if (newsList.isEmpty() || loadFromNet) {
            downloadNewsInGrid((GridView) view);
        }
        else {
            fillNewsGrid((GridView) view);
        }
    }

    private void downloadNewsInGrid(final GridView gridview) {

        RestClient.get().getAllNews(new Callback<ShortNewsEntry[]>() {
            @Override
            public void success(ShortNewsEntry[] news, Response response) {
                newsList = Arrays.asList(news);
                fillNewsGrid(gridview);
                ((MainActivity) getActivity()).onLoadFinished(newsList);
                newsOverviewFragmentLogger.trace("News list downloaded from server");
            }

            @Override
            public void failure(RetrofitError error) {
                newsOverviewFragmentLogger.error("Error downloading news from server");
                error.printStackTrace();
                ((MainActivity) getActivity()).onLoadFailed();
            }
        });
    }

    private void fillNewsGrid(GridView gridview) {
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

        gridview.setAdapter(new ArrayAdapterForNewsGrid(getActivity(), R.layout.news_cell,
                topicNews,
                faveIcon,
                topicColor));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent readNewsIntent = new Intent(getActivity(), ReadNewsEntryActivity.class);
                readNewsIntent.putExtra(TOPIC_COLOR_KEY, topicColor);
                readNewsIntent.putExtra(NEWS_ID_KEY, topicNews.get(position).getId());
                startActivity(readNewsIntent);
            }
        });
    }

}
