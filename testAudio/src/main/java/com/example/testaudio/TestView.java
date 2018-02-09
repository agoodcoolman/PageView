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
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextPaint;
import android.util.AttributeSet;
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
        initView();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
        initView();

    }

    public TestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
        initView();
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
    //将图像分成多少格
    private int WIDTH = 200;
    private int HEIGHT = 200;

    //交点坐标的个数
    private int COUNT = (WIDTH + 1) * (HEIGHT + 1);

    //用于保存COUNT的坐标
    //x0, y0, x1, y1......
    private float[] verts = new float[COUNT * 2];

    //用于保存原始的坐标
    private float[] orig = new float[COUNT * 2];

    private Bitmap mBitmap;

    private float K = 1;
    private void initView() {
        int index = 0;
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/IMG_20180206_095106.jpg";

        mBitmap = adjustImage(s);
        float bmWidth = mBitmap.getWidth();
        float bmHeight = mBitmap.getHeight();

        for (int i = 0; i < HEIGHT + 1; i++) {
            float fy = bmHeight * i / HEIGHT;
            for (int j = 0; j < WIDTH + 1; j++) {
                float fx = bmWidth * j / WIDTH;
                //X轴坐标 放在偶数位
                verts[index * 2 + 0] = fx;
                orig[index * 2 + 0] = verts[index * 2 + 0];
                //Y轴坐标 放在奇数位
                //向下移动200
                verts[index * 2 + 1] = fy + 200;
                orig[index * 2 + 1] = verts[index * 2 + 1];
                index += 1;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < HEIGHT + 1; i++) {
            for (int j = 0; j < WIDTH + 1; j++) {
                verts[(i * (WIDTH + 1) + j) * 2 + 0] += 0;
                //利用正弦函数的周期性
                float offsetY = (float) Math.sin((float) j / WIDTH * 2 * Math.PI + K * 2 * Math.PI);
                verts[(i * (WIDTH + 1) + j) * 2 + 1] = orig[(i * (WIDTH + 1) + j) * 2 + 1] + offsetY * 50;
            }
        }

        //平移 旗帜飘扬效果
        K += 0.1F;

        canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);

        invalidate();
       /* // TODO: consider storing these as member variables to reduce
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


        canvas.drawCircle(0,0 , 50, paint);
        matrix.reset();


        float sin0 = (float) Math.sin(210), cos0 = (float) Math.cos(210);
        float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };
        // 设置翻转和旋转矩阵
        mMatrixArray[0] = -(1-2 * sin0 * sin0);
        mMatrixArray[1] = 2 * sin0 * cos0;
        mMatrixArray[3] = 2 * sin0 * cos0;
        mMatrixArray[4] = 1 - 2 * sin0 * sin0;

        matrix.reset();
        matrix.setValues(mMatrixArray);// 翻转和旋转
        matrix.preTranslate(-320, -320);// 沿当前XY轴负方向位移得到 矩形A₃B₃C₃D₃
        matrix.postTranslate(320, 320);//沿原XY轴方向位移得到 矩形A4 B4 C4 D4





      *//* matrix.postScale(-1f, 1f);
        Log.i("jin2", matrix.toString());
       matrix.postTranslate(bitmap.getWidth()/2, bitmap.getHeight()/2);
        matrix.preTranslate(-bitmap.getWidth()/2, -bitmap.getHeight()/2);
        canvas.drawBitmap(bitmap, matrix, paint);
        Bitmap bitmap2 = Bitmap.createBitmap(this.bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);*//*
        int withPointsNumber = 200, heightPointsNumber = 200, index = 0;
        float[] vets = new float[(withPointsNumber + 1) * (heightPointsNumber + 1) * 2];
        float fx, fy;
        float width = this.bitmap.getWidth(), heigth = this.bitmap.getHeight();
        for (int j = 0; j <= heightPointsNumber; j++) {
            for (int i = 0; i <= withPointsNumber; i++) {


                fx = width * i / withPointsNumber ;
                fy = heigth * j / heightPointsNumber ;
                if (j == heightPointsNumber) {
                    fy  +=  1* 50;
                    if (i > 100 && i < 180){

                    }
                }

                if (i == withPointsNumber) {
                    fx += 1* 50;
                }

                if (j == 0) {
                    fy  -=  1* 50;
                }

                if (i == 0) {
                    fx -= 1* 50;
                }

                vets[index * 2] = fx;
                vets[index * 2 + 1] = fy;
                index ++;
            }
        }

        canvas.drawBitmap(bitmap, 0, 0 , new Paint());
        canvas.concat(matrix);
        canvas.drawBitmapMesh(bitmap, withPointsNumber, heightPointsNumber, vets, 0, null, 0, null);
        canvas.drawCircle(0,50, 50, new Paint());
        canvas.restore();

*/


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
