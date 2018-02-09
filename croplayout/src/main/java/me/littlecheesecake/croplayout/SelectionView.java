package me.littlecheesecake.croplayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

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

    private List<ScalableBox> displayBoxes;
    private int prevX;
    private int prevY;

    private int prevBoxX1;
    private int prevBoxX2;
    private int prevBoxY1;
    private int prevBoxY2;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float lineWidth;
    private float cornerWidth;
    private float cornerLength;
    private float offset;
    private float offset_2;
    private int lineColor;
    private int cornerColor;
    private int dotColor;
    private int shadowColor;

    // animating parameters
    private boolean animatingExpanding = false;
    private int[] startingCenter = new int[4];
    private int[] targetingBoxes = new int[4];


    public SelectionView(Context context,
                         float lineWidth, float cornerWidth, float cornerLength,
                         int lineColor, int cornerColor, int dotColor, int shadowColor,
                         EditableImage editableImage) {
        super(context);

        setOnTouchListener(this);

        this.editableImage = editableImage;
        this.lineWidth = lineWidth;
        this.cornerWidth = cornerWidth;
        this.cornerLength = cornerLength;
        this.lineColor = lineColor;
        this.cornerColor = cornerColor;
        this.dotColor = dotColor;
        this.shadowColor = shadowColor;

        this.displayBoxes = new ArrayList<>();

        offset = lineWidth / 4;
        offset_2 = lineWidth;
    }

    public void resetBoxSize(int bitmapWidth, int bitmapHeight) {
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;

        int size = (bitmapWidth < bitmapHeight) ? bitmapWidth : bitmapHeight;

        for (ScalableBox displayBox : displayBoxes) {
            displayBox.setX1((getWidth() - size) / 2);
            displayBox.setX2((getWidth() + size) / 2);
            displayBox.setY1((getHeight() - size) / 2);
            displayBox.setY2((getHeight() + size) / 2);
        }
        invalidate();
    }

    public void setBoxSize(EditableImage editableImage, List<ScalableBox> originalBoxes, int widthX, int heightY) {
        this.bitmapWidth = editableImage.getFitSize()[0];
        this.bitmapHeight = editableImage.getFitSize()[1];
        int originX = (widthX - bitmapWidth) / 2;
        int originY = (heightY - bitmapHeight) / 2;
        this.originX = originX;
        this.originY = originY;

        setDisplayBoxes(originalBoxes);

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
        if (animatingExpanding) {
            expandBox(canvas);
        } else {
            drawShadow(canvas);
            drawLines(canvas);
            drawCorner(canvas);
            drawDot(canvas);
        }
    }

    private void drawShadow(Canvas canvas) {
        mPaint.setStrokeWidth(0.0f);
        mPaint.setColor(shadowColor);

        ScalableBox displayBox = displayBoxes.get(editableImage.getActiveBoxIdx());
        canvas.drawRect(originX, originY, originX + bitmapWidth, displayBox.getY1(), mPaint);
        canvas.drawRect(originX, displayBox.getY1(), displayBox.getX1(), displayBox.getY2(), mPaint);
        canvas.drawRect(displayBox.getX2(), displayBox.getY1(), originX + bitmapWidth, displayBox.getY2(), mPaint);
        canvas.drawRect(originX, displayBox.getY2(), originX + bitmapWidth, originY + bitmapHeight, mPaint);
    }

    private void drawLines(Canvas canvas) {
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(lineColor);

        ScalableBox displayBox = displayBoxes.get(editableImage.getActiveBoxIdx());
        canvas.drawLine(displayBox.getX1(), displayBox.getY1(), displayBox.getX2(), displayBox.getY1(), mPaint);
        canvas.drawLine(displayBox.getX2(), displayBox.getY1(), displayBox.getX2(), displayBox.getY2(), mPaint);
        canvas.drawLine(displayBox.getX2(), displayBox.getY2(), displayBox.getX1(), displayBox.getY2(), mPaint);
        canvas.drawLine(displayBox.getX1(), displayBox.getY2(), displayBox.getX1(), displayBox.getY1(), mPaint);
    }

    private void drawCorner(Canvas canvas) {
        mPaint.setStrokeWidth(cornerWidth);
        mPaint.setColor(cornerColor);

        ScalableBox displayBox = displayBoxes.get(editableImage.getActiveBoxIdx());
        int x1 = displayBox.getX1();
        int x2 = displayBox.getX2();
        int y1 = displayBox.getY1();
        int y2 = displayBox.getY2();

        int minSize = (int) cornerLength;

        canvas.drawLine(x1 - offset_2, y1 - offset, x1 - offset + minSize, y1 - offset, mPaint);
        canvas.drawLine(x1 - offset, y1 - offset_2, x1 - offset, y1 - offset + minSize, mPaint);

        canvas.drawLine(x2 + offset_2, y1 - offset, x2 + offset - minSize, y1 - offset, mPaint);
        canvas.drawLine(x2 + offset, y1 - offset_2, x2 + offset, y1 - offset + minSize, mPaint);

        canvas.drawLine(x1 - offset_2, y2 + offset, x1 - offset + minSize, y2 + offset, mPaint);
        canvas.drawLine(x1 - offset, y2 + offset_2, x1 - offset, y2 + offset - minSize, mPaint);

        canvas.drawLine(x2 + offset_2, y2 + offset, x2 + offset - minSize, y2 + offset, mPaint);
        canvas.drawLine(x2 + offset, y2 + offset_2, x2 + offset, y2 + offset - minSize, mPaint);
    }

    private void expandBox(Canvas canvas) {
        int step = 10;
        float aspectRation = (targetingBoxes[3] - targetingBoxes[2]) * 1.0f / (targetingBoxes[1] - targetingBoxes[0]);
        startingCenter[0] = startingCenter[0] - step;
        startingCenter[1]  = startingCenter[1] + step;
        startingCenter[2] = (int)(startingCenter[2] - step * aspectRation);
        startingCenter[3] = (int)(startingCenter[3] + step * aspectRation);


        if (startingCenter[0] <= targetingBoxes[0] || startingCenter[1] >= targetingBoxes[1]
                || startingCenter[2] <= targetingBoxes[2] || startingCenter[3] >= targetingBoxes[3]) {
            startingCenter[0] = targetingBoxes[0];
            startingCenter[1] = targetingBoxes[1];
            startingCenter[2] = targetingBoxes[2];
            startingCenter[3] = targetingBoxes[3];

            animatingExpanding = false;
        }

        // add shade
        mPaint.setStrokeWidth(0.0f);
        mPaint.setColor(shadowColor);
        canvas.drawRect(originX, originY, originX + bitmapWidth, originY + bitmapHeight, mPaint);

        // draw box
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(lineColor);
        int x1 = startingCenter[0];
        int x2 = startingCenter[1];
        int y1 = startingCenter[2];
        int y2 = startingCenter[3];
        int minSize = (int) cornerLength;
        canvas.drawLine(x1, y1, x2, y1, mPaint);
        canvas.drawLine(x2, y1, x2, y2, mPaint);
        canvas.drawLine(x2, y2, x1, y2, mPaint);
        canvas.drawLine(x1, y2, x1, y1, mPaint);

        // draw corner
        mPaint.setColor(cornerColor);
        canvas.drawLine(x1 - offset_2, y1 - offset, x1 - offset + minSize, y1 - offset, mPaint);
        canvas.drawLine(x1 - offset, y1 - offset_2, x1 - offset, y1 - offset + minSize, mPaint);
        canvas.drawLine(x2 + offset_2, y1 - offset, x2 + offset - minSize, y1 - offset, mPaint);
        canvas.drawLine(x2 + offset, y1 - offset_2, x2 + offset, y1 - offset + minSize, mPaint);
        canvas.drawLine(x1 - offset_2, y2 + offset, x1 - offset + minSize, y2 + offset, mPaint);
        canvas.drawLine(x1 - offset, y2 + offset_2, x1 - offset, y2 + offset - minSize, mPaint);
        canvas.drawLine(x2 + offset_2, y2 + offset, x2 + offset - minSize, y2 + offset, mPaint);
        canvas.drawLine(x2 + offset, y2 + offset_2, x2 + offset, y2 + offset - minSize, mPaint);
        invalidate();
    }

    private void setUpExpanding(int centerX, int centerY, ScalableBox dot) {
        animatingExpanding = true;
        int step = 1;

        float aspectRation = (targetingBoxes[3] - targetingBoxes[2]) * 1.0f / (targetingBoxes[1] - targetingBoxes[0]);
        startingCenter[0] = centerX - step;
        startingCenter[1] = centerX + step;
        startingCenter[2] = (int)(centerY - step * aspectRation);
        startingCenter[3] = (int)(centerY + step * aspectRation);

        targetingBoxes[0] = dot.getX1();
        targetingBoxes[1] = dot.getX2();
        targetingBoxes[2] = dot.getY1();
        targetingBoxes[3] = dot.getY2();
    }

    private void setDisplayBoxes(List<ScalableBox> originalBoxes) {
        displayBoxes.clear();
        for (ScalableBox originalBox : originalBoxes) {
            ScalableBox displayBox = new ScalableBox(originalBox.getX1(), originalBox.getY1(), originalBox.getX2(), originalBox.getY2());

            if (originalBox.getX1() >= 0
                    && originalBox.getX2() > 0
                    && originalBox.getY1() >= 0
                    && originalBox.getY2() > 0) {

                Log.d(SELECTION_VIEW,
                        "original box: + (" + originalBox.getX1() + " " + originalBox.getY1() + ")"
                                + " (" + originalBox.getX2() + " " + originalBox.getY2() + ")");

                float scale = ((float) editableImage.getFitSize()[0]) / editableImage.getActualSize()[0];
                int scaleX1 = (int) Math.ceil((originalBox.getX1() * scale) + originX);
                int scaleX2 = (int) Math.ceil((originalBox.getX2() * scale) + originX);
                int scaleY1 = (int) Math.ceil((originalBox.getY1() * scale) + originY);
                int scaleY2 = (int) Math.ceil((originalBox.getY2() * scale) + originY);

                //resize the box size to image
                displayBox.setX1(scaleX1);
                displayBox.setX2(scaleX2);
                displayBox.setY1(scaleY1);
                displayBox.setY2(scaleY2);
            } else {
                displayBox.setX1(originX);
                displayBox.setX2(originX + bitmapWidth);
                displayBox.setY1(originY);
                displayBox.setY2(originY + bitmapHeight);
            }
            displayBoxes.add(displayBox);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawDot(Canvas canvas) {
        mPaint.setStrokeWidth(cornerWidth);
        int dotSize = 20;
        float dotSizeOuterRatio = 1.5f;

        for (ScalableBox dot : displayBoxes) {
            if (displayBoxes.indexOf(dot) != editableImage.getActiveBoxIdx()) {
                int centerX = (dot.getX1() + dot.getX2()) / 2;
                int centerY = (dot.getY1() + dot.getY2()) / 2;

                mPaint.setColor(dotColor);
                canvas.drawOval(
                        centerX - dotSize * dotSizeOuterRatio,
                        centerY - dotSize * dotSizeOuterRatio,
                        centerX + dotSize * dotSizeOuterRatio,
                        centerY + dotSize * dotSizeOuterRatio, mPaint);
                mPaint.setColor(Color.parseColor("#ffffff"));
                canvas.drawOval(centerX - dotSize, centerY - dotSize, centerX + dotSize,
                        centerY + dotSize, mPaint);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int[] loc = new int[2];
        getLocationOnScreen(loc);
        int curX = (int) motionEvent.getRawX();
        int curY = (int) motionEvent.getRawY();


        if (animatingExpanding) {
            return false;
        }
        // or box scaling and moving
        int activeIdx = editableImage.getActiveBoxIdx();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prevX = curX;
                prevY = curY;

                prevBoxX1 = editableImage.getActiveBox().getX1();
                prevBoxX2 = editableImage.getActiveBox().getX2();
                prevBoxY1 = editableImage.getActiveBox().getY1();
                prevBoxY2 = editableImage.getActiveBox().getY2();

                return true;

            case MotionEvent.ACTION_MOVE:
                int diffX = curX - prevX;
                int diffY = curY - prevY;

                displayBoxes.get(activeIdx).resizeBox(curX - loc[0], curY - loc[1], diffX, diffY,
                        (getWidth() - bitmapWidth) / 2,
                        (getHeight() - bitmapHeight) / 2,
                        (getWidth() + bitmapWidth) / 2,
                        (getHeight() + bitmapHeight) / 2,
                        (int) cornerLength);
                updateOriginalBox();


                invalidate();

                prevX = curX;
                prevY = curY;
                return true;

            case MotionEvent.ACTION_UP:
                // check click on dot
                for (ScalableBox dot : displayBoxes) {
                    if (displayBoxes.indexOf(dot) != editableImage.getActiveBoxIdx()) {
                        int buffer = 25;
                        int x1 = dot.getX1();
                        int x2 = dot.getX2();
                        int y1 = dot.getY1();
                        int y2 = dot.getY2();
                        int dotX = (x1 + x2) / 2;
                        int dotY = (y1 + y2) / 2;
                        int pointX = curX - loc[0];
                        int pointY = curY - loc[1];

                        if ((dotX - buffer <= pointX) && (pointX <= dotX + buffer) &&
                                (dotY - buffer <= pointY) && (pointY <= dotY + buffer)
                                ) {

                            // expand the box
                            setUpExpanding(dotX, dotY, dot);
                            editableImage.setActiveBoxIdx(displayBoxes.indexOf(dot));
                            invalidate();

                            if (onBoxChangedListener != null) {
                                onBoxChangedListener.onChanged(
                                        editableImage.getActiveBox().getX1(),
                                        editableImage.getActiveBox().getY1(),
                                        editableImage.getActiveBox().getX2(),
                                        editableImage.getActiveBox().getY2());
                            }
                            // reset the display box
                            setDisplayBoxes(editableImage.getBoxes());
                            return false;
                        }
                    }
                }

                ScalableBox originalBox = editableImage.getActiveBox();
                if (onBoxChangedListener != null
                        && (prevBoxX1 != originalBox.getX1()
                        || prevBoxX2 != originalBox.getX2()
                        || prevBoxY1 != originalBox.getY1()
                        || prevBoxY2 != originalBox.getY2())) {
                    onBoxChangedListener.onChanged(originalBox.getX1(), originalBox.getY1(), originalBox.getX2(), originalBox.getY2());
                }

                prevBoxX1 = originalBox.getX1();
                prevBoxX2 = originalBox.getX2();
                prevBoxY1 = originalBox.getY1();
                prevBoxY2 = originalBox.getY2();
        }
        return false;
    }

    /**
     * Calculate the relative position of the box w.r.t the bitmap size
     * Return a new box that can be used in uploading
     */
    public void updateOriginalBox() {
        int viewWidth = editableImage.getViewWidth();
        int viewHeight = editableImage.getViewHeight();
        int width = editableImage.getOriginalImage().getWidth();
        int height = editableImage.getOriginalImage().getHeight();
        ScalableBox displayBox = displayBoxes.get(editableImage.getActiveBoxIdx());

        float ratio = width / (float) height;
        float viewRatio = viewWidth / (float) viewHeight;
        float factor;

        //width dominate, fit w
        if (ratio > viewRatio) {
            factor = viewWidth / (float) width;
        } else {
            //height dominate, fit h
            factor = viewHeight / (float) height;
        }

        float coorX, coorY;
        coorX = (viewWidth - width * factor) / 2f;
        coorY = (viewHeight - height * factor) / 2f;

        int originX1 = (displayBox.getX1() - coorX) / factor <= width ? (int) ((displayBox.getX1() - coorX) / factor) : width;
        int originY1 = (displayBox.getY1() - coorY) / factor <= height ? (int) ((displayBox.getY1() - coorY) / factor) : height;
        int originX2 = (displayBox.getX2() - coorX) / factor <= width ? (int) ((displayBox.getX2() - coorX) / factor) : width;
        int originY2 = (displayBox.getY2() - coorY) / factor <= height ? (int) ((displayBox.getY2() - coorY) / factor) : height;
        editableImage.getActiveBox().setX1(originX1);
        editableImage.getActiveBox().setY1(originY1);
        editableImage.getActiveBox().setX2(originX2);
        editableImage.getActiveBox().setY2(originY2);
    }

}
