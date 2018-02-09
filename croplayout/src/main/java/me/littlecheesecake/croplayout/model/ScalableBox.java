package me.littlecheesecake.croplayout.model;

/**
 * box that can be scaled and moved
 * Created by yulu on 11/12/14.
 */
public class ScalableBox implements Cloneable {

    //limit
    private int x1;
    private int x2;
    private int y1;
    private int y2;

    private int _x1;
    private int _x2;
    private int _y1;
    private int _y2;

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    private enum AREA {
        TL, TR, BL, BR,
    }

    public ScalableBox() {

    }

    public ScalableBox(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Resize box, move box and move corner, constrained by the boundary
     */
    public void resizeBox(int x, int y, int dx, int dy, int limitX1, int limitY1, int limitX2, int limitY2, int cornerBoundary) {
        _x1 = limitX1;
        _x2 = limitX2;
        _y1 = limitY1;
        _y2 = limitY2;

        if (Math.abs(x - x1) < cornerBoundary && Math.abs(y - y1) < cornerBoundary) {
            if (checkLimit(cornerBoundary) && dx > 0 ) dx = 0;
            if (checkLimit(cornerBoundary) && dy > 0 ) dy = 0;
            moveCorner(AREA.TL, dx, dy);
        }
        else if (Math.abs(x - x2) < cornerBoundary && Math.abs(y - y1) < cornerBoundary) {
            if (checkLimit(cornerBoundary) && dx < 0) dx = 0;
            if (checkLimit(cornerBoundary) && dy > 0) dy = 0;
            moveCorner(AREA.TR, dx, dy);
        }
        else if (Math.abs(x - x1) < cornerBoundary && Math.abs(y - y2) < cornerBoundary) {
            if (checkLimit(cornerBoundary) && dx > 0 ) dx = 0;
            if (checkLimit(cornerBoundary) && dy < 0 ) dy = 0;
            moveCorner(AREA.BL, dx, dy);
        }
        else if (Math.abs(x - x2) < cornerBoundary && Math.abs(y - y2) < cornerBoundary) {
            if (checkLimit(cornerBoundary) && dx < 0 ) dx = 0;
            if (checkLimit(cornerBoundary) && dy < 0 ) dy = 0;
            moveCorner(AREA.BR, dx, dy);
        }
        else if (x > x1 && x < x2 && y > y1 && y < y2) {
            if ((x1 <= limitX1 && dx < 0) || (x2 >= limitX2 && dx > 0) ) dx = 0;
            if ((y1 <= limitY1 && dy < 0) || (y2 >= limitY2 && dy > 0) ) dy = 0;
            moveBox(dx, dy);
        }
    }

    /**
     * move box
     */
    private void moveBox(int dx, int dy) {
        setX1(checkBoundaryX(x1 + dx));
        setY1(checkBoundaryY(y1 + dy));
        setX2(checkBoundaryX(x2 + dx));
        setY2(checkBoundaryY(y2 + dy));
    }

    private int checkBoundaryX(int p) {
        //left boundary
        if (p <= _x1)
            return _x1;

        //right boundary
        if (p >= _x2)
            return _x2;

        return p;
    }

    private int checkBoundaryY(int p) {
        //top boundary
        if (p <= _y1)
            return _y1;

        //bottom boundary
        if (p >= _y2)
            return _y2;

        return p;
    }
    /**
     * move corner
     */
    private void moveCorner(AREA corner, int dx, int dy) {
        switch (corner) {
            case TL:
                setX1(checkBoundaryX(x1 + dx));
                setY1(checkBoundaryY(y1 + dy));
                break;
            case TR:
                setX2(checkBoundaryX(x2 + dx));
                setY1(checkBoundaryY(y1 + dy));
                break;
            case BL:
                setX1(checkBoundaryX(x1 + dx));
                setY2(checkBoundaryY(y2 + dy));
                break;
            case BR:
                setX2(checkBoundaryX(x2 + dx));
                setY2(checkBoundaryY(y2 + dy));
                break;
        }
    }

    private boolean checkLimit(int cornerBoundary) {
        return (x2 - x1) <= cornerBoundary * 2 || (y2 - y1) <= cornerBoundary * 2;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (ScalableBox)super.clone();
    }

}
