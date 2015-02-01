package noveo.school.android.newsapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android_news.newsapp.R;
import com.squareup.picasso.Picasso;
import noveo.school.android.newsapp.activity.ReadNewsEntryActivity;
import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.interfaces.RestClientCallbackForNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;

/**
 * Created by Arseniy Nazarov on 23.01.2015.
 */
public class NewsEntryFragment extends Fragment implements RestClientCallbackForNewsEntry {

    //private static final Logger newsEntryFragmentLogger = LoggerFactory.getLogger(NewsEntryFragment.class);

    public NewsEntryFragment() {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_news_entry,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        ((ReadNewsEntryActivity) getActivity()).onLoadStart();
        RestClient.downloadNewsEntry(this, intent.getStringExtra(NewsTopicFragment.NEWS_ENTRY_ID_KEY));
    }


    @Override
    public void onLoadFinished(FullNewsEntry news) {
        WebView newsWV = (WebView) getView().findViewById(R.id.news_wv);
        LinearLayout imagesLayout = (LinearLayout) getView().findViewById(R.id.imagesContainerLayout);

        String[] imageUrls = news.getImages();
        if (imageUrls.length != 0) {
            imagesLayout.setVisibility(View.VISIBLE);
            for (String imageUrl : imageUrls) {
                ImageView photoView = new ImageView(getActivity());
                LinearLayout.LayoutParams vp =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                photoView.setLayoutParams(vp);
                Picasso.with(getActivity())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_stub_loading)
                        .error(R.drawable.ic_stub_error)
                        .into(photoView);
                imagesLayout.addView(photoView);
            }
        }

        newsWV.loadData(news.getHtml(), "text/html; charset=UTF-8", null);

        ((ReadNewsEntryActivity) getActivity()).onLoadFinished();
    }

    @Override
    public void onLoadFailed(RestClient.Error reason) {
        ((ReadNewsEntryActivity) getActivity()).onLoadFailed(reason);
    }

    @Override
    public void onLoadStart() {
        ((ReadNewsEntryActivity) getActivity()).onLoadStart();
    }
}