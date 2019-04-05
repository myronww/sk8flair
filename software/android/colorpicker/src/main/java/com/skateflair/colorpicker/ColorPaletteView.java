package com.skateflair.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by myron on 2/26/16.
 */
public class ColorPaletteView extends View {

    public static final String TAG = "ColorPalette";

    static class SavedState extends BaseSavedState {

        protected int p_ColorSelected;
        protected float p_HueCursorAngle;
        protected float p_ShadeCursorAngle;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.p_ColorSelected = in.readInt();
            this.p_HueCursorAngle = in.readFloat();
            this.p_ShadeCursorAngle = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.p_ColorSelected);
            out.writeFloat(this.p_HueCursorAngle);
            out.writeFloat(this.p_ShadeCursorAngle);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public static final double PIDOUBLE = Math.PI * 2;

    final protected Paint m_ColorBandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_BlackoutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_WhiteoutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_HuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_HueCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_ShadeCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_ColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    final protected Paint m_TestPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    final protected float m_GradientPositions[] = {
            0.000000f, 0.200000f, 0.203162f, 0.206324f, 0.209486f,
            0.212648f, 0.215810f, 0.218972f, 0.222134f, 0.225296f,
            0.228458f, 0.231620f, 0.234782f, 0.237944f, 0.241106f,
            0.244268f, 0.247430f, 0.250592f, 0.253754f, 0.256916f,
            0.260078f, 0.263240f, 0.266402f, 0.269564f, 0.272726f,
            0.275888f, 0.279050f, 0.282212f, 0.285374f, 0.288536f,
            0.291698f, 0.294860f, 0.298022f, 0.301184f, 0.304346f,
            0.307508f, 0.310670f, 0.313832f, 0.316994f, 0.320156f,
            0.323318f, 0.326480f, 0.329642f, 0.332804f, 0.335966f,
            0.339128f, 0.342290f, 0.345452f, 0.348614f, 0.351776f,
            0.354938f, 0.358100f, 0.361262f, 0.364424f, 0.367586f,
            0.370748f, 0.373910f, 0.377072f, 0.380234f, 0.383396f,
            0.386558f, 0.389720f, 0.392882f, 0.396044f, 0.399206f,
            0.402368f, 0.405530f, 0.408692f, 0.411854f, 0.415016f,
            0.418178f, 0.421340f, 0.424502f, 0.427664f, 0.430826f,
            0.433988f, 0.437150f, 0.440312f, 0.443474f, 0.446636f,
            0.449798f, 0.452960f, 0.456122f, 0.459284f, 0.462446f,
            0.465608f, 0.468770f, 0.471932f, 0.475094f, 0.478256f,
            0.481418f, 0.484580f, 0.487742f, 0.490904f, 0.494066f,
            0.497228f, 0.500390f, 0.503552f, 0.506714f, 0.509876f,
            0.513038f, 0.516200f, 0.519362f, 0.522524f, 0.525686f,
            0.528848f, 0.532010f, 0.535172f, 0.538334f, 0.541496f,
            0.544658f, 0.547820f, 0.550982f, 0.554144f, 0.557306f,
            0.560468f, 0.563630f, 0.566792f, 0.569954f, 0.573116f,
            0.576278f, 0.579440f, 0.582602f, 0.585764f, 0.588926f,
            0.592088f, 0.595250f, 0.598412f, 0.601574f, 0.604736f,
            0.607898f, 0.611060f, 0.614222f, 0.617384f, 0.620546f,
            0.623708f, 0.626870f, 0.630032f, 0.633194f, 0.636356f,
            0.639518f, 0.642680f, 0.645842f, 0.649004f, 0.652166f,
            0.655328f, 0.658490f, 0.661652f, 0.664814f, 0.667976f,
            0.671138f, 0.674300f, 0.677462f, 0.680624f, 0.683786f,
            0.686948f, 0.690110f, 0.693272f, 0.696434f, 0.699596f,
            0.702758f, 0.705920f, 0.709082f, 0.712244f, 0.715406f,
            0.718568f, 0.721730f, 0.724892f, 0.728054f, 0.731216f,
            0.734378f, 0.737540f, 0.740702f, 0.743864f, 0.747026f,
            0.750188f, 0.753350f, 0.756512f, 0.759674f, 0.762836f,
            0.765998f, 0.769160f, 0.772322f, 0.775484f, 0.778646f,
            0.781808f, 0.784970f, 0.788132f, 0.791294f, 0.794456f,
            0.797618f, 0.800780f, 0.803942f, 0.807104f, 0.810266f,
            0.813428f, 0.816590f, 0.819752f, 0.822914f, 0.826076f,
            0.829238f, 0.832400f, 0.835562f, 0.838724f, 0.841886f,
            0.845048f, 0.848210f, 0.851372f, 0.854534f, 0.857696f,
            0.860858f, 0.864020f, 0.867182f, 0.870344f, 0.873506f,
            0.876668f, 0.879830f, 0.882992f, 0.886154f, 0.889316f,
            0.892478f, 0.895640f, 0.898802f, 0.901964f, 0.905126f,
            0.908288f, 0.911450f, 0.914612f, 0.917774f, 0.920936f,
            0.924098f, 0.927260f, 0.930422f, 0.933584f, 0.936746f,
            0.939908f, 0.943070f, 0.946232f, 0.949394f, 0.952556f,
            0.955718f, 0.958880f, 0.962042f, 0.965204f, 0.968366f,
            0.971528f, 0.974690f, 0.977852f, 0.981014f, 0.984176f,
            0.987338f, 0.990500f, 0.993662f, 0.996824f, 1.000000f,

    };

