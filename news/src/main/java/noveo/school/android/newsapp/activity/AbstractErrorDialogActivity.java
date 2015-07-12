package noveo.school.android.newsapp.activity;

import android.app.Activity;
import android.app.FragmentManager;

import noveo.school.android.newsapp.fragment.ErrorDialogFragment;
import noveo.school.android.newsapp.retrofit.service.RestClient;

/**
 * Activity that supports displaying error dialog
 */
public abstract class AbstractErrorDialogActivity extends Activity implements ErrorDialogFragment.OnRetryListener {

    private ErrorDialogFragment errorDialog;

    public static final String ERROR_DIALOG_TAG = "error";

    protected void showErrorDialog(RestClient.Error reason) {
        errorDialog = ErrorDialogFragment.newInstance(reason);
        FragmentManager fm = getFragmentManager();
        errorDialog.setOnRetryListener(this);
        errorDialog.show(fm, ERROR_DIALOG_TAG);
    }

    public ErrorDialogFragment getErrorDialog() {
        return errorDialog;
    }

}
