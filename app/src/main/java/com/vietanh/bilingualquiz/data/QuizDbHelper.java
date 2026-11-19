package com.vietanh.bilingualquiz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Stores quiz attempts and questions answered incorrectly. */
public class QuizDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bilingual_quiz.db";
    private static final int DATABASE_VERSION = 1;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE attempts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "grade INTEGER NOT NULL, score INTEGER NOT NULL, total INTEGER NOT NULL, " +
                "created_at INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE wrong_questions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, question_id INTEGER NOT NULL, " +
                "grade INTEGER NOT NULL, question_vi TEXT NOT NULL, question_en TEXT, " +
                "correct_answer TEXT, created_at INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS attempts");
        db.execSQL("DROP TABLE IF EXISTS wrong_questions");
        onCreate(db);
    }

    public long saveAttempt(int grade, int score, int total) {
        ContentValues values = new ContentValues();
        values.put("grade", grade);
        values.put("score", score);
        values.put("total", total);
        values.put("created_at", System.currentTimeMillis());
        return getWritableDatabase().insert("attempts", null, values);
    }

    public long saveWrongQuestion(int questionId, int grade, String questionVi,
                                  String questionEn, String correctAnswer) {
        ContentValues values = new ContentValues();
        values.put("question_id", questionId);
        values.put("grade", grade);
        values.put("question_vi", questionVi);
        values.put("question_en", questionEn);
        values.put("correct_answer", correctAnswer);
        values.put("created_at", System.currentTimeMillis());
        return getWritableDatabase().insert("wrong_questions", null, values);
    }
}
