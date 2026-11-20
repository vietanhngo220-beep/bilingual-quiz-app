package com.vietanh.bilingualquiz.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

/** A small chart surface that can be bound to real statistics without a chart dependency. */
public class StatisticsChartView extends View {
    private final boolean donut;
    private final boolean vietnamese;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private String[] labels = new String[0];
    private float[] values = new float[0];
    private int[] colors = new int[0];
    private double total;

    public StatisticsChartView(Context context, boolean donut, boolean vietnamese) {
        super(context);
        this.donut = donut;
        this.vietnamese = vietnamese;
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        updateDescription();
    }

    /** Empty or all-zero values render an empty state; negative or non-finite values are invalid. */
    public void setData(String[] labels, float[] values, int[] colors) {
        if (labels == null || values == null || colors == null
                || labels.length != values.length || labels.length != colors.length) {
            throw new IllegalArgumentException("Chart labels, values and colors must have equal lengths");
        }
        double nextTotal = 0;
        for (int i = 0; i < values.length; i++) {
            if (labels[i] == null || Float.isNaN(values[i]) || Float.isInfinite(values[i])
                    || values[i] < 0) {
                throw new IllegalArgumentException("Chart values must be finite and non-negative, with non-null labels");
            }
            nextTotal += values[i];
        }
        this.labels = labels.clone();
        this.values = values.clone();
        this.colors = colors.clone();
        total = nextTotal;
        updateDescription();
        invalidate();
    }

    private void updateDescription() {
        if (total <= 0) {
            setContentDescription(vietnamese ? "Biểu đồ: chưa có dữ liệu" : "Chart: no data yet");
            return;
        }
        StringBuilder description = new StringBuilder(vietnamese ? "Biểu đồ. " : "Chart. ");
        for (int i = 0; i < labels.length; i++) {
            description.append(labels[i]).append(": ").append(number(values[i])).append(". ");
        }
        setContentDescription(description.toString());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) (dp(220) * Math.max(1f,
                getResources().getConfiguration().fontScale));
        setMeasuredDimension(resolveSize(dp(280), widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() < dp(80) || getHeight() < dp(48)) return;
        if (donut) drawDonut(canvas);
        else drawBars(canvas);
    }

    private void drawDonut(Canvas canvas) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 2f - dp(18);
        float stroke = Math.min(dp(30), radius * .25f);
        RectF bounds = new RectF(centerX - radius + stroke / 2,
                centerY - radius + stroke / 2, centerX + radius - stroke / 2,
                centerY + radius - stroke / 2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(AppUi.BORDER);
        canvas.drawOval(bounds, paint);
        if (total > 0) {
            float start = -90;
            for (int i = 0; i < values.length; i++) {
                float sweep = (float) (360d * values[i] / total);
                paint.setColor(colors[i]);
                canvas.drawArc(bounds, start, sweep, false, paint);
                start += sweep;
            }
        }
        paint.setStyle(Paint.Style.FILL);
        float innerWidth = Math.max(dp(12), (radius - stroke) * 1.75f);
        if (total <= 0) {
            centeredText(canvas, vietnamese ? "Chưa có dữ liệu" : "No data yet",
                    centerX, centerY, 14, AppUi.MUTED, innerWidth, false);
        } else {
            centeredText(canvas, number(total), centerX, centerY - dp(8), 28,
                    AppUi.TEXT, innerWidth, true);
            centeredText(canvas, vietnamese ? "Tổng số" : "Total", centerX,
                    centerY + dp(22), 13, AppUi.MUTED, innerWidth, false);
        }
    }

    private void drawBars(Canvas canvas) {
        float left = dp(42);
        float right = getWidth() - dp(8);
        float top = dp(14);
        float bottom = getHeight() - Math.max(dp(36), sp(30));
        double max = 0;
        for (float value : values) max = Math.max(max, value);
        double scale = max <= 0 ? 1 : max;
        textPaint.setTextSize(sp(10));
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setColor(AppUi.MUTED);
        for (int i = 0; i <= 4; i++) {
            float y = bottom - (bottom - top) * i / 4;
            paint.setColor(AppUi.BORDER);
            paint.setStrokeWidth(dp(1));
            canvas.drawLine(left, y, right, y, paint);
            if (total > 0 || i == 0) {
                String tick = number(scale * i / 4);
                CharSequence fitted = TextUtils.ellipsize(tick, textPaint, left - dp(8),
                        TextUtils.TruncateAt.END);
                textPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(fitted.toString(), left - dp(8), y + sp(3), textPaint);
            }
        }
        if (total <= 0) {
            float centerY = (top + bottom) / 2;
            paint.setColor(Color.WHITE);
            canvas.drawRect(left, centerY - sp(15), right, centerY + sp(15), paint);
            centeredText(canvas, vietnamese ? "Chưa có dữ liệu" : "No data yet",
                    (left + right) / 2, centerY, 14, AppUi.MUTED, right - left, false);
            return;
        }
        float slot = (right - left) / values.length;
        float barWidth = Math.max(1, Math.min(dp(42), slot * .58f));
        int labelStep = Math.max(1, (int) Math.ceil(dp(36) / slot));
        for (int i = 0; i < values.length; i++) {
            float x = left + slot * (i + .5f);
            float y = bottom - (float) ((bottom - top) * values[i] / scale);
            paint.setColor(colors[i]);
            canvas.drawRect(x - barWidth / 2, y, x + barWidth / 2, bottom, paint);
            if (i % labelStep == 0) {
                textPaint.setColor(AppUi.MUTED);
                textPaint.setTextSize(sp(10));
                textPaint.setTextAlign(Paint.Align.CENTER);
                CharSequence label = TextUtils.ellipsize(labels[i], textPaint,
                        Math.max(1, Math.min(slot * labelStep - dp(4),
                                2 * Math.min(x - left, right - x))), TextUtils.TruncateAt.END);
                canvas.drawText(label.toString(), x, bottom + sp(18), textPaint);
            }
        }
    }

    private void centeredText(Canvas canvas, String text, float x, float y, int size,
                              int color, float maxWidth, boolean bold) {
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(color);
        textPaint.setTypeface(bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        textPaint.setTextSize(sp(size));
        float width = textPaint.measureText(text);
        if (width > maxWidth) textPaint.setTextSize(textPaint.getTextSize() * maxWidth / width);
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        canvas.drawText(text, x, y - (metrics.ascent + metrics.descent) / 2, textPaint);
    }

    private String number(double value) {
        return String.format(Locale.getDefault(), value == Math.rint(value) ? "%.0f" : "%.1f", value);
    }

    private int dp(int value) { return AppUi.dp(getContext(), value); }
    private float sp(int value) { return value * getResources().getDisplayMetrics().scaledDensity; }
}
