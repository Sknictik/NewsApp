package noveo.school.android.newsapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android_news.newsapp.R;
import noveo.school.android.newsapp.view.CheckableLinearLayout;

/**
 * Created by Arseniy Nazarov on 25.12.2014.
 */
public class ArrayAdapterForNavigationDrawer extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;

    private final int mViewResourceId;
    private final int mTextViewResourceId;
    private final int[] highlightColors;
    private final int[] titleColors;
    private final String[] mTitles;

    public ArrayAdapterForNavigationDrawer(Context context, int resource, int textViewResourceId,
                                           String[] titles, int[] titleColors, int[] highlightColors) {
        super(context, resource, textViewResourceId, titles);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        mViewResourceId = resource;
        mTextViewResourceId = textViewResourceId;
        this.highlightColors = highlightColors;
        this.titleColors = titleColors;
        mTitles = titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(mViewResourceId, null);
        }

        TextView tv = (TextView) convertView.findViewById(mTextViewResourceId);
        tv.setText(mTitles[position]);

        ((CheckableLinearLayout) convertView).setHighlightColor(highlightColors[position]);


        ImageView iv = (ImageView) convertView.findViewById(R.id.news_topic_color);
        iv.setBackgroundColor(titleColors[position]);


        return convertView;
    }


}
