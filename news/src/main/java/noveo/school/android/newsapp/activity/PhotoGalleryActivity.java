package noveo.school.android.newsapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android_news.newsapp.R;
import noveo.school.android.newsapp.fragment.NewsEntryFragment;
import noveo.school.android.newsapp.view.adapter.FullScreenImageAdapter;

/**
 * Created by Arseniy Nazarov on 03.02.2015.
 */
// TODO CR#1(DONE) the same as MainActivity (move the key to class constant)
public class PhotoGalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        final String[] imagePaths = getIntent().getStringArrayExtra(NewsEntryFragment.IMAGE_PATHS_PARAM_KEY);

        int pos = getIntent().getIntExtra(NewsEntryFragment.POSITION_PARAM_KEY, 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FullScreenImageAdapter(this,
                imagePaths));
        // TODO CR#1 (DONE) extract hardcoded strings into xml resources
        // And use string formatting (getString(int resId, Object... formatArgs))
        String actionBarTitle = getString(R.string.image_count_title, pos + 1, imagePaths.length);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(actionBarTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Resources res = getResources();
        TypedArray colors = res.obtainTypedArray(R.array.newsActionBarColorsArray);
        int topicNum = MainActivity.getCurrentTopic().ordinal();
        final int color = colors.getColor(topicNum, 0);
        colors.recycle();
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //Do nothing
            }

            @Override
            public void onPageSelected(int i) {
                // TODO CR#1 (DONE) the same
                String actionBarTitle = getString(R.string.image_count_title, i + 1, imagePaths.length);
                getActionBar().setTitle(Html.fromHtml(actionBarTitle));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //Do nothing
            }
        });
        viewPager.setCurrentItem(pos);

        ((TextView) findViewById(R.id.photoCaption)).setText(getIntent().getStringExtra(
                NewsEntryFragment.CAPTION_PARAM_KEY));
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
        }
        return super.onOptionsItemSelected(item);
    }
}
