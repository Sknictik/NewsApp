package noveo.school.android.newsapp.view;

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
public class ArrayAdapterForNavigationDrawer extends ArrayAdapter<String> {

    LayoutInflater mInflater = null;

    private int mViewResourceId;
    private int mTextViewResourceId;
    private int mImageViewResourceId;
    private int[] highlightColors;
    private int[] titleColors;
    private String[] mTitles;

    public ArrayAdapterForNavigationDrawer(Context context, int resource, int textViewResourceId,
                                           String[] titles, int[] titleColors, int[] highlightColors,
                                           int imageViewResourceId) {
        super(context, resource, textViewResourceId, titles);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        mViewResourceId = resource;
        mTextViewResourceId = textViewResourceId;
        mImageViewResourceId = imageViewResourceId;
        this.highlightColors = highlightColors;
        this.titleColors = titleColors;
        mTitles = titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(mViewResourceId, null);
        }

        TextView tv = (TextView)convertView.findViewById(mTextViewResourceId);
        tv.setText(mTitles[position]);


        switch(position) {
            case 0: {((CheckableLinearLayout) convertView).setHighlightColor(highlightColors[position]); break;}
            case 1: {((CheckableLinearLayout) convertView).setHighlightColor(highlightColors[position]); break;}
            case 2: {((CheckableLinearLayout) convertView).setHighlightColor(highlightColors[position]); break;}
            case 3: {((CheckableLinearLayout) convertView).setHighlightColor(highlightColors[position]); break;}
            case 4: {((CheckableLinearLayout) convertView).setHighlightColor(highlightColors[position]); break;}
        }


        ImageView iv = (ImageView)convertView.findViewById(mImageViewResourceId);
        iv.setBackgroundColor(titleColors[position]);


        return convertView;
    }


}
