package noveo.school.android.newsapp.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android_news.newsapp.R;
import com.squareup.picasso.Picasso;
import noveo.school.android.newsapp.picasso.PicassoSingleton;

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private String[] _imagePaths;
    private LayoutInflater inflater;
    private String caption;

    // constructor
    public FullScreenImageAdapter(Activity activity,
                                  String[] imagePaths, String caption) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.caption = caption;
    }

    @Override
    public int getCount() {
        return this._imagePaths.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        String actionBarTitle = "Фото " + (position + 1) + " из " + getCount();
        _activity.getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>" + actionBarTitle + "</font>"));

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.photo_layout, container,
                false);

        ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.photoView);
        TextView captionView = (TextView) viewLayout.findViewById(R.id.photoCaption);
        captionView.setText(caption);

        Picasso picasso = PicassoSingleton.get(_activity);

        picasso.with(_activity)
                .load(_imagePaths[position])
                .placeholder(R.drawable.ic_stub_loading)
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