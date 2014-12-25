package android_news.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Arseniy Nazarov on 25.12.2014.
 */
public class ArrayAdapterForNewsTitles extends ArrayAdapter<String> {

    LayoutInflater mInflater = null;

    private int mViewResourceId;
    private int mTextViewResourceId;
    private int mImageViewResourceId;
    private int[] colors;
    private String[] mTitles;

    public ArrayAdapterForNewsTitles(Context context, int resource, int textViewResourceId,
                                     String[] titles, int[] titleColors,
                                     int imageViewResourceId) {
        super(context, resource, textViewResourceId, titles);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        mViewResourceId = resource;
        mTextViewResourceId = textViewResourceId;
        mImageViewResourceId = imageViewResourceId;
        colors = titleColors;
        mTitles = titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        //convertView.setMinimumHeight(132);
        TextView tv = (TextView)convertView.findViewById(mTextViewResourceId);
        tv.setText(mTitles[position]);
        tv.setHighlightColor(colors[position]);

        ImageView iv = (ImageView)convertView.findViewById(mImageViewResourceId);
        iv.setBackgroundColor(colors[position]);

        return convertView;
    }


}
