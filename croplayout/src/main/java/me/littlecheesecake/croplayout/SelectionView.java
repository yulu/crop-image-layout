package me.littlecheesecake.croplayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;

/**
 * box that can be scaled and moved
 * Created by yulu on 11/12/14.
 */
public class SelectionView extends View implements View.OnTouchListener {
    private static final String SELECTION_VIEW = "BOX SELECTION VIEW";

    private OnBoxChangedListener onBoxChangedListener;
    private EditableImage editableImage;

    private int bitmapWidth;
    private int bitmapHeight;
    private int originX;
    private int originY;

    private ScalableBox box;
    private int prevX;
    private int prevY;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float lineWidth;
    private float cornerWidth;
    private float cornerLength;
    private float offset;
    private float offset_2;
    private int lineColor;
    private int cornerColor;
    private int shadowColor;

    public SelectionView(Context context, ScalableBox box,
                         float lineWidth, float cornerWidth, float cornerLength,
                         int lineColor, int cornerColor, int shadowColor,
                         EditableImage editableImage) {
        super(context);
        this.box = box;

        setOnTouchListener(this);

        this.editableImage = editableImage;

        this.lineWidth = lineWidth;
        this.cornerWidth = cornerWidth;
        this.cornerLength = cornerLength;
        this.lineColor = lineColor;
        this.cornerColor = cornerColor;
        this.shadowColor = shadowColor;

        offset = lineWidth / 4;
        offset_2 = lineWidth;
    }

    public void resetBoxSize(int bitmapWidth, int bitmapHeight) {
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;

        int size = (bitmapWidth < bitmapHeight) ? bitmapWidth:bitmapHeight;

        box.setX1((getWidth() - size) / 2);
        box.setX2((getWidth() + size) / 2);
        box.setY1((getHeight() - size) / 2);
        box.setY2((getHeight() + size) / 2);

        invalidate();
    }

    public void setBoxSize(EditableImage editableImage, ScalableBox detectBox, int widthX, int heightY) {
        this.bitmapWidth = editableImage.getFitSize()[0];
        this.bitmapHeight = editableImage.getFitSize()[1];
        int originX = (widthX - bitmapWidth) / 2;
        int originY = (heightY - bitmapHeight) / 2;
        this.originX = originX;
        this.originY = originY;

        if (detectBox != null
                && detectBox.getX1() >= 0
                && detectBox.getX2() > 0
                && detectBox.getY1() >= 0
                && detectBox.getY2() > 0) {

            Log.d(SELECTION_VIEW,
                    "original box: + (" + detectBox.getX1() + " " + detectBox.getY1() + ")"
                            + " (" + detectBox.getX2() + " " + detectBox.getY2() + ")");


            float scale = ((float) editableImage.getFitSize()[0]) / editableImage.getActualSize()[0];
            int scaleX1 = (int) Math.ceil((detectBox.getX1() * scale) + originX);
            int scaleX2 = (int) Math.ceil((detectBox.getX2() * scale) + originX);
            int scaleY1 = (int) Math.ceil((detectBox.getY1() * scale) + originY);
            int scaleY2 = (int) Math.ceil((detectBox.getY2() * scale) + originY);

            //resize the box size to image
            box.setX1(scaleX1);
            box.setX2(scaleX2);
            box.setY1(scaleY1);
            box.setY2(scaleY2);
        } else {
            box.setX1(originX);
            box.setX2(originX + bitmapWidth);
            box.setY1(originY);
            box.setY2(originY + bitmapHeight);
        }

        invalidate();
    }

    public void setOnBoxChangedListener(OnBoxChangedListener listener) {
        this.onBoxChangedListener = listener;
    }

    @Override
    protected void onSizeChanged(int x, int y, int oldx, int oldy) {
        super.onSizeChanged(x, y, oldx, oldy);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawShadow(canvas);
        drawLines(canvas);
        drawCorner(canvas);
    }

    private void drawShadow(Canvas canvas) {
        mPaint.setStrokeWidth(0.0f);
        mPaint.setColor(shadowColor);

        canvas.drawRect(originX, originY, originX + bitmapWidth, box.getY1(), mPaint);
        canvas.drawRect(originX, box.getY1(), box.getX1(), box.getY2(), mPaint);
        canvas.drawRect(box.getX2(), box.getY1(), originX + bitmapWidth, box.getY2(), mPaint);
        canvas.drawRect(originX, box.getY2(), originX + bitmapWidth, originY + bitmapHeight, mPaint);
    }

    private void drawLines(Canvas canvas) {
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(lineColor);

        canvas.drawLine(box.getX1(), box.getY1(), box.getX2(), box.getY1(), mPaint);
        canvas.drawLine(box.getX2(), box.getY1(), box.getX2(), box.getY2(), mPaint);
        canvas.drawLine(box.getX2(), box.getY2(), box.getX1(), box.getY2(), mPaint);
        canvas.drawLine(box.getX1(), box.getY2(), box.getX1(), box.getY1(), mPaint);
    }

    private void drawCorner(Canvas canvas) {
        mPaint.setStrokeWidth(cornerWidth);
        mPaint.setColor(cornerColor);

        int x1 = box.getX1();
        int x2 = box.getX2();
        int y1 = box.getY1();
        int y2 = box.getY2();

        int minSize = (int)cornerLength;

        canvas.drawLine(x1 - offset_2, y1 - offset, x1 - offset + minSize, y1 - offset, mPaint);
        canvas.drawLine(x1 - offset, y1 - offset_2, x1 - offset, y1 - offset + minSize, mPaint);

        canvas.drawLine(x2 + offset_2, y1 - offset, x2 + offset - minSize, y1 - offset, mPaint);
        canvas.drawLine(x2 + offset, y1 - offset_2, x2 + offset, y1 - offset + minSize, mPaint);

        canvas.drawLine(x1 - offset_2, y2 + offset, x1 - offset + minSize, y2 + offset, mPaint);
        canvas.drawLine(x1 - offset, y2 + offset_2, x1 - offset, y2 + offset - minSize, mPaint);

        canvas.drawLine(x2 + offset_2, y2 + offset, x2 + offset - minSize, y2 + offset, mPaint);
        canvas.drawLine(x2 + offset, y2 + offset_2, x2 + offset, y2 + offset - minSize, mPaint);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int curX = (int)motionEvent.getRawX();
        int curY = (int)motionEvent.getRawY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prevX = curX;
                prevY = curY;
                return true;

            case MotionEvent.ACTION_MOVE:

                int diffX = curX - prevX;
                int diffY = curY - prevY;

                int[] loc = new int[2];
                getLocationOnScreen(loc);

                box.resizeBox(curX - loc[0], curY - loc[1], diffX, diffY,
                        (getWidth() - bitmapWidth) / 2,
                        (getHeight() - bitmapHeight) / 2,
                        (getWidth() + bitmapWidth) / 2,
                        (getHeight() + bitmapHeight) / 2,
                        (int)cornerLength);

                invalidate();

                prevX = curX;
                prevY = curY;
                return true;
            case MotionEvent.ACTION_UP:
                if (onBoxChangedListener != null) {
                    ScalableBox originalBox = editableImage.getSearchBox(box);
                    onBoxChangedListener.onChanged(originalBox.getX1(), originalBox.getY1(), originalBox.getX2(), originalBox.getY2());
                }
        }
        return false;
    }

}
