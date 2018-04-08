package com.talmacel.iosif.wearebase.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Created by Iosif on 18/03/2018.
 */

public class MarkerUtils {
    public static int[] markerColors = new int[]{
            Color.rgb(255, 61, 53),
            Color.rgb(237, 155, 23),
            Color.rgb(255, 234, 52),
            Color.rgb(147, 197, 74),
            Color.rgb(4, 174, 252),
    };
    private static Rect r = new Rect();

    public static Bitmap drawMarker(int nr, int color){
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(74, 74, conf);
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setTextSize(35);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(37, 37, 37, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawCircle(37, 37, 35, paint);

        paint.setColor(Color.BLACK);
        drawCenter(canvas, paint, String.valueOf(nr));
        return bmp;
    }

    private static void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }
}
