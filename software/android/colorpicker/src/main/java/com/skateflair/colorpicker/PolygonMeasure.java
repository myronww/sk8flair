package com.skateflair.colorpicker;

import android.graphics.PointF;

/**
 * Created by myron on 2/28/16.
 */
public class PolygonMeasure {

    // PolygonMeasure coodinates.
    private final float[] polyY, polyX;

    // Number of sides in the polygon.
    private final int polySides;

    /**
     * Default constructor.
     * @param px PolygonMeasure y coods.
     * @param py PolygonMeasure x coods.
     * @param ps PolygonMeasure sides count.
     */
    public PolygonMeasure(final float[] px, final float[] py, final int ps) {
        polyX = px;
        polyY = py;
        polySides = ps;
    }

    public PolygonMeasure(PointF[] points){
        polySides = points.length;
        polyY = new float[polySides];
        polyX = new float[polySides];

        for(int i = 0; i < polySides; i++){
            polyY[i] = points[i].y;
            polyX[i] = points[i].x;
        }
    }

    /**
     * Checks if the PolygonMeasure contains a point.
     * @see "http://alienryderflex.com/polygon/"
     * @param x Point horizontal pos.
     * @param y Point vertical pos.
     * @return Point is in Poly flag.
     */
    public boolean Contains( final float x, final float y ) {

        boolean oddTransitions = false;
        for( int i = 0, j = polySides -1; i < polySides; j = i++ ) {

            if( ( polyY[ i ] < y && polyY[ j ] >= y ) || ( polyY[ j ] < y && polyY[ i ] >= y ) ) {
                if( polyX[ i ] + ( y - polyY[ i ] ) / ( polyY[ j ] - polyY[ i ] ) * ( polyX[ j ] - polyX[ i ] ) < x ) {
                    oddTransitions = !oddTransitions;
                }
            }

        }
        return oddTransitions;
    }


}
