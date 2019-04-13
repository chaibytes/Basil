package com.example.basilandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

public class OverlayImageView extends AppCompatImageView {

    private RectF circleRect = null;
    private RectF rect = null;
    private int radius;

    public OverlayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setCircle(RectF rect, int radius) {
        this.circleRect = rect;
        this.radius = radius;
        //Redraw after defining circle
        postInvalidate();
    }

    public void setRect(RectF rect) {
        this.rect = rect;
        //Redraw after defining rectangle
        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setColor(getResources().getColor(R.color.camerabackground));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        if(circleRect != null) {
            canvas.drawRoundRect(circleRect, radius, radius, paint);
        } else if (rect != null) {
            canvas.drawRect(rect, paint);
        }

    }

}
