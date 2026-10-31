package com.rocket.esp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQ_OVERLAY = 1;
    private static final String PREFS = "appcheat_settings";
    private TextView statusView;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildMenu();
    }

    private void buildMenu() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(9, 13, 24));

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(26), dp(20), dp(24));
        scroll.addView(root);

        TextView brand = text("ROCKET  /  APPCHEAT", 12, 0xFF57D6FF, true);
        brand.setLetterSpacing(0.12f);
        root.addView(brand);

        TextView title = text("Панель управления", 27, Color.WHITE, true);
        LinearLayout.LayoutParams titleParams = params(-1, -2);
        titleParams.topMargin = dp(8);
        root.addView(title, titleParams);

        TextView subtitle = text("ANDROID 9  •  OVERLAY CONTROL", 11, 0xFF8794AE, true);
        LinearLayout.LayoutParams subParams = params(-1, -2);
        subParams.topMargin = dp(5);
        root.addView(subtitle, subParams);

        LinearLayout statusCard = card();
        LinearLayout.LayoutParams statusParams = params(-1, -2);
        statusParams.topMargin = dp(22);
        root.addView(statusCard, statusParams);

        statusCard.addView(text("СОСТОЯНИЕ", 11, 0xFF57D6FF, true));
        statusView = text("Оверлей не запущен. Выдай разрешение и нажми «Запустить».",
                14, 0xFFE6ECF7, false);
        statusView.setPadding(0, dp(8), 0, 0);
        statusCard.addView(statusView);

        addSection("НАСТРОЙКИ ОВЕРЛЕЯ");
        addToggle("Показывать рамку", "demo_box", true);
        addToggle("Показывать подпись", "demo_label", true);
        addToggle("Направляющая линия", "demo_line", false);

        TextView note = text("Настройки применяются при следующем запуске оверлея. "
                + "Рамка демонстрационная и не отслеживает игроков.", 12, 0xFF98A5BD, false);
        note.setPadding(dp(2), dp(10), dp(2), dp(2));
        root.addView(note);

        addSection("УПРАВЛЕНИЕ");
        Button permission = button("РАЗРЕШЕНИЕ НА ОВЕРЛЕЙ", false);
        permission.setOnClickListener(v -> requestOverlayPermission());
        root.addView(permission, params(-1, dp(50)));

        Button start = button("ЗАПУСТИТЬ ОВЕРЛЕЙ", true);
        LinearLayout.LayoutParams startParams = params(-1, dp(52));
        startParams.topMargin = dp(10);
        root.addView(start, startParams);
        start.setOnClickListener(v -> startOverlay());

        Button stop = button("ОСТАНОВИТЬ ОВЕРЛЕЙ", false);
        LinearLayout.LayoutParams stopParams = params(-1, dp(50));
        stopParams.topMargin = dp(10);
        root.addView(stop, stopParams);
        stop.setOnClickListener(v -> {
            stopService(new Intent(this, OverlayService.class));
            setStatus("Команда остановки отправлена.");
        });

        TextView footer = text("APPCHEAT  •  BUILD 1.1  •  ANDROID PIE+", 10,
                0xFF65728B, true);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerParams = params(-1, -2);
        footerParams.topMargin = dp(24);
        root.addView(footer, footerParams);

        setContentView(scroll);
    }

    private void addSection(String label) {
        TextView heading = text(label, 11, 0xFF57D6FF, true);
        LinearLayout.LayoutParams p = params(-1, -2);
        p.topMargin = dp(22);
        p.bottomMargin = dp(10);
        root.addView(heading, p);
    }

    private void addToggle(String label, String key, boolean defaultValue) {
        LinearLayout row = card();
        LinearLayout.LayoutParams p = params(-1, -2);
        p.bottomMargin = dp(8);
        root.addView(row, p);

        Switch toggle = new Switch(this);
        toggle.setText(label);
        toggle.setTextColor(Color.WHITE);
        toggle.setTextSize(14);
        toggle.setPadding(0, dp(2), 0, dp(2));
        toggle.setChecked(getSharedPreferences(PREFS, MODE_PRIVATE)
                .getBoolean(key, defaultValue));
        toggle.setOnCheckedChangeListener((button, checked) ->
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean(key, checked).apply());
        row.addView(toggle, params(-1, -2));
    }

    private LinearLayout card() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(14), dp(16), dp(14));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF151E30);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(1), 0xFF26344D);
        box.setBackground(bg);
        return box;
    }

    private Button button(String label, boolean primary) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(12);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setTextColor(primary ? 0xFF071321 : 0xFFE7F0FF);
        b.setAllCaps(false);
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                primary ? new int[]{0xFF57D6FF, 0xFF69F0C1}
                        : new int[]{0xFF202D43, 0xFF202D43});
        bg.setCornerRadius(dp(12));
        b.setBackground(bg);
        if (Build.VERSION.SDK_INT >= 21) b.setElevation(dp(2));
        return b;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setStatus(String message) {
        if (statusView != null) statusView.setText(message);
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_OVERLAY);
        } else {
            setStatus("Разрешение на отображение поверх приложений есть.");
        }
    }

    private void startOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            setStatus("Сначала предоставь разрешение на оверлей.");
            requestOverlayPermission();
            return;
        }
        Intent serviceIntent = new Intent(this, OverlayService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(serviceIntent);
        else startService(serviceIntent);
        setStatus("Отправлена команда запуска оверлея.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_OVERLAY) {
            setStatus(Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
                    ? "Разрешение на оверлей предоставлено." : "Разрешение не предоставлено.");
        }
    }
}
