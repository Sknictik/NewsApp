package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.ProgressBar;
import android_news.newsapp.R;
import noveo.school.android.newsapp.Utils;
import noveo.school.android.newsapp.fragment.NavigationDrawerFragment;
import noveo.school.android.newsapp.fragment.NewsEmptyFragment;
import noveo.school.android.newsapp.fragment.NewsTopicFragment;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.view.ToastDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private static ToastDialog errorDialog;

    private Menu menu;

    //SavedInstanceState keys and values
    private final String IS_ERROR_DIALOG_ON_SCREEN_KEY = "noveo.school.android.newsapp.IS_ERROR_DIALOG_ON_SCREEN";
    private final String IS_INITIAL_START_KEY = "noveo.school.android.newsapp.IS_INITIAL_START";
    private String mTitle;
    private final String TITLE_KEY = "noveo.school.android.newsapp.TITLE";
    private boolean initialLoading = true;

    public enum NewsTopic {MAIN, POLICY, TECH, CULTURE, SPORT}

    private static NewsTopic heading = NewsTopic.MAIN;

    private static List<ShortNewsEntry> newsList = new ArrayList<>();

    private static final Logger newsOverviewActivityLogger = LoggerFactory.getLogger(MainActivity.class);

    private boolean isEmptyListOnScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            boolean isErrorDialogOnScreen = savedInstanceState.getBoolean(IS_ERROR_DIALOG_ON_SCREEN_KEY, false);
            if (isErrorDialogOnScreen) {
                errorDialog = Utils.showErrorDialog(this);
            }
            initialLoading = savedInstanceState.getBoolean(IS_INITIAL_START_KEY, true);
            mTitle = savedInstanceState.getString(TITLE_KEY, getResources().getString(R.string.title_main));
            refreshActionBar();
        }
        else {
            mTitle = getTitle().toString();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_ERROR_DIALOG_ON_SCREEN_KEY, isErrorDialogOnScreen());
        savedInstanceState.putBoolean(IS_INITIAL_START_KEY, initialLoading);
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

        if (initialLoading) {
            initialLoading = false;
            if (checkWiFiConnection()) {
                setNewsTopicFragment(true);
            }
        }
        else if (!newsList.isEmpty()){
            setNewsTopicFragment(false);
        }

    }

    private boolean checkWiFiConnection() {

        NetworkInfo mWifi = ((ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE)).
                getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mWifi.isConnected()) {
            if (newsList.isEmpty() && !isEmptyListOnScreen) {
                setEmptyFragment();
            }
            if (errorDialog == null || !errorDialog.isShowing()) {
                errorDialog = Utils.showErrorDialog(this);
            }
            newsOverviewActivityLogger.error("Wifi offline. Aborting download.");
            return false;
        }
        return true;
    }


    private void setEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new NewsEmptyFragment())
                .commit();
        isEmptyListOnScreen = true;
    }

    private void setNewsTopicFragment(boolean loadFromNet) {
        if (loadFromNet) {
            setActionBarLoading();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, NewsTopicFragment.newInstance(heading, newsList, loadFromNet))
                .commit();
        isEmptyListOnScreen = false;
    }

    private void setActionBarLoading() {
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
        loadingBar.setVisibility(View.GONE);

        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowCustomEnabled(false);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayUseLogoEnabled(true);


        if (menu != null && !menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
    }

    public void onLoadFinished(List<ShortNewsEntry> news) {
        newsList = news;
        restoreActionBar();
        /*getActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);*/

    }

    public void onLoadFailed() {
        if (newsList.isEmpty() && !isEmptyListOnScreen) {
            setEmptyFragment();
        }
    }

    public void refreshActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(this.getResources().getIntArray(
                (R.array.newsActionBarColorsArray))[heading.ordinal()]));
        //actionBar.setDisplayShowTitleEnabled(true);
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

    public void onRetryClick(View v) {
        errorDialog.dismiss();
        refreshNews();
    }

    public void refreshNews() {
        setActionBarLoading();
        if (checkWiFiConnection()) {
            setNewsTopicFragment(true);
        }
        else {
            refreshActionBar();
        }
    }

    public static boolean isErrorDialogOnScreen() {
        return errorDialog != null && errorDialog.isShowing();
    }

    public static void setErrorDialog(ToastDialog errorDialog) {
        MainActivity.errorDialog = errorDialog;
    }
}
