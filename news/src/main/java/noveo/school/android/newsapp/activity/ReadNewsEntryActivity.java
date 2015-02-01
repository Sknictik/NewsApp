package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
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
import noveo.school.android.newsapp.fragment.NewsEmptyFragment;
import noveo.school.android.newsapp.fragment.NewsEntryFragment;
import noveo.school.android.newsapp.fragment.NewsTopicFragment;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.ToastDialog;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Arseniy Nazarov on 22.01.2015.
 */
public class ReadNewsEntryActivity extends Activity {

    private String mTitle;
    private final Format TIME_FORMAT = new SimpleDateFormat("yyyy.MM.dd | HH:mm");
    private Menu menu;
    private ToastDialog errorDialog;

    //SavedInstanceState keys and values
    private final String ERROR_DIALOG_KEY = "noveo.school.android.newsapp.ERROR_DIALOG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news_entry);

        Intent intent = getIntent();

        TextView dateTV = (TextView) findViewById(R.id.date_tv);

        Resources res = getResources();
        TypedArray colors = res.obtainTypedArray(R.array.newsHighlightColorsArray);
        int topicNum = intent.getIntExtra(NewsTopicFragment.TOPIC_NUM_KEY, 0);
        final int color = colors.getColor(topicNum, 0);
        dateTV.setTextColor(color);

        Date newsDate = (Date) intent.getSerializableExtra(NewsTopicFragment.NEWS_ENTRY_DATE_KEY);
        String stringTime = TIME_FORMAT.format(newsDate);
        dateTV.setText(stringTime);

        TextView titleTV = (TextView) findViewById(R.id.title_tv);
        titleTV.setText(intent.getStringExtra(NewsTopicFragment.NEWS_ENTRY_TITLE_KEY));

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new NewsEntryFragment()).commit();
        }
        else {
            int errorNum = savedInstanceState.getInt(ERROR_DIALOG_KEY, -1);
            if (errorNum != -1) {
                showErrorDialog(RestClient.Error.values()[errorNum]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        Intent parentIntent = getIntent();
        ActionBar actionBar = getActionBar();
        mTitle = parentIntent.getStringExtra(NewsTopicFragment.TOPIC_KEY);
        actionBar.setTitle(mTitle);

        if (parentIntent.getBooleanExtra(NewsTopicFragment.NEWS_IS_FAVE_KEY, false)) {
            MenuItem faveBtn = menu.getItem(0);
            faveBtn.setIcon(R.drawable.ic_menu_star_checked);
            faveBtn.setChecked(true);
        }

        Resources res = getResources();
        TypedArray colors = res.obtainTypedArray(R.array.newsActionBarColorsArray);
        int topicNum = parentIntent.getIntExtra(NewsTopicFragment.TOPIC_NUM_KEY, 0);
        final int color = colors.getColor(topicNum, 0);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        final int displayOptions = getActionBar().getDisplayOptions();
        if ((displayOptions & ActionBar.DISPLAY_SHOW_CUSTOM) == 0) {
            getMenuInflater().inflate(R.menu.news_entry_menu, menu);
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (errorDialog != null) {
            savedInstanceState.putInt(ERROR_DIALOG_KEY, errorDialog.getReason().ordinal());
        }
        //savedInstanceState.putString(TITLE_KEY, mTitle);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.fave_btn) {
            if (item.isChecked()) {
                item.setIcon(R.drawable.ic_menu_star_default);
                item.setChecked(false);
                //TODO DELETE ENTRY
            }
            else {
                item.setIcon(R.drawable.ic_menu_star_checked);
                item.setChecked(true);
                //TODO SAVE ENTRY
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onLoadFinished() {
        restoreActionBar();
    }

    public void onLoadFailed(RestClient.Error reason) {
        restoreActionBar();
        showErrorDialog(reason);
        setEmptyFragment();
    }

    private void setEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new NewsEmptyFragment())
                .commit();
    }

    private void setActionBarLoading() {
        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_loading);
        bar.setDisplayShowHomeEnabled(false);

        if (menu != null) {
            menu.clear();
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
        bar.setTitle(mTitle);

        ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.INVISIBLE);

        if (menu != null && !menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.news_entry_menu, menu);
        }
    }


    public void onLoadStart() {
        setActionBarLoading();
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
