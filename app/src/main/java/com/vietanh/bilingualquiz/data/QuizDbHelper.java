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
        return saveWrongQuestionValues(questionId, grade, questionVi, questionEn,
                correctAnswer, null, null);
    }

    public long saveWrongQuestion(int questionId, int grade, String questionVi,
                                  String questionEn, String correctAnswerVi, String correctAnswerEn) {
        return saveWrongQuestionValues(questionId, grade, questionVi, questionEn,
                correctAnswerVi, correctAnswerVi, correctAnswerEn);
    }

    public long saveWrongQuestion(int questionId, int grade, String questionVi,
                                  String questionEn, String correctAnswerVi, String correctAnswerEn,
                                  String explanationVi, String explanationEn) {
        return saveWrongQuestionValues(questionId, grade, questionVi, questionEn,
                correctAnswerVi, correctAnswerVi, correctAnswerEn, explanationVi, explanationEn);
    }

    private long saveWrongQuestionValues(int questionId, int grade, String questionVi,
                                         String questionEn, String legacyAnswer,
                                         String correctAnswerVi, String correctAnswerEn) {
        return saveWrongQuestionValues(questionId, grade, questionVi, questionEn,
                legacyAnswer, correctAnswerVi, correctAnswerEn, null, null);
    }

    private long saveWrongQuestionValues(int questionId, int grade, String questionVi,
                                         String questionEn, String legacyAnswer,
                                         String correctAnswerVi, String correctAnswerEn,
                                         String explanationVi, String explanationEn) {
        ContentValues values = new ContentValues();
        values.put("question_id", questionId);
        values.put("grade", grade);
        values.put("question_vi", questionVi);
        values.put("question_en", questionEn);
        values.put("correct_answer", legacyAnswer);
        if (correctAnswerVi != null) values.put("correct_answer_vi", correctAnswerVi);
        if (correctAnswerEn != null) values.put("correct_answer_en", correctAnswerEn);
        if (explanationVi != null) values.put("explanation_vi", explanationVi);
        if (explanationEn != null) values.put("explanation_en", explanationEn);
        values.put("created_at", System.currentTimeMillis());
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            long rowId = -1;
            try (android.database.Cursor existing = db.query("wrong_questions", new String[]{"id"},
                    "question_id = ? AND grade = ?",
                    new String[]{String.valueOf(questionId), String.valueOf(grade)},
                    null, null, "id DESC", "1")) {
                if (existing.moveToFirst()) rowId = existing.getLong(0);
            }
            long result;
            if (rowId >= 0) {
                db.update("wrong_questions", values, "id = ?", new String[]{String.valueOf(rowId)});
                result = rowId;
            } else {
                result = db.insertOrThrow("wrong_questions", null, values);
            }
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
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

    /** Summary columns: count, total score, average score, highest score, total questions. */
    public android.database.Cursor getStatistics(int grade) {
        if (grade < 0 || grade > 12) {
            throw new IllegalArgumentException("Grade must be 0 or between 1 and 12");
        }
        return getReadableDatabase().rawQuery(
                "SELECT COUNT(*), COALESCE(SUM(score), 0), " +
                "COALESCE(AVG(CAST(score AS REAL)), 0), COALESCE(MAX(score), 0), " +
                "COALESCE(SUM(total), 0) " +
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
                new String[]{"question_id", "grade", "question_vi", "question_en", "correct_answer",
                        "correct_answer_vi", "correct_answer_en", "explanation_vi", "explanation_en"},
                "id IN (SELECT MAX(id) FROM wrong_questions GROUP BY grade, question_id)"
                        + (grade == 0 ? "" : " AND grade = ?"),
                grade == 0 ? null : new String[]{String.valueOf(grade)},
                null, null, "created_at DESC, id DESC");
    }
}
