package noveo.school.android.newsapp.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/*
 * This class is useful for using inside of ListView that needs to have checkable items.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private CheckedTextView _checkbox;

    private int highlightColor;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // find checked text view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View v = getChildAt(i);
            if (v instanceof CheckedTextView) {
                _checkbox = (CheckedTextView)v;
            }
        }
    }

    @Override
    public boolean isChecked() {
        return _checkbox != null && _checkbox.isChecked();
    }

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked) {
            _checkbox.setBackgroundColor(highlightColor);
        } else {
            StateListDrawable states = new StateListDrawable();
            states.addState(new int[] {android.R.attr.state_pressed},
                    new ColorDrawable(highlightColor));
            states.addState(new int[] {android.R.attr.state_checked},
                    new ColorDrawable(highlightColor));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                _checkbox.setBackground(states);
            }
            else {
                _checkbox.setBackgroundDrawable(states);
            }

        }
    }

    @Override
    public void toggle() {
        if (_checkbox != null) {
            _checkbox.toggle();
        }
    }
}
