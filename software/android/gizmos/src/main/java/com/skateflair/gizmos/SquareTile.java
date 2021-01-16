package com.skateflair.gizmos;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class SquareTile extends View {


    public SquareTile(Context context) {
        super(context);
        init(null, 0);
    }

    public SquareTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SquareTile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }



}

