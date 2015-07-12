package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.otto.Subscribe;

import android_news.newsapp.R;
import noveo.school.android.newsapp.ApplicationState;
import noveo.school.android.newsapp.NewsTopic;
import noveo.school.android.newsapp.fragment.NavigationDrawerFragment;
import noveo.school.android.newsapp.fragment.NewsEmptyFragment;
import noveo.school.android.newsapp.fragment.NewsTopicFragment;
import noveo.school.android.newsapp.retrofit.events.OttoFailLoadNews;
import noveo.school.android.newsapp.retrofit.events.OttoFinishLoadNews;
import noveo.school.android.newsapp.retrofit.events.OttoStartLoadNews;
import noveo.school.android.newsapp.retrofit.service.RestClient;

/**
 * Starting activity which shows all news related to current chosen topic.
 */
public class MainActivity extends AbstractErrorDialogActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String SHARED_PREFERENCE_NAME = "noveo.school.android.newsapp.NEWS_APP";
    // CR#2 (DONE) Your activity looks like a "God object". It knows too much.
    // Activity and news state (current topic, cached news) aren't connected.
    // Extract it into new class.
    // CR#1 (DONE) It's bad practice. Please do singleton class
    // which holds shared state (e.g. current topic, cached news)
    // And after that you don't need to pass
    // the current topic in ReadNewsEntryActivity

    /**
     * Fragment managing the behaviors, interactions and presentation
     * of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;
    private NewsTopicFragment newsOverviewFragment;
    private MenuItem refreshBtn;

    // CR#2 (DONE) The same as above. Activity and Bus are
    // different entities and they aren't connected.
    // You should create class which holds instance of Bus


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Register for server download events
        ApplicationState.getBusInstance().register(this);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            //  CR#1(DONE) keys for bundle or intents should be a public static constants
            //  CR#1(DONE) (move the key to class constant)
            Fragment onScreenFragment = getFragmentManager().findFragmentById(R.id.container);
            if (onScreenFragment instanceof NewsTopicFragment) {
                newsOverviewFragment = (NewsTopicFragment) onScreenFragment;
            }
        } else {
            ApplicationState.setCurrentTopic(NewsTopic.MAIN);
            newsOverviewFragment = setNewsTopicFragment();
        }
        refreshActionBar();
        navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);


        // Set up the drawer.
        navigationDrawerFragment.setup(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onDestroy() {
        ApplicationState.getBusInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer(Gravity.LEFT);
        } else if (getErrorDialog() != null && getErrorDialog().isVisible()) {
            getErrorDialog().dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        ApplicationState.setCurrentTopic(NewsTopic.values()[position]);

        refreshActionBar();

        if (!ApplicationState.getNews().isEmpty() && newsOverviewFragment.isAdded()) {
            newsOverviewFragment.fillNewsGrid(ApplicationState.getNews());
        }

    }

    private void setEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        NewsEmptyFragment emptyFragment = NewsEmptyFragment.newInstance(false);

        fragmentManager.beginTransaction()
                .replace(R.id.container, emptyFragment)
                .commit();
    }

    private NewsTopicFragment setNewsTopicFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        NewsTopicFragment fragment = NewsTopicFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        return fragment;
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
        newsOverviewFragment.cancelPullToRefreshLayoutRefresh();
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
    @SuppressWarnings("unused")
    @Subscribe
    public void onLoadFinished(OttoFinishLoadNews event) {
        ApplicationState.setNews(event.getNews());
        restoreActionBar();
        if (newsOverviewFragment.isAdded()) {
            newsOverviewFragment.fillNewsGrid(event.getNews());
        }

    }
    @SuppressWarnings("unused")
    @Subscribe
    public void onLoadFailed(OttoFailLoadNews event) {
        restoreActionBar();
        if (!ApplicationState.getNews().isEmpty()) {
            newsOverviewFragment.fillNewsGrid(ApplicationState.getNews());
        } else if (!(getFragmentManager().findFragmentById(R.id.container)
                instanceof NewsEmptyFragment)) {
            setEmptyFragment();
        }

        showErrorDialog(event.getError());
    }
    @SuppressWarnings("unused")
    @Subscribe
    public void onLoadStart(OttoStartLoadNews event) {
        setLoadingState();
    }

    public void refreshActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(
                getResources().getIntArray(R.array.newsActionBarColorsArray)[ApplicationState.getCurrentTopic().ordinal()]));
        actionBar.setTitle(getResources().getStringArray(R.array.topics)[ApplicationState.getCurrentTopic().ordinal()]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!navigationDrawerFragment.isDrawerOpen()) {
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
        if (!navigationDrawerFragment.isDrawerOpen()) {
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
            RestClient.downloadNews();
        }
    }

    @Override
    public void onRetryClick() {
        getErrorDialog().dismiss();
        refreshNews();
    }

}
