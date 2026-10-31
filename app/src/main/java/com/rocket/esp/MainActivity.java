package com.rocket.esp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String PREFS = "menu_preferences";
    private static final int BG = 0xFF0B0D13;
    private static final int PANEL = 0xFF12151D;
    private static final int CARD = 0xFF171B24;
    private static final int MUTED = 0xFF969BA8;
    private static final int ACCENT = 0xFFFF7547;

    private SharedPreferences prefs;
    private LinearLayout content;
    private TextView pageTitle;
    private String activeTab = "Визуал";
    private String activeSide = "Объекты";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        buildScreen();
    }

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }

    private GradientDrawable shape(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(8), dp(8), dp(8), dp(8));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setBackground(shape(PANEL, 22, 0));
        top.setPadding(dp(10), dp(8), dp(10), dp(8));
        root.addView(top, lp(-1, dp(62)));

        TextView logo = label("◉", 25, ACCENT, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(shape(0xFF171B24, 18, 0));
        top.addView(logo, lp(dp(48), dp(48)));

        ScrollView tabsScroll = new ScrollView(this);
        tabsScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout tabs = new LinearLayout(this);
        tabs.setGravity(Gravity.CENTER_VERTICAL);
        tabs.setPadding(dp(8), 0, dp(8), 0);
        String[] tabNames = {"◉  Визуал", "⊙", "◎", "⚒"};
        for (int i = 0; i < tabNames.length; i++) {
            final String tab = i == 0 ? "Визуал" : i == 1 ? "Прицел" : i == 2 ? "Мир" : "Профиль";
            Button b = smallButton(tabNames[i], activeTab.equals(tab));
            LinearLayout.LayoutParams p = lp(-2, dp(42));
            p.setMargins(dp(4), 0, dp(4), 0);
            tabs.addView(b, p);
            b.setOnClickListener(v -> {
                activeTab = tab;
                buildContent();
                buildScreenRefreshTabs();
            });
        }
        tabsScroll.addView(tabs);
        LinearLayout.LayoutParams tabParams = lp(0, -1);
        tabParams.weight = 1;
        tabParams.leftMargin = dp(10);
        top.addView(tabsScroll, tabParams);
        root.addView(top);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams bodyParams = lp(-1, 0);
        bodyParams.weight = 1;
        bodyParams.topMargin = dp(12);
        root.addView(body, bodyParams);

        LinearLayout rail = new LinearLayout(this);
        rail.setOrientation(LinearLayout.VERTICAL);
        rail.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        rail.setBackground(shape(PANEL, 20, 0));
        rail.setPadding(dp(5), dp(12), dp(5), dp(12));
        body.addView(rail, lp(dp(58), -1));

        addRailButton(rail, "⛶", "Объекты");
        addRailButton(rail, "T", "Текст");
        addRailButton(rail, "⚙", "Общее");

        LinearLayout mainPanel = new LinearLayout(this);
        mainPanel.setOrientation(LinearLayout.VERTICAL);
        mainPanel.setBackground(shape(BG, 20, 0));
        LinearLayout.LayoutParams mainParams = lp(0, -1);
        mainParams.weight = 1;
        mainParams.leftMargin = dp(10);
        body.addView(mainPanel, mainParams);

        pageTitle = label(activeTab + "  ·  " + activeSide, 18, 0xFFF0F1F5, true);
        pageTitle.setPadding(dp(20), dp(16), dp(16), dp(12));
        mainPanel.addView(pageTitle);

        ScrollView scroller = new ScrollView(this);
        scroller.setFillViewport(false);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12), dp(2), dp(12), dp(18));
        scroller.addView(content);
        mainPanel.addView(scroller, lp(-1, 0));
        buildContent();
        setContentView(root);
    }

    private void buildScreenRefreshTabs() {
        // Refresh the selected tab highlight while keeping saved settings intact.
        buildScreen();
    }

    private Button smallButton(String text, boolean selected) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setTextColor(selected ? 0xFFF4F4F6 : 0xFFB6BAC4);
        b.setPadding(dp(14), 0, dp(14), 0);
        b.setBackground(shape(selected ? 0xFF292C35 : PANEL, 16, 0));
        return b;
    }

    private void addRailButton(LinearLayout rail, String icon, String name) {
        Button b = new Button(this);
        b.setText(icon);
        b.setTextSize(19);
        b.setAllCaps(false);
        b.setTextColor(activeSide.equals(name) ? Color.WHITE : MUTED);
        b.setBackground(shape(activeSide.equals(name) ? 0xFF292C35 : PANEL, 12, 0));
        LinearLayout.LayoutParams p = lp(dp(44), dp(48));
        p.bottomMargin = dp(8);
        rail.addView(b, p);
        b.setOnClickListener(v -> {
            activeSide = name;
            buildScreen();
        });
    }

    private void buildContent() {
        if (content == null) return;
        content.removeAllViews();
        if (pageTitle != null) pageTitle.setText(activeTab + "  ·  " + activeSide);

        LinearLayout rowOne = new LinearLayout(this);
        rowOne.setOrientation(LinearLayout.HORIZONTAL);
        content.addView(rowOne, lp(-1, -2));

        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);

        boolean wide = getResources().getConfiguration().screenWidthDp >= 650;
        if (wide) {
            LinearLayout.LayoutParams col = lp(0, -2);
            col.weight = 1;
            col.rightMargin = dp(8);
            rowOne.addView(left, col);
            LinearLayout.LayoutParams col2 = lp(0, -2);
            col2.weight = 1;
            col2.leftMargin = dp(8);
            rowOne.addView(right, col2);
        } else {
            rowOne.setOrientation(LinearLayout.VERTICAL);
            rowOne.addView(left, lp(-1, -2));
            rowOne.addView(right, lp(-1, -2));
        }

        if ("Визуал".equals(activeTab)) {
            addCard(left, "Подсветка объектов", new String[][]{
                    {"Включено", "switch:highlight"},
                    {"Показывать союзников", "switch:friends"},
                    {"Плавное появление", "switch:fade"}
            });
            addSlider(left, "Поле обзора", "fov", 0, 100, 50, "%");
            addSlider(left, "Размер текста", "text_size", 25, 200, 100, "%");
            addSlider(left, "Предел дистанции", "distance", 0, 500, 0, " м");

            addCard(left, "Рамка", new String[][]{
                    {"Включено", "switch:box"}
            });
            addSpinner(left, "Стиль", "box_style",
                    new String[]{"Угловой", "Полный", "Контур"});
            addSlider(left, "Толщина", "box_width", 1, 100, 50, "%");

            addCard(right, "Индикаторы за экраном", new String[][]{
                    {"Включено", "switch:offscreen"}
            });
            addSlider(right, "Размер", "arrow_size", 25, 200, 100, "%");
            addSlider(right, "Радиус", "arrow_radius", 0, 100, 27, "%");
            addColorButton(right, "Цвет индикатора", "arrow_color");

            addCard(right, "Подписи", new String[][]{
                    {"Имя объекта", "switch:name"},
                    {"Дистанция", "switch:range"},
                    {"Дополнительная информация", "switch:details"}
            });
        } else {
            addCard(left, activeTab + " · Основные настройки", new String[][]{
                    {"Включено", "switch:" + activeTab + "_enabled"},
                    {"Плавная анимация", "switch:" + activeTab + "_animation"}
            });
            addSlider(left, "Размер элементов", activeTab + "_size", 25, 200, 100, "%");
            addSlider(right, "Прозрачность", activeTab + "_alpha", 0, 100, 85, "%");
            addColorButton(right, "Основной цвет", activeTab + "_color");
        }
    }

    private void addCard(LinearLayout parent, String title, String[][] toggles) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackground(shape(CARD, 16, 0xFF222631));
        LinearLayout.LayoutParams p = lp(-1, -2);
        p.bottomMargin = dp(10);
        parent.addView(card, p);

        TextView heading = label(title, 15, 0xFFF0F1F5, true);
        card.addView(heading);
        View divider = new View(this);
        divider.setBackgroundColor(0xFF292D37);
        LinearLayout.LayoutParams divP = lp(-1, dp(1));
        divP.topMargin = dp(7);
        divP.bottomMargin = dp(8);
        card.addView(divider, divP);

        for (String[] item : toggles) {
            if (!item[1].startsWith("switch:")) continue;
            String key = item[1].substring(7);
            Switch sw = new Switch(this);
            sw.setText(item[0]);
            sw.setTextSize(14);
            sw.setTextColor(0xFFE1E3E9);
            sw.setPadding(0, dp(5), 0, dp(5));
            sw.setChecked(prefs.getBoolean(key, true));
            sw.setOnCheckedChangeListener((button, checked) ->
                    prefs.edit().putBoolean(key, checked).apply());
            card.addView(sw, lp(-1, -2));
        }
    }

    private void addSlider(LinearLayout parent, String title, String key,
                           int min, int max, int initial, String suffix) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(10), dp(14), dp(10));
        card.setBackground(shape(CARD, 14, 0xFF222631));
        LinearLayout.LayoutParams p = lp(-1, -2);
        p.bottomMargin = dp(10);
        parent.addView(card, p);

        LinearLayout labels = new LinearLayout(this);
        labels.setGravity(Gravity.CENTER_VERTICAL);
        TextView name = label(title, 14, 0xFFE1E3E9, false);
        labels.addView(name, new LinearLayout.LayoutParams(0, -2, 1));
        TextView value = label("", 13, 0xFFE1E3E9, false);
        labels.addView(value);
        card.addView(labels);

        SeekBar seek = new SeekBar(this);
        seek.setMax(max - min);
        int saved = prefs.getInt(key, initial);
        seek.setProgress(Math.max(0, Math.min(max - min, saved - min)));
        value.setText(saved == 0 && " м".equals(suffix) ? "Без предела" : saved + suffix);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                int v = progress + min;
                value.setText(v == 0 && " м".equals(suffix) ? "Без предела" : v + suffix);
                if (fromUser) prefs.edit().putInt(key, v).apply();
            }
            public void onStartTrackingTouch(SeekBar bar) {}
            public void onStopTrackingTouch(SeekBar bar) {}
        });
        card.addView(seek);
    }

    private void addSpinner(LinearLayout parent, String title, String key, String[] options) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(10), dp(14), dp(10));
        card.setBackground(shape(CARD, 14, 0xFF222631));
        LinearLayout.LayoutParams p = lp(-1, -2);
        p.bottomMargin = dp(10);
        parent.addView(card, p);
        card.addView(label(title, 14, 0xFFE1E3E9, false));
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(adapter);
        spinner.setSelection(prefs.getInt(key, 0));
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onItemSelected(android.widget.AdapterView<?> a, View v, int pos, long id) {
                prefs.edit().putInt(key, pos).apply();
            }
            public void onNothingSelected(android.widget.AdapterView<?> a) {}
        });
        card.addView(spinner);
    }

    private void addColorButton(LinearLayout parent, String title, String key) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackground(shape(CARD, 14, 0xFF222631));
        LinearLayout.LayoutParams p = lp(-1, -2);
        p.bottomMargin = dp(10);
        parent.addView(card, p);
        card.addView(label(title, 14, 0xFFE1E3E9, false),
                new LinearLayout.LayoutParams(0, -2, 1));
        Button color = new Button(this);
        color.setText("");
        int[] colors = {0xFFFF555D, 0xFF57D6FF, 0xFF70E0A0, 0xFFB58BFF, 0xFFFFC45C};
        int index = prefs.getInt(key, 0) % colors.length;
        color.setBackground(shape(colors[index], 8, 0));
        card.addView(color, lp(dp(38), dp(30)));
        color.setOnClickListener(v -> {
            int next = (prefs.getInt(key, 0) + 1) % colors.length;
            prefs.edit().putInt(key, next).apply();
            color.setBackground(shape(colors[next], 8, 0));
        });
    }
}
