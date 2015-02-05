package noveo.school.android.newsapp.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android_news.newsapp.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Arseniy Nazarov on 06.01.2015.
 * */


public class ArrayAdapterForNewsGrid extends ArrayAdapter<ShortNewsEntry> implements Target {

    private final Format TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy | HH:mm");

    LayoutInflater mInflater = null;

    private int layoutId;
    private List<ShortNewsEntry> news;
    private Drawable faveIcon = null;
    private int styleColor;
    private Context context;
    private static final Logger newsGridAdapterLogger = LoggerFactory.getLogger(ArrayAdapterForNewsGrid.class);

    public ArrayAdapterForNewsGrid(Context context, int layoutResource,
                                   List<ShortNewsEntry> news,
                                   Drawable faveIcon,
                                   int topicColor) {
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

        convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,
                (int) context.getResources().getDimension(R.dimen.news_row_height)));

        TextView dateTV = (TextView) convertView.findViewById(R.id.dateTextView);

        ShortNewsEntry newsEntry = news.get(position);

        String stringTime = TIME_FORMAT.format(newsEntry.getPubDate());
        dateTV.setText(stringTime);
        dateTV.setTextColor(styleColor);

        TextView newsTV = (TextView) convertView.findViewById(R.id.newsTextView);
        newsTV.setText(newsEntry.getTitle());
        String imageUrl = newsEntry.getImage();

        final ImageView newsIV = (ImageView) convertView.findViewById(R.id.newsImageView);

        if (imageUrl != null) {
            newsGridAdapterLogger.trace("Picasso", "Start loading " + newsEntry.getPubDate());

            Picasso.with(context)
                    .load(imageUrl)
                    .noFade()
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

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
