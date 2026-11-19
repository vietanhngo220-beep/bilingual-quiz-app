package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vietanh.bilingualquiz.LanguageSettings;
import com.vietanh.bilingualquiz.data.QuizDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Shows individual quiz attempts after opening history from statistics. */
public class HistoryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean vi = LanguageSettings.isVietnamese(this);
        ScrollView scroll = new ScrollView(this);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(24), dp(24), dp(24), dp(24));
        scroll.addView(screen);
        screen.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(dp(24) + insets.getSystemWindowInsetLeft(),
                    dp(24) + insets.getSystemWindowInsetTop(),
                    dp(24) + insets.getSystemWindowInsetRight(),
                    dp(24) + insets.getSystemWindowInsetBottom());
            return insets;
        });

        TextView title = new TextView(this);
        title.setText(vi ? "Lịch sử làm bài" : "Quiz history");
        title.setTextSize(24);
        title.setTextColor(Color.rgb(20, 60, 120));
        screen.addView(title);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        QuizDbHelper database = new QuizDbHelper(this);
        try (Cursor history = database.getAttemptHistory()) {
            int attemptNumber = history.getCount();
            while (history.moveToNext()) {
                TextView attempt = new TextView(this);
                String date = dateFormat.format(new Date(history.getLong(3)));
                attempt.setText(vi ? "Lần " + attemptNumber + " - Lớp " + history.getInt(0)
                        + "\nĐiểm: " + history.getInt(1) + "/" + history.getInt(2)
                        + "\nThời gian: " + date
                        : "Attempt " + attemptNumber + " - Grade " + history.getInt(0)
                        + "\nScore: " + history.getInt(1) + "/" + history.getInt(2)
                        + "\nTime: " + date);
                attempt.setTextSize(16);
                attempt.setTextColor(Color.DKGRAY);
                attempt.setPadding(0, dp(16), 0, dp(16));
                screen.addView(attempt);
                attemptNumber--;
            }
            if (history.getCount() == 0) {
                TextView empty = new TextView(this);
                empty.setText(vi ? "Chưa có lịch sử làm bài." : "No quiz history yet.");
                empty.setTextSize(16);
                empty.setPadding(0, dp(16), 0, 0);
                screen.addView(empty);
            }
        } finally {
            database.close();
        }
        setContentView(scroll);
        screen.requestApplyInsets();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
