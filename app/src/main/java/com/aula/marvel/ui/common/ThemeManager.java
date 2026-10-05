package com.aula.marvel.ui.common;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/** Persiste e aplica o tema (claro/escuro). Padrão: escuro, como no protótipo. */
public final class ThemeManager {
    private static final String PREFS = "marvel_preferences";
    private static final String KEY_DARK = "dark_theme";

    private ThemeManager() {}

    public static boolean isDark(Context context) {
        return prefs(context).getBoolean(KEY_DARK, true);
    }

    public static void apply(Context context) {
        AppCompatDelegate.setDefaultNightMode(isDark(context)
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void toggle(Context context) {
        prefs(context).edit().putBoolean(KEY_DARK, !isDark(context)).apply();
        apply(context);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
