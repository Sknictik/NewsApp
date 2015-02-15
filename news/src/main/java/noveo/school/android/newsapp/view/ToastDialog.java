package noveo.school.android.newsapp.view;

/**
 * Error dialog
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Display;
import android.widget.TextView;
import android_news.newsapp.R;
import noveo.school.android.newsapp.retrofit.service.RestClient;

public class ToastDialog extends Dialog {

    private static final int MIN_BIG_SCREEN_PORTRAIT_WIDTH = 1000;
    private static final int MIN_BIG_SCREEN_LANDSCAPE_WIDTH = 1600;
    private static final double BIG_SCREEN_PART = 0.66;
    private final RestClient.Error reason;
    private Activity parentActivity;

    public ToastDialog(final Activity parentActivity, RestClient.Error reason) {
        super(parentActivity);

        this.reason = reason;
        this.parentActivity = parentActivity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) parentActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.toast_dialog_layout, null);

        String errMsg;

        switch (reason) {
            case NO_CONNECTION:
                errMsg = getContext().getString(R.string.no_connection_error);
                break;
            case CONNECTION_TIMEOUT:
                errMsg = getContext().getString(R.string.timeout_error);
                break;
            case UNKNOWN_ERROR:
            default:
                errMsg = getContext().getString(R.string.unknown_error);
                break;
        }

        TextView tv = (TextView) layout.findViewById(R.id.errorMsg);
        tv.setText(errMsg);
        setContentView(layout);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        window.setBackgroundDrawable(new InsetDrawable(new ColorDrawable(Color.TRANSPARENT),
                (int) parentActivity.getResources().getDimension(R.dimen.small_margin)));

        WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //If device has wide of a phablet or more display dialog in 2/3rd of the screen
        if (size.x >= MIN_BIG_SCREEN_PORTRAIT_WIDTH && parentActivity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT
                || size.x >= MIN_BIG_SCREEN_LANDSCAPE_WIDTH && parentActivity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {

            layout.getLayoutParams().width = (int) (size.x * BIG_SCREEN_PART);
        } else {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, dismiss the dialog.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            dismiss();
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            parentActivity.finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    public RestClient.Error getReason() {
        return reason;
    }

}
