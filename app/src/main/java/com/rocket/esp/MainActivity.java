package com.rocket.esp;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQ_OVERLAY = 1;
    private static final int REQ_PROJECTION = 2;

    private MediaProjectionManager projectionManager;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        projectionManager = (MediaProjectionManager)
                getSystemService(MEDIA_PROJECTION_SERVICE);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("ROCKET AppCheat");
        title.setTextSize(24);
        layout.addView(title);

        statusView = new TextView(this);
        statusView.setText(
                "1. Разреши отображение поверх других приложений.\n" +
                "2. Запусти демонстрационный ESP-оверлей.\n\n" +
                "Пока модуль обнаружения объектов не настроен: " +
                "на экране будет отображаться только тестовая рамка."
        );
        layout.addView(statusView);

        Button overlayButton = new Button(this);
        overlayButton.setText("Разрешить оверлей");
        overlayButton.setOnClickListener(v -> requestOverlayPermission());
        layout.addView(overlayButton);

        Button startButton = new Button(this);
        startButton.setText("Запустить ESP-оверлей");
        startButton.setOnClickListener(v -> startOverlay());
        layout.addView(startButton);

        Button stopButton = new Button(this);
        stopButton.setText("Остановить ESP");
        stopButton.setOnClickListener(v ->
                stopService(new Intent(this, OverlayService.class)));
        layout.addView(stopButton);

        setContentView(layout);
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, REQ_OVERLAY);
        } else {
            statusView.setText("Разрешение на оверлей уже предоставлено.");
        }
    }

    private void startOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(this)) {
            statusView.setText("Сначала предоставь разрешение на оверлей.");
            requestOverlayPermission();
            return;
        }

        if (projectionManager == null) {
            statusView.setText("Не удалось получить MediaProjectionManager.");
            return;
        }

        // Android requires the user to approve each screen-capture session.
        startActivityForResult(
                projectionManager.createScreenCaptureIntent(),
                REQ_PROJECTION
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_OVERLAY) {
            statusView.setText(
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                            || Settings.canDrawOverlays(this)
                            ? "Разрешение на оверлей предоставлено."
                            : "Разрешение на оверлей не предоставлено."
            );
            return;
        }

        if (requestCode == REQ_PROJECTION) {
            if (resultCode != RESULT_OK || data == null) {
                statusView.setText("Захват экрана отменён.");
                return;
            }

            Intent serviceIntent = new Intent(this, OverlayService.class);
            serviceIntent.putExtra(OverlayService.EXTRA_RESULT_CODE, resultCode);
            serviceIntent.putExtra(OverlayService.EXTRA_RESULT_DATA, data);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            statusView.setText("Оверлей запущен. Для остановки нажми кнопку ниже.");
        }
    }
}
