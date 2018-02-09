package me.littlecheesecake.croplayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;

/**
 * Created by yulu on 12/3/15.
 *
 * View to display photo and selection box
 */
public class EditPhotoView extends FrameLayout {

    private static final int LINE_WIDTH = 2;
    private static final int CORNER_LENGTH = 30;

    private Context context;

    private ImageView           imageView;
    private SelectionView       selectionView;
    private EditableImage       editableImage;

    private float lineWidth;
    private float cornerWidth;
    private float cornerLength;
    private int lineColor;
    private int cornerColor;
    private int dotColor;
    private int shadowColor;

    public EditPhotoView(Context context) {
        super(context);
        this.context = context;
    }

    public EditPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        obtainAttributes(context, attrs);
    }

    public EditPhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        obtainAttributes(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //set the default image and selection view
        if (editableImage != null) {
            editableImage.setViewSize(w, h);
            imageView.setImageBitmap(editableImage.getOriginalImage());
            selectionView.setBoxSize(editableImage, editableImage.getBoxes(), w, h);
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * update view with editable image
     * @param context activity
     * @param editableImage image to be edited
     */
    public void initView(Context context, EditableImage editableImage) {
        this.editableImage = editableImage;

        selectionView = new SelectionView(context,
                lineWidth, cornerWidth, cornerLength,
                lineColor, cornerColor, dotColor, shadowColor, editableImage);
        imageView = new ImageView(context);

        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        selectionView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        addView(imageView, 0);
        addView(selectionView, 1);
    }

    public void setOnBoxChangedListener(OnBoxChangedListener onBoxChangedListener) {
        selectionView.setOnBoxChangedListener(onBoxChangedListener);
    }

    /**
     * rotate image
     */
    public void rotateImageView() {
        //rotate bitmap
        editableImage.rotateOriginalImage(90);

        //re-calculate and draw selection box
        editableImage.getActiveBox().setX1(0);
        editableImage.getActiveBox().setY1(0);
        editableImage.getActiveBox().setX2(editableImage.getActualSize()[0]);
        editableImage.getActiveBox().setY2(editableImage.getActualSize()[1]);
        selectionView.setBoxSize(editableImage, editableImage.getBoxes(), editableImage.getViewWidth(), editableImage.getViewHeight());

        //set bitmap as view
        imageView.setImageBitmap(editableImage.getOriginalImage());
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropLayout);

        lineWidth = ta.getDimension(R.styleable.CropLayout_crop_line_width, dp2px(LINE_WIDTH));
        lineColor = ta.getColor(R.styleable.CropLayout_crop_line_color, Color.parseColor("#ffffff"));
        dotColor = ta.getColor(R.styleable.CropLayout_crop_dot_color, Color.parseColor("#ffffff"));
        cornerWidth = ta.getDimension(R.styleable.CropLayout_crop_corner_width, dp2px(LINE_WIDTH * 2));
        cornerLength = ta.getDimension(R.styleable.CropLayout_crop_corner_length, dp2px(CORNER_LENGTH));
        cornerColor = ta.getColor(R.styleable.CropLayout_crop_corner_color, Color.parseColor("#ffffff"));
        shadowColor = ta.getColor(R.styleable.CropLayout_crop_shadow_color, Color.parseColor("#aa111111"));
    }

    protected int dp2px(float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
