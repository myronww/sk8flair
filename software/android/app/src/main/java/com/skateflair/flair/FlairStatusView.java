package com.skateflair.flair;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by myron on 3/12/16.
 */
public class FlairStatusView extends View {

    private Bitmap m_FlairIcon;
    private Rect m_FlairIconRect;

    private Bitmap m_FlairInActiveIcon;
    private Rect m_FlairInActiveIconRect;

    private Bitmap m_FlairActiveIcon;
    private Rect m_FlairActiveIconRect;

    private Rect lo_ImageDestRect;

    private boolean m_DeviceActive;

    public FlairStatusView(Context context) {
        super(context);
        Resources res = getResources();

        m_DeviceActive = false;

        m_FlairIcon = null;
        m_FlairIconRect = null;

        m_FlairInActiveIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_flair_inactive);
        m_FlairActiveIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_flair_active);

        m_FlairInActiveIconRect = new Rect(0, 0, m_FlairInActiveIcon.getWidth(), m_FlairInActiveIcon.getHeight());
        m_FlairActiveIconRect = new Rect(0, 0, m_FlairActiveIcon.getWidth(), m_FlairActiveIcon.getHeight());
    }

    public FlairStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = getResources();

        m_DeviceActive = false;

        m_FlairIcon = null;
        m_FlairIconRect = null;

        m_FlairInActiveIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_flair_inactive);
        m_FlairActiveIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_flair_active);

        m_FlairInActiveIconRect = new Rect(0, 0, m_FlairInActiveIcon.getWidth(), m_FlairInActiveIcon.getHeight());
        m_FlairActiveIconRect = new Rect(0, 0, m_FlairActiveIcon.getWidth(), m_FlairActiveIcon.getHeight());
    }

    public FlairStatusView(Context context, AttributeSet attrs, Bitmap flair_icon) {
        super(context, attrs);
        Resources res = getResources();

        m_DeviceActive = false;

        m_FlairIcon = flair_icon;

        m_FlairInActiveIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_flair_inactive);
        m_FlairActiveIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_flair_active);

        m_FlairIconRect = new Rect(0, 0, m_FlairIcon.getWidth(), m_FlairIcon.getHeight());
        m_FlairInActiveIconRect = new Rect(0, 0, m_FlairInActiveIcon.getWidth(), m_FlairInActiveIcon.getHeight());
        m_FlairActiveIconRect = new Rect(0, 0, m_FlairActiveIcon.getWidth(), m_FlairActiveIcon.getHeight());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(m_DeviceActive) {
            canvas.drawBitmap(m_FlairActiveIcon, m_FlairActiveIconRect, lo_ImageDestRect, null);
        }
        else {
            canvas.drawBitmap(m_FlairInActiveIcon, m_FlairInActiveIconRect, lo_ImageDestRect, null);
        }

        if(m_FlairIcon != null) {
            canvas.drawBitmap(m_FlairIcon, m_FlairIconRect, lo_ImageDestRect, null);
        }
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
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        reconfigureMeasurements();
    }

    public void setFlairStatus(boolean active) {
        m_DeviceActive = active;
        invalidate();
    }

    public void setFlairIcon(Bitmap flair_icon) {
        m_FlairIcon = flair_icon;
        int width = m_FlairIcon.getWidth();
        int height = m_FlairIcon.getHeight();
        m_FlairIconRect = new Rect(0, 0, m_FlairIcon.getWidth(), m_FlairIcon.getHeight());
    }

    private void reconfigureMeasurements() {
        float paddingLeft = getPaddingLeft();
        float paddingTop = getPaddingTop();
        float paddingRight = getPaddingRight();
        float paddingBottom = getPaddingBottom();

        float contentWidth = getWidth() - (paddingRight + paddingLeft);
        float contentHeight = getHeight() - (paddingTop +  paddingBottom);

        int contentTop = (int)paddingTop;
        int contentLeft = (int)paddingLeft;
        int contentBottom = (int)(paddingTop + contentHeight);
        int contentRight = (int)(paddingLeft + contentWidth);

        lo_ImageDestRect = new Rect(contentLeft, contentTop, contentRight, contentBottom);
    }
}