    private int p_ColorSelected;
    private float p_HueCursorAngle;
    private float p_ShadeCursorAngle;

    private RectF m_PalletRect = null;

    private float m_CenterY;
    private float m_CenterX;

    private float m_BandWidth;
    private float m_BandHalfWidth;

    private float m_ColorCursorX;
    private float m_ColorCursorY;
    private float m_ColorDotRadius;

    private float m_HueCursorRadius;
    private float m_HueCursorLineWidth;

    private float m_HueBandMiddleRadius;
    private float m_HueBandInnerRadius;
    private float m_HueBandOuterRadius;

    private CircleMeasure m_HueBandInnerMeasure;
    private CircleMeasure m_HueBandOuterMeasure;

    private float m_HueCursorX;
    private float m_HueCursorY;

    private float m_ShadeCursorExtent;
    private float m_ShadeCursorRadius;
    private float m_ShadeCursorX;
    private float m_ShadeCursorY;

    private float m_BlackoutTailX;
    private float m_BlackoutTailY;
    private float m_BlackoutScaler;
    private float m_BlackoutTailAngle;

    private float m_CommonHeadX;
    private float m_CommonHeadY;

    private float m_WhiteoutTailX;
    private float m_WhiteoutTailY;
    private float m_WhiteoutScaler;
    private float m_WhiteoutTailAngle;

    private TouchLine m_BlackoutMeasure;
    private TouchLine m_WhiteoutMeasure;

    private float m_VarBandMiddleRadius;

    private int mActivePointerId;

    private ColorMap1440 m_ColorMap;

    private ArrayList<IOnColorSelectedListener> m_ColorChangeListeners = new ArrayList<IOnColorSelectedListener>();

    public ColorPaletteView(Context context) {
        super(context);

        init();
    }

