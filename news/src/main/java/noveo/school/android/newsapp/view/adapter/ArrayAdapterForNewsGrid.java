package noveo.school.android.newsapp.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android_news.newsapp.R;
import com.squareup.picasso.Picasso;
import noveo.school.android.newsapp.picasso.PicassoSingleton;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Arseniy Nazarov on 06.01.2015.
 * */


public class ArrayAdapterForNewsGrid extends ArrayAdapter<ShortNewsEntry> {

    private final Format TIME_FORMAT = new SimpleDateFormat("yyyy.MM.dd | HH:mm");

    LayoutInflater mInflater = null;

    private int layoutId;
    private List<ShortNewsEntry> news;
    private Drawable faveIcon = null;
    private int styleColor;
    private Context context;

    public ArrayAdapterForNewsGrid(Context context, int layoutResource,
                                   List<ShortNewsEntry> news,
                                   Drawable faveIcon,
                                   int topicColor
    ) {
        super(context, layoutResource, news);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        layoutId = layoutResource;
        this.news = news;
        this.faveIcon = faveIcon;
        this.styleColor = topicColor;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, null);
        }

        TextView dateTV = (TextView) convertView.findViewById(R.id.dateTextView);

        ShortNewsEntry newsEntry = news.get(position);

        String stringTime = TIME_FORMAT.format(newsEntry.getPubDate());
        dateTV.setText(stringTime);
        dateTV.setTextColor(styleColor);

        TextView newsTV = (TextView) convertView.findViewById(R.id.newsTextView);
        newsTV.setText(newsEntry.getTitle());
        String imageUrl = newsEntry.getImage();

        ImageView newsIV = (ImageView) convertView.findViewById(R.id.newsImageView);

        if (imageUrl != null) {
            Picasso picasso = PicassoSingleton.get(context);

            picasso.with(context)
                    .load(newsEntry.getImage())
                    .placeholder(R.drawable.ic_stub_loading)
                    .error(R.drawable.ic_stub_error)
                    .into(newsIV);
            newsIV.setVisibility(View.VISIBLE);
        }
        else {
            newsIV.setVisibility(View.GONE);
        }
        ImageView faveIV = (ImageView) convertView.findViewById(R.id.faveIcon);
        faveIV.setImageDrawable(faveIcon);

        if (newsEntry.isFavourite()) {
            faveIV.setVisibility(View.VISIBLE);
        }
        else {
            faveIV.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
