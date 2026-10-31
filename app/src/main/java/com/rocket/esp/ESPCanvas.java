package com.rocket.esp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Transparent demonstration HUD.
 *
 * This starter implementation deliberately does not inspect game frames or
 * infer player/entity positions. Add a legitimate, user-authorized detection
 * source before drawing any real ESP markers.
 */
public class ESPCanvas extends View {
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF demoBounds = new RectF();

    public ESPCanvas(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        outlinePaint.setColor(0xFF35E58A);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(3f);

        textPaint.setColor(0xFF35E58A);
        textPaint.setTextSize(14f);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float density = getResources().getDisplayMetrics().density;
        float left = 24f * density;
        float top = 72f * density;
        float right = getWidth() - 24f * density;
        float bottom = Math.min(getHeight() - 24f * density, top + 150f * density);

        demoBounds.set(left, top, right, bottom);
        canvas.drawRoundRect(demoBounds, 10f * density, 10f * density, outlinePaint);
        canvas.drawText("APPCHEAT • DEMO OVERLAY", left + 12f * density,
                top + 28f * density, textPaint);
        canvas.drawText("No object detection configured", left + 12f * density,
                top + 52f * density, textPaint);
    }
}
