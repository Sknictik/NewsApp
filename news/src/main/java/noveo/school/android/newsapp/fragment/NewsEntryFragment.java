package noveo.school.android.newsapp.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android_news.newsapp.R;
import com.squareup.picasso.Picasso;
import noveo.school.android.newsapp.Utils;
import noveo.school.android.newsapp.activity.MainActivity;
import noveo.school.android.newsapp.activity.ReadNewsEntryActivity;
import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;
import noveo.school.android.newsapp.view.ToastDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Arseniy Nazarov on 23.01.2015.
 */
public class NewsEntryFragment extends Fragment {

    private static final Logger newsEntryFragmentLogger = LoggerFactory.getLogger(NewsEntryFragment.class);

    public NewsEntryFragment() {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_news_entry,
                container, false);


        if (!checkWiFiConnection()) {
            setEmptyFragment();
        }
        else {
            ((ReadNewsEntryActivity) getActivity()).onLoadStart();

            downloadNewsEntry((WebView) rootView.findViewById(R.id.news_wv),
                    (LinearLayout) rootView.findViewById(R.id.imagesContainerLayout),
                    intent.getStringExtra(NewsTopicFragment.NEWS_ENTRY_ID_KEY));
        }

        return rootView;
    }

    private void downloadNewsEntry(final WebView newsWV, final LinearLayout imagesLayout, String newsId) {

        RestClient.get().getNewsById(newsId, new Callback<FullNewsEntry>() {
            @Override
            public void success(FullNewsEntry news, Response response) {
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
                newsEntryFragmentLogger.trace("News entry " + news.getId() + " downloaded from server");
            }

            @Override
            public void failure(RetrofitError error) {
                //TODO display error
                newsEntryFragmentLogger.error("Error downloading news from server");
                error.printStackTrace();
            }
        });
    }

    private boolean checkWiFiConnection() {

        NetworkInfo mWifi = ((ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).
                getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mWifi.isConnected()) {
            setEmptyFragment();
            if (!MainActivity.isErrorDialogOnScreen()) {
                ToastDialog errorDialog = Utils.showErrorDialog(getActivity());
                errorDialog.findViewById(R.id.retryButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO retry download
                        //downloadNewsEntry();
                    }
                });
                MainActivity.setErrorDialog(errorDialog);
            }
            newsEntryFragmentLogger.error("Wifi offline. Aborting download.");
            return false;
        }
        return true;
    }


    private void setEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new NewsEmptyFragment())
                .commit();
    }

}