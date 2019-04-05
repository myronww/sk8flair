package com.skateflair.colorpicker;

import android.graphics.PointF;

/**
 * Created by myron on 2/27/16.
 */
public class CircleMeasure {

    private float m_CenterX;
    private float m_CenterY;
    private float m_Radius;

    public CircleMeasure(float centerX, float centerY, float radius) {
        m_CenterX = centerX;
        m_CenterY = centerY;
        m_Radius = radius;
    }

    public boolean ContainsPoint(float x, float y)
    {
        float zeroed_x  = x - m_CenterX;
        float zeroed_y = y - m_CenterY;

        // Convert the zeroed coordinates to a radius from 0, 0
        float pt_radius = (float)Math.sqrt( (zeroed_x * zeroed_x) + (zeroed_y * zeroed_y));

        boolean result = pt_radius <= m_Radius ? true : false;
        return result;
    }

    public float RelativeAngleDegrees(float x, float y) {

        float angleInRadians = this.RelativeAngleRadians(x, y);

        return (float)Math.toDegrees(angleInRadians);
    }

    public float RelativeAngleRadians(float x, float y) {
        float zeroed_x  = x - m_CenterX;
        float zeroed_y = y - m_CenterY;

        float pt_radius = (float)Math.sqrt( (zeroed_x * zeroed_x) + (zeroed_y * zeroed_y));

        float angleInRadians = (float)Math.acos( zeroed_x / pt_radius );

        if (zeroed_y < 0.0f) {
            angleInRadians = (float)(Math.PI * 2) - angleInRadians;
        }

        return angleInRadians;
    }

    public PointF CalculateCoordinateUsingDegrees(float angleDegrees) {
        float ix = 0.0f;
        float iy = 0.0f;

        double angleRadians = Math.toRadians(angleDegrees);

        ix = ((float)Math.cos(angleRadians) * m_Radius) + m_CenterX;
        iy = ((float)Math.sin(angleRadians) * m_Radius) + m_CenterY;

        PointF interceptPoint = new PointF(ix, iy);

        return interceptPoint;
    }
}
