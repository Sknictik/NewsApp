package noveo.school.android.newsapp.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android_news.newsapp.R;

/**
 * Created by Arseniy Nazarov on 22.01.2015.
 */
public class ReadNewsEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news_entry);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new NewsEntryFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.news_entry_menu, menu);
            return true;
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
            }
            else {
                item.setIcon(R.drawable.ic_menu_star_checked);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class NewsEntryFragment extends Fragment {

        public NewsEntryFragment() {};

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_news_entry,
                    container, false);

            return rootView;
        }
    }
}
