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


public class ArrayAdapterForNavigationDrawer extends ArrayAdapter<String> {

    private final LayoutInflater inflater;

    private final int viewResourceId;
    private final int textViewResourceId;
    private final int[] highlightColors;
    private final int[] titleColors;
    private final String[] titles;

    public ArrayAdapterForNavigationDrawer(Context context, int resource, int textViewResourceId,
                                           String[] titles, int[] titleColors, int[] highlightColors) {
        super(context, resource, textViewResourceId, titles);

        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        viewResourceId = resource;
        this.textViewResourceId = textViewResourceId;
        this.highlightColors = highlightColors;
        this.titleColors = titleColors;
        this.titles = titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(viewResourceId, null);
        }

        TextView tv = (TextView) convertView.findViewById(textViewResourceId);
        tv.setText(titles[position]);

        ((CheckableLinearLayout) convertView).setHighlightColor(highlightColors[position]);


        ImageView iv = (ImageView) convertView.findViewById(R.id.news_topic_color);
        iv.setBackgroundColor(titleColors[position]);

        return convertView;
    }


}
