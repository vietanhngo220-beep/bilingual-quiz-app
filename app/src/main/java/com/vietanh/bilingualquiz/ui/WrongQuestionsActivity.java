package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.vietanh.bilingualquiz.LanguageSettings;
import com.vietanh.bilingualquiz.data.QuizDbHelper;

public class WrongQuestionsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean vi = LanguageSettings.isVietnamese(this);
        LinearLayout screen = AppUi.screen(this,
                vi ? "Câu hỏi sai" : "Wrong questions", true);
        AppUi.add(screen, AppUi.text(this,
                vi ? "Xem lại câu hỏi và đáp án đúng để ôn tập."
                        : "Review the questions and their correct answers.",
                15, AppUi.MUTED, false), 6);

        try (QuizDbHelper database = new QuizDbHelper(this);
             Cursor cursor = database.getWrongQuestions()) {
            int number = 1;
            while (cursor.moveToNext()) {
                LinearLayout card = AppUi.card(this);
                AppUi.add(card, AppUi.text(this, number + ". "
                        + (vi ? "Lớp " : "Grade ") + cursor.getInt(1),
                        14, AppUi.BLUE, true), 0);
                AppUi.add(card, AppUi.text(this,
                        vi ? cursor.getString(2) : cursor.getString(3),
                        18, AppUi.TEXT, true), 12);
                AppUi.add(card, AppUi.text(this,
                        (vi ? "Đáp án đúng: " : "Correct answer: ") + cursor.getString(4),
                        16, AppUi.GREEN, false), 14);
                AppUi.add(screen, card, number == 1 ? 24 : 12);
                number++;
            }
            if (number == 1) {
                LinearLayout empty = AppUi.card(this);
                AppUi.add(empty, AppUi.text(this,
                        vi ? "Chưa có câu sai được lưu" : "No wrong questions saved",
                        18, AppUi.TEXT, true), 0);
                AppUi.add(empty, AppUi.text(this,
                        vi ? "Hãy hoàn thành một bài luyện tập trước."
                                : "Complete a practice quiz first.",
                        15, AppUi.MUTED, false), 8);
                AppUi.add(screen, empty, 24);
            }
        }
    }
}
