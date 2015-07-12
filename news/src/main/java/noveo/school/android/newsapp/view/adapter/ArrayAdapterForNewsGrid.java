package noveo.school.android.newsapp.view.adapter;

import android.content.Context;
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
import noveo.school.android.newsapp.retrofit.entities.ShortNewsEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class ArrayAdapterForNewsGrid extends ArrayAdapter<ShortNewsEntry> {

    private static final Logger NEWS_GRID_ADAPTER_LOGGER = LoggerFactory.getLogger(ArrayAdapterForNewsGrid.class);
    private final Format timeFormat = new SimpleDateFormat("dd.MM.yyyy | HH:mm", new Locale("ru"));
    private final LayoutInflater inflater;
    private final int layoutId;
    private final List<ShortNewsEntry> news;
    private final Drawable faveIcon;
    private final int styleColor;
    private final Context context;

    public ArrayAdapterForNewsGrid(Context context, int layoutResource,
                                   List<ShortNewsEntry> news,
                                   Drawable faveIcon,
                                   int topicColor) {
        super(context, layoutResource, news);

        inflater = (LayoutInflater) context.getSystemService(
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
            convertView = inflater.inflate(layoutId, null);
        }

        convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,
                (int) context.getResources().getDimension(R.dimen.news_row_height)));

        TextView dateTV = (TextView) convertView.findViewById(R.id.dateTextView);

        ShortNewsEntry newsEntry = news.get(position);

        String stringTime = timeFormat.format(newsEntry.getPubDate());
        dateTV.setText(stringTime);
        dateTV.setTextColor(styleColor);

        TextView newsTV = (TextView) convertView.findViewById(R.id.newsTextView);
        newsTV.setText(newsEntry.getTitle());
        String imageUrl = newsEntry.getImage();

        final ImageView newsIV = (ImageView) convertView.findViewById(R.id.newsImageView);

        if (imageUrl != null) {
            NEWS_GRID_ADAPTER_LOGGER.trace("Picasso", "Start loading " + newsEntry.getPubDate());

            Picasso.with(context)
                    .load(imageUrl)
                    .noFade()
                    .placeholder(R.drawable.ic_stub_loading)
                    .error(R.drawable.ic_stub_error)
                    .into(newsIV);
            newsIV.setVisibility(View.VISIBLE);
        } else {
            newsIV.setVisibility(View.GONE);
        }
        ImageView faveIV = (ImageView) convertView.findViewById(R.id.faveIcon);
        faveIV.setImageDrawable(faveIcon);

        if (newsEntry.isFavourite()) {
            faveIV.setVisibility(View.VISIBLE);
        } else {
            faveIV.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}
