package com.skateflair.colorpicker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class ColorPickerView extends LinearLayout implements IOnColorSelectedListener {

    static class SavedState extends BaseSavedState {

        protected int p_ColorTarget;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.p_ColorTarget = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.p_ColorTarget);
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_RED = "red";
    private static final String ARG_GREEN = "green";
    private static final String ARG_BLUE = "blue";
    private static final String ARG_INTENSITY = "intensity";

    // TODO: Rename and change types of parameters
    private int m_Red;
    private int m_Green;
    private int m_Blue;
    private int m_Intensity;

    private ColorPaletteView m_ColorPalette;
    private ColorTile m_ColorSamplePalette;

    private ColorTile m_ColorSample01;
    private ColorTile m_ColorSample02;
    private ColorTile m_ColorSample03;
    private ColorTile m_ColorSample04;
    private ColorTile m_ColorSample05;
    private ColorTile m_ColorSample06;
    private ColorTile m_ColorSample07;
    private ColorTile m_ColorSample08;
    private ColorTile m_ColorSample09;
    private ColorTile m_ColorSample10;
    private ColorTile m_ColorSample11;
    private ColorTile m_ColorSample12;
    private ColorTile m_ColorSample13;
    private ColorTile m_ColorSample14;
    private ColorTile m_ColorSample15;
    private ColorTile m_ColorSample16;
    private ColorTile m_ColorSample17;
    private ColorTile m_ColorSample18;

    private ColorTile m_SelectedColorTile;

    private ColorTile[] m_ColorTargets;

    private ArrayList<IOnColorSelectedListener> m_ColorChangeListeners = new ArrayList<IOnColorSelectedListener>();

    public ColorPickerView(Context context) {
        super(context);

        inflate(context, R.layout.view_color_picker, this);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.view_color_picker, this);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_color_picker, this);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        inflate(context, R.layout.view_color_picker, this);
    }

    public void addOnColorSelectedListener(IOnColorSelectedListener listener) {
        this.m_ColorChangeListeners.add(listener);
    }

    public int getSelectedColor() throws Exception {
        if (m_SelectedColorTile == null)
        {
            throw new Exception("Color not selected.");
        }

        int selected_color = m_SelectedColorTile.getSampleColor();
        return selected_color;
    }


    public int getColorTarget() {
        int color_target = -1;

        for (int i=0; i < 19; i++) {
            if (m_ColorTargets[i].getIsSelected()) {
                color_target = i;
                break;
            }
        }

        return color_target;
    }

    public int getPaletteColor() {
        return m_ColorPalette.getSelectedColor();
    }

    public float getHueAngle() {
        return m_ColorPalette.getHueAngle();
    }

    public float getShadeAngle() {
        return m_ColorPalette.getShadeAngle();
    }

    public void setColorPickerParameters(int colorTarget, int paletteColor, float hueAngle, float shadeAngle) {
        set_color_target(colorTarget);

        m_ColorPalette.setPaletteParameters(paletteColor, hueAngle, shadeAngle);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        m_SelectedColorTile = null;

        m_ColorPalette = (ColorPaletteView)findViewById(R.id.colorpicker_palette);
        m_ColorPalette.addOnColorSelectedListener(this);

        m_ColorSamplePalette = (ColorTile)findViewById(R.id.colorpicker_sample);
        m_ColorSamplePalette.setOnClickListener(onColorTileClick);

        m_ColorSample01 = (ColorTile)findViewById(R.id.colorsample01);
        m_ColorSample01.setOnClickListener(onColorTileClick);
        m_ColorSample02 = (ColorTile)findViewById(R.id.colorsample02);
        m_ColorSample02.setOnClickListener(onColorTileClick);
        m_ColorSample03 = (ColorTile)findViewById(R.id.colorsample03);
        m_ColorSample03.setOnClickListener(onColorTileClick);
        m_ColorSample04 = (ColorTile)findViewById(R.id.colorsample04);
        m_ColorSample04.setOnClickListener(onColorTileClick);
        m_ColorSample05 = (ColorTile)findViewById(R.id.colorsample05);
        m_ColorSample05.setOnClickListener(onColorTileClick);
        m_ColorSample06 = (ColorTile)findViewById(R.id.colorsample06);
        m_ColorSample06.setOnClickListener(onColorTileClick);
        m_ColorSample07 = (ColorTile)findViewById(R.id.colorsample07);
        m_ColorSample07.setOnClickListener(onColorTileClick);
        m_ColorSample08 = (ColorTile)findViewById(R.id.colorsample08);
        m_ColorSample08.setOnClickListener(onColorTileClick);
        m_ColorSample09 = (ColorTile)findViewById(R.id.colorsample09);
        m_ColorSample09.setOnClickListener(onColorTileClick);
        m_ColorSample10 = (ColorTile)findViewById(R.id.colorsample10);
        m_ColorSample10.setOnClickListener(onColorTileClick);
        m_ColorSample11 = (ColorTile)findViewById(R.id.colorsample11);
        m_ColorSample11.setOnClickListener(onColorTileClick);
        m_ColorSample12 = (ColorTile)findViewById(R.id.colorsample12);
        m_ColorSample12.setOnClickListener(onColorTileClick);
        m_ColorSample13 = (ColorTile)findViewById(R.id.colorsample13);
        m_ColorSample13.setOnClickListener(onColorTileClick);
        m_ColorSample14 = (ColorTile)findViewById(R.id.colorsample14);
        m_ColorSample14.setOnClickListener(onColorTileClick);
        m_ColorSample15 = (ColorTile)findViewById(R.id.colorsample15);
        m_ColorSample15.setOnClickListener(onColorTileClick);
        m_ColorSample16 = (ColorTile)findViewById(R.id.colorsample16);
        m_ColorSample16.setOnClickListener(onColorTileClick);
        m_ColorSample17 = (ColorTile)findViewById(R.id.colorsample17);
        m_ColorSample17.setOnClickListener(onColorTileClick);
        m_ColorSample18 = (ColorTile)findViewById(R.id.colorsample18);
        m_ColorSample18.setOnClickListener(onColorTileClick);

        m_ColorTargets = new ColorTile[] {
                m_ColorSamplePalette,
                m_ColorSample01,
                m_ColorSample02,
                m_ColorSample03,
                m_ColorSample04,
                m_ColorSample05,
                m_ColorSample06,
                m_ColorSample07,
                m_ColorSample08,
                m_ColorSample09,
                m_ColorSample10,
                m_ColorSample11,
                m_ColorSample12,
                m_ColorSample13,
                m_ColorSample14,
                m_ColorSample15,
                m_ColorSample16,
                m_ColorSample17,
                m_ColorSample18
        };
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

        clear_color_selections();

        int color_target = ss.p_ColorTarget;
        set_color_target(color_target);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        int color_target = -1;
        for (int i=0; i < 19; i++) {
            if (m_ColorTargets[i].getIsSelected()) {
                color_target = i;
                break;
            }
        }

        ss.p_ColorTarget = color_target;

        return ss;
    }

    @Override
    public void onColorSelected(int selectedColor) {
        m_ColorSamplePalette.setSampleColor(selectedColor);
    }

    private View.OnClickListener onColorTileClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clear_color_selections();

            m_SelectedColorTile = (ColorTile)view;
            m_SelectedColorTile.setIsSelected(true);

            notify_color_change_listeners();
        }
    };

    private void clear_color_selections()
    {
        m_ColorSamplePalette.setIsSelected(false);
        m_ColorSample01.setIsSelected(false);
        m_ColorSample02.setIsSelected(false);
        m_ColorSample03.setIsSelected(false);
        m_ColorSample04.setIsSelected(false);
        m_ColorSample05.setIsSelected(false);
        m_ColorSample06.setIsSelected(false);
        m_ColorSample07.setIsSelected(false);
        m_ColorSample08.setIsSelected(false);
        m_ColorSample09.setIsSelected(false);
        m_ColorSample10.setIsSelected(false);
        m_ColorSample11.setIsSelected(false);
        m_ColorSample12.setIsSelected(false);
        m_ColorSample13.setIsSelected(false);
        m_ColorSample14.setIsSelected(false);
        m_ColorSample15.setIsSelected(false);
        m_ColorSample16.setIsSelected(false);
        m_ColorSample17.setIsSelected(false);
        m_ColorSample18.setIsSelected(false);
    }

    private void set_color_target(int color_target) {
        if (color_target > -1) {
            int targetIndex = color_target % 19;
            m_ColorTargets[targetIndex].setIsSelected(true);
            m_SelectedColorTile = m_ColorTargets[targetIndex];
        }
    }


    private void notify_color_change_listeners() {
        if (m_ColorChangeListeners != null) {
            for (IOnColorSelectedListener listener : m_ColorChangeListeners) {
                try {
                    int selected_color = m_SelectedColorTile.getSampleColor();
                    listener.onColorSelected(selected_color);
                } catch (Exception e) {
                    //Squash individual listener exceptions
                }
            }
        }
    }
}
