package com.skateflair.colorpicker;

import android.graphics.PointF;

/**
 * Created by myron on 2/28/16.
 */
public final class LineOperations {
    public static PointF GetIntersecept(PointF pt1, PointF pt2, PointF pt3, PointF pt4) {
        PointF intersect = GetIntersecept(pt1.x, pt1.y, pt2.x, pt2.y, pt3.x, pt3.y, pt4.x, pt4.y);

        return intersect;
    }

    public static PointF GetIntersecept(float r1_x, float r1_y, float r2_x, float r2_y, float c1_x, float c1_y, float c2_x, float c2_y) {
        PointF intersect = null;

        float r_rise = r2_y - r1_y;
        float r_run = r2_x - r1_x;
        float r_slope = r_rise / r_run;

        float c_rise = c2_y - c1_y;
        float c_run = c2_x - c1_x;
        float c_slope = c_rise / c_run;

        if (r_slope != c_slope) {
            float s = (-r_rise * (r1_x - c1_x) + r_run * (r1_y - c1_y)) / (-c_run * r_rise + r_run * c_rise);
            float t = (c_run * (r1_y - c1_y) - c_rise * (r1_x - c1_x)) / (-c_run * r_rise + r_run * c_rise);

            if (s >= 0.0f && s <= 1.0f && t >= 0.0f && t <= 1.0f) {
                float ipt_x = r1_x + (t * r_run);
                float ipt_y = r1_y + (t * r_rise);
                intersect = new PointF(ipt_x, ipt_y);
            }
        }

        return intersect;
    }

    public static PointF ExtendLine(float x1, float y1, float x2, float y2, float ext_factor)
    {
        float dx = x2 - x1;
        float dy = y2 - y1;

        float length = (float)Math.sqrt((dx * dx) + (dy * dy));
        float angle = (float)Math.acos(dx / length);
        if (dy < 0.0f) {
            angle = (float)(Math.PI * 2) - angle;
        }

        float new_length = length * ext_factor;

        float nx = ((float)Math.cos( angle ) * new_length) + x1;
        float ny = ((float)Math.sin( angle) * new_length) + y1;

        return new PointF(nx, ny);
    }

    public static float LineLength(float x1, float y1, float x2, float y2)
    {
        float dx = x2 - x1;
        float dy = y2 - y1;

        float length = (float)Math.sqrt((dx * dx) + (dy * dy));

        return length;
    }

    public static float LineAngleRadians(float x1, float y1, float x2, float y2)
    {
        float dx = x2 - x1;
        float dy = y2 - y1;

        float length = (float)Math.sqrt((dx * dx) + (dy * dy));
        float angle = (float)Math.atan2(dy , dx);

        return angle;
    }

    public static float LineAngleRadians(float dx, float dy, float length)
    {
        float angle = (float)Math.acos( dx / length );
        if (dy < 0.0f) {
            angle = (float)(Math.PI * 2) - angle;
        }
        return angle;
    }

    // polar to Cartesian
    //double x = Math.cos( angleInRadians ) * radius;
    //double y = Math.sin( angleInRadians ) * radius;

    // Cartesian to polar.
    //double radius = Math.sqrt( x * x + y * y );
    //double angleInRadians = Math.acos( x / radius );
}
