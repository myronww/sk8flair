package com.skateflair.colorpicker;

import android.graphics.Path;
import android.graphics.PointF;

/**
 * Created by myron on 2/28/16.
 */
public class TouchLine {

    public static final float HALF_PI = (float)(Math.PI / 2.0f);
    public static final float QUARTER_PI = (float)(Math.PI / 4.0f);
    public static final float DOUBLE_PI = (float)(Math.PI * 2.0f);
    public static final float MINUS_HALF_PI = DOUBLE_PI - HALF_PI;
    public static final float MINUS_QUARTER_PI = DOUBLE_PI - QUARTER_PI;

    private PolygonMeasure m_PerimeterMeasure;
    private PointF[] m_Perimeter = new PointF[10];

    private float m_StartX;
    private float m_StartY;
    private float m_EndX;
    private float m_EndY;
    private float m_Width;
    private float m_Length;

    public TouchLine(float start_x, float start_y, float end_x, float end_y, float width) {

        m_StartX = start_x;
        m_StartY = start_y;
        m_EndX = end_x;
        m_EndY = end_y;
        m_Width = width;

        float half_width = (width / 2.0f);

        float dx = end_x - start_x;
        float dy = end_y - start_y;

        m_Length = (float)Math.sqrt((dx * dx) + (dy * dy));
        float line_angle = (float)Math.acos( dx / m_Length );
        if (dy < 0.0f) {
            line_angle = (float)(Math.PI * 2) - line_angle;
        }

        int pindex = 0;
        float nxt_angle = (line_angle + HALF_PI) % DOUBLE_PI;
        while(pindex < 4) {
            m_Perimeter[pindex] = point_from_polar(start_x, start_y, nxt_angle, half_width);
            nxt_angle = (nxt_angle + QUARTER_PI) % DOUBLE_PI;
            pindex += 1;
        }
        m_Perimeter[pindex] = point_from_polar(start_x, start_y, nxt_angle, half_width);

        nxt_angle = (line_angle + MINUS_HALF_PI) % DOUBLE_PI;
        pindex += 1;
        while(pindex < 9) {
            m_Perimeter[pindex] = point_from_polar(end_x, end_y, nxt_angle, half_width);
            nxt_angle = (nxt_angle + QUARTER_PI) % DOUBLE_PI;
            pindex += 1;
        }
        m_Perimeter[pindex] = point_from_polar(end_x, end_y, nxt_angle, half_width);

        m_PerimeterMeasure = new PolygonMeasure(m_Perimeter);
    }

    public boolean Contains(float x, float y) {
        return m_PerimeterMeasure.Contains(x, y);
    }

    public PointF Intersection(PointF pt1, PointF pt2) {
        PointF intersect = Intersection(pt1.x, pt1.y, pt2.x, pt2.y);

        return intersect;
    }

    public PointF Intersection(float c1_x, float c1_y, float c2_x, float c2_y) {
        PointF intersect = null;

        float r1_x = m_StartX;
        float r1_y = m_StartY;
        float r2_x = m_EndX;
        float r2_y = m_EndY;

        float s1_x = r2_x - r1_x;
        float s1_y = r2_y - r1_y;

        float s2_x = c2_x - c1_x;
        float s2_y = c2_y - c1_y;

        float s = (-s1_y * (r1_x - c1_x) + s1_x * (r1_y - c1_y)) / (-s2_x * s1_y + s1_x * s2_y);
        float t = ( s2_x * (r1_y - c1_y) - s2_y * (r1_x - c1_x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0.0f && s <= 1.0f && t >= 0.0f && t <= 1.0f)
        {
            float ipt_x = r1_x + (t * s1_x);
            float ipt_y = r1_y + (t * s1_y);
            intersect = new PointF(ipt_x, ipt_y);
        }

        return intersect;
    }

    public float ScalerForPoint(PointF intersect) {

        float dx = m_EndX - intersect.x;
        float dy = m_EndY - intersect.y;

        float pos_length = (float)Math.sqrt((dx * dx) + (dy * dy));

        float pos_scaler = pos_length / m_Length;

        return pos_scaler;
    }

    public PointF[] getPerimeter(){
        return m_Perimeter;
    }

    public Path getPerimeterAsPath(){
        Path ppath = new Path();
        int pindex = 0;
        int pcount = m_Perimeter.length;

        PointF first_point = m_Perimeter[pindex];
        ppath.moveTo(first_point.x, first_point.y);

        while(pindex < pcount){
            PointF nxt_point = m_Perimeter[pindex];
            ppath.lineTo(nxt_point.x, nxt_point.y);
            pindex += 1;
        }

        ppath.lineTo(first_point.x, first_point.y);

        return ppath;
    }

    private PointF point_from_polar(float ref_x, float ref_y, float angle_radians, float length_radius)
    {
        float x = (float)(Math.cos( angle_radians ) * length_radius) + ref_x;
        float y = (float)(Math.sin( angle_radians ) * length_radius) + ref_y;

        return new PointF(x, y);
    }
}
