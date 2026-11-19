package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vietanh.bilingualquiz.data.QuizDbHelper;
import com.vietanh.bilingualquiz.LanguageSettings;

public class WrongQuestionsActivity extends Activity {
    private static final String SELECTED_GRADE = "selectedGrade";
    private boolean vi;
    private int selectedGrade;
    private LinearLayout questionList;

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
        title.setText(vi ? "Câu hỏi sai" : "Wrong questions");
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

        questionList = new LinearLayout(this);
        questionList.setOrientation(LinearLayout.VERTICAL);
        screen.addView(questionList);
        gradeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGrade = position;
                showWrongQuestions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        showWrongQuestions();
        setContentView(scroll);
        screen.requestApplyInsets();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_GRADE, selectedGrade);
        super.onSaveInstanceState(outState);
    }

    private void showWrongQuestions() {
        questionList.removeAllViews();
        QuizDbHelper database = new QuizDbHelper(this);
        try (Cursor cursor = database.getWrongQuestions(selectedGrade)) {
            int number = 1;
            while (cursor.moveToNext()) {
                TextView item = new TextView(this);
                item.setText("\n" + number + ". " + (vi ? "Lớp " : "Grade ") + cursor.getInt(1) + "\n" +
                        (vi ? cursor.getString(2) : cursor.getString(3)) +
                        "\n" + (vi ? "Đáp án đúng: " : "Correct answer: ") + cursor.getString(4));
                item.setTextSize(17);
                item.setTextColor(Color.DKGRAY);
                questionList.addView(item);
                number++;
            }

            if (number == 1) {
                TextView empty = new TextView(this);
                empty.setText(selectedGrade == 0
                        ? (vi ? "\nChưa có câu sai được lưu.\nHãy hoàn thành một bài luyện tập trước."
                                : "\nNo wrong questions saved.\nComplete a practice quiz first.")
                        : (vi ? "\nChưa có câu sai được lưu cho lớp " + selectedGrade + "."
                                : "\nNo wrong questions saved for grade " + selectedGrade + "."));
                empty.setTextSize(18);
                questionList.addView(empty);
            }
        } finally {
            database.close();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
