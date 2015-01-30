package noveo.school.android.newsapp;

import android.content.Context;
import android.content.DialogInterface;
import android_news.newsapp.R;
import noveo.school.android.newsapp.view.ToastDialog;

/**
 * Created by Arseniy Nazarov on 23.01.2015.
 */
public class Utils {

    public static ToastDialog showErrorDialog(Context context) {
        final ToastDialog errorDialog = new ToastDialog(context,
                context.getResources().getString(R.string.no_connection_error));
        errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        errorDialog.show();
        return errorDialog;
    }

}
