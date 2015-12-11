package me.littlecheesecake.croplayout;

import android.content.Context;
import android.graphics.Bitmap;

import me.littlecheesecake.croplayout.model.ScalableBox;
import me.littlecheesecake.croplayout.util.ImageHelper;

/**
 * Editable image, manage bitmap rotation, calculate fit size and search box size
 * Created by yulu on 11/19/14.
 */
public class EditableImage {
    private Bitmap          originalImage;
    private ScalableBox     box;

    private int             viewWidth;
    private int             viewHeight;


    public EditableImage(String localPath) {
        //load image from path to bitmap
        originalImage = ImageHelper.getBitmapFromPath(localPath);

        //init the search box
        box = new ScalableBox();
    }

    public EditableImage(Context context, int id) {
        originalImage = ImageHelper.getBitmapFromResource(context.getResources(), id);

        //init the search box
        box = new ScalableBox();
    }

    public void setViewSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public Bitmap getOriginalImage() {
        return originalImage;
    }

    public void setBox(ScalableBox box) {
        this.box = box;
    }

    public ScalableBox getBox() {
        return box;
    }

    public void rotateOriginalImage(int degree) {
        originalImage = ImageHelper.rotateImage(originalImage, degree);
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
        ScalableBox relativeBox = getSearchBox(box);
        return ImageHelper.saveImageCropToPath(originalImage,
                relativeBox.getX1(), relativeBox.getY1(), relativeBox.getX2(), relativeBox.getY2(),
                path, imageName
        );
    }

    /**
     * Calculate the relative position of the box w.r.t the bitmap size
     * Return a new box that can be used in uploading
     */
    public ScalableBox getSearchBox(ScalableBox box) {
        ScalableBox newBox = new ScalableBox();

        int width, height;

        width = originalImage.getWidth();
        height = originalImage.getHeight();

        float ratio = width / (float)height;
        float viewRatio = viewWidth / (float)viewHeight;
        float factor;

        //width dominate, fit w
        if(ratio > viewRatio) {
            factor = viewWidth / (float)width;
        } else {
            //height dominate, fit h
            factor = viewHeight / (float)height;
        }

        int[] coor = new int[2];
        coor[0] = (int)(viewWidth - width*factor) / 2;
        coor[1] = (int)(viewHeight - height*factor) / 2;

        newBox.setX1((int)((box.getX1() - coor[0])/factor));
        newBox.setY1((int)((box.getY1() - coor[1])/factor));
        newBox.setX2((int)((box.getX2() - coor[0])/factor));
        newBox.setY2((int)((box.getY2() - coor[1])/factor));

        return newBox;
    }
}
