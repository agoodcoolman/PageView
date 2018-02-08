package com.example.testaudio;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * TODO: document your custom view class.
 */
public class TestView extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private Bitmap bitmap;
    private Matrix matrix;

    public TestView(Context context) {
        super(context);
        init(null, 0);

    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);


    }

    public TestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/IMG_20180206_095106.jpg";
        bitmap = adjustImage(s);
        matrix = new Matrix();
//        matrix.postTranslate(120,
        Camera camera = new Camera();
//        camera.rotateX(60);
        camera.getMatrix(matrix);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TestView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.TestView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.TestView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.TestView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.TestView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.TestView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    private Bitmap adjustImage(String absolutePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(absolutePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        opt.inScaled = false;
        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 10;
        // 根据屏的大小和图片大小计算出缩放比例
//        if (picWidth > picHeight) {
//            if (picWidth > screenWidth)
//                opt.inSampleSize = picWidth / screenWidth;
//        } else {
//            if (picHeight > screenHeight)
//
//                opt.inSampleSize = picHeight / screenHeight;
//        }

        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
//        opt.outWidth = 200;
//        opt.outHeight = 200;
        return BitmapFactory.decodeFile(absolutePath, opt);

        // 用imageview显示出bitmap

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        canvas.save();
        canvas.translate(500, 500);
        Rect rect = new Rect(100, 100, 700, 700);
        Paint paint = new Paint();
        canvas.drawLine(bitmap.getWidth(), 0, bitmap.getWidth(), 1200, paint);
        canvas.drawLine(0, bitmap.getHeight(), bitmap.getWidth()+ 200, bitmap.getHeight(), paint);
        canvas.drawLine(-100,0, bitmap.getWidth()+200, 0, paint);
        canvas.drawLine(0,-100, 0, bitmap.getHeight() + 200, paint);

//        canvas.drawBitmap(bitmap, 200, 200, null);



//        canvas.clipRect(rect);

//        matrix.postScale(1, -1);+
//        matrix.preTranslate(-bitmap.getWidth()/2, -bitmap.getHeight()/2);
//        matrix.postTranslate(bitmap.getWidth()/2, bitmap.getHeight()/2);

//        canvas.drawBitmap(bitmap,0, 0, new Paint());

//        matrix.preScale(1f, 1f);
//        matrix.preTranslate(100, 100);


//        matrix.setScale(-1f, 1f);
        canvas.drawCircle(0,0 , 50, paint);
        matrix.reset();
//        matrix.postTranslate(-bitmap.getWidth()/2, -bitmap.getHeight()/2);
//        Log.i("jin1", matrix.toString());
        matrix.postScale(-1f, 1f);
        Log.i("jin2", matrix.toString());
        matrix.postTranslate(bitmap.getWidth()/2, bitmap.getHeight()/2);
//        Log.i("jin3", matrix.toString());
        matrix.postTranslate(300, 300);
        canvas.drawBitmap(bitmap, matrix, paint);
        canvas.restore();

        /*matrix.reset();
        matrix.preTranslate(100, 100);
        canvas.drawBitmap(bitmap, matrix, null);*/


//        matrix.reset();
//        matrix.preTranslate(-100, -100);
//        canvas.drawBitmap(bitmap, matrix, paint);





//        canvas.drawBitmap(bitmap, matrix, null);
        // Draw the text.
        /*canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
