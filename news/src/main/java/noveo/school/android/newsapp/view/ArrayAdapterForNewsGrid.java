package noveo.school.android.newsapp.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android_news.newsapp.R;

/**
 * Created by Arseniy Nazarov on 06.01.2015.
 * */


public class ArrayAdapterForNewsGrid extends ArrayAdapter<String> {

    LayoutInflater mInflater = null;

    private int layoutId;
    private String[] mDates;
    private Drawable[] mNewsImages;
    private String[] mNewsTexts;
    private Drawable faveIcon = null;

    public ArrayAdapterForNewsGrid(Context context, int layoutResource,
                                   String[] dates,
                                   Drawable[] newsImages,
                                   String[] newsTexts,
                                   Drawable faveIcon
                                           ) {
        super(context, layoutResource, R.id.newsTextView, newsTexts);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        layoutId = layoutResource;
        mNewsTexts = newsTexts;
        mNewsImages = newsImages;
        mDates = dates;
        this.faveIcon = faveIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, null);
        }

        TextView dateTV = (TextView)convertView.findViewById(R.id.dateTextView);
        dateTV.setText(mDates[position]);

        TextView newsView = (TextView)convertView.findViewById(R.id.newsTextView);
        newsView.setCompoundDrawables(mNewsImages[position], null, null, null);
        newsView.setText(mNewsTexts[position]);

        if (faveIcon != null) {
            ImageView faveIV = (ImageView)convertView.findViewById(R.id.faveIcon);
            faveIV.setImageDrawable(faveIcon);
        }

        return convertView;
    }
}