    public ColorPaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ColorPaletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public ColorPaletteView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }


    private void init() {
        mActivePointerId = MotionEvent.INVALID_POINTER_ID;

        p_HueCursorAngle = 0.0f;
        p_ShadeCursorAngle = 0.0f;

        m_BlackoutScaler = -1.0f;
        m_WhiteoutScaler = -1.0f;

        // Calculate Initial Layout Angles
        m_BlackoutTailAngle = (p_HueCursorAngle + 120.0f) % 360.0f;
        m_WhiteoutTailAngle = (p_HueCursorAngle + 240.0f) % 360.0f;

        m_HueCursorLineWidth = 20.0f;


        m_ColorMap = new ColorMap1440();
        m_ColorMap.setHueAngleDegrees(p_HueCursorAngle);

        m_ColorBandPaint.setStyle(Paint.Style.STROKE);
        m_ColorPaint.setStyle(Paint.Style.FILL);

        m_BlackoutPaint.setStyle(Paint.Style.STROKE);
        m_BlackoutPaint.setStrokeCap(Paint.Cap.ROUND);

        m_WhiteoutPaint.setStyle(Paint.Style.STROKE);
        m_WhiteoutPaint.setStrokeCap(Paint.Cap.ROUND);

        m_HuePaint.setStyle(Paint.Style.STROKE);
        m_HuePaint.setStrokeCap(Paint.Cap.ROUND);

        m_HueCursorPaint.setColor(0xffffffff);
        m_HueCursorPaint.setStyle(Paint.Style.STROKE);
        m_HueCursorPaint.setStrokeWidth(m_HueCursorLineWidth);

        m_ShadeCursorPaint.setColor(0xffffffff);
        m_ShadeCursorPaint.setStyle(Paint.Style.STROKE);
        m_ShadeCursorPaint.setStrokeWidth(m_HueCursorLineWidth);

        m_TestPaint.setColor(0xff000000);
        m_TestPaint.setStyle(Paint.Style.STROKE);
        m_TestPaint.setStrokeWidth(10.0f);
    }

    public int getSelectedColor() {
        return p_ColorSelected;
    }

    public float getHueAngle() {
        return p_HueCursorAngle;
    }

    public float getShadeAngle() {
        return p_ShadeCursorAngle;
    }

    public void setPaletteParameters(int selectedColor, float hueAngle, float shadeAngle) {
        p_ColorSelected = selectedColor;

        recalculate_hue_position_from_angle(hueAngle);

        recalculate_shade_position_from_angle(shadeAngle);
    }

    public void addOnColorSelectedListener(IOnColorSelectedListener listener) {
        this.m_ColorChangeListeners.add(listener);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        recalculate_layout_and_bands(w, h);

        recalculate_palette_xy_coordinates_touchlines();

        recalculate_hue_position_from_angle(this.p_HueCursorAngle);

        recalculate_shade_position_from_angle(this.p_ShadeCursorAngle);

        update_drawing_paints_and_shaders();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int[] color_table = m_ColorMap.getPrimaryHueColorTable();

        float rad_next = 0;
        for (int ci_next = 0; ci_next < ColorMap1440.STEP_COUNT; ci_next += 1) {
            int color_int = color_table[ci_next];
            m_ColorBandPaint.setColor(color_int);

            float rad_next_end = rad_next + ColorMap1440.DEGREES_PER_STEP;

            canvas.drawArc(m_PalletRect, rad_next, ColorMap1440.DEGREES_PER_STEP, false, m_ColorBandPaint);

            rad_next = rad_next_end;
        }

        canvas.drawCircle(m_HueCursorX, m_HueCursorY, m_HueCursorRadius, m_HueCursorPaint);

        canvas.drawLine(m_CommonHeadX, m_CommonHeadY, m_WhiteoutTailX, m_WhiteoutTailY, m_WhiteoutPaint);
        canvas.drawLine(m_CommonHeadX, m_CommonHeadY, m_BlackoutTailX, m_BlackoutTailY, m_BlackoutPaint);

        canvas.drawCircle(m_ShadeCursorX, m_ShadeCursorY, m_ShadeCursorRadius, m_ShadeCursorPaint);

        canvas.drawCircle(m_ColorCursorX, m_ColorCursorY, m_ColorDotRadius, m_ColorPaint);
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
    public void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end

        this.p_ColorSelected = ss.p_ColorSelected;
        this.p_HueCursorAngle = ss.p_HueCursorAngle;
        this.p_ShadeCursorAngle = ss.p_ShadeCursorAngle;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.p_ColorSelected = this.p_ColorSelected;
        ss.p_HueCursorAngle = this.p_HueCursorAngle;
        ss.p_ShadeCursorAngle = this.p_ShadeCursorAngle;

        return ss;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean start_drag = super.onTouchEvent(event);

        int action = event.getActionMasked();

        if ((action == MotionEvent.ACTION_DOWN) || (action == MotionEvent.ACTION_MOVE)) {

            if (action == MotionEvent.ACTION_DOWN) {
                mActivePointerId = event.getPointerId(0);

            }

            start_drag = true;

            int pointer_index = event.findPointerIndex(mActivePointerId);

            float mx = event.getX(pointer_index);
            float my = event.getY(pointer_index);

            float tx = mx - getLeft();
            float ty = my - getTop();

            recalculate_based_on_touch(tx, ty);

        }
        else if (action == MotionEvent.ACTION_UP) {
            mActivePointerId = MotionEvent.INVALID_POINTER_ID;
        }

        return start_drag;
    }

    private void notify_color_change_listeners() {
        if (m_ColorChangeListeners != null) {
            for (IOnColorSelectedListener listener : m_ColorChangeListeners) {
                try {
                    listener.onColorSelected(p_ColorSelected);
                } catch (Exception e) {
                    //Squash individual listener exceptions
                }
            }
        }
    }

    private void recalculate_based_on_touch(float tx, float ty) {

        if(m_HueBandOuterMeasure.ContainsPoint(tx, ty) && !m_HueBandInnerMeasure.ContainsPoint(tx, ty))
        {
            // Capture the difference angle for the shade curser before we update the hue cursor angle
            float shade_angle_diff =  p_ShadeCursorAngle - p_HueCursorAngle;

            p_HueCursorAngle = m_HueBandOuterMeasure.RelativeAngleDegrees(tx, ty);

            // Update the color view angles
            recalculate_palette_view_angles(shade_angle_diff);

            // Re-calculate all the X, Y coordinates for the UI and the measurement touchlines
            recalculate_palette_xy_coordinates_touchlines();

            // Update the color map so we can pull the new shade tables
            recalculate_colormap_shades_and_selected_color();

            // Update the color objects and shaders
            update_drawing_paints_and_shaders();

            // Notify any listeners of the color change
            notify_color_change_listeners();

            // Invalidate the view
            invalidate();
        }
        else if(m_BlackoutMeasure.Contains(tx, ty) && m_WhiteoutMeasure.Contains(tx, ty)){
            p_ShadeCursorAngle = p_HueCursorAngle;
            m_ShadeCursorExtent = m_VarBandMiddleRadius;
            m_ShadeCursorX = ((float) Math.cos(Math.toRadians(p_ShadeCursorAngle)) * m_ShadeCursorExtent) + m_CenterX;
            m_ShadeCursorY = ((float) Math.sin(Math.toRadians(p_ShadeCursorAngle)) * m_ShadeCursorExtent) + m_CenterY;

            m_BlackoutScaler = -1.0f;
            m_WhiteoutScaler = -1.0f;

            recalculate_colormap_shades_and_selected_color();

            update_drawing_paints_and_shaders();

            notify_color_change_listeners();

            invalidate();
        }
        else if(m_BlackoutMeasure.Contains(tx, ty)){
            PointF centerPt = new PointF(m_CenterX, m_CenterY);
            PointF checkPt = LineOperations.ExtendLine(m_CenterX, m_CenterY, tx, ty, 2.0f);
            PointF intersect = m_BlackoutMeasure.Intersection(centerPt, checkPt);
            if(intersect != null){
                m_ShadeCursorX = intersect.x;
                m_ShadeCursorY = intersect.y;

                float adj_x = m_ShadeCursorX - m_CenterX;
                float adj_y = m_ShadeCursorY - m_CenterY;
                float sh_radius = (float)Math.sqrt((adj_x * adj_x) + (adj_y * adj_y));
                p_ShadeCursorAngle = (float)Math.toDegrees(Math.acos(adj_x / sh_radius));
                m_ShadeCursorExtent = (float)Math.sqrt( (adj_x * adj_x) + (adj_y * adj_y) );

                m_BlackoutScaler = m_BlackoutMeasure.ScalerForPoint(intersect);
                m_WhiteoutScaler = -1.0f;

                recalculate_colormap_shades_and_selected_color();

                update_drawing_paints_and_shaders();

                notify_color_change_listeners();

                invalidate();
            }
        }
        else if(m_WhiteoutMeasure.Contains(tx, ty)){
            PointF centerPt = new PointF(m_CenterX, m_CenterY);
            PointF checkPt = LineOperations.ExtendLine(m_CenterX, m_CenterY, tx, ty, 2.0f);
            PointF intersect = m_WhiteoutMeasure.Intersection(centerPt, checkPt);
            if(intersect != null){
                m_ShadeCursorX = intersect.x;
                m_ShadeCursorY = intersect.y;

                float adj_x = m_ShadeCursorX - m_CenterX;
                float adj_y = m_ShadeCursorY - m_CenterY;
                float sh_radius = (float)Math.sqrt((adj_x * adj_x) + (adj_y * adj_y));
                p_ShadeCursorAngle = (float)Math.toDegrees(Math.acos(adj_x / sh_radius));
                m_ShadeCursorExtent = (float)Math.sqrt((adj_x * adj_x) + (adj_y * adj_y));

                m_BlackoutScaler = -1.0f;
                m_WhiteoutScaler = m_WhiteoutMeasure.ScalerForPoint(intersect);

                recalculate_colormap_shades_and_selected_color();

                update_drawing_paints_and_shaders();

                notify_color_change_listeners();

                invalidate();
            }
        }

    }

    private void recalculate_colormap_shades_and_selected_color() {
        m_ColorMap.setHueAngleDegrees(p_HueCursorAngle);

        // Update the selected color
        if ((m_BlackoutScaler == -1) && (m_WhiteoutScaler == -1)) {
            p_ColorSelected = m_ColorMap.getPrimaryHue();
        }
        else if (m_BlackoutScaler != -1) {
            int[] shade_table = m_ColorMap.getBlackoutColorTable();
            int shade_index = (int)((float)shade_table.length * m_BlackoutScaler);
            p_ColorSelected = shade_table[shade_index];
        }
        else if (m_WhiteoutScaler != -1) {
            int[] shade_table = m_ColorMap.getWhiteoutColorTable();
            int shade_index = (int)((float)shade_table.length * m_WhiteoutScaler);
            p_ColorSelected = shade_table[shade_index];
        }
    }

    private void recalculate_hue_position_from_angle(float hueAngle) {
        PointF hue_band_pnt = m_HueBandOuterMeasure.CalculateCoordinateUsingDegrees(hueAngle);

        recalculate_based_on_touch(hue_band_pnt.x, hue_band_pnt.y);
    }

    private void recalculate_shade_position_from_angle(float shadeAngle) {
        PointF shade_pnt = m_HueBandOuterMeasure.CalculateCoordinateUsingDegrees(shadeAngle);
        PointF intercept = null;

        PointF bo_intercept = m_BlackoutMeasure.Intersection(m_CenterX, m_CenterY, shade_pnt.x, shade_pnt.y);
        PointF wo_intercept = m_WhiteoutMeasure.Intersection(m_CenterX, m_CenterY, shade_pnt.x, shade_pnt.y);
        if ((bo_intercept != null) && (wo_intercept != null)) {
            float bo_len = LineOperations.LineLength(m_CenterX, m_CenterY, bo_intercept.x, bo_intercept.y);
            float wo_len = LineOperations.LineLength(m_CenterX, m_CenterY, wo_intercept.x, wo_intercept.y);

            if (bo_len < wo_len) {
                intercept = bo_intercept;
            }
            else {
                intercept = wo_intercept;
            }
        }
        else if (bo_intercept != null) {
            intercept = bo_intercept;
        }
        else {
            intercept = wo_intercept;
        }

        recalculate_based_on_touch(intercept.x, intercept.y);
    }

    private void recalculate_layout_and_bands(int w, int h) {
        float padding_left = getPaddingLeft();
        float padding_right = getPaddingRight();
        float padding_top = getPaddingTop();
        float padding_bottom = getPaddingBottom();

        float pad_width = w - (padding_left + padding_right);
        float pad_height = h - (padding_top + padding_bottom);


        m_CenterX = pad_width / 2.0f;
        m_CenterY = pad_height / 2.0f;

        m_HueBandOuterRadius = m_CenterY < m_CenterX ? m_CenterY : m_CenterX;

        m_BandWidth = (m_HueBandOuterRadius / 4.0f);
        m_BandHalfWidth = m_BandWidth / 2.0f;
        m_HueCursorRadius = m_BandHalfWidth - m_HueCursorLineWidth;

        m_ColorDotRadius = m_BandWidth / 2.0f;

        m_HueBandInnerRadius = m_HueBandOuterRadius - m_BandWidth;
        m_HueBandMiddleRadius =  m_HueBandOuterRadius - m_BandHalfWidth;

        m_HueBandInnerMeasure = new CircleMeasure(m_CenterX, m_CenterY, m_HueBandInnerRadius);
        m_HueBandOuterMeasure = new CircleMeasure(m_CenterX, m_CenterY, m_HueBandOuterRadius);

        m_PalletRect = new RectF(m_CenterX - m_HueBandMiddleRadius, m_CenterY - m_HueBandMiddleRadius, m_CenterX + m_HueBandMiddleRadius, m_CenterY + m_HueBandMiddleRadius);

        m_VarBandMiddleRadius = m_HueBandInnerRadius - m_BandWidth;

        m_ShadeCursorRadius = m_BandHalfWidth - m_HueCursorLineWidth;
        m_ShadeCursorExtent = m_VarBandMiddleRadius;

        m_BlackoutPaint.setStrokeWidth(m_BandWidth);
        m_WhiteoutPaint.setStrokeWidth(m_BandWidth);
        m_ColorBandPaint.setStrokeWidth(m_BandWidth);
        m_HuePaint.setStrokeWidth(m_BandWidth);

    }

    private void recalculate_palette_view_angles(float shade_angle_diff) {

        m_BlackoutTailAngle = (p_HueCursorAngle + 120.0f) % 360.0f;
        m_WhiteoutTailAngle = (p_HueCursorAngle + 240.0f) % 360.0f;
        p_ShadeCursorAngle = (p_HueCursorAngle + (360.0f + shade_angle_diff)) % 360.0f;
    }

    private void recalculate_palette_xy_coordinates_touchlines() {
        m_HueCursorX = ((float) Math.cos(Math.toRadians(p_HueCursorAngle)) * m_HueBandMiddleRadius) + m_CenterX;
        m_HueCursorY = ((float) Math.sin(Math.toRadians(p_HueCursorAngle)) * m_HueBandMiddleRadius) + m_CenterY;

        m_ShadeCursorX = ((float) Math.cos(Math.toRadians(p_ShadeCursorAngle)) * m_ShadeCursorExtent) + m_CenterX;
        m_ShadeCursorY = ((float) Math.sin(Math.toRadians(p_ShadeCursorAngle)) * m_ShadeCursorExtent) + m_CenterY;

        m_BlackoutTailX = ((float)Math.cos(Math.toRadians(m_BlackoutTailAngle)) * m_VarBandMiddleRadius) + m_CenterX;
        m_BlackoutTailY = ((float)Math.sin(Math.toRadians(m_BlackoutTailAngle)) * m_VarBandMiddleRadius) + m_CenterY;

        m_CommonHeadX = ((float)Math.cos(Math.toRadians(p_HueCursorAngle)) * m_VarBandMiddleRadius) + m_CenterX;
        m_CommonHeadY = ((float)Math.sin(Math.toRadians(p_HueCursorAngle)) * m_VarBandMiddleRadius) + m_CenterY;

        m_WhiteoutTailX = ((float)Math.cos(Math.toRadians(m_WhiteoutTailAngle)) * m_VarBandMiddleRadius) + m_CenterX;
        m_WhiteoutTailY = ((float)Math.sin(Math.toRadians(m_WhiteoutTailAngle)) * m_VarBandMiddleRadius) + m_CenterY;

        m_ColorCursorX = (m_BlackoutTailX + m_WhiteoutTailX) / 2.0f;
        m_ColorCursorY = (m_BlackoutTailY + m_WhiteoutTailY) / 2.0f;

        m_BlackoutMeasure = new TouchLine(m_BlackoutTailX, m_BlackoutTailY, m_CommonHeadX, m_CommonHeadY, m_BandWidth);
        m_WhiteoutMeasure = new TouchLine(m_WhiteoutTailX, m_WhiteoutTailY, m_CommonHeadX, m_CommonHeadY, m_BandWidth);
    }

    private void update_drawing_paints_and_shaders() {
        m_HuePaint.setColor(m_ColorMap.getPrimaryHue());
        m_BlackoutPaint.setShader(new LinearGradient(m_CommonHeadX, m_CommonHeadY, m_BlackoutTailX, m_BlackoutTailY,
                m_ColorMap.getBlackoutColorTable(), m_GradientPositions, Shader.TileMode.MIRROR));

        m_WhiteoutPaint.setShader(new LinearGradient(m_CommonHeadX, m_CommonHeadY, m_WhiteoutTailX, m_WhiteoutTailY,
                m_ColorMap.getWhiteoutColorTable(), m_GradientPositions, Shader.TileMode.MIRROR));

        m_ColorPaint.setColor(p_ColorSelected);
    }

}
