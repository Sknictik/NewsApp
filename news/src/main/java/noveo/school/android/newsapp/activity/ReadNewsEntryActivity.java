package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android_news.newsapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import noveo.school.android.newsapp.fragment.NewsEmptyFragment;
import noveo.school.android.newsapp.fragment.NewsEntryFragment;
import noveo.school.android.newsapp.fragment.NewsTopicFragment;
import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.ToastDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity used to display full information about single news entry.
 */
// TODO CR#1 (DONE) the same as MainActivity (move the key to class constant)
public class ReadNewsEntryActivity extends Activity {

    public static final String ID_RESULT_KEY = "noveo.school.android.newsapp.ReadNewsEntryActivity.NEWS_ID_RESULT";
    public static final String IS_FAVE_RESULT_KEY = "noveo.school.android.newsapp.ReadNewsEntryActivity.IS_FAVE_RESULT";
    public static final String NEWS_TITLE_PARAM_KEY = "noveo.school.android.newsapp.ReadNewsEntryActivity.NEWS_TITLE_PARAM";
    public static final String NEWS_ENTRY_PARCELABLE_PARAM_KEY =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.NEWS_ENTRY_PARCELABLE_PARAM";
    public static final String NEWS_ENTRY_ID_PARAM_KEY =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.NEWS_ENTRY_ID_PARAM";
    private static final String SAVED_IS_FAVE_KEY =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.SAVED_IS_FAVE";
    private static final String SAVED_IS_RESULT_SET_KEY =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.SAVED_IS_RESULT_SET";
    private static final Logger READ_NEWS_ENTRY_ACTIVITY_LOGGER = LoggerFactory.getLogger(ReadNewsEntryActivity.class);
    //SavedInstanceState keys and values
    private static final String ERROR_DIALOG_KEY = "noveo.school.android.newsapp.ERROR_DIALOG";
    private final Format timeFormat = new SimpleDateFormat("yyyy.MM.dd | HH:mm", new Locale("ru"));
    private MenuItem faveBtn;
    private ToastDialog errorDialog;
    private ShortNewsEntry passedNewsEntryObj;
    private FullNewsEntry newsEntryObj;
    private boolean isFavourite;
    private boolean isResultSet = false;

    public static Intent newIntent() {
        return new Intent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news_entry);

        Intent intent = getIntent();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView dateTV = (TextView) findViewById(R.id.date_tv);

        Resources res = getResources();
        TypedArray colors = res.obtainTypedArray(R.array.newsHighlightColorsArray);

        final int color = colors.getColor(MainActivity.getCurrentTopic().ordinal(), 0);
        colors.recycle();
        dateTV.setTextColor(color);
        passedNewsEntryObj = intent.getParcelableExtra(NewsTopicFragment.NEWS_ENTRY_KEY);
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
            //If result was already set and configuration change happened set result again as it is likely reset.
            isResultSet = savedInstanceState.getBoolean(SAVED_IS_RESULT_SET_KEY);
            this.setResult(RESULT_OK, intent);

            int errorNum = savedInstanceState.getInt(ERROR_DIALOG_KEY, -1);
            if (errorNum != -1) {
                showErrorDialog(RestClient.Error.values()[errorNum]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Intent parentIntent = getIntent();
        ActionBar actionBar = getActionBar();
        String mTitle = getResources().getStringArray(R.array.topics)[MainActivity.getCurrentTopic().ordinal()];
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

        Resources res = getResources();
        TypedArray colors = res.obtainTypedArray(R.array.newsActionBarColorsArray);
        int topicNum = MainActivity.getCurrentTopic().ordinal();
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
        if (errorDialog != null) {
            savedInstanceState.putInt(ERROR_DIALOG_KEY, errorDialog.getReason().ordinal());
        }
        savedInstanceState.putBoolean(SAVED_IS_FAVE_KEY, isFavourite);
        savedInstanceState.putBoolean(SAVED_IS_RESULT_SET_KEY, isResultSet);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (errorDialog != null) {
            errorDialog.dismiss();
            errorDialog = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //TODO Сохранение картинок для кнопки
        // "В избранное"
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

        NewsEmptyFragment mFragment = NewsEmptyFragment.newInstance();
        Bundle params = new Bundle();
        params.putBoolean(NewsEmptyFragment.IS_BACKGROUND_WHITE_PARAM, true);
        mFragment.setArguments(params);
        fragmentManager.beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();
    }

    private void setNewsEntryFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        NewsEntryFragment fragment = new NewsEntryFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(NEWS_TITLE_PARAM_KEY,
                ((TextView) findViewById(R.id.title_tv)).getText().toString());
        mBundle.putString(NEWS_ENTRY_ID_PARAM_KEY, passedNewsEntryObj.getId());
        fragment.setArguments(mBundle);
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment).commit();
    }

    private void setNewsEntryFragment(FullNewsEntry newsEntry) {
        FragmentManager fragmentManager = getFragmentManager();
        NewsEntryFragment fragment = NewsEntryFragment.newInstance();
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(NEWS_ENTRY_PARCELABLE_PARAM_KEY, newsEntry);
        mBundle.putString(ReadNewsEntryActivity.NEWS_TITLE_PARAM_KEY,
                ((TextView) findViewById(R.id.title_tv)).getText().toString());
        mBundle.putString(NEWS_ENTRY_ID_PARAM_KEY, passedNewsEntryObj.getId());
        fragment.setArguments(mBundle);
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

    public void onRetryClick(View v) {
        errorDialog.dismiss();
        setNewsEntryFragment();
    }

}
