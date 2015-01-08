package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android_news.newsapp.R;
import noveo.school.android.newsapp.fragment.NavigationDrawerFragment;
import noveo.school.android.newsapp.retrofit.entities.NewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.ArrayAdapterForNewsGrid;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private enum NewsTopic {MAIN, POLICY, TECH, CULTURE, SPORT}

    /**
     * Used to store current topic
     */
    private NewsTopic topic;

    /**
     * Used to store the last loaded news.
     */
    private List<NewsEntry> newsList = new ArrayList<NewsEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //If news list is empty load from site
        if (newsList == null) {
            RestClient.get().getAllNews(new Callback<NewsEntry[]>() {
                @Override
                public void success(NewsEntry[] news, Response response) {
                    for (NewsEntry entry : news) {
                        String[] topics = entry.getTopics();
                        for (String aTopic : topics) {
                            if (topic.name().toLowerCase().equals(aTopic)) {
                                newsList.add(entry);
                                break;
                            }
                        }
                    }
                /*Log.i("AppAPP", news[0].getId());
                Log.i("AppAPP", news[0].getTitle());*/
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getApplicationContext(),
                            "Не удается получить новости",
                            Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, NewsTopicFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        topic = NewsTopic.values()[number];
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_main);
                break;
            case 2:
                mTitle = getString(R.string.title_politics);
                break;
            case 3:
                mTitle = getString(R.string.title_tech);
                break;
            case 4:
                mTitle = getString(R.string.title_culture);
                break;
            case 5:
                mTitle = getString(R.string.title_sports);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class NewsTopicFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static NewsTopicFragment newInstance(int sectionNumber) {
            NewsTopicFragment fragment = new NewsTopicFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public NewsTopicFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_news_grid, container, false);
            GridView gridview = (GridView) rootView;
            gridview.setAdapter(new ArrayAdapterForNewsGrid(getActivity(), R.layout.news_cell,
                    new String[] {"date 1", "date 2", "date 3"},
                    new Drawable[] {getResources().getDrawable(R.drawable.ic_stub_loading),
                            getResources().getDrawable(R.drawable.ic_stub_loading),
                            getResources().getDrawable(R.drawable.ic_stub_loading)},
                    new String[]{"Новость 1", "Новость 2", "Новость 3"},
                    new Boolean[]{true, false, true},
                    getResources().getDrawable(R.drawable.ic_star_red),
                    getResources().getColor(R.color.main_news_color)));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
