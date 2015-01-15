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
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Arseniy Nazarov on 06.01.2015.
 * */


public class ArrayAdapterForNewsGrid extends ArrayAdapter<String> {

    LayoutInflater mInflater = null;

    private int layoutId;
    private List<ShortNewsEntry> news;
    private Drawable faveIcon = null;
    private Boolean[] isFavourite;
    private int styleColor;

    private final Format timeFormat = new SimpleDateFormat("yyyy.MM.dd | HH:mm");

    public ArrayAdapterForNewsGrid(Context context, int layoutResource,
                                   List<ShortNewsEntry> news,
                                   Boolean[] isFavourite,
                                   Drawable faveIcon,
                                   int topicColor
    ) {
        super(context, layoutResource, R.id.newsTextView, new String[] {"1", "2"});

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        layoutId = layoutResource;
        this.news = news;
        this.faveIcon = faveIcon;
        this.isFavourite = isFavourite;
        this.styleColor = topicColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, null);
        }

        TextView dateTV = (TextView) convertView.findViewById(R.id.dateTextView);

        ShortNewsEntry newsEntry = news.get(position);

        String stringTime = timeFormat.format(newsEntry.getPubDate());
        dateTV.setText(stringTime);
        dateTV.setTextColor(styleColor);

        TextView newsTV = (TextView) convertView.findViewById(R.id.newsTextView);
        newsTV.setText(newsEntry.getTitle());

        //TODO load image with picasso

        ImageView newsIV = (ImageView) convertView.findViewById(R.id.newsImageView);
        //newsIV.setImageDrawable(mNewsImages[position]);

        if (isFavourite[position]) {
            ImageView faveIV = (ImageView) convertView.findViewById(R.id.faveIcon);
            faveIV.setImageDrawable(faveIcon);
        }

        return convertView;
    }
}
