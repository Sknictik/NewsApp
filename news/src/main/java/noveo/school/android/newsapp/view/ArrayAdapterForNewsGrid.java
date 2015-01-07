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
    private Boolean[] isFavourite;
    private int styleColor;

    public ArrayAdapterForNewsGrid(Context context, int layoutResource,
                                   String[] dates,
                                   Drawable[] newsImages,
                                   String[] newsTexts,
                                   Boolean[] isFavourite,
                                   Drawable faveIcon,
                                   int color
    ) {
        super(context, layoutResource, R.id.newsTextView, newsTexts);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        layoutId = layoutResource;
        mNewsTexts = newsTexts;
        mNewsImages = newsImages;
        mDates = dates;
        this.faveIcon = faveIcon;
        this.isFavourite = isFavourite;
        this.styleColor = color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, null);
        }

        TextView dateTV = (TextView) convertView.findViewById(R.id.dateTextView);
        dateTV.setText(mDates[position]);
        dateTV.setTextColor(styleColor);

        TextView newsTV = (TextView) convertView.findViewById(R.id.newsTextView);
        newsTV.setText(mNewsTexts[position]);

        ImageView newsIV = (ImageView) convertView.findViewById(R.id.newsImageView);
        newsIV.setImageDrawable(mNewsImages[position]);

        if (isFavourite[position]) {
            ImageView faveIV = (ImageView) convertView.findViewById(R.id.faveIcon);
            faveIV.setImageDrawable(faveIcon);
        }

        return convertView;
    }
}
