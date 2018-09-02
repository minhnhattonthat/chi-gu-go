package com.nhatton.ggtalkvn.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class FixRecyclerView extends RecyclerView {

    public FixRecyclerView(Context context) {
        super(context);
    }

    public FixRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
