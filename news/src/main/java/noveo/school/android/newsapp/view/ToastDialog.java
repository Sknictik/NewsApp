package noveo.school.android.newsapp.view;

/**
 * Created by Arseniy Nazarov on 18.01.2015.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.*;
import android.widget.TextView;
import android_news.newsapp.R;

public class ToastDialog extends Dialog {

    public ToastDialog(Context context, String text) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(android.content.Context.
                        LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.toast_dialog_layout, null);
        TextView tv = (TextView) layout.findViewById(R.id.errorMsg);
        tv.setText(text);
        setContentView(layout);

        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new InsetDrawable(new ColorDrawable(Color.TRANSPARENT),
                (int) context.getResources().getDimension(R.dimen.small_margin)));

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        final int MIN_BIG_SCREEN_PORTRAIT_WIDTH = 1000;
        final int MIN_BIG_SCREEN_LANDSCAPE_WIDTH = 1600;
        //If device has wide of a phablet or more display dialog in 2/3rd of the screen
        if (size.x >= MIN_BIG_SCREEN_PORTRAIT_WIDTH && context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT ||
                size.x >= MIN_BIG_SCREEN_LANDSCAPE_WIDTH && context.getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE) {
            layout.getLayoutParams().width = (int) (size.x * 0.66);
        }
        else {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}