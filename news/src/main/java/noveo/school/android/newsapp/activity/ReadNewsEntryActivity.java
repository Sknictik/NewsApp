package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android_news.newsapp.R;
import noveo.school.android.newsapp.ApplicationState;
import noveo.school.android.newsapp.NewsUtils;
import noveo.school.android.newsapp.fragment.NewsEmptyFragment;
import noveo.school.android.newsapp.fragment.NewsEntryFragment;
import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;

/**
 * Activity used to display full information about single news entry.
 */
//  CR#1 (DONE) the same as MainActivity (move the key to class constant)
public class ReadNewsEntryActivity extends AbstractErrorDialogActivity {

    public static final String ID_RESULT_KEY = "noveo.school.android.newsapp.ReadNewsEntryActivity.NEWS_ID_RESULT";
    public static final String IS_FAVE_RESULT_KEY = "noveo.school.android.newsapp.ReadNewsEntryActivity.IS_FAVE_RESULT";
    public static final String NEWS_TITLE_PARAM_KEY = "noveo.school.android.newsapp.ReadNewsEntryActivity.NEWS_TITLE_PARAM";
    private static final String SAVED_IS_FAVE_KEY =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.SAVED_IS_FAVE";
    public static final String NEWS_ENTRY_ID_PARAM_KEY =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.NEWS_ENTRY_ID_PARAM";
    private static final String SAVED_IS_RESULT_SET_KEY =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.SAVED_IS_RESULT_SET";
    public static final String NEWS_ENTRY_KEY = "noveo.school.android.newsapp.NewsTopicFragment.NEWS_ENTRY";
    private static final Logger READ_NEWS_ENTRY_ACTIVITY_LOGGER = LoggerFactory.getLogger(ReadNewsEntryActivity.class);
    //SavedInstanceState keys and values
    private final Format timeFormat = new SimpleDateFormat("yyyy.MM.dd | HH:mm", new Locale("ru"));
    private MenuItem faveBtn;
    private ShortNewsEntry passedNewsEntryObj;
    private FullNewsEntry newsEntryObj;
    private boolean isFavourite;
    private boolean isResultSet;

    public static Intent createIntent(Context context, ShortNewsEntry shortNewsEntry) {
        return new Intent(context, ReadNewsEntryActivity.class).putExtra(NEWS_ENTRY_KEY, shortNewsEntry);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news_entry);

        Intent intent = getIntent();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView dateTV = (TextView) findViewById(R.id.date_tv);

        // CR#2 (DONE) I see the same code in another classes.
        // Please do some utility class for colors
        TypedArray colors = NewsUtils.getTypedArray(getResources(), R.array.newsHighlightColorsArray);

        final int color = colors.getColor(ApplicationState.getCurrentTopic().ordinal(), 0);
        colors.recycle();
        dateTV.setTextColor(color);
        passedNewsEntryObj = intent.getParcelableExtra(NEWS_ENTRY_KEY);
        Date newsDate = passedNewsEntryObj.getPubDate();
        String stringTime = timeFormat.format(newsDate);
        dateTV.setText(stringTime);
        TextView titleTV = (TextView) findViewById(R.id.title_tv);
        titleTV.setText(passedNewsEntryObj.getTitle());

