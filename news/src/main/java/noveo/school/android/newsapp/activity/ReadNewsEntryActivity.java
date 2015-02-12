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
import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.ToastDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Arseniy Nazarov on 22.01.2015.
 */
// CR#1 the same as MainActivity (move the key to class constant)
public class ReadNewsEntryActivity extends Activity {

    private final Format timeFormat = new SimpleDateFormat("yyyy.MM.dd | HH:mm", new Locale("ru"));
    private MenuItem faveBtn;
    private ToastDialog errorDialog;
    private FullNewsEntry newsEntryObj;
    private static final Logger READ_NEWS_ENTRY_ACTIVITY_LOGGER = LoggerFactory.getLogger(ReadNewsEntryActivity.class);

    //SavedInstanceState keys and values
    private static final String ERROR_DIALOG_KEY = "noveo.school.android.newsapp.ERROR_DIALOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news_entry);

        Intent intent = getIntent();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView dateTV = (TextView) findViewById(R.id.date_tv);

        Resources res = getResources();
        TypedArray colors = res.obtainTypedArray(R.array.newsHighlightColorsArray);
        int topicNum = intent.getIntExtra(getString(R.string.news_topic_fragment_topic_num_key), 0);
        final int color = colors.getColor(topicNum, 0);
        colors.recycle();
        dateTV.setTextColor(color);
        Date newsDate = (Date) intent.getSerializableExtra(getString(R.string.news_topic_fragment_news_entry_date_key));
        String stringTime = timeFormat.format(newsDate);
        dateTV.setText(stringTime);

        TextView titleTV = (TextView) findViewById(R.id.title_tv);
        titleTV.setText(intent.getStringExtra(getString(R.string.news_topic_fragment_news_entry_title_key)));

        // Check whether we're restoring a previously destroyed instance
        if (savedInstanceState == null) {
            if (intent.getBooleanExtra(getString(R.string.read_news_entry_activity_news_is_fave_result_key),
                    intent.getBooleanExtra(getString(R.string.news_topic_fragment_news_is_fave_key),
                            false))) {
                SharedPreferences mPrefs = getSharedPreferences(getString(R.string.shared_preference_name),
                        Context.MODE_PRIVATE);
                Gson gson = new Gson();
                FullNewsEntry newsEntry;
                String jsonString = mPrefs.getString(intent.getStringExtra(
                        getString(R.string.news_topic_fragment_news_entry_id_key)), "");
                try {
                    newsEntry = gson.fromJson(jsonString, FullNewsEntry.class);
                } catch (JsonSyntaxException e) {
                    READ_NEWS_ENTRY_ACTIVITY_LOGGER.error("Unable to convert stored json string in object.", e);
                    setNewsEntryFragment();
                    return;
                }
                setNewsEntryFragment(newsEntry);
            } else {
                setNewsEntryFragment();
            }
        } else {
            int errorNum = savedInstanceState.getInt(ERROR_DIALOG_KEY, -1);
            if (errorNum != -1) {
                showErrorDialog(RestClient.Error.values()[errorNum]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent parentIntent = getIntent();
        ActionBar actionBar = getActionBar();
        String mTitle = parentIntent.getStringExtra(getString(R.string.news_topic_fragment_topic_key));
        actionBar.setTitle(mTitle);

        getMenuInflater().inflate(R.menu.news_entry_menu, menu);
        faveBtn = menu.findItem(R.id.fave_btn);
        if (parentIntent.getBooleanExtra(getString(R.string.news_topic_fragment_news_is_fave_key), false)) {
            faveBtn.setIcon(R.drawable.ic_menu_star_checked);
            faveBtn.setChecked(true);
        } else {
            faveBtn.setIcon(R.drawable.ic_menu_star_default);
            faveBtn.setChecked(false);
        }

        Resources res = getResources();
        TypedArray colors = res.obtainTypedArray(R.array.newsActionBarColorsArray);
        int topicNum = parentIntent.getIntExtra(getString(R.string.news_topic_fragment_topic_num_key), 0);
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
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
            SharedPreferences mPrefs = getSharedPreferences(getString(R.string.shared_preference_name),
                    MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Intent intent = this.getIntent();
            if (item.isChecked()) {
                item.setIcon(R.drawable.ic_menu_star_default);
                item.setChecked(false);
                    prefsEditor.remove(newsEntryObj.getId());
                    intent.putExtra(getString(R.string.read_news_entry_activity_news_is_fave_result_key), false);
                    READ_NEWS_ENTRY_ACTIVITY_LOGGER.trace("News object was marked for deletion");
            } else {
                item.setIcon(R.drawable.ic_menu_star_checked);
                item.setChecked(true);
                    Gson gson = new Gson();
                    String jsonObj = gson.toJson(newsEntryObj);
                    prefsEditor.putString(newsEntryObj.getId(), jsonObj);
                    intent.putExtra(getString(R.string.read_news_entry_activity_news_is_fave_result_key), true);
                    READ_NEWS_ENTRY_ACTIVITY_LOGGER.trace("News object was marked for storage");
            }
            intent.putExtra(getString(R.string.read_news_entry_activity_id_result_key), newsEntryObj.getId());
            this.setResult(RESULT_OK, intent);
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
        fragmentManager.beginTransaction()
                .replace(R.id.container, NewsEmptyFragment.newInstance(true))
                .commit();
    }

    private void setNewsEntryFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        NewsEntryFragment fragment = new NewsEntryFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(getString(R.string.read_news_entry_activity_news_title_param_key),
                ((TextView) findViewById(R.id.title_tv)).getText().toString());
        mBundle.putInt(getString(R.string.read_news_entry_activity_topic_num_key),
                getIntent().getIntExtra(getString(R.string.news_topic_fragment_topic_num_key), 0));
        fragment.setArguments(mBundle);
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment).commit();
    }

    private void setNewsEntryFragment(FullNewsEntry newsEntry) {
        FragmentManager fragmentManager = getFragmentManager();
        NewsEntryFragment fragment = NewsEntryFragment.newInstance(newsEntry);
        Bundle mBundle = new Bundle();
        mBundle.putString(getString(R.string.read_news_entry_activity_news_title_param_key),
                ((TextView) findViewById(R.id.title_tv)).getText().toString());
        mBundle.putInt(getString(R.string.read_news_entry_activity_topic_num_key),
                getIntent().getIntExtra(getString(R.string.news_topic_fragment_topic_num_key), 0));
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
        //bar.setTitle(mTitle);

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
