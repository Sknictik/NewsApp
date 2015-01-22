package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android_news.newsapp.R;
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

    private ToastDialog errorDialog;

    //SavedInstanceState keys and values
    private String mTitle;
    private final String TITLE_KEY = "noveo.school.android.newsapp.TITLE";
    private static boolean isErrorDialogOnScreen = false;
    private final String IS_ERROR_DIALOG_ON_SCREEN_KEY = "noveo.school.android.newsapp.IS_ERROR_DIALOG_ON_SCREEN";

    public enum NewsTopic {MAIN, POLICY, TECH, CULTURE, SPORT}

    private static NewsTopic heading = NewsTopic.MAIN;

    private static List<ShortNewsEntry> newsList = new ArrayList<>();

    private static final Logger newsOverviewActivityLogger = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            isErrorDialogOnScreen = savedInstanceState.getBoolean(IS_ERROR_DIALOG_ON_SCREEN_KEY, false);
            if (isErrorDialogOnScreen) {
                showErrorDialog();
            }
            mTitle = savedInstanceState.getString(TITLE_KEY, getResources().getString(R.string.title_main));
            refreshActionBar();
        }
        else {
            mTitle = getTitle().toString();
        }

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_ERROR_DIALOG_ON_SCREEN_KEY, isErrorDialogOnScreen);
        savedInstanceState.putString(TITLE_KEY, mTitle);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
        heading = NewsTopic.values()[position];
        switch (position) {
            case 0:
                mTitle = getString(R.string.title_main);
                break;
            case 1:
                mTitle = getString(R.string.title_politics);
                break;
            case 2:
                mTitle = getString(R.string.title_tech);
                break;
            case 3:
                mTitle = getString(R.string.title_culture);
                break;
            case 4:
                mTitle = getString(R.string.title_sports);
                break;
        }

        refreshActionBar();

        if (checkWiFiConnection()) {
            setNewsTopicFragment(false);
        }
    }

    private boolean checkWiFiConnection() {

        NetworkInfo mWifi = ((ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE)).
                getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mWifi.isConnected()) {
            if (newsList.isEmpty()) {
                setEmptyFragment();
            }
            if (!isErrorDialogOnScreen) {
                showErrorDialog();
                isErrorDialogOnScreen = true;
            }
            newsOverviewActivityLogger.error("Wifi offline. Aborting download.");
            return false;
        }
        return true;
    }

    private void showErrorDialog() {
        errorDialog = new ToastDialog(this,
                getResources().getString(R.string.no_connection_error));
        errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isErrorDialogOnScreen = false;
            }
        });

        errorDialog.show();
    }

    private void setEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new NewsEmptyFragment())
                .commit();
    }

    private void setNewsTopicFragment(boolean loadFromNet) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, NewsTopicFragment.newInstance(heading, newsList, loadFromNet))
                .commit();
    }


    public void onLoadFinished(List<ShortNewsEntry> news) {
        newsList = news;
        getActionBar().setTitle(mTitle);
    }

    public void onLoadFailed() {
        if (newsList.isEmpty()) {
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
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            //refreshActionBar();
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

    public void onRetryClick(View v) {
        errorDialog.dismiss();
        isErrorDialogOnScreen = false;
        refreshNews();
    }

    public void refreshNews() {
        if (checkWiFiConnection()) {
            setNewsTopicFragment(true);
        }
        else {
            refreshActionBar();
        }
    }
}