        // Check whether we're restoring a previously destroyed instance
        if (savedInstanceState == null) {
            if (intent.getBooleanExtra(IS_FAVE_RESULT_KEY, passedNewsEntryObj.isFavourite())) {
                isFavourite = true;
                SharedPreferences mPrefs = getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
                Gson gson = new Gson();
                FullNewsEntry newsEntry;
                String jsonString = mPrefs.getString(passedNewsEntryObj.getId(), "");
                try {
                    newsEntry = gson.fromJson(jsonString, FullNewsEntry.class);
                } catch (JsonSyntaxException e) {
                    READ_NEWS_ENTRY_ACTIVITY_LOGGER.error("Unable to convert stored json string in object.", e);
                    setNewsEntryFragment();
                    return;
                }
                setNewsEntryFragment(newsEntry);
            } else {
                isFavourite = false;
                setNewsEntryFragment();
            }
        } else {
            isFavourite = savedInstanceState.getBoolean(SAVED_IS_FAVE_KEY);
            //If result was already set and configuration change
            // happened set result again as it is likely reset.
            isResultSet = savedInstanceState.getBoolean(SAVED_IS_RESULT_SET_KEY);
            this.setResult(RESULT_OK, intent);
        }
    }


    @Override
    public void onBackPressed() {
        if (getErrorDialog() != null && getErrorDialog().isVisible()) {
            getErrorDialog().dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getActionBar();
        String mTitle = getResources().getStringArray(R.array.topics)[ApplicationState.getCurrentTopic().ordinal()];
        actionBar.setTitle(mTitle);

        getMenuInflater().inflate(R.menu.news_entry_menu, menu);
        faveBtn = menu.findItem(R.id.fave_btn);
        if (isFavourite) {
            faveBtn.setIcon(R.drawable.ic_menu_star_checked);
            faveBtn.setChecked(true);
        } else {
            faveBtn.setIcon(R.drawable.ic_menu_star_default);
            faveBtn.setChecked(false);
        }

        TypedArray colors = NewsUtils.getTypedArray(getResources(), R.array.newsActionBarColorsArray);
        int topicNum = ApplicationState.getCurrentTopic().ordinal();
        final int color = colors.getColor(topicNum, 0);
        colors.recycle();
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final int displayOptions = getActionBar().getDisplayOptions();
        if ((displayOptions & ActionBar.DISPLAY_SHOW_CUSTOM) == 0) {
            faveBtn.setVisible(true);
        } else {
            faveBtn.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SAVED_IS_FAVE_KEY, isFavourite);
        savedInstanceState.putBoolean(SAVED_IS_RESULT_SET_KEY, isResultSet);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.fave_btn) {
            if (newsEntryObj == null) {
                return true;
            }
            SharedPreferences mPrefs = getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME,
                    MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Intent intent = getIntent();
            if (item.isChecked()) {
                item.setIcon(R.drawable.ic_menu_star_default);
                item.setChecked(false);
                prefsEditor.remove(newsEntryObj.getId());
                intent.putExtra(IS_FAVE_RESULT_KEY, false);
                READ_NEWS_ENTRY_ACTIVITY_LOGGER.trace("News object was marked for deletion");
                isFavourite = false;
            } else {
                item.setIcon(R.drawable.ic_menu_star_checked);
                item.setChecked(true);
                Gson gson = new Gson();
                String jsonObj = gson.toJson(newsEntryObj);
                prefsEditor.putString(newsEntryObj.getId(), jsonObj);
                intent.putExtra(IS_FAVE_RESULT_KEY, true);
                READ_NEWS_ENTRY_ACTIVITY_LOGGER.trace("News object was marked for storage");
                isFavourite = true;
            }
            intent.putExtra(ID_RESULT_KEY, newsEntryObj.getId());
            this.setResult(RESULT_OK, intent);
            isResultSet = true;
            prefsEditor.apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onLoadFinished(FullNewsEntry news) {
        newsEntryObj = news;
        restoreActionBar();
    }

    public void onLoadFailed(RestClient.Error reason) {
        restoreActionBar();
        showErrorDialog(reason);
        setEmptyFragment();
    }

    public void onLoadStart() {
        setActionBarLoading();
    }

    private void setEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        NewsEmptyFragment mFragment = NewsEmptyFragment.newInstance(true);

        fragmentManager.beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();
    }

    private void setNewsEntryFragment() {
        setNewsEntryFragment(null);
    }

    private void setNewsEntryFragment(FullNewsEntry newsEntry) {
        FragmentManager fragmentManager = getFragmentManager();
        NewsEntryFragment fragment = NewsEntryFragment.newInstance(newsEntry,
                ((TextView) findViewById(R.id.title_tv)).getText().toString(), passedNewsEntryObj.getId());

        fragmentManager.beginTransaction()
                .add(R.id.container, fragment).commit();
    }

    private void setActionBarLoading() {
        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_loading);
        bar.setDisplayShowHomeEnabled(false);

        if (faveBtn != null) {
            faveBtn.setVisible(false);
            invalidateOptionsMenu();
        }

        ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void restoreActionBar() {
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowCustomEnabled(false);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayUseLogoEnabled(true);

        ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.GONE);

        if (faveBtn != null) {
            faveBtn.setVisible(true);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onRetryClick() {
        getErrorDialog().dismiss();
        setNewsEntryFragment();
    }
}
