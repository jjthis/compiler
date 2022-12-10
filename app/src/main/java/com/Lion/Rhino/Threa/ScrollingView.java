package com.Lion.Rhino.Threa;

/**
 * Created by Me on 2019-03-18.
 */

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;


public class ScrollingView extends NestedScrollView {

    private int maxHeight = 0;
    private final int defaultHeight = 200;

    public ScrollingView(Context context) {
        this(context, null);
    }

    public ScrollingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public ScrollingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        return;

//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollingView);
//
//        try {
//            setMaxHeight();
//        } finally {
//            a.recycle();
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxHeight != 0)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setMaxHeightDp(int maxHeightDp) {
        this.maxHeight = UIUtils.dpToPixels(getContext(), maxHeightDp);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMaxHeightDp() {
        return UIUtils.pixelsToDp(getContext(), maxHeight);
    }

    public void setMaxLines(int line) {
        this.maxHeight = 65 * line;
    }

}

class UIUtils {
    static int dpToPixels(Context c, int dp) {
        return (int) (c.getResources().getDisplayMetrics().density * dp);
    }

    static int pixelsToDp(Context c, int pixel) {
        return (int) ((float) pixel / c.getResources().getDisplayMetrics().density);
    }
}