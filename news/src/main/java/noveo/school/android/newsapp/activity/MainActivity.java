package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android_news.newsapp.R;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import noveo.school.android.newsapp.fragment.NavigationDrawerFragment;
import noveo.school.android.newsapp.fragment.NewsEmptyFragment;
import noveo.school.android.newsapp.fragment.NewsTopicFragment;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoFailLoadNews;
import noveo.school.android.newsapp.retrofit.events.OttoFinishLoadNews;
import noveo.school.android.newsapp.retrofit.events.OttoStartLoadNews;
import noveo.school.android.newsapp.retrofit.interfaces.RestClientCallbackForNewsOverview;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.ToastDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/*
 * Starting activity which shows all news related to current chosen topic.
 */
public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, RestClientCallbackForNewsOverview {

    public static final String ERROR_DIALOG_KEY = "noveo.school.android.newsapp.MainActivity.ERROR_DIALOG";
    public static final String TITLE_KEY = "noveo.school.android.newsapp.MainActivity.TITLE";
    public static final String SHARED_PREFERENCE_NAME = "noveo.school.android.newsapp.NEWS_APP";
    private static final Logger NEWS_OVERVIEW_ACTIVITY_LOGGER = LoggerFactory.getLogger(MainActivity.class);
    // CR#2 Your activity looks like a "God object". It knows too much.
    // Activity and news state (current topic, cached news) aren't connected.
    // Extract it into new class.
    //TODO CR#1 (DONE) It's bad practice. Please do singleton class which holds shared state (e.g. current topic, cached news)
    // And after that you don't need to pass the current topic in ReadNewsEntryActivity
    private static NewsTopic heading = NewsTopic.MAIN;
    private static List<ShortNewsEntry> newsList = new ArrayList<>();
    private static Bus busInstance;
    /**
     * Fragment managing the behaviors, interactions and presentation
     * of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NewsTopicFragment newsOverviewFragment;
    private ToastDialog errorDialog;
    private MenuItem refreshBtn;

    public static NewsTopic getCurrentTopic() {
        return heading;
    }

    // CR#2 The same as above. Activity and Bus are different entities and they aren't connected. You should create class which holds instance of Bus
    public static Bus getBusInstance() {
        if (busInstance == null) {
            busInstance = new Bus();
        }
        return busInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Register for server download events
        getBusInstance().register(this);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // TODO CR#1(DONE) keys for bundle or intents should be a public static constants
            int errorNum = savedInstanceState.getInt(ERROR_DIALOG_KEY, -1);
            if (errorNum != -1) {
                showErrorDialog(RestClient.Error.values()[errorNum]);
            }
            // TODO CR#1(DONE) (move the key to class constant)
            heading = NewsTopic.values()[savedInstanceState.getInt(TITLE_KEY, 0)];
            Fragment onScreenFragment = getFragmentManager().findFragmentById(R.id.container);
            if (onScreenFragment instanceof NewsTopicFragment) {
                newsOverviewFragment = (NewsTopicFragment) onScreenFragment;
            }
        } else {
            heading = NewsTopic.MAIN;
            newsOverviewFragment = setNewsTopicFragment();
        }
        refreshActionBar();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);


        // Set up the drawer.
        mNavigationDrawerFragment.setup(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onDestroy() {
        //NEWS_OVERVIEW_ACTIVITY_LOGGER.trace("Cancel all tasks");
        //RestClient.shutdownAll();
        getBusInstance().unregister(this);
        if (errorDialog != null) {
            errorDialog.dismiss();
            errorDialog = null;
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (errorDialog != null) {
            savedInstanceState.putInt(ERROR_DIALOG_KEY,
                    errorDialog.getReason().ordinal());
        }
        savedInstanceState.putInt(TITLE_KEY, heading.ordinal());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        heading = NewsTopic.values()[position];

        refreshActionBar();

        if (!newsList.isEmpty() && newsOverviewFragment.isAdded()) {
            newsOverviewFragment.fillNewsGrid(newsList);
        }

    }

    private void setEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        NewsEmptyFragment emptyFragment = NewsEmptyFragment.newInstance();
        Bundle mBundle = new Bundle();
        mBundle.putBoolean(NewsEmptyFragment.IS_BACKGROUND_WHITE_PARAM, false);
        emptyFragment.setArguments(mBundle);

        fragmentManager.beginTransaction()
                .replace(R.id.container, emptyFragment)
                .commit();
    }

    private NewsTopicFragment setNewsTopicFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        NewsTopicFragment mFragment = NewsTopicFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();
        return mFragment;
    }

    private void setLoadingState() {
        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_loading);
        bar.setDisplayShowHomeEnabled(false);
        ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.VISIBLE);

        if (refreshBtn != null) {
            refreshBtn.setVisible(false);
        }
    }

    private void restoreActionBar() {
        ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.GONE);

        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowCustomEnabled(false);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayUseLogoEnabled(true);

        if (refreshBtn != null) {
            refreshBtn.setVisible(true);
            invalidateOptionsMenu();
        }

    }

    @Override
    @Subscribe
    public void onLoadFinished(OttoFinishLoadNews event) {
        newsList = event.getNews();
        restoreActionBar();
        if (newsOverviewFragment.isAdded()) {
            newsOverviewFragment.fillNewsGrid(newsList);
        }
    }

    @Override
    @Subscribe
    public void onLoadFailed(OttoFailLoadNews event) {
        restoreActionBar();
        if (!newsList.isEmpty()) {
            newsOverviewFragment.fillNewsGrid(newsList);
        } else if (!(getFragmentManager().findFragmentById(R.id.container)
                instanceof NewsEmptyFragment)) {
            setEmptyFragment();
        }

        showErrorDialog(event.getError());
    }

    @Override
    @Subscribe
    public void onLoadStart(OttoStartLoadNews event) {
        setLoadingState();
    }

    public void refreshActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(
                getResources().getIntArray(R.array.newsActionBarColorsArray)[heading.ordinal()]));
        actionBar.setTitle(getResources().getStringArray(R.array.topics)[heading.ordinal()]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        refreshBtn = menu.findItem(R.id.refresh_btn);
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            final int displayOptions = getActionBar().getDisplayOptions();

            if ((displayOptions & ActionBar.DISPLAY_SHOW_CUSTOM) != 0) {
                refreshBtn.setVisible(false);
            } else {
                refreshBtn.setVisible(true);
            }

            return true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void refreshNews() {
        if (!(getFragmentManager().findFragmentById(R.id.container)
                instanceof NewsTopicFragment)) {
            newsOverviewFragment = setNewsTopicFragment();
        } else {
            RestClient.downloadNews(this);
        }
    }

    public void onRetryClick(View v) {
        errorDialog.dismiss();
        refreshNews();
    }

    private void showErrorDialog(RestClient.Error reason) {
        if (errorDialog != null) {
            errorDialog.dismiss();
        }

        errorDialog = new ToastDialog(this, reason);
        errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                errorDialog = null;
            }
        });

        errorDialog.show();
    }

    public enum NewsTopic { MAIN, POLICY, TECH, CULTURE, SPORT }
}
