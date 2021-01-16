package com.skateflair.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
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

    final protected Paint m_BlackoutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_WhiteoutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_HuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_HueCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_ShadeCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final protected Paint m_ColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    final protected Paint m_TestPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    final protected Paint m_BitmapPaint = new Paint(Paint.DITHER_FLAG);

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

    final protected int m_GrayScaleColors[] = {
            0xff000000, 0xff010101, 0xff020202, 0xff030303, 0xff040404, 0xff050505, 0xff060606, 0xff070707,
            0xff080808, 0xff090909, 0xff0a0a0a, 0xff0b0b0b, 0xff0c0c0c, 0xff0d0d0d, 0xff0e0e0e, 0xff0f0f0f,
            0xff101010, 0xff111111, 0xff121212, 0xff131313, 0xff141414, 0xff151515, 0xff161616, 0xff171717,
            0xff181818, 0xff191919, 0xff1a1a1a, 0xff1b1b1b, 0xff1c1c1c, 0xff1d1d1d, 0xff1e1e1e, 0xff1f1f1f,
            0xff202020, 0xff212121, 0xff222222, 0xff232323, 0xff242424, 0xff252525, 0xff262626, 0xff272727,
            0xff282828, 0xff292929, 0xff2a2a2a, 0xff2b2b2b, 0xff2c2c2c, 0xff2d2d2d, 0xff2e2e2e, 0xff2f2f2f,
            0xff303030, 0xff313131, 0xff323232, 0xff333333, 0xff343434, 0xff353535, 0xff363636, 0xff373737,
            0xff383838, 0xff393939, 0xff3a3a3a, 0xff3b3b3b, 0xff3c3c3c, 0xff3d3d3d, 0xff3e3e3e, 0xff3f3f3f,
            0xff404040, 0xff414141, 0xff424242, 0xff434343, 0xff444444, 0xff454545, 0xff464646, 0xff474747,
            0xff484848, 0xff494949, 0xff4a4a4a, 0xff4b4b4b, 0xff4c4c4c, 0xff4d4d4d, 0xff4e4e4e, 0xff4f4f4f,
            0xff505050, 0xff515151, 0xff525252, 0xff535353, 0xff545454, 0xff555555, 0xff565656, 0xff575757,
            0xff585858, 0xff595959, 0xff5a5a5a, 0xff5b5b5b, 0xff5c5c5c, 0xff5d5d5d, 0xff5e5e5e, 0xff5f5f5f,
            0xff606060, 0xff616161, 0xff626262, 0xff636363, 0xff646464, 0xff656565, 0xff666666, 0xff676767,
            0xff686868, 0xff696969, 0xff6a6a6a, 0xff6b6b6b, 0xff6c6c6c, 0xff6d6d6d, 0xff6e6e6e, 0xff6f6f6f,
            0xff707070, 0xff717171, 0xff727272, 0xff737373, 0xff747474, 0xff757575, 0xff767676, 0xff777777,
            0xff787878, 0xff797979, 0xff7a7a7a, 0xff7b7b7b, 0xff7c7c7c, 0xff7d7d7d, 0xff7e7e7e, 0xff7f7f7f,
            0xff808080, 0xff818181, 0xff828282, 0xff838383, 0xff848484, 0xff858585, 0xff868686, 0xff878787,
            0xff888888, 0xff898989, 0xff8a8a8a, 0xff8b8b8b, 0xff8c8c8c, 0xff8d8d8d, 0xff8e8e8e, 0xff8f8f8f,
            0xff909090, 0xff919191, 0xff929292, 0xff939393, 0xff949494, 0xff959595, 0xff969696, 0xff979797,
            0xff989898, 0xff999999, 0xff9a9a9a, 0xff9b9b9b, 0xff9c9c9c, 0xff9d9d9d, 0xff9e9e9e, 0xff9f9f9f,
            0xffa0a0a0, 0xffa1a1a1, 0xffa2a2a2, 0xffa3a3a3, 0xffa4a4a4, 0xffa5a5a5, 0xffa6a6a6, 0xffa7a7a7,
            0xffa8a8a8, 0xffa9a9a9, 0xffaaaaaa, 0xffababab, 0xffacacac, 0xffadadad, 0xffaeaeae, 0xffafafaf,
            0xffb0b0b0, 0xffb1b1b1, 0xffb2b2b2, 0xffb3b3b3, 0xffb4b4b4, 0xffb5b5b5, 0xffb6b6b6, 0xffb7b7b7,
            0xffb8b8b8, 0xffb9b9b9, 0xffbababa, 0xffbbbbbb, 0xffbcbcbc, 0xffbdbdbd, 0xffbebebe, 0xffbfbfbf,
            0xffc0c0c0, 0xffc1c1c1, 0xffc2c2c2, 0xffc3c3c3, 0xffc4c4c4, 0xffc5c5c5, 0xffc6c6c6, 0xffc7c7c7,
            0xffc8c8c8, 0xffc9c9c9, 0xffcacaca, 0xffcbcbcb, 0xffcccccc, 0xffcdcdcd, 0xffcecece, 0xffcfcfcf,
            0xffd0d0d0, 0xffd1d1d1, 0xffd2d2d2, 0xffd3d3d3, 0xffd4d4d4, 0xffd5d5d5, 0xffd6d6d6, 0xffd7d7d7,
            0xffd8d8d8, 0xffd9d9d9, 0xffdadada, 0xffdbdbdb, 0xffdcdcdc, 0xffdddddd, 0xffdedede, 0xffdfdfdf,
            0xffe0e0e0, 0xffe1e1e1, 0xffe2e2e2, 0xffe3e3e3, 0xffe4e4e4, 0xffe5e5e5, 0xffe6e6e6, 0xffe7e7e7,
            0xffe8e8e8, 0xffe9e9e9, 0xffeaeaea, 0xffebebeb, 0xffececec, 0xffededed, 0xffeeeeee, 0xffefefef,
            0xfff0f0f0, 0xfff1f1f1, 0xfff2f2f2, 0xfff3f3f3, 0xfff4f4f4, 0xfff5f5f5, 0xfff6f6f6, 0xfff7f7f7,
            0xfff8f8f8, 0xfff9f9f9, 0xfffafafa, 0xfffbfbfb, 0xfffcfcfc, 0xfffdfdfd, 0xfffefefe, 0xffffffff
    };

    private int p_ColorSelected;
    private float p_HueCursorAngle;
    private float p_ShadeCursorAngle;

    private RectF m_PalletRect = null;
    private RectF m_ShadeRect = null;

    private float m_CenterY;
    private float m_CenterX;

    private float m_BandWidth;
    private float m_BandHalfWidth;

    private Bitmap m_ColorHueBitmap;
    private Canvas m_ColorHueCanvas;

    private float m_HueCursorRadius;
    private float m_HueCursorLineWidth;

    private float m_HueBandMiddleRadius;
    private float m_HueBandInnerRadius;
    private float m_HueBandOuterRadius;

    private CircleMeasure m_HueBandInnerMeasure;
    private CircleMeasure m_HueBandOuterMeasure;

    private float m_HueCursorX;
    private float m_HueCursorY;

    private Bitmap m_ColorShadeBitmap;
    private Canvas m_ColorShadeCanvas;

    private CircleMeasure m_ShadeOuterMeasure;
    private CircleMeasure m_ShadeMiddleMeasure;
    private CircleMeasure m_ShadeInnerMeasure;

    private float m_ShadeCursorRadius;
    private float m_ShadeCursorX;
    private float m_ShadeCursorY;

    private float m_ShadeMiddleRadius;
    private float m_ShadeInnerRadius;
    private float m_ShadeOuterRadius;
    private float m_ShadeLowerBoundAngle;
    private float m_ShadeUpperBoundAngle;

    private boolean m_SetColor;

    private int m_ActivePointerId;

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
        m_SetColor = false;

        m_ActivePointerId = MotionEvent.INVALID_POINTER_ID;

        p_HueCursorAngle = 0.0f;
        p_ShadeCursorAngle = 0.0f;

        m_HueCursorLineWidth = 20.0f;

        m_ColorMap = new ColorMap1440();
        m_ColorMap.setHueAngleDegrees(p_HueCursorAngle);

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
        m_SetColor = true;
        p_ColorSelected = selectedColor;

        recalculate_hue_position_from_angle(hueAngle);

        recalculate_shade_position_from_angle(shadeAngle);
    }

    public void addOnColorSelectedListener(IOnColorSelectedListener listener) {
        this.m_ColorChangeListeners.add(listener);
    }

    public void recalculate_color_section_band() {
        // Draw the offscreen bitmap for the color selection band
        Paint colorBandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorBandPaint.setStyle(Paint.Style.STROKE);
        colorBandPaint.setStrokeWidth(m_BandWidth);

        int[] color_table = m_ColorMap.getPrimaryHueColorTable();

        float rad_next = 0;
        for (int ci_next = 0; ci_next < ColorMap1440.STEP_COUNT; ci_next += 1) {
            int color_int = color_table[ci_next];
            colorBandPaint.setColor(color_int);

            float rad_next_end = rad_next + ColorMap1440.DEGREES_PER_STEP;

            m_ColorHueCanvas.drawArc(m_PalletRect, rad_next, ColorMap1440.DEGREES_PER_STEP, false, colorBandPaint);

            rad_next = rad_next_end;
        }
    }

    public void recalculate_color_shade_band() {
        // Draw the offscreen bitmap for the color selection band
        Paint colorBandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorBandPaint.setStyle(Paint.Style.STROKE);
        colorBandPaint.setStrokeWidth(m_BandHalfWidth);

        int[] black_color_table = m_ColorMap.getBlackoutColorTable();
        int[] white_color_table = m_ColorMap.getWhiteoutColorTable();

        int blackout_len = black_color_table.length;
        int whiteout_len = white_color_table.length;

        float rndRadius = m_BandHalfWidth * 0.5f;
        float blackRefAngle =  360.0f - ((float)blackout_len * ColorMap1440.DEGREES_PER_STEP);
        int color_int = black_color_table[blackout_len - 1];
        colorBandPaint.setColor(color_int);
        PointF lowerRndPos = m_ShadeMiddleMeasure.CalculateCoordinateUsingDegrees(blackRefAngle);
        m_ColorShadeCanvas.drawCircle(lowerRndPos.x, lowerRndPos.y, rndRadius, colorBandPaint);

        m_ShadeLowerBoundAngle = blackRefAngle - 180.0f;

        float whiteRefAngle = ((float)whiteout_len * ColorMap1440.DEGREES_PER_STEP);
        color_int = white_color_table[whiteout_len - 1];
        colorBandPaint.setColor(color_int);
        PointF upperRndPos = m_ShadeMiddleMeasure.CalculateCoordinateUsingDegrees(whiteRefAngle);
        m_ColorShadeCanvas.drawCircle(upperRndPos.x, upperRndPos.y, rndRadius, colorBandPaint);

        m_ShadeUpperBoundAngle = 180.0f + whiteRefAngle;

        colorBandPaint.setStrokeWidth(m_BandWidth);

        float rad_next = 0;
        for (int ci_next = 0; ci_next < blackout_len; ci_next += 1) {
            color_int = black_color_table[ci_next];
            colorBandPaint.setColor(color_int);

            float rad_next_end = rad_next - ColorMap1440.DEGREES_PER_STEP;

            m_ColorShadeCanvas.drawArc(m_ShadeRect, rad_next, ColorMap1440.DEGREES_PER_STEP, false, colorBandPaint);

            rad_next = rad_next_end;
        }

        rad_next = 0;
        for (int ci_next = 0; ci_next < whiteout_len; ci_next += 1) {
            color_int = white_color_table[ci_next];
            colorBandPaint.setColor(color_int);

            float rad_next_end = rad_next + ColorMap1440.DEGREES_PER_STEP;

            m_ColorShadeCanvas.drawArc(m_ShadeRect, rad_next, ColorMap1440.DEGREES_PER_STEP, false, colorBandPaint);

            rad_next = rad_next_end;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(m_ColorHueBitmap, 0, 0, m_BitmapPaint);

        canvas.save();
        canvas.rotate(p_HueCursorAngle, m_CenterX, m_CenterY);
        canvas.drawBitmap(m_ColorShadeBitmap, 0, 0, m_BitmapPaint);
        canvas.restore();

        canvas.drawCircle(m_HueCursorX, m_HueCursorY, m_HueCursorRadius, m_HueCursorPaint);

        canvas.drawCircle(m_ShadeCursorX, m_ShadeCursorY, m_ShadeCursorRadius, m_ShadeCursorPaint);
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

        m_ColorHueBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        m_ColorHueCanvas = new Canvas(m_ColorHueBitmap);

        // Draw the offscreen color shade triangle bitmap
        m_ColorShadeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        m_ColorShadeCanvas = new Canvas(m_ColorShadeBitmap);

        recalculate_layout_and_bands(w, h);

        recalculate_color_section_band();

        recalculate_hue_position_from_angle(this.p_HueCursorAngle);

        recalculate_color_shade_band();

        //float[] shadeVertices = new float[] { m_ShadeVertex1.x, m_ShadeVertex1.y, m_ShadeVertex2.x, m_ShadeVertex2.y, m_ShadeVertex3.x, m_ShadeVertex3.y};
        //int[] colorsVirtices = new int[] { hueColor, Color.WHITE, Color.BLACK, -0x1000000, -0x1000000, -0x1000000 };

        //m_ColorShadeCanvas.drawVertices(Canvas.VertexMode.TRIANGLES, 6, shadeVertices, 0, null, 0, colorsVirtices, 0, null, 0, 0, p);

        update_drawing_paints_and_shaders();

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
                m_ActivePointerId = event.getPointerId(0);
            }

            start_drag = true;

            int pointer_index = event.findPointerIndex(m_ActivePointerId);

            float mx = event.getX(pointer_index);
            float my = event.getY(pointer_index);

            float tx = mx - getLeft();
            float ty = my - getTop();

            recalculate_based_on_touch(tx, ty);

        }
        else if (action == MotionEvent.ACTION_UP) {
            m_ActivePointerId = MotionEvent.INVALID_POINTER_ID;
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
            // Capture the difference angle for the shade cursor before we update the hue cursor angle
            float shade_angle_diff =  p_ShadeCursorAngle - p_HueCursorAngle;

            p_HueCursorAngle = m_HueBandOuterMeasure.RelativeAngleDegrees(tx, ty);

            // Update the color view angles
            recalculate_palette_view_angles(shade_angle_diff);

            // Re-calculate all the X, Y coordinates for the UI and the measurement touchlines
            recalculate_palette_xy_coordinates_touchlines();

            // Update the color map so we can pull the new shade tables
            recalculate_colormap_shades_and_selected_color();

            recalculate_color_shade_band();

            // Update the color objects and shaders
            update_drawing_paints_and_shaders();

            // Notify any listeners of the color change
            notify_color_change_listeners();

            // Invalidate the view
            invalidate();
        } else if(m_ShadeOuterMeasure.ContainsPoint(tx, ty) && !m_ShadeInnerMeasure.ContainsPoint(tx, ty)) {

            float shadeRelAngle = m_ShadeOuterMeasure.RelativeAngleDegrees(tx, ty);

            float hueAdjAngle = 0.0f;
            float shadeReferenceAngle = 0.0f;
            if (p_HueCursorAngle <= 180.0f) {
                hueAdjAngle = 180.0f - p_HueCursorAngle;
                shadeReferenceAngle = shadeRelAngle + hueAdjAngle;
            }
            else {
                hueAdjAngle = p_HueCursorAngle - 180.0f;
                shadeReferenceAngle = shadeRelAngle - hueAdjAngle;
            }

            if ((shadeReferenceAngle <= m_ShadeUpperBoundAngle) && (shadeReferenceAngle >= m_ShadeLowerBoundAngle)) {

                p_ShadeCursorAngle = shadeRelAngle;

                // Capture the difference angle for the shade cursor before we update the hue cursor angle
                float shade_angle_diff =  p_ShadeCursorAngle - p_HueCursorAngle;

                m_SetColor = true;

                // Re-calculate all the X, Y coordinates for the UI and the measurement touchlines
                recalculate_palette_xy_coordinates_touchlines();

                // Update the color objects and shaders
                update_drawing_paints_and_shaders();

                // Notify any listeners of the color change
                notify_color_change_listeners();

                // Invalidate the view
                invalidate();
            }
        }
    }

    private void recalculate_colormap_shades_and_selected_color() {
        m_ColorMap.setHueAngleDegrees(p_HueCursorAngle);
    }

    private void recalculate_hue_position_from_angle(float hueAngle) {
        PointF hue_band_pnt = m_HueBandOuterMeasure.CalculateCoordinateUsingDegrees(hueAngle);

        recalculate_based_on_touch(hue_band_pnt.x, hue_band_pnt.y);
    }

    private void recalculate_shade_position_from_angle(float shadeAngle) {
        PointF shade_pnt = m_ShadeMiddleMeasure.CalculateCoordinateUsingDegrees(shadeAngle);

        recalculate_based_on_touch(shade_pnt.x, shade_pnt.y);
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

        m_HueBandInnerRadius = m_HueBandOuterRadius - m_BandWidth;
        m_HueBandMiddleRadius =  m_HueBandOuterRadius - m_BandHalfWidth;

        m_HueBandInnerMeasure = new CircleMeasure(m_CenterX, m_CenterY, m_HueBandInnerRadius);
        m_HueBandOuterMeasure = new CircleMeasure(m_CenterX, m_CenterY, m_HueBandOuterRadius);

        m_PalletRect = new RectF(m_CenterX - m_HueBandMiddleRadius, m_CenterY - m_HueBandMiddleRadius, m_CenterX + m_HueBandMiddleRadius, m_CenterY + m_HueBandMiddleRadius);

        m_ShadeMiddleRadius = m_HueBandInnerRadius - m_BandWidth;
        m_ShadeOuterRadius = m_ShadeMiddleRadius + m_BandHalfWidth;
        m_ShadeInnerRadius = m_ShadeMiddleRadius - m_BandHalfWidth;

        m_ShadeOuterMeasure = new CircleMeasure(m_CenterX, m_CenterY, m_ShadeOuterRadius);
        m_ShadeMiddleMeasure = new CircleMeasure(m_CenterX, m_CenterY, m_ShadeMiddleRadius);
        m_ShadeInnerMeasure = new CircleMeasure(m_CenterX, m_CenterY, m_ShadeInnerRadius);

        m_ShadeRect = new RectF(m_CenterX - m_ShadeMiddleRadius, m_CenterY - m_ShadeMiddleRadius, m_CenterX + m_ShadeMiddleRadius, m_CenterY + m_ShadeMiddleRadius);

        m_ShadeCursorRadius = m_BandHalfWidth - m_HueCursorLineWidth;

        m_HuePaint.setStrokeWidth(m_BandWidth);

    }

    private void recalculate_palette_view_angles(float shade_angle_diff) {

        p_ShadeCursorAngle = (p_HueCursorAngle + (360.0f + shade_angle_diff)) % 360.0f;
    }

    private void recalculate_palette_xy_coordinates_touchlines() {
        m_HueCursorX = ((float) Math.cos(Math.toRadians(p_HueCursorAngle)) * m_HueBandMiddleRadius) + m_CenterX;
        m_HueCursorY = ((float) Math.sin(Math.toRadians(p_HueCursorAngle)) * m_HueBandMiddleRadius) + m_CenterY;

        m_ShadeCursorX = ((float) Math.cos(Math.toRadians(p_ShadeCursorAngle)) * m_ShadeMiddleRadius) + m_CenterX;
        m_ShadeCursorY = ((float) Math.sin(Math.toRadians(p_ShadeCursorAngle)) * m_ShadeMiddleRadius) + m_CenterY;
    }

    private void update_drawing_paints_and_shaders() {
        m_HuePaint.setColor(m_ColorMap.getPrimaryHue());
        m_ColorPaint.setColor(p_ColorSelected);
    }

}
