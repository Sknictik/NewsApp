package noveo.school.android.newsapp;

import android.content.res.Resources;
import android.content.res.TypedArray;

/**
 * Utility class for commonplace functions
 */
public final class NewsUtils {

    private NewsUtils() { }

    public static TypedArray getTypedArray(Resources res, int arrId) {
        return res.obtainTypedArray(arrId);
    }
}
