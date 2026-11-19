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

    /** One consistent summary: count, total score, average score, highest score. */
    public android.database.Cursor getStatistics(int grade) {
        if (grade < 0 || grade > 12) {
            throw new IllegalArgumentException("Grade must be 0 or between 1 and 12");
        }
        return getReadableDatabase().rawQuery(
                "SELECT COUNT(*), COALESCE(SUM(score), 0), " +
                "COALESCE(AVG(CAST(score AS REAL)), 0), COALESCE(MAX(score), 0) " +
                "FROM attempts" + (grade == 0 ? "" : " WHERE grade = ?"),
                grade == 0 ? null : new String[]{String.valueOf(grade)});
    }

    public android.database.Cursor getAttemptHistory() {
        return getAttemptHistory(0);
    }

    /** Grade 0 returns all attempts; grades 1–12 return only that grade. */
    public android.database.Cursor getAttemptHistory(int grade) {
        if (grade < 0 || grade > 12) {
            throw new IllegalArgumentException("Grade must be 0 or between 1 and 12");
        }
        return getReadableDatabase().query(
                "attempts",
                new String[]{"grade", "score", "total", "created_at"},
                grade == 0 ? null : "grade = ?",
                grade == 0 ? null : new String[]{String.valueOf(grade)},
                null, null, "created_at DESC, id DESC");
    }

    private int getInt(String query) {
        android.database.Cursor cursor = getReadableDatabase().rawQuery(query, null);
        int value = 0;
        if (cursor.moveToFirst()) value = cursor.getInt(0);
        cursor.close();
        return value;
    }
    public android.database.Cursor getWrongQuestions() {
        return getWrongQuestions(0);
    }

    /** Grade 0 returns all wrong answers; grades 1–12 filter by grade. */
    public android.database.Cursor getWrongQuestions(int grade) {
        if (grade < 0 || grade > 12) {
            throw new IllegalArgumentException("Grade must be 0 or between 1 and 12");
        }
        return getReadableDatabase().query(
                "wrong_questions",
                new String[]{"question_id", "grade", "question_vi", "question_en", "correct_answer"},
                grade == 0 ? null : "grade = ?",
                grade == 0 ? null : new String[]{String.valueOf(grade)},
                null, null, "created_at DESC, id DESC");
    }
}
