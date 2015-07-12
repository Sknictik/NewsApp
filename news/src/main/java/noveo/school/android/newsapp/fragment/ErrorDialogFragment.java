package noveo.school.android.newsapp.fragment;

/**
 * Error dialog
 */
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import android_news.newsapp.R;
import noveo.school.android.newsapp.retrofit.service.RestClient;

public class ErrorDialogFragment extends DialogFragment {

    private static final int MIN_BIG_SCREEN_PORTRAIT_WIDTH = 1000;
    private static final int MIN_BIG_SCREEN_LANDSCAPE_WIDTH = 1600;
    private static final double BIG_SCREEN_PART = 0.66;
    private static final String ERROR_REASON_KEY = "noveo.school.android.newsapp.ErrorDialogFragment.ERROR_REASON";
    //private final RestClient.Error reason;
    private OnRetryListener onRetryListener;

    public static ErrorDialogFragment newInstance(RestClient.Error reason) {
        Bundle args = new Bundle();
        args.putSerializable(ERROR_REASON_KEY, reason);
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    public void setOnRetryListener(OnRetryListener onRetryListener) {
        this.onRetryListener = onRetryListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.toast_dialog_layout, container);

        String errMsg;
        RestClient.Error reason = (RestClient.Error) getArguments().getSerializable(ERROR_REASON_KEY);
        switch (reason) {
            case NO_CONNECTION:
                errMsg = getActivity().getString(R.string.no_connection_error);
                break;
            case CONNECTION_TIMEOUT:
                errMsg = getActivity().getString(R.string.timeout_error);
                break;
            case UNKNOWN_ERROR:
            default:
                errMsg = getActivity().getString(R.string.unknown_error);
                break;
        }

        layout.findViewById(R.id.retryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryListener.onRetryClick();
            }
        });

        TextView tv = (TextView) layout.findViewById(R.id.errorMsg);
        tv.setText(errMsg);


        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        window.setBackgroundDrawable(new InsetDrawable(new ColorDrawable(Color.TRANSPARENT),
                (int) getActivity().getResources().getDimension(R.dimen.small_margin)));


        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //If device has wide of a phablet or more display dialog in 2/3rd of the screen
        if (size.x >= MIN_BIG_SCREEN_PORTRAIT_WIDTH && getActivity().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT
                || size.x >= MIN_BIG_SCREEN_LANDSCAPE_WIDTH && getActivity().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {

            window.setLayout((int) (size.x * BIG_SCREEN_PART), ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public interface OnRetryListener {
        void onRetryClick();
    }
}
