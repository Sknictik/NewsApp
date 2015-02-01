package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android_news.newsapp.R;
import noveo.school.android.newsapp.fragment.NavigationDrawerFragment;
import noveo.school.android.newsapp.fragment.NewsEmptyFragment;
import noveo.school.android.newsapp.fragment.NewsTopicFragment;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.interfaces.RestClientCallbackForNewsOverview;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.ToastDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, RestClientCallbackForNewsOverview {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NewsTopicFragment newsOverviewFragment;

    private ToastDialog errorDialog;

    private Menu menu;

    //SavedInstanceState keys and values
    private final String ERROR_DIALOG_KEY = "noveo.school.android.newsapp.ERROR_DIALOG";
    private final String TITLE_KEY = "noveo.school.android.newsapp.TITLE";

    private String mTitle;

    public enum NewsTopic {MAIN, POLICY, TECH, CULTURE, SPORT}

    private static NewsTopic heading = NewsTopic.MAIN;

    private static List<ShortNewsEntry> newsList = new ArrayList<>();

    private static final Logger newsOverviewActivityLogger = LoggerFactory.getLogger(MainActivity.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            int errorNum = savedInstanceState.getInt(ERROR_DIALOG_KEY, -1);
            if (errorNum != -1) {
                showErrorDialog(RestClient.Error.values()[errorNum]);
            }

            mTitle = savedInstanceState.getString(TITLE_KEY, getResources().getString(R.string.title_main));
        }
        else {
            mTitle = getTitle().toString();
        }

        refreshActionBar();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        newsOverviewFragment = setNewsTopicFragment();
        //setLoadingState();

    }

    @Override
    public void onDestroy() {
        newsOverviewActivityLogger.trace("Cancel all tasks");
        RestClient.shutdownAll();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (errorDialog != null) {
            savedInstanceState.putInt(ERROR_DIALOG_KEY, errorDialog.getReason().ordinal());
        }
        savedInstanceState.putString(TITLE_KEY, mTitle);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        heading = NewsTopic.values()[position];
        mTitle = getResources().getStringArray(R.array.topics)[position];

        refreshActionBar();

        if (!newsList.isEmpty() && newsOverviewFragment.isAdded()){
            newsOverviewFragment.fillNewsGrid(heading, newsList);
        }

    }

    private void setEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        NewsEmptyFragment emptyFragment = new NewsEmptyFragment();

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

        if (menu != null) {
            menu.clear();
        }
    }

    private void restoreActionBar() {
        ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.INVISIBLE);

        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowCustomEnabled(false);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayUseLogoEnabled(true);
        bar.setTitle(mTitle);
        if (menu != null && !menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

    }

    @Override
    public void onLoadFinished(List<ShortNewsEntry> news) {
        newsList = news;
        restoreActionBar();
        newsOverviewFragment.fillNewsGrid(heading, newsList);
    }

    @Override
    public void onLoadFailed(RestClient.Error reason) {
        restoreActionBar();
        if (!newsList.isEmpty()) {
            newsOverviewFragment.fillNewsGrid(heading, newsList);
        }
        else if (!(getFragmentManager().findFragmentById(R.id.container)
                instanceof NewsEmptyFragment)) {
            setEmptyFragment();
        }

        showErrorDialog(reason);
    }

    @Override
    public void onLoadStart() {
        setLoadingState();
    }

    public void refreshActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(this.getResources().getIntArray(
                (R.array.newsActionBarColorsArray))[heading.ordinal()]));
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            final int displayOptions = getActionBar().getDisplayOptions();
            if ((displayOptions & ActionBar.DISPLAY_SHOW_CUSTOM) == 0) {
                getMenuInflater().inflate(R.menu.main, menu);
            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void refreshNews() {
        if (!(getFragmentManager().findFragmentById(R.id.container)
                instanceof NewsTopicFragment)) {
            newsOverviewFragment = setNewsTopicFragment();
        }
        else {
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
}
