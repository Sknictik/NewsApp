package noveo.school.android.newsapp.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/*
 * This class is used inside of
  * ListView that needs to have checkable items.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private CheckedTextView checkbox;

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
                checkbox = (CheckedTextView) v;
            }
        }
    }

    @Override
    public boolean isChecked() {
        return checkbox != null && checkbox.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked) {
            checkbox.setBackgroundColor(highlightColor);
        } else {
            StateListDrawable states = new StateListDrawable();
            states.addState(new int[]{android.R.attr.state_checked},
                    new ColorDrawable(highlightColor));
            checkbox.setBackgroundDrawable(states);
        }
    }

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
    }

    @Override
    public void toggle() {
        if (checkbox != null) {
            checkbox.toggle();
        }
    }
}
