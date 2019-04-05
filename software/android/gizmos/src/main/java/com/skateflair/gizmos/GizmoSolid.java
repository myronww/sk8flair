package com.skateflair.gizmos;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skateflair.colorpicker.ColorPickerView;
import com.skateflair.colorpicker.IOnColorSelectedListener;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by myron on 2/21/16.
 */
public class GizmoSolid extends Gizmo implements IOnColorSelectedListener {

    public static final String TAG = "GizmoSolid";

    public static final UUID GIZMO_UUID = UUID.fromString("21106c6e-8084-455d-8a0d-2b07e19decaa");

    public class GizmoState implements Serializable {
        private int paletteColor;
        private float paletteHueAngle;
        private float paletteShadeAngle;
        private int colorTarget;

        public GizmoState() {
        }

        public GizmoState(int colorTarget, int paletteColor, float hueAngle, float shadeAngle) {
            this.paletteColor = paletteColor;
            this.paletteHueAngle = hueAngle;
            this.paletteShadeAngle = shadeAngle;
            this.colorTarget = colorTarget;
        }

        public int getPaletteColor() {
            return paletteColor;
        }

        public void setPaletteColor(int paletteColor)
        {
            this.paletteColor = paletteColor;
        }

        public float getPaletteHueAngle()
        {
            return paletteHueAngle;
        }

        public void setPaletteHueAngle(float hueAngle)
        {
            this.paletteHueAngle = hueAngle;
        }

        public float getPaletteShadeAngle()
        {
            return paletteShadeAngle;
        }

        public void setPaletteShadeAngle(float shadeAngle)
        {
            this.paletteShadeAngle = shadeAngle;
        }

        public int getColorTarget()
        {
            return colorTarget;
        }

        public void setPaletteShadeAngle(int colorTarget)
        {
            this.colorTarget = colorTarget;
        }
    }

    public static final String PROFILE_NAME = "solid";

    public final String CONTENT_HEADER =
            "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<FlairEngine>\n" +
                    "    <Motion>\n" +
                    "        <FallThreshold>-5000</FallThreshold>\n" +
                    "        <FallDetectTime>5000</FallDetectTime>\n" +
                    "        <RecoverDetectTime>2000</RecoverDetectTime>\n" +
                    "    <StompImpactThreshold>15000</StompImpactThreshold>\n" +
                    "    <StompRiseThreshold>-2000</StompRiseThreshold>\n" +
                    "    <StompTimeout>2000</StompTimeout>\n" +
                    "    </Motion>\n" +
                    "    <Palette type='LightPaletteSolid'>\n";

    public final String CONTENT_TAIL =
            "    </Palette>\n" +
                    "    <EffectChain>\n" +
                    "        <!-- Highest priority effect is last. -->\n" +
                    "        <Effect type='LightEffectStomp'>\n" +
                    "            <Duration>800</Duration>\n" +
                    "            <ActiveColor>\n" +
                    "                <Brightness>1.0</Brightness>\n" +
                    "                <Red>255</Red>\n" +
                    "                <Green>255</Green>\n" +
                    "                <Blue>255</Blue>\n" +
                    "            </ActiveColor>\n" +
                    "        </Effect>\n" +
                    "        <Effect type='LightEffectFall'>\n" +
                    "            <Duration>1000</Duration>\n" +
                    "        </Effect>\n" +
                    "    </EffectChain>\n" +
                    "</FlairEngine>";

    public final String RED_TAG_TEMPLATE = "<Red>%1$d</Red>\n";
    public final String GREEN_TAG_TEMPLATE = "<Green>%1$d</Green>\n";
    public final String BLUE_TAG_TEMPLATE = "<Blue>%1$d</Blue>\n";

    protected ColorPickerView colorPicker;

    public GizmoSolid() {
        // Required empty public constructor
    }

    /* ================================================================================
                                    Gizmo Full Fragment
       ================================================================================ */

    public static class GizmoFragmentFull extends Gizmo.GizmoFragmentBase {

        public static GizmoFragmentBase newInstance(Gizmo gizmo) {
            GizmoFragmentBase frag = new GizmoFragmentFull();
            frag.m_gizmo = gizmo;
            return frag;
        }

        public GizmoFragmentFull() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View fview = inflater.inflate(R.layout.gizmo_solid, container, false);

            ((GizmoSolid)this.m_gizmo).colorPicker = (ColorPickerView)fview.findViewById(R.id.colorpicker);
            ((GizmoSolid)this.m_gizmo).colorPicker.addOnColorSelectedListener(((GizmoSolid)this.m_gizmo));

            return fview;
        }

    }

    /* ================================================================================
                                    Gizmo Thumb Fragment
       ================================================================================ */

    public static class GizmoFragmentThumb extends Gizmo.GizmoFragmentBase {

        public static GizmoFragmentBase newInstance(Gizmo gizmo) {
            GizmoFragmentBase frag = new GizmoFragmentThumb();
            frag.m_gizmo = gizmo;
            return frag;
        }

        public GizmoFragmentThumb() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View fview = inflater.inflate(R.layout.gizmo_solid, container, false);

            ((GizmoSolid)this.m_gizmo).colorPicker = (ColorPickerView)fview.findViewById(R.id.colorpicker);
            ((GizmoSolid)this.m_gizmo).colorPicker.addOnColorSelectedListener(((GizmoSolid)this.m_gizmo));

            return fview;
        }

    }

    /* ================================================================================
                                 IGizmo Implementations
       ================================================================================ */

    @Override
    public Fragment attachFullFragment() {
        GizmoFragmentFull frag = new GizmoFragmentFull();
        frag.m_gizmo = this;

        return frag;
    }

    @Override
    public Fragment attachThumbFragment() {
        GizmoFragmentThumb frag = new GizmoFragmentThumb();
        frag.m_gizmo = this;

        return frag;
    }

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }


    @Override
    public void onColorSelected(int selectedColor) {
        notifyGizmoListenerOfDirty();
    }

    @Override
    public UUID getGizmoUUID() {
        return GIZMO_UUID;
    }

    @Override
    public String getProfileXML() throws GizmoProfileException
    {
        String content = null;

        try {
            int selected_color = colorPicker.getSelectedColor();

            int red_comp = (int) ((selected_color & 0x00ff0000) >>> 16);
            int green_comp = (int) ((selected_color & 0x0000ff00) >>> 8);
            int blue_comp = (int) (selected_color & 0x000000ff);

            content = CONTENT_HEADER;

            content += String.format(RED_TAG_TEMPLATE, red_comp);
            content += String.format(GREEN_TAG_TEMPLATE, green_comp);
            content += String.format(BLUE_TAG_TEMPLATE, blue_comp);

            content += CONTENT_TAIL;
        } catch (Exception xcpt) {
            throw new GizmoProfileException("GizmoSolid: Unable to create configuration document.", xcpt);
        }

        return content;
    }

    @Override
    public Serializable saveProfile() {
        int colorTarget = colorPicker.getColorTarget();
        int paletteColor = colorPicker.getPaletteColor();
        float hueAngle = colorPicker.getHueAngle();
        float shadeAngle = colorPicker.getShadeAngle();

        GizmoState state = new GizmoState(colorTarget, paletteColor, hueAngle, shadeAngle);

        return state;
    }

    @Override
    public void restoreProfile(Serializable obj){
        GizmoState state = (GizmoState)obj;

        colorPicker.setColorPickerParameters(state.colorTarget, state.paletteColor, state.paletteHueAngle, state.paletteShadeAngle);
    }

}
