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
import noveo.school.android.newsapp.view.adapter.FullScreenImageAdapter;

/**
 * Created by Arseniy Nazarov on 03.02.2015.
 */
public class PhotoGalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        final String[] imagePaths = getIntent().getStringArrayExtra(
                getString(R.string.news_entry_fragment_image_paths_param_key));

        int pos = getIntent().getIntExtra(getString(R.string.news_entry_fragment_pos_param_key), 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FullScreenImageAdapter(this,
                imagePaths));
        String actionBarTitle = "Фото " + (pos + 1) + " из " + imagePaths.length;
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(actionBarTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Resources res = getResources();
        TypedArray colors = res.obtainTypedArray(R.array.newsActionBarColorsArray);
        int topicNum = getIntent().getIntExtra(getString(R.string.news_entry_fragment_topic_num_key), 0);
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
                String actionBarTitle = "Фото " + (i + 1) + " из " + imagePaths.length;
                getActionBar().setTitle(Html.fromHtml(actionBarTitle));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //Do nothing
            }
        });
        viewPager.setCurrentItem(pos);

        ((TextView) findViewById(R.id.photoCaption)).setText(getIntent().getStringExtra(
                getString(R.string.news_entry_fragment_caption_param_key)));
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
