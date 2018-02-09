package me.littlecheesecake.croplayout;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import me.littlecheesecake.croplayout.model.ScalableBox;
import me.littlecheesecake.croplayout.util.ImageHelper;

/**
 * Editable image, manage bitmap rotation, calculate fit size and search box size
 * Created by yulu on 11/19/14.
 */
public class EditableImage {
    private Bitmap                originalImage;
    private List<ScalableBox>     originalBoxes;
    private int                   activeBoxIdx = 0;
    private ScalableBox           copyOfActiveBox;

    private int             viewWidth;
    private int             viewHeight;

    public EditableImage(String localPath) {
        //load image from path to bitmap
        originalImage = ImageHelper.getBitmapFromPath(localPath);

        //init the search box
        originalBoxes = new ArrayList<>();
    }

    public EditableImage(Context context, int id) {
        originalImage = ImageHelper.getBitmapFromResource(context.getResources(), id);

        //init the search box
        //init the search box
        originalBoxes = new ArrayList<>();
    }

    public void setViewSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public Bitmap getOriginalImage() {
        return originalImage;
    }

    public void setBoxes(List<ScalableBox> boxes) {
        setBoxes(boxes, 0);
    }

    public void setBoxes(List<ScalableBox> boxes, int activeBoxIdx) {
        this.originalBoxes = boxes;
        if (boxes.size() > 0) {
            try {
                copyOfActiveBox = (ScalableBox) originalBoxes.get(activeBoxIdx).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                copyOfActiveBox = new ScalableBox();
                copyOfActiveBox.setX1(originalBoxes.get(activeBoxIdx).getX1());
                copyOfActiveBox.setX2(originalBoxes.get(activeBoxIdx).getX2());
                copyOfActiveBox.setY1(originalBoxes.get(activeBoxIdx).getY1());
                copyOfActiveBox.setY2(originalBoxes.get(activeBoxIdx).getY2());
            }
        }
    }

    public List<ScalableBox> getBoxes() {
        return originalBoxes;
    }

    public int getActiveBoxIdx() {
        return activeBoxIdx;
    }

    public void setActiveBoxIdx(int activeBoxIdx) {
        this.activeBoxIdx = activeBoxIdx;
        try {
            copyOfActiveBox = (ScalableBox) originalBoxes.get(activeBoxIdx).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            copyOfActiveBox = new ScalableBox();
            copyOfActiveBox.setX1(originalBoxes.get(activeBoxIdx).getX1());
            copyOfActiveBox.setX2(originalBoxes.get(activeBoxIdx).getX2());
            copyOfActiveBox.setY1(originalBoxes.get(activeBoxIdx).getY1());
            copyOfActiveBox.setY2(originalBoxes.get(activeBoxIdx).getY2());
        }
    }

    public ScalableBox getActiveBox() {
        return copyOfActiveBox;
    }

    public void rotateOriginalImage(int degree) {
        originalImage = ImageHelper.rotateImage(originalImage, degree);
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    /**
     * get the size of the bitmap when it is fit to view display
     * @return width and height as int[]
     */
    public int[] getFitSize() {
        int[] fitSize = new int[2];

        float ratio = originalImage.getWidth() / (float)originalImage.getHeight();
        float viewRatio = viewWidth / (float)viewHeight;

        //width dominate, fit w
        if(ratio > viewRatio) {
            float factor = viewWidth / (float)originalImage.getWidth();
            fitSize[0] = viewWidth;
            fitSize[1] = (int)(originalImage.getHeight() * factor);

        } else {
            //height dominate, fit h
            float factor = viewHeight / (float)originalImage.getHeight();
            fitSize[0] = (int)(originalImage.getWidth() * factor);
            fitSize[1] = viewHeight;
        }

        return fitSize;
    }

    /**
     * get actual size of the image
     * @return int array size[0] is width, size[1] is height
     */
    public int[] getActualSize() {
        int[] actualSize = new int[2];

        actualSize[0] = originalImage.getWidth();
        actualSize[1] = originalImage.getHeight();

        return actualSize;
    }

    public String cropOriginalImage(String path, String imageName) {
        ScalableBox relativeBox = getActiveBox();
        return ImageHelper.saveImageCropToPath(originalImage,
                relativeBox.getX1(), relativeBox.getY1(), relativeBox.getX2(), relativeBox.getY2(),
                path, imageName
        );
    }

    public void saveEditedImage(String path) {
        ImageHelper.saveImageToPath(originalImage, path);
    }

}
