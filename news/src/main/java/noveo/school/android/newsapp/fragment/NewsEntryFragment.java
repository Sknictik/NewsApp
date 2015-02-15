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
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import noveo.school.android.newsapp.activity.MainActivity;
import noveo.school.android.newsapp.activity.PhotoGalleryActivity;
import noveo.school.android.newsapp.activity.ReadNewsEntryActivity;
import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoFailLoadNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoFinishLoadNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoStartLoadNewsEntry;
import noveo.school.android.newsapp.retrofit.interfaces.RestClientCallbackForNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;

/**
 * This fragment displays news description and images related
 * to a single news entry. It is used in ReadNewsEntryActivity.
 */
public class NewsEntryFragment extends Fragment implements RestClientCallbackForNewsEntry {
    public static final String CAPTION_PARAM_KEY = "noveo.school.android.newsapp.NewsEntryFragment.CAPTION_PARAM";
    public static final String IMAGE_PATHS_PARAM_KEY = "noveo.school.android.newsapp.NewsEntryFragment.IMAGE_PATHS_PARAM";
    public static final String POSITION_PARAM_KEY = "noveo.school.android.newsapp.NewsEntryFragment.POSITION_PARAM";

    private FullNewsEntry storedNewsEntry = null;
    private String newsEntryId = null;

    // TODO (DONE) CR#1 the same as NewsEmptyFragment
    public static NewsEntryFragment newInstance() {
        return new NewsEntryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_news_entry,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        MainActivity.getBusInstance().register(this);

        if (newsEntryId == null) {
            newsEntryId = getArguments().getString(
                    ReadNewsEntryActivity.NEWS_ENTRY_ID_PARAM_KEY);
        }
        if (storedNewsEntry == null) {
            storedNewsEntry = getArguments().getParcelable(
                    ReadNewsEntryActivity.NEWS_ENTRY_PARCELABLE_PARAM_KEY);
        }
        if (storedNewsEntry == null) {
            RestClient.downloadNewsEntry(this, newsEntryId);
        } else {
            onLoadFinished(new OttoFinishLoadNewsEntry(storedNewsEntry));
        }
    }

    @Override
    public void onDetach() {
        MainActivity.getBusInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    @Subscribe
    public void onLoadFinished(OttoFinishLoadNewsEntry event) {
        storedNewsEntry = event.getNewsEntry();

        //If fragment is not attached discard data
        if (!isAdded()) {
            return;
        }
        WebView newsWV = (WebView) getView().findViewById(R.id.news_wv);
        LinearLayout imagesLayout = (LinearLayout) getView().findViewById(R.id.imagesContainerLayout);

        final String[] imageUrls = storedNewsEntry.getImages();
        if (imageUrls.length != 0) {
            imagesLayout.setVisibility(View.VISIBLE);
            for (int urlPos = 0; urlPos < imageUrls.length; urlPos++) {
                ImageView photoView = new ImageView(getActivity());
                int width = (int) getResources().getDimension(R.dimen.photo_width_horiz);
                int height = (int) getResources().getDimension(R.dimen.photo_height_horiz);

                LinearLayout.LayoutParams vp =
                        new LinearLayout.LayoutParams(width, height);
                int marg = (int) getResources().getDimension(R.dimen.small_padding);
                vp.setMargins(marg, marg, marg, marg);
                photoView.setLayoutParams(vp);
                photoView.setScaleType(ImageView.ScaleType.FIT_XY);
                photoView.setTag(urlPos);
                int pad = (int) getResources().getDimension(R.dimen.border_padding);
                photoView.setPadding(pad, pad, pad, pad);
                photoView.setBackgroundResource(R.drawable.bg_image);
                photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewPhotoIntent = new Intent(getActivity(), PhotoGalleryActivity.class);
                        viewPhotoIntent.putExtra(CAPTION_PARAM_KEY,
                                getArguments().getString(ReadNewsEntryActivity.NEWS_TITLE_PARAM_KEY));
                        viewPhotoIntent.putExtra(IMAGE_PATHS_PARAM_KEY, imageUrls);
                        viewPhotoIntent.putExtra(POSITION_PARAM_KEY, (int) v.getTag());
                        startActivity(viewPhotoIntent);
                    }
                });


                Picasso.with(getActivity())
                        .load(imageUrls[urlPos])
                        .placeholder(R.drawable.ic_stub_loading)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_stub_error)
                        .into(photoView);
                imagesLayout.addView(photoView);
            }
        }

        newsWV.loadData(storedNewsEntry.getHtml(), "text/html; charset=UTF-8", null);

        ((ReadNewsEntryActivity) getActivity()).onLoadFinished(storedNewsEntry);
    }

    @Override
    @Subscribe
    public void onLoadFailed(OttoFailLoadNewsEntry event) {
        ((ReadNewsEntryActivity) getActivity()).onLoadFailed(event.getError());
    }

    @Override
    @Subscribe
    public void onLoadStart(OttoStartLoadNewsEntry event) {
        ((ReadNewsEntryActivity) getActivity()).onLoadStart();
    }

}
