package com.rocket.esp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Configurable demonstration HUD. It does not detect or track game players.
 */
public class ESPCanvas extends View {
    private static final String PREFS = "appcheat_settings";
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF demoBounds = new RectF();
    private final SharedPreferences preferences;

    public ESPCanvas(Context context) {
        super(context);
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        outlinePaint.setColor(0xFF57D6FF);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(2.5f);

        textPaint.setColor(0xFF57D6FF);
        textPaint.setTextSize(14f);
        textPaint.setStyle(Paint.Style.FILL);

        linePaint.setColor(0xFF69F0C1);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float density = getResources().getDisplayMetrics().density;
        float left = 24f * density;
        float top = 90f * density;
        float right = getWidth() - 24f * density;
        float bottom = Math.min(getHeight() - 24f * density, top + 150f * density);
        demoBounds.set(left, top, right, bottom);

        if (preferences.getBoolean("demo_line", false)) {
            canvas.drawLine(getWidth() / 2f, getHeight() - 24f * density,
                    demoBounds.centerX(), demoBounds.bottom, linePaint);
        }
        if (preferences.getBoolean("demo_box", true)) {
            canvas.drawRoundRect(demoBounds, 10f * density, 10f * density, outlinePaint);
        }
        if (preferences.getBoolean("demo_label", true)) {
            canvas.drawText("APPCHEAT  •  DEMO", left + 12f * density,
                    top + 28f * density, textPaint);
            canvas.drawText("Object detection not configured", left + 12f * density,
                    top + 52f * density, textPaint);
        }
    }
}
