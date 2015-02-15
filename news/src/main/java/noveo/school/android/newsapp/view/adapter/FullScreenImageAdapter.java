package noveo.school.android.newsapp.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android_news.newsapp.R;
import com.squareup.picasso.Picasso;

/*
 * ArrayAdapter used in PhotoGalleryActivity
 */

public class FullScreenImageAdapter extends PagerAdapter {

    private final Activity activity;
    private final String[] imagePaths;

    // constructor
    public FullScreenImageAdapter(Activity activity,
                                  String[] imagePaths) {
        super();
        this.activity = activity;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this.imagePaths.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.photo_layout, container,
                false);

        ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.photoView);

        Picasso.with(activity)
                .load(imagePaths[position])
                .placeholder(R.drawable.ic_stub_loading)
                .fit()
                .centerInside()
                .error(R.drawable.ic_stub_error)
                .into(imgDisplay);

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
