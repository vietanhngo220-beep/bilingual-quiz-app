package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private static final String SELECTED_GRADE = "selectedGrade";
    private boolean vi;
    private int selectedGrade;
    private LinearLayout historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vi = LanguageSettings.isVietnamese(this);
        selectedGrade = savedInstanceState == null ? 0
                : savedInstanceState.getInt(SELECTED_GRADE, 0);
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

        TextView filterLabel = new TextView(this);
        filterLabel.setText(vi ? "Lọc theo lớp" : "Filter by grade");
        filterLabel.setPadding(0, dp(16), 0, dp(4));
        screen.addView(filterLabel);

        String[] grades = new String[13];
        grades[0] = vi ? "Tất cả lớp" : "All grades";
        for (int grade = 1; grade <= 12; grade++) {
            grades[grade] = (vi ? "Lớp " : "Grade ") + grade;
        }
        Spinner gradeFilter = new Spinner(this);
        gradeFilter.setContentDescription(vi ? "Lọc theo lớp" : "Filter by grade");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, grades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeFilter.setAdapter(adapter);
        gradeFilter.setSelection(selectedGrade);
        screen.addView(gradeFilter, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(48)));

        historyList = new LinearLayout(this);
        historyList.setOrientation(LinearLayout.VERTICAL);
        screen.addView(historyList);
        gradeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGrade = position;
                showHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        showHistory();
        setContentView(scroll);
        screen.requestApplyInsets();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_GRADE, selectedGrade);
        super.onSaveInstanceState(outState);
    }

    private void showHistory() {
        historyList.removeAllViews();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        QuizDbHelper database = new QuizDbHelper(this);
        try (Cursor history = database.getAttemptHistory(selectedGrade)) {
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
                historyList.addView(attempt);
                attemptNumber--;
            }
            if (history.getCount() == 0) {
                TextView empty = new TextView(this);
                empty.setText(selectedGrade == 0
                        ? (vi ? "Chưa có lịch sử làm bài." : "No quiz history yet.")
                        : (vi ? "Chưa có bài làm cho lớp " + selectedGrade + "."
                                : "No attempts for grade " + selectedGrade + "."));
                empty.setTextSize(16);
                empty.setPadding(0, dp(16), 0, 0);
                historyList.addView(empty);
            }
        } finally {
            database.close();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
