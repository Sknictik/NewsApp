package noveo.school.android.newsapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android_news.newsapp.R;
import noveo.school.android.newsapp.fragment.NewsEntryFragment;
import noveo.school.android.newsapp.view.adapter.FullScreenImageAdapter;

/**
 * Created by Arseniy Nazarov on 03.02.2015.
 */
public class PhotoGalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        String[] imagePaths = getIntent().getStringArrayExtra(NewsEntryFragment.IMAGE_PATHS_ARGUMENT_KEY);

        int pos = getIntent().getIntExtra(NewsEntryFragment.POSITION_ARGUMENT_KEY, 1);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FullScreenImageAdapter(this,
                imagePaths,
                getIntent().getStringExtra(NewsEntryFragment.CAPTION_KEY)));
        viewPager.setCurrentItem(pos);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
