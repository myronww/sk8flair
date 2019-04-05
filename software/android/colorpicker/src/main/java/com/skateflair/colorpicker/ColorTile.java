package com.skateflair.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ColorTile extends View {

    private int a_CornerRounding = 0;
    private boolean a_IsSelected = false;
    private boolean a_LockedSquare = false;
    private int a_SampleColor = Color.DKGRAY;
    private int a_SelectColor = Color.WHITE;

    private int lo_PaddingLeft;
    private int lo_PaddingTop;
    private int lo_PaddingRight;
    private int lo_PaddingBottom;

    private int lo_ContentHeight;
    private int lo_ContentWidth;

    private float lo_ContentTop;
    private float lo_ContentLeft;
    private float lo_ContentBottom;
    private float lo_ContentRight;

    private float lo_SelectTop;
    private float lo_SelectLeft;
    private float lo_SelectBottom;
    private float lo_SelectRight;

    private float lo_SelectStroke;

    private Paint d_SelectPaint;
    private Paint d_SamplePaint;

    public ColorTile(Context context) {
        super(context);
        init(null, 0);
    }

    public ColorTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ColorTile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ColorTile, defStyle, 0);

        if (a.hasValue(R.styleable.ColorTile_CornerRounding)) {
            a_CornerRounding = a.getInteger(R.styleable.ColorTile_CornerRounding, a_CornerRounding);
        }

        if (a.hasValue(R.styleable.ColorTile_IsSelected)) {
            a_IsSelected = a.getBoolean(R.styleable.ColorTile_IsSelected, false);
        }

        if (a.hasValue(R.styleable.ColorTile_LockSquare)) {
            a_LockedSquare = a.getBoolean(R.styleable.ColorTile_LockSquare, false);
        }

        if (a.hasValue(R.styleable.ColorTile_SampleColor)) {
            a_SampleColor = a.getColor(R.styleable.ColorTile_SampleColor, a_SampleColor);
        }

        if (a.hasValue(R.styleable.ColorTile_SelectColor)) {
            a_SelectColor = a.getColor(R.styleable.ColorTile_SelectColor, a_SelectColor);
        }

        a.recycle();

        reconfigureMeasurements();

        reconfigurePaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (a_CornerRounding == 0) {
            // Draw the color sample
            canvas.drawRect(lo_ContentLeft, lo_ContentTop, lo_ContentRight, lo_ContentBottom, d_SamplePaint);

            if (a_IsSelected) {
                canvas.drawRect(lo_SelectLeft, lo_SelectTop, lo_SelectRight, lo_SelectBottom , d_SelectPaint);
            }
        }
        else {
            // Draw the color sample
            canvas.drawRoundRect(lo_ContentLeft, lo_ContentTop, lo_ContentRight, lo_ContentBottom, a_CornerRounding, a_CornerRounding, d_SamplePaint);

            if (a_IsSelected) {
                canvas.drawRoundRect(lo_SelectLeft, lo_SelectTop, lo_SelectRight, lo_SelectBottom, a_CornerRounding, a_CornerRounding, d_SelectPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (a_LockedSquare) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int size = width > height ? height : width;
            setMeasuredDimension(size, size);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        reconfigureMeasurements();

        reconfigurePaint();
    }


    /**
     * Gets the radius of the corners of the rounded rectangle.
     *
     * @return The value of the radius of the corners of the rounded rectangle.
     */
    public int getCornerRounding() {
        return a_CornerRounding;
    }

    /**
     * Sets the radius of the corners of the rounded rectangle.
     *
     * @param cornerRounding The value of the radius of the corners of the rounded rectangle.
     */
    public void setCornerRounding(int cornerRounding) {
        a_CornerRounding = cornerRounding;
        invalidate();
    }

    /**
     * Gets the value of the 'LockedSquare' attribute value.
     *
     * @return The boolean indicating if the view is locked to a square layout.
     */
    public boolean getLockedSquare() {
        return a_LockedSquare;
    }

    /**
     * Sets the view's measurment method to always reflect a square view.
     *
     * @param lockedSquare A boolean indicating if the view should be square.
     */
    public void setLockedSquare(boolean lockedSquare) {
        a_LockedSquare = lockedSquare;
        requestLayout();
    }

    /**
     * Gets the value indicating if the view is selected.
     *
     * @return The value indicating if the view is selected.
     */
    public boolean getIsSelected() {
        return a_IsSelected;
    }

    /**
     * Sets the view's measurment method to always reflect a square view.
     *
     * @param isSelected A boolean indicating if the view should be selected.
     */
    public void setIsSelected(boolean isSelected) {
        a_IsSelected = isSelected;
        invalidate();
    }

    /**
     * Gets the color used to display the selection cursor.
     *
     * @return The example color attribute value.
     */
    public int getSelectColor() {
        return a_SelectColor;
    }

    /**
     * Sets the color used to display the selection cursor.
     *
     * @param selectColor The select cursor color attribute value to use.
     */
    public void setSelectColor(int selectColor) {
        a_SelectColor = selectColor;
        reconfigurePaint();
        invalidate();
    }

    /**
     * Gets the color used to display the color sample.
     *
     * @return The sample color attribute value.
     */
    public int getSampleColor() {
        return a_SampleColor;
    }

    /**
     * Sets the color used to display the color sample.
     *
     * @param sampleColor The sample color attribute value to use.
     */
    public void setSampleColor(int sampleColor) {
        a_SampleColor = sampleColor;
        reconfigurePaint();
        invalidate();
    }

    private void reconfigurePaint()
    {
        d_SelectPaint = new Paint();
        d_SelectPaint.setStyle(Paint.Style.STROKE);
        d_SelectPaint.setStrokeWidth(lo_SelectStroke);
        d_SelectPaint.setColor(a_SelectColor);

        d_SamplePaint = new Paint();
        d_SamplePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        d_SamplePaint.setColor(a_SampleColor);
    }

    private void reconfigureMeasurements() {
        lo_PaddingLeft = getPaddingLeft();
        lo_PaddingTop = getPaddingTop();
        lo_PaddingRight = getPaddingRight();
        lo_PaddingBottom = getPaddingBottom();

        lo_ContentWidth = getWidth() - (lo_PaddingRight + lo_PaddingLeft);
        lo_ContentHeight = getHeight() - (lo_PaddingTop +  lo_PaddingBottom);

        lo_ContentTop = lo_PaddingTop;
        lo_ContentLeft = lo_PaddingLeft;
        lo_ContentBottom = lo_PaddingTop + lo_ContentHeight;
        lo_ContentRight = lo_PaddingLeft + lo_ContentWidth;

        float content_min = lo_ContentWidth > lo_ContentHeight? lo_ContentHeight : lo_ContentWidth;
        float select_cursor_inset = content_min * 0.1f;

        lo_SelectStroke = select_cursor_inset * 2;

        lo_SelectTop = lo_ContentTop + select_cursor_inset;
        lo_SelectLeft = lo_ContentLeft + select_cursor_inset;
        lo_SelectBottom = lo_ContentBottom - select_cursor_inset;
        lo_SelectRight = lo_ContentRight - select_cursor_inset;
    }


    private void invalidatePaintAndMeasurements()
    {
        reconfigureMeasurements();

        reconfigurePaint();
    }
}
