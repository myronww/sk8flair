package com.skateflair.flair;

import android.content.Context;
        import android.support.v4.view.ViewPager;
        import android.util.AttributeSet;
        import android.view.MotionEvent;

public class FlairGizmoViewPager extends ViewPager {

    public FlairGizmoViewPager(Context context) {
        super(context);
    }

    public FlairGizmoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}