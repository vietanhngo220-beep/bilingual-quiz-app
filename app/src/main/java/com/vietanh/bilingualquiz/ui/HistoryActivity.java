package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.LinearLayout;

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
        LinearLayout screen = AppUi.screen(this, vi ? "Lịch sử làm bài" : "Quiz history", true);
        LinearLayout filterCard = AppUi.card(this);
        AppUi.add(filterCard, AppUi.text(this, vi ? "Lọc theo lớp" : "Filter by grade",
                14, AppUi.MUTED, true), 0);
        AppUi.add(screen, filterCard, 20);

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
        gradeFilter.setMinimumHeight(AppUi.dp(this, 48));
        AppUi.add(filterCard, gradeFilter, 4);

        historyList = new LinearLayout(this);
        historyList.setOrientation(LinearLayout.VERTICAL);
        AppUi.add(screen, historyList, 4);
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
                LinearLayout card = AppUi.card(this);
                String date = dateFormat.format(new Date(history.getLong(3)));
                AppUi.add(card, AppUi.text(this,
                        (vi ? "Lần " : "Attempt ") + attemptNumber + " · "
                                + (vi ? "Lớp " : "Grade ") + history.getInt(0),
                        14, AppUi.BLUE, true), 0);
                AppUi.add(card, AppUi.text(this,
                        (vi ? "Điểm: " : "Score: ") + history.getInt(1) + "/" + history.getInt(2),
                        24, AppUi.TEXT, true), 10);
                AppUi.add(card, AppUi.text(this,
                        (vi ? "Thời gian: " : "Time: ") + date,
                        14, AppUi.MUTED, false), 8);
                AppUi.add(historyList, card, 12);
                attemptNumber--;
            }
            if (history.getCount() == 0) {
                LinearLayout empty = AppUi.card(this);
                AppUi.add(empty, AppUi.text(this, selectedGrade == 0
                        ? (vi ? "Chưa có lịch sử làm bài." : "No quiz history yet.")
                        : (vi ? "Chưa có bài làm cho lớp " + selectedGrade + "."
                                : "No attempts for grade " + selectedGrade + "."),
                        16, AppUi.MUTED, false), 0);
                AppUi.add(historyList, empty, 12);
            }
        } finally {
            database.close();
        }
    }

}
