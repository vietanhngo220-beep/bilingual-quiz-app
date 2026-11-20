package com.vietanh.bilingualquiz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Stores quiz attempts and questions answered incorrectly. */
public class QuizDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bilingual_quiz.db";
    // Keep the schema compatible when switching between main and the statistics branch.
    private static final int DATABASE_VERSION = 3;

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
                "correct_answer TEXT, correct_answer_vi TEXT, correct_answer_en TEXT, " +
                "explanation_vi TEXT, explanation_en TEXT, " +
                "created_at INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE wrong_questions ADD COLUMN correct_answer_vi TEXT");
            db.execSQL("ALTER TABLE wrong_questions ADD COLUMN correct_answer_en TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE wrong_questions ADD COLUMN explanation_vi TEXT");
            db.execSQL("ALTER TABLE wrong_questions ADD COLUMN explanation_en TEXT");
        }
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
    public int getAttemptCount() {
        return getInt("SELECT COUNT(*) FROM attempts");
    }

    public int getTotalScore() {
        return getInt("SELECT COALESCE(SUM(score), 0) FROM attempts");
    }

    public double getAverageScore() {
        android.database.Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COALESCE(AVG(CAST(score AS REAL)), 0) FROM attempts", null);
        double value = 0;
        if (cursor.moveToFirst()) value = cursor.getDouble(0);
        cursor.close();
        return value;
    }

    public int getHighestScore() {
        return getInt("SELECT COALESCE(MAX(score), 0) FROM attempts");
    }

    private int getInt(String query) {
        android.database.Cursor cursor = getReadableDatabase().rawQuery(query, null);
        int value = 0;
        if (cursor.moveToFirst()) value = cursor.getInt(0);
        cursor.close();
        return value;
    }
    public android.database.Cursor getWrongQuestions() {
        return getReadableDatabase().query(
                "wrong_questions",
                new String[]{"question_id", "grade", "question_vi", "question_en", "correct_answer"},
                null, null, null, null, "created_at DESC");
    }
}
