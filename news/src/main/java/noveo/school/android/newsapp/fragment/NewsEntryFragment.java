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

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import android_news.newsapp.R;
import noveo.school.android.newsapp.ApplicationState;
import noveo.school.android.newsapp.activity.PhotoGalleryActivity;
import noveo.school.android.newsapp.activity.ReadNewsEntryActivity;
import noveo.school.android.newsapp.retrofit.entities.FullNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoFailLoadNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoFinishLoadNewsEntry;
import noveo.school.android.newsapp.retrofit.events.OttoStartLoadNewsEntry;
import noveo.school.android.newsapp.retrofit.service.RestClient;

/**
 * This fragment displays news description and images related
 * to a single news entry. It is used in ReadNewsEntryActivity.
 */
public class NewsEntryFragment extends Fragment {
    public static final String CAPTION_PARAM_KEY = "noveo.school.android.newsapp.NewsEntryFragment.CAPTION_PARAM";
    public static final String IMAGE_PATHS_PARAM_KEY = "noveo.school.android.newsapp.NewsEntryFragment.IMAGE_PATHS_PARAM";
    public static final String POSITION_PARAM_KEY = "noveo.school.android.newsapp.NewsEntryFragment.POSITION_PARAM";
    public static final String NEWS_ENTRY_PARCELABLE_PARAM_KEY =
            "noveo.school.android.newsapp.ReadNewsEntryActivity.NEWS_ENTRY_PARCELABLE_PARAM";
    private FullNewsEntry storedNewsEntry;
    private String newsEntryId;

    //  (DONE) CR#1 the same as NewsEmptyFragment
    public static NewsEntryFragment newInstance(FullNewsEntry newsEntry, String newsTitle, String newsEntryId) {
        NewsEntryFragment entryFragment = new NewsEntryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NEWS_ENTRY_PARCELABLE_PARAM_KEY, newsEntry);
        bundle.putString(ReadNewsEntryActivity.NEWS_TITLE_PARAM_KEY, newsTitle);
        bundle.putString(ReadNewsEntryActivity.NEWS_ENTRY_ID_PARAM_KEY, newsEntryId);
        entryFragment.setArguments(bundle);
        return entryFragment;
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
        ApplicationState.getBusInstance().register(this);

        if (newsEntryId == null) {
            newsEntryId = getArguments().getString(
                    ReadNewsEntryActivity.NEWS_ENTRY_ID_PARAM_KEY);
        }
        if (storedNewsEntry == null) {
            storedNewsEntry = getArguments().getParcelable(NEWS_ENTRY_PARCELABLE_PARAM_KEY);
        }
        if (storedNewsEntry == null) {
            RestClient.downloadNewsEntry(newsEntryId);
        } else {
            onLoadFinished(new OttoFinishLoadNewsEntry(storedNewsEntry));
        }
    }

    @Override
    public void onDetach() {
        ApplicationState.getBusInstance().unregister(this);
        super.onDestroy();
    }

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
                        Intent viewPhotoIntent = PhotoGalleryActivity.createIntent(getActivity(), (int) v.getTag(),
                                imageUrls, getArguments().getString(ReadNewsEntryActivity.NEWS_TITLE_PARAM_KEY));
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

    @Subscribe
    public void onLoadFailed(OttoFailLoadNewsEntry event) {
        ((ReadNewsEntryActivity) getActivity()).onLoadFailed(event.getError());
    }

    @Subscribe
    public void onLoadStart(OttoStartLoadNewsEntry event) {
        ((ReadNewsEntryActivity) getActivity()).onLoadStart();
    }

}
