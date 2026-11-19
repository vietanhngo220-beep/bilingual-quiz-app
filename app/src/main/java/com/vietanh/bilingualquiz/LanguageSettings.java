package com.vietanh.bilingualquiz;

import android.content.Context;

/** Reads the app-wide language choice shared by every screen. */
public final class LanguageSettings {
    private static final String SETTINGS = "settings";
    private static final String LANGUAGE_KEY = "vietnamese";

    private LanguageSettings() { }

    public static boolean isVietnamese(Context context) {
        return context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
                .getBoolean(LANGUAGE_KEY, true);
    }
}
