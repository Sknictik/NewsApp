package noveo.school.android.newsapp.view;

/**
 * Created by Arseniy Nazarov on 18.01.2015.
 */

import android.app.Dialog;
import android.content.Context;
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
        show();
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}