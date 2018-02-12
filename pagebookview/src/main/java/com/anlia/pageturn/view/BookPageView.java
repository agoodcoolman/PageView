package com.anlia.pageturn.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.anlia.pageturn.DateCenter;
import com.anlia.pageturn.bean.MyPoint;
import com.anlia.pageturn.utils.BitmapUtils;

import org.jetbrains.annotations.Nullable;

/**
 * Created by anlia on 2017/10/19.
 *
 * TODO 增加LRC 最近最少图的框架
 */

public class BookPageView extends View {
    private Paint pointPaint;//绘制各标识点的画笔
    private Paint bgPaint;//背景画笔
    private Paint pathAPaint;//绘制A区域画笔
    private Paint pathBPaint;//绘制B区域画笔
    private Paint pathCPaint;//绘制C区域画笔
    private Paint textPaint;//绘制文字画笔
    private Paint pathCContentPaint;//绘制C区域内容画笔

    private MyPoint a,f,g,e,h,c,j,b,k,d,i;
    private Path pathA;
    private Path pathB;
    private Path pathC;

    private int defaultWidth;//默认宽度
    private int defaultHeight;//默认高度

    private int viewWidth;
    private int viewHeight;

    float lPathAShadowDis = 0;//A区域左阴影矩形短边长度参考值
    float rPathAShadowDis = 0;//A区域右阴影矩形短边长度参考值
    private float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };
    private Matrix mMatrix;

    private Scroller mScroller;

    public static int LAST_PAGE = 0x1;
    public static int NEXT_PAGE = 0x2;
    private String style;
    public static final String STYLE_LEFT = "STYLE_LEFT";//点击左边区域
    public static final String STYLE_RIGHT = "STYLE_RIGHT";//点击右边区域
    public static final String STYLE_MIDDLE = "STYLE_MIDDLE";//点击中间区域
    public static final String STYLE_TOP_RIGHT = "STYLE_TOP_RIGHT";//f点在右上角
    public static final String STYLE_LOWER_RIGHT = "STYLE_LOWER_RIGHT";//f点在右下角
    public static final String STYLE_TOP_LEFT = "STYLE_TOP_LEFT";//f点在左上角
    public static final String STYLE_LOWER_LEFT = "STYLE_LOWER_LEFT";//f点在左下角
    private GradientDrawable drawableLeftTopRight;
    private GradientDrawable drawableLeftLowerRight;

    private GradientDrawable drawableRightTopRight;
    private GradientDrawable drawableRightLowerRight;
    private GradientDrawable drawableHorizontalLowerRight;

    private GradientDrawable drawableBTopRight;
    private GradientDrawable drawableBLowerRight;

    private GradientDrawable drawableCTopRight;
    private GradientDrawable drawableCLowerRight;

    private Bitmap pathAContentBitmap;//A区域内容Bitmap
    private Bitmap pathBContentBitmap;//B区域内容Bitmap
    private Bitmap pathCContentBitmap;//C区域内容Bitmap
    private DateCenter dateCenter;

    public BookPageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs){

        dateCenter = new DateCenter();

        defaultWidth = 600;
        defaultHeight = 1000;

        a = new MyPoint();
        f = new MyPoint();
        g = new MyPoint();
        e = new MyPoint();
        h = new MyPoint();
        c = new MyPoint();
        j = new MyPoint();
        b = new MyPoint();
        k = new MyPoint();
        d = new MyPoint();
        i = new MyPoint();

        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setTextSize(25);
        pointPaint.setStyle(Paint.Style.STROKE);

        bgPaint = new Paint();
        bgPaint.setColor(Color.GREEN);

        pathAPaint = new Paint();
        pathAPaint.setColor(Color.GREEN);
        pathAPaint.setAntiAlias(true);//设置抗锯齿

        pathBPaint = new Paint();
        pathBPaint.setColor(Color.RED);
        pathBPaint.setAntiAlias(true);//设置抗锯齿

        pathCPaint = new Paint();
        pathCPaint.setColor(Color.YELLOW);
        pathCPaint.setAntiAlias(true);//设置抗锯齿

        pathCContentPaint = new Paint();
        pathCContentPaint.setColor(Color.YELLOW);
        pathCContentPaint.setAntiAlias(true);//设置抗锯齿

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setSubpixelText(true);//设置自像素。如果该项为true，将有助于文本在LCD屏幕上的显示效果。
        textPaint.setTextSize(30);

        pathA = new Path();
        pathB = new Path();
        pathC = new Path();

        style = STYLE_LOWER_RIGHT;
        mScroller = new Scroller(context,new LinearInterpolator());
        mMatrix = new Matrix();

        createGradientDrawable();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureSize(defaultHeight, heightMeasureSpec);
        int width = measureSize(defaultWidth, widthMeasureSpec);
        setMeasuredDimension(width, height);

        viewWidth = width;
        viewHeight = height;
        a.x = -1;
        a.y = -1;
        pathAContentBitmap = dateCenter.currentPage().copy(Bitmap.Config.ARGB_8888, true);
        pathBContentBitmap = dateCenter.nextPage().copy(Bitmap.Config.ARGB_8888, true);
        pathCContentBitmap = dateCenter.currentPage().copy(Bitmap.Config.ARGB_8888, true);
//        pathCContentBitmap = BitmapUtils.changeBitmapSize(pathCContentBitmap, pathCContentBitmap.getWidth() + 100, pathCContentBitmap.getHeight() + 100);


    }

    private int measureSize(int defaultSize,int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(a.x==-1 && a.y==-1){
            drawPathAContent(canvas, getPathDefault());
        }else {
            if(f.x==viewWidth && f.y==0){
                drawPathAContent(canvas,getPathAFromTopRight());

                drawPathCContent(canvas,getPathAFromTopRight());
                drawPathBContent(canvas,getPathAFromTopRight());
            }else if(f.x==viewWidth && f.y==viewHeight){

                beginTrace("drawPathA");
                drawPathAContent(canvas,getPathAFromLowerRight());
                endTrace();

                beginTrace("drawPathC");
                drawPathCContent(canvas,getPathAFromLowerRight());
                endTrace();

                beginTrace("drawPathB");
                drawPathBContent(canvas,getPathAFromLowerRight());
                endTrace();
            } else if(f.x == 0 && f.y == viewHeight) {
                beginTrace("drawPathA");
                drawPathAContent(canvas,getPathAFromLowerLeft());
                endTrace();

                beginTrace("drawPathC");
                drawPathCContent(canvas,getPathAFromLowerLeft());
                endTrace();

                beginTrace("drawPathB");
                drawPathBContent(canvas,getPathAFromLowerLeft());
                endTrace();

            } else if (f.x == 0 && f.y == 0) {

                beginTrace("drawPathA");
                drawPathAContent(canvas,getPathAFromTopLeft());
                endTrace();

                beginTrace("drawPathC");
                drawPathCContent(canvas,getPathAFromTopLeft());
                endTrace();

                beginTrace("drawPathB");
                drawPathBContent(canvas,getPathAFromTopLeft());
                endTrace();
            }
        }

    }
    /*private Paint paint = new Paint();
    public void drawPoint(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        canvas.drawPoint(f.x, f.y, paint);
        canvas.drawText("f",f.x, f.y, paint);
        canvas.drawPoint(a.x, a.y, paint);
        canvas.drawText("a",a.x, a.y, paint);
        canvas.drawPoint(b.x, b.y, paint);
        canvas.drawText("b",b.x, b.y, paint);
        canvas.drawPoint(c.x, c.y, paint);
        canvas.drawText("c",c.x, c.y, paint);
        canvas.drawPoint(d.x, d.y, paint);
        canvas.drawText("d",d.x, d.y, paint);
        canvas.drawPoint(e.x, e.y, paint);
        canvas.drawText("e",e.x, e.y, paint);
        canvas.drawPoint(j.x, j.y, paint);
        canvas.drawText("j",j.x, j.y, paint);
        canvas.drawPoint(h.x, h.y, paint);
        canvas.drawText("h",h.x, h.y, paint);
        canvas.drawPoint(i.x, i.y, paint);
        canvas.drawText("i",i.x, i.y, paint);
        canvas.drawPoint(j.x, j.y, paint);
        canvas.drawText("j",j.x, j.y, paint);
        canvas.drawPoint(k.x, k.y, paint);
        canvas.drawText("k",k.x, k.y, paint);


    }*/
    @TargetApi(18)
    private void beginTrace(String tag){
        Trace.beginSection(tag);
    }

    @TargetApi(18)
    private void endTrace(){
        Trace.endSection();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            if(style.equals(STYLE_TOP_RIGHT)){
                setTouchPoint(x,y,STYLE_TOP_RIGHT);
            }else if (style.equals(STYLE_LOWER_RIGHT)) {
                setTouchPoint(x,y,STYLE_LOWER_RIGHT);
            } else if (style.equals(STYLE_LOWER_LEFT)) {
                setTouchPoint(x,y,STYLE_LOWER_LEFT);
            } else if (style.equals(STYLE_TOP_LEFT)) {
                setTouchPoint(x,y,STYLE_TOP_LEFT);
            }else if(style.equals(STYLE_LEFT)){
                setTouchPoint(x,y,STYLE_LEFT);
            } else if(style.equals(STYLE_RIGHT)) {
                setTouchPoint(x,y,STYLE_RIGHT);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if(x<=viewWidth/3 ){//左
                    if (y<=viewHeight/3) { // shang
                        style = STYLE_TOP_LEFT;
                    } else if (y>=viewHeight/3 && y<=viewHeight*2/3) { // zhong
                        style = STYLE_LEFT;
                    } else if (y>=viewHeight*2/3) { // xia
                        style = STYLE_LOWER_LEFT;
                    }

                    setTouchPoint(x,y,style);

                }else if(x>viewWidth*2/3 ){//右
                    if (y<=viewHeight/3) { // shang
                        style = STYLE_TOP_RIGHT;
                        setTouchPoint(x,y,style);
                    } else if (y>viewHeight/3 && y<=viewHeight*2/3) { // zhong
                        style = STYLE_RIGHT;
                        setTouchPoint(x,y,style);
                    } else if(y>viewHeight*2/3) {
                        style = STYLE_LOWER_RIGHT;
                        setTouchPoint(x,y,style);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                setTouchPoint(event.getX(),event.getY(),style);
                break;
            case MotionEvent.ACTION_UP:

                if (style.contains("RIGHT")) {
                    if(event.getX() > defaultWidth * 0.8) {
                        startCancelAnimFromRigth();
                    } else {
                        finishAnimRigth();
                    }
                } else if (style.contains("LEFT")) {
                    if(event.getX() < defaultWidth * 0.2) {
                        startCancelAnimFromLeft();
                    } else {
                        finishAnimLeft();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 结束翻页 结尾的动画
     */
    public void finishAnimRigth() {
        // 直接让a点的横坐标 往左翻页
        mScroller.startScroll((int) a.x, (int) a.y, -2*viewWidth, 0, 400);
    }

    /**
     * 取消翻页动画,计算滑动位置与时间
     */
    public void startCancelAnimFromRigth(){
        int dx,dy;
        //让a滑动到f点所在位置，留出1像素是为了防止当a和f重叠时出现View闪烁的情况
        if(style.equals(STYLE_TOP_RIGHT)){
            dx = (int) (viewWidth-1-a.x);
            dy = (int) (1-a.y);
        }else {
            dx = (int) (viewWidth-1-a.x);
            dy = (int) (viewHeight-1-a.y);
        }
        mScroller.startScroll((int) a.x, (int) a.y, dx, dy, 400);
    }

    /**
     * 结束翻页 结尾的动画
     */
    public void finishAnimLeft() {
        // 直接让a点的横坐标 往左翻页
        // 开始执行startScroll 之后，回调用compute方法
        if (!style.contains("TOP") && !style.contains("LOWER")) {
            // 翻页从中间开始的

            mScroller.startScroll((int) a.x, (int) a.y, (int)a.x+ viewWidth, (int) a.y, 400);
        } else {
            // 翻页从上或者下开始的
            mScroller.startScroll((int) a.x, (int) a.y, 2*viewWidth, 0, 400);
        }

    }

    /**
     * 取消翻页动画,计算滑动位置与时间
     */
    public void startCancelAnimFromLeft(){
        int dx,dy;
        // 让a滑动到f点所在位置，留出1像素是为了防止当a和f重叠时出现View闪烁的情况
        if(style.equals(STYLE_TOP_RIGHT)){
            dx = (int) (viewWidth+1+a.x);
            dy = (int) (1-a.y);
        }else {
            dx = (int) (viewWidth+1+a.x);
            dy = (int) (viewHeight+1+a.y);
        }
        mScroller.startScroll((int) a.x, (int) a.y, dx, dy, 400);
    }

    /**
     * 设置触摸点
     * @param x
     * @param y
     * @param style
     */
    public void setTouchPoint(float x, float y, String style){
        MyPoint touchPoint = new MyPoint();

        a.x = x;
        a.y = y;
        this.style = style;
        switch (style){
            case STYLE_TOP_RIGHT:
                f.x = viewWidth;
                f.y = 0;
                calcPointsXY(a,f);
                touchPoint = new MyPoint(x,y);
                /*if(calcPointCX(touchPoint,f)<0){//如果c点x坐标小于0则重新测量a点坐标
                    calcPointAByTouchPoint();
                    calcPointsXY(a,f);
                }*/
                postInvalidate();
                break;

            case STYLE_RIGHT:
                a.y = viewHeight-1;
                f.x = viewWidth;
                f.y = viewHeight;
                calcPointsXY(a,f);
                postInvalidate();
                break;
            case STYLE_LOWER_RIGHT:
                f.x = viewWidth;
                f.y = viewHeight;
                calcPointsXY(a,f);
                touchPoint = new MyPoint(x,y);
                /*if(calcPointCX(touchPoint,f)<0){//如果c点x坐标小于0则重新测量a点坐标
                    calcPointAByTouchPoint();
                    calcPointsXY(a,f);
                }*/
                postInvalidate();
                break;
            case STYLE_LOWER_LEFT:
                f.x = 0;
                f.y = viewHeight;
                calcPointsXY(a,f);

                postInvalidate();
                break;
            case STYLE_TOP_LEFT:
                f.x = 0;
                f.y = 0;
                calcPointsXY(a,f);
                postInvalidate();
                break;
            case STYLE_LEFT:
                a.y = 1;
                f.x = 0;
                f.y = 0;
                calcPointsXY(a,f);
                postInvalidate();
                break;
            default:
                break;
        }
    }

    /**
     * 如果c点x坐标小于0,根据触摸点重新测量a点坐标
     */
    private void calcPointAByTouchPoint(){
        float w0 = viewWidth - c.x;

        float w1 = Math.abs(f.x - a.x);
        float w2 = viewWidth * w1 / w0;
        a.x = Math.abs(f.x - w2);

        float h1 = Math.abs(f.y - a.y);
        float h2 = w2 * h1 / w1;
        a.y = Math.abs(f.y - h2);
    }

    /**
     * 回到默认状态
     */
    public void setDefaultPath(){
        a.x = -1;
        a.y = -1;
        postInvalidate();
    }

    /**
     * 初始化各区域阴影GradientDrawable
     */
    private void createGradientDrawable(){
        int deepColor = 0x33333333;
        int lightColor = 0x01333333;
        int[] gradientColors = new int[]{lightColor,deepColor};//渐变颜色数组
        drawableLeftTopRight = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        drawableLeftTopRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawableLeftLowerRight = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, gradientColors);
        drawableLeftLowerRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        deepColor = 0x22333333;
        lightColor = 0x01333333;
        gradientColors =  new int[]{deepColor,lightColor,lightColor};
        drawableRightTopRight = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradientColors);
        drawableRightTopRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawableRightLowerRight = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientColors);
        drawableRightLowerRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        deepColor = 0x44333333;
        lightColor = 0x01333333;
        gradientColors = new int[]{lightColor,deepColor};//渐变颜色数组
        drawableHorizontalLowerRight = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);;
        drawableHorizontalLowerRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        deepColor = 0x55111111;
        lightColor = 0x00111111;
        gradientColors = new int[] {deepColor,lightColor};//渐变颜色数组
        drawableBTopRight =new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,gradientColors);
        drawableBTopRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);//线性渐变
        drawableBLowerRight =new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,gradientColors);
        drawableBLowerRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        deepColor = 0x55333333;
        lightColor = 0x00333333;
        gradientColors = new int[]{lightColor,deepColor};//渐变颜色数组
        drawableCTopRight = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        drawableCTopRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawableCLowerRight = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, gradientColors);
        drawableCLowerRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }

    /**
     * 绘制A区域内容
     * @param canvas
     * @param pathA
     */
    private void drawPathAContent(Canvas canvas, Path pathA){
        canvas.save();
        canvas.clipPath(pathA, Region.Op.INTERSECT);//对绘制内容进行裁剪，取和A区域的交集
        canvas.drawBitmap(pathAContentBitmap, 0, 0, null);

        if(style.equals(STYLE_LEFT) || style.equals(STYLE_RIGHT)){
            drawPathAHorizontalShadow(canvas,pathA);
        }else {
            drawPathALeftShadow(canvas,pathA);
            drawPathARightShadow(canvas,pathA);
        }
        canvas.restore();
    }

    /**
     * 绘制A区域左阴影
     *
     * @param canvas
     */
    private void drawPathALeftShadow(Canvas canvas, Path pathA){
        canvas.restore();
        canvas.save();

        int left;
        int right;
        int top = (int) e.y;
        int bottom = (int) (e.y+viewHeight);
        Path mPath = new Path();

        GradientDrawable gradientDrawable;
        if (style.equals(STYLE_TOP_RIGHT)) {
            gradientDrawable = drawableLeftTopRight;
            left = (int) (e.x - lPathAShadowDis /2);
            right = (int) (e.x);
            mPath.moveTo(a.x- Math.max(rPathAShadowDis, lPathAShadowDis) /2,a.y);
            mPath.lineTo(d.x,d.y);
            mPath.lineTo(e.x,e.y);
            mPath.lineTo(a.x,a.y);


        } else if (style.equals(STYLE_TOP_LEFT)){
            gradientDrawable = drawableLeftLowerRight;
            left = (int) (e.x);
            right = (int) (e.x + lPathAShadowDis /2);
            mPath.moveTo(a.x + Math.max(rPathAShadowDis, lPathAShadowDis) /2,a.y);
            mPath.lineTo(d.x,d.y);
            mPath.lineTo(e.x,e.y);
            mPath.lineTo(a.x,a.y);

        } else if (style.equals(STYLE_LOWER_LEFT)) {
            gradientDrawable = drawableLeftTopRight;
            left = (int) (e.x );
            right = (int) (e.x + lPathAShadowDis);

            mPath.moveTo(a.x +  Math.max(rPathAShadowDis, lPathAShadowDis) , a.y +  Math.max(rPathAShadowDis, lPathAShadowDis));
            mPath.lineTo(d.x,d.y);
            mPath.lineTo(e.x,e.y);
            mPath.lineTo(a.x,a.y);

        } else if (style.equals(STYLE_TOP_LEFT)) {
            gradientDrawable = drawableLeftTopRight;
            left = (int) (e.x - lPathAShadowDis /2);
            right = (int) (e.x +  lPathAShadowDis /2);

            mPath.moveTo(a.x +  Math.min(rPathAShadowDis, lPathAShadowDis) /2, a.y);
            mPath.lineTo(d.x,d.y);
            mPath.lineTo(e.x,e.y);
            mPath.lineTo(a.x,a.y);

        } else {
            // 所有情况都不是
            gradientDrawable = drawableLeftTopRight;
            left = (int) (e.x + lPathAShadowDis /2);
            right = (int) (e.x);


        }

        mPath.close();
        canvas.clipPath(pathA);
        canvas.clipPath(mPath, Region.Op.INTERSECT);
        float mDegrees = (float) Math.toDegrees(Math.atan2(e.x-a.x, a.y-e.y));
        canvas.rotate(mDegrees, e.x, e.y);
        gradientDrawable.setBounds(left,top,right,bottom);
        gradientDrawable.draw(canvas);
    }


    /**
     * 绘制A区域右阴影
     * @param canvas
     */
    private void drawPathARightShadow(Canvas canvas, Path pathA){
        canvas.restore();
        canvas.save();

        float viewDiagonalLength = (float) Math.hypot(viewWidth, viewHeight);//view对角线长度
        int left = (int) h.x;
        int right = (int) (h.x + viewDiagonalLength*10);//需要足够长的长度
        int top, bottom;
        Path mPath = new Path();
        GradientDrawable gradientDrawable;

        if (style.equals(STYLE_TOP_RIGHT)) {
            gradientDrawable = drawableRightTopRight;
            top = (int) (h.y- rPathAShadowDis /2);
            bottom = (int) h.y;

            mPath.moveTo(a.x- Math.max(rPathAShadowDis, lPathAShadowDis) /2,a.y);
            mPath.lineTo(h.x,h.y);
            mPath.lineTo(a.x,a.y);

        } else if(style.equals(STYLE_LOWER_RIGHT)){
            gradientDrawable = drawableRightLowerRight;
            top = (int) h.y;
            bottom = (int) (h.y+ rPathAShadowDis /2);

            mPath.moveTo(a.x- Math.max(rPathAShadowDis, lPathAShadowDis) /2,a.y);
            mPath.lineTo(h.x,h.y);
            mPath.lineTo(a.x,a.y);

        } else if (style.equals(STYLE_TOP_LEFT)) {
            gradientDrawable = drawableRightLowerRight;
            top = (int) h.y;
            bottom = (int) (h.y+ rPathAShadowDis /2);

            mPath.moveTo(a.x+ Math.max(rPathAShadowDis, lPathAShadowDis) /2,a.y);
            mPath.lineTo(h.x,h.y);
            mPath.lineTo(a.x,a.y);

        } else if (style.equals(STYLE_LOWER_LEFT)) {

            gradientDrawable = drawableRightLowerRight;
            top = (int) h.y;
            bottom = (int) (h.y+ rPathAShadowDis /2);

            mPath.moveTo(a.x+ Math.max(rPathAShadowDis, lPathAShadowDis) /2,a.y);
            mPath.lineTo(h.x,h.y);
            mPath.lineTo(a.x,a.y);

        } else {
            gradientDrawable = drawableRightLowerRight;
            top = (int) h.y;
            bottom = (int) (h.y+ rPathAShadowDis /2);
        }
        gradientDrawable.setBounds(left,top,right,bottom);

        mPath.close();
        canvas.clipPath(pathA);
        canvas.clipPath(mPath, Region.Op.INTERSECT);

        float mDegrees = (float) Math.toDegrees(Math.atan2(a.y-h.y, a.x-h.x));
        canvas.rotate(mDegrees, h.x, h.y);
        gradientDrawable.draw(canvas);
    }

    /**
     * 绘制A区域水平翻页阴影
     * @param canvas
     */
    private void drawPathAHorizontalShadow(Canvas canvas, Path pathA){
        canvas.restore();
        canvas.save();
        canvas.clipPath(pathA, Region.Op.INTERSECT);

        int maxShadowWidth = 30;//阴影矩形最大的宽度
        int left = (int) (a.x - Math.min(maxShadowWidth,(rPathAShadowDis/2)));
        int right = (int) (a.x);
        int top = 0;
        int bottom = viewHeight;
        GradientDrawable gradientDrawable = drawableHorizontalLowerRight;
        gradientDrawable.setBounds(left,top,right,bottom);

        float mDegrees = (float) Math.toDegrees(Math.atan2(f.x-a.x,f.y-h.y));
        canvas.rotate(mDegrees, a.x, a.y);
        gradientDrawable.draw(canvas);
    }

    /**
     * 绘制默认的界面
     * @return
     */
    private Path getPathDefault(){
        pathA.reset();
        pathA.lineTo(0, viewHeight);
        pathA.lineTo(viewWidth,viewHeight);
        pathA.lineTo(viewWidth,0);
        pathA.close();
        return pathA;
    }

    /**
     * 获取f点在右上角的pathA
     * @return
     */
    private Path getPathAFromTopRight(){
        pathA.reset();
        pathA.lineTo(c.x,c.y);//移动到c点
        pathA.quadTo(e.x,e.y,b.x,b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x,a.y);//移动到a点
        pathA.lineTo(k.x,k.y);//移动到k点
        pathA.quadTo(h.x,h.y,j.x,j.y);//从k到j画贝塞尔曲线，控制点为h
        pathA.lineTo(viewWidth,viewHeight);//移动到右下角
        pathA.lineTo(0, viewHeight);//移动到左下角
        pathA.close();
        return pathA;
    }

    /**
     * 获取f点在右下角的pathA
     * @return
     */
    private Path getPathAFromLowerRight(){
        pathA.reset();
        pathA.lineTo(0, viewHeight);//移动到左下角
        pathA.lineTo(c.x,c.y);//移动到c点
        pathA.quadTo(e.x,e.y,b.x,b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x,a.y);//移动到a点
        pathA.lineTo(k.x,k.y);//移动到k点
        pathA.quadTo(h.x,h.y,j.x,j.y);//从k到j画贝塞尔曲线，控制点为h
        pathA.lineTo(viewWidth,0);//移动到右上角
        pathA.close();//闭合区域
        return pathA;
    }

    /**
     * 获取f点在左上角的pathA
     * @return
     */
    private Path getPathAFromTopLeft(){
        pathA.reset();
        pathA.moveTo(c.x,c.y);// 移动到c点
        pathA.quadTo(e.x,e.y,b.x,b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x,a.y);//移动到a点
        pathA.lineTo(k.x,k.y);//移动到k点
        pathA.quadTo(h.x,h.y,j.x,j.y);//从k到j画贝塞尔曲线，控制点为h
        pathA.lineTo(0, viewHeight);//移动到
        pathA.lineTo(viewWidth, viewHeight);//移动到
        pathA.lineTo(viewWidth, 0);//移动到
        pathA.close();
        return pathA;
    }

    /**
     * 获取f点在左下角的pathA
     * @return
     */
    private Path getPathAFromLowerLeft(){
        pathA.reset();
        pathA.lineTo(viewWidth, 0);// 移动到 右上顶点
        pathA.lineTo(viewWidth, viewHeight);// 移动到 右下
        pathA.lineTo(c.x,c.y);//移动到c点
        pathA.quadTo(e.x,e.y,b.x,b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x,a.y);//移动到a点
        pathA.lineTo(k.x,k.y);//移动到k点
        pathA.quadTo(h.x,h.y,j.x,j.y);//从k到j画贝塞尔曲线，控制点为h
        pathA.lineTo(0,0);//移动到右上角
        pathA.close();//闭合区域
        return pathA;
    }

    /**
     * 绘制B区域内容
     * @param canvas
     * @param pathA
     */
    private void drawPathBContent(Canvas canvas, Path pathA){
        canvas.save();
        canvas.clipPath(pathA);//裁剪出A区域
        canvas.clipPath(getPathC(),Region.Op.UNION);// 裁剪出A和C区域的全集
        canvas.clipPath(getPathB(), Region.Op.REVERSE_DIFFERENCE);//裁剪出B区域中不同于与AC区域的部分
        canvas.drawBitmap(pathBContentBitmap, 0, 0, null);

        drawPathBShadow(canvas);
        canvas.restore();
    }

    /**
     * 绘制B区域阴影，阴影左深右浅
     * @param canvas
     */
    private void drawPathBShadow(Canvas canvas){
        int deepOffset = 0;//深色端的偏移值
        int lightOffset = 0;//浅色端的偏移值
        float aTof =(float) Math.hypot((a.x - f.x),(a.y - f.y));//a到f的距离
        float viewDiagonalLength = (float) Math.hypot(viewWidth, viewHeight);//对角线长度

        int left;
        int right;
        int top = 0;
        int bottom = 0;
        GradientDrawable gradientDrawable;
        if(style.equals(STYLE_TOP_RIGHT)){//f点在右上角

             top = (int) c.y;
             bottom = (int) (viewDiagonalLength + c.y);
            //从左向右线性渐变
            gradientDrawable = drawableBTopRight;

            left = (int) (c.x - deepOffset);//c点位于左上角
            right = (int) (c.x + aTof/4 + lightOffset);
        }else if(style.equals(STYLE_LOWER_RIGHT)) {
             top = (int) c.y;
             bottom = (int) (viewDiagonalLength + c.y);
            //从右向左线性渐变
            gradientDrawable = drawableBLowerRight;

            left = (int) (c.x - aTof/4 - lightOffset);//c点位于左下角
            right = (int) (c.x + deepOffset);
        } else if (style.equals(STYLE_TOP_LEFT)) {
             top = (int) c.y;
             bottom = (int) (viewDiagonalLength - c.y);
            //
            gradientDrawable = drawableBTopRight;

            left = (int) (c.x - deepOffset);//
            right = (int) (c.x - aTof/4 - lightOffset);

        } else if (style.equals(STYLE_LOWER_LEFT)) {
            top = (int) c.y;
            bottom = (int) (viewDiagonalLength - c.y);
            gradientDrawable = drawableBTopRight;

            left = (int) (c.x - deepOffset);//
            right = (int) (c.x + aTof/4 - lightOffset);
        } else {
            //从右向左线性渐变
            gradientDrawable = drawableBLowerRight;

            left = (int) (c.x - aTof/4 - lightOffset);//c点位于左下角
            right = (int) (c.x + deepOffset);
        }
        gradientDrawable.setBounds(left,top,right,bottom);//设置阴影矩形
        float rotateDegrees = (float) Math.toDegrees(Math.atan2(e.x- f.x, h.y - f.y));//旋转角度
        canvas.rotate(rotateDegrees, c.x, c.y);//以c为中心点旋转
        gradientDrawable.draw(canvas);
    }

    /**
     * 绘制区域B
     * @return
     */
    private Path getPathB(){
        pathB.reset();
        pathB.lineTo(0, viewHeight);//移动到左下角
        pathB.lineTo(viewWidth,viewHeight);//移动到右下角
        pathB.lineTo(viewWidth,0);//移动到右上角
        pathB.close();//闭合区域
        return pathB;
    }

    /**
     * 绘制C区域内容
     * @param canvas
     * @param pathA
     */
    private void drawPathCContent(Canvas canvas, Path pathA){
        // 细分值横竖各200个网格
        int SUB_WIDTH = 200, SUB_HEIGHT = 200;


        canvas.save();
        canvas.clipPath(pathA);
        canvas.clipPath(getPathC(), Region.Op.REVERSE_DIFFERENCE);// 裁剪出C区域不同于A区域的部分

        float eh = (float) Math.hypot((float)(f.x - e.x),(float)(h.y - f.y));
        float sin0 = (f.x - e.x) / eh;
        float cos0 = (h.y - f.y) / eh;
        double radiu = Math.atan((float)Math.abs(h.y - f.y) / (float)(f.x - e.x)) * 180 / Math.PI;
//        double radiu = Math.asin((float)(f.x - e.x) / eh)* 180 / Math.PI;

        mMatrix.reset();
//        mMatrix.setValues(mMatrixArray);// 翻转和旋转
//        mMatrix.preTranslate(-e.x, -e.y);// 沿当前XY轴负方向位移得到 矩形A₃B₃C₃D₃
//        mMatrix.postTranslate(e.x , e.y );//沿原XY轴方向位移得到 矩形A4 B4 C4 D4
        mMatrix.preScale(-1, 1);
        mMatrix.preRotate(-(float) ((2 * (90 - radiu))));

        float[] pts = new float[]{f.x, f.y};
        mMatrix.mapPoints(pts);


        Log.i("jin2", "point Controller" + "a.x = " + a.x + ", a.y = " + a.y);
//        Log.i("jin2", "pts pts.x = " + pts[0] + ", pts.y = " + pts[1]);
//        Log.i("jin2", "postTranslate x = " + (Math.abs(a.x) + Math.abs(pts[0])) + ", y = " + (-Math.abs(Math.abs(pts[1]) - Math.abs(a.y))));
//        Log.i("jin2", "radiu  = " + radiu);
        if (radiu > 45) {
            mMatrix.postTranslate(a.x- pts[0], a.y - pts[1]);
        } else if (radiu <= 45) {
            mMatrix.postTranslate(Math.abs(a.x) + Math.abs(pts[0]),  Math.abs(Math.abs(pts[1]) + Math.abs(a.y)));

        }
        pts = new float[]{f.x, f.y};
        mMatrix.mapPoints(pts);

        Log.i("jin2", "after move pts.x = " + pts[0] + ", pts.y = " + pts[1]);

        // ab 长度
        float ab = (float) Math.hypot(a.x - b.x , a.y - b.y -10);
        // ab延长线到底部长度
//        float abextend = (float)Math.hypot(a.x - viewWidth, a.y - viewHeight);

        // ak 长度
        float ak = (float) Math.hypot(a.x - k.x, a.y - k.y);
//        // ak 的延长线
//        float akextend = (float) Math.hypot(a.x - viewWidth, a.y);

        float[] vets = new float[(SUB_WIDTH + 1) * (SUB_HEIGHT + 1) * 2];
        float fx, fy;
        int index = 0, offset = 10, testOffset = 50;

        int width = pathCContentBitmap.getWidth(), height = pathCContentBitmap.getHeight();

        for (int heightNum = 0; heightNum <= SUB_HEIGHT; heightNum++) {
            fy = height * heightNum/SUB_HEIGHT ;
            for (int widthNum = 0; widthNum <= SUB_WIDTH; widthNum++) {

                fx = width * widthNum/SUB_WIDTH ;
                // 这里是最边上的要进行拉伸
//                fx += 1f * (offset) ;
//                fy += 1f * (offset);
                if (width == SUB_WIDTH ) {
//                    fx += 1f * (offset) ;

                    if (viewHeight/SUB_HEIGHT * (heightNum + 1) > 100) {

                    }
                } else

                if (height == SUB_HEIGHT ) {
//                    fy += 1f * (offset);
                    if (viewWidth/SUB_WIDTH * (widthNum + 1) > 100) {

                    }
                } else

                if (width == 0) {
//                    fx -= 2f * (offset) ;
                    if (viewHeight/SUB_HEIGHT * (heightNum + 1) > 100) {

                    }
                } else
                if (height == 0) {
//                    fy -= 1f * (offset) ;
                    if (viewWidth/SUB_WIDTH * (widthNum + 1) > 100) {

                    }
                }

                vets[index*2] = fx;
                vets[index*2 + 1] = fy;
                index ++;
            }
        }

        canvas.concat(mMatrix);

        canvas.drawBitmapMesh(pathCContentBitmap, SUB_WIDTH, SUB_HEIGHT, vets, 0, null, 0, null);
        drawPathCShadow(canvas);
//        Paint paint = new Paint();
//        paint.setColor(Color.YELLOW);
//        canvas.drawCircle(0, pathCContentBitmap.getHeight(), 50, paint);
//        canvas.drawCircle(pathCContentBitmap.getWidth(), pathCContentBitmap.getHeight(), 50, paint);
//        canvas.drawCircle(-pathCContentBitmap.getWidth(), -pathCContentBitmap.getHeight(), 50, paint);
//        canvas.drawCircle(0, 0, 50, paint);
//
//        paint.setColor(Color.RED);
//        paint.setTextSize(20);
//        canvas.drawText("one", 0, pathCContentBitmap.getHeight(), paint);
//        canvas.drawText("two", pathCContentBitmap.getWidth(), pathCContentBitmap.getHeight(), paint);
//        canvas.drawText("three", -pathCContentBitmap.getWidth(), -pathCContentBitmap.getHeight(), paint);
//        canvas.drawText("four", 0, 0, paint);
        canvas.restore();
    }

    /**
     * 绘制C区域阴影，阴影左浅右深
     * @param canvas
     */
    private void drawPathCShadow(Canvas canvas){
        int deepOffset = 1;//深色端的偏移值
        int lightOffset = -30;//浅色端的偏移值
        float viewDiagonalLength = (float) Math.hypot(viewWidth, viewHeight);//view对角线长度
        int midpoint_ce = (int) (c.x + e.x) / 2;//ce中点
        int midpoint_jh = (int) (j.y + h.y) / 2;//jh中点
        float minDisToControlPoint = Math.min(Math.abs(midpoint_ce - e.x), Math.abs(midpoint_jh - h.y));//中点到控制点的最小值

        int left;
        int right;
        int top = (int) c.y;
        int bottom = (int) (viewDiagonalLength + c.y);
        GradientDrawable gradientDrawable;
        float mDegrees  = (float) Math.toDegrees(Math.atan2(e.x- f.x, h.y - f.y));
        if (style.equals(STYLE_TOP_RIGHT)) {

            gradientDrawable = drawableCTopRight;
            left = (int) (c.x - lightOffset);
            right = (int) (c.x + minDisToControlPoint + deepOffset);
        } else if(style.equals(STYLE_LOWER_RIGHT)) {
            gradientDrawable = drawableCLowerRight;
            left = (int) (c.x - minDisToControlPoint - deepOffset);
            right = (int) (c.x + lightOffset);
        } else if(style.equals(STYLE_TOP_LEFT)) {

            gradientDrawable = drawableCLowerRight;
            left = (int) (c.x - minDisToControlPoint - deepOffset);
            right = (int) (c.x + lightOffset);

        } else if(style.equals(STYLE_LOWER_LEFT)) {

            gradientDrawable = drawableCTopRight;
            left = (int) (c.x - lightOffset);
            right = (int) (c.x + minDisToControlPoint + deepOffset);

        } else if (style.equals(STYLE_LEFT)){
            gradientDrawable = drawableCLowerRight;
            left = (int) (c.x - minDisToControlPoint - deepOffset);
            right = (int) (c.x + lightOffset);
        } else {

            gradientDrawable = drawableCLowerRight;
            left = (int) (c.x - minDisToControlPoint - deepOffset);
            right = (int) (c.x + lightOffset);
        }
        gradientDrawable.setBounds(left,top,right,bottom);
        canvas.rotate(mDegrees, c.x, c.y);
        gradientDrawable.draw(canvas);
    }

    /**
     * 绘制区域C
     * @return
     */
    private Path getPathC(){
        pathC.reset();
        pathC.moveTo(i.x,i.y);//移动到i点
        pathC.lineTo(d.x,d.y);//移动到d点
        pathC.lineTo(b.x,b.y);//移动到b点
        pathC.lineTo(a.x,a.y);//移动到a点
        pathC.lineTo(k.x,k.y);//移动到k点
        pathC.close();//闭合区域
        return pathC;
    }

    /**
     * 计算各点坐标
     * @param a
     * @param f
     */
    private void calcPointsXY(MyPoint a, MyPoint f){
        if (f.x >0) { // 右边往左 下一页
            g.x = (a.x + f.x) / 2;
            g.y = (a.y + f.y) / 2;

            e.x = g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x);
            e.y = f.y;

            h.x = f.x;
            h.y = g.y - (f.x - g.x) * (f.x - g.x) / (f.y - g.y);

            c.x = e.x - (f.x - e.x) / 2;
            c.y = f.y;

            j.x = f.x;
            j.y = h.y - (f.y - h.y) / 2;

            b = getIntersectionPoint(a,e,c,j);
            k = getIntersectionPoint(a,h,c,j);

            d.x = (c.x + 2 * e.x + b.x) / 4;
            d.y = (2 * e.y + c.y + b.y) / 4;

            i.x = (j.x + 2 * h.x + k.x) / 4;
            i.y = (2 * h.y + j.y + k.y) / 4;

            //计算d点到ae的距离
            float lA = a.y-e.y;
            float lB = e.x-a.x;
            float lC = a.x*e.y-e.x*a.y;
            lPathAShadowDis = Math.abs((lA*d.x+lB*d.y+lC)/(float) Math.hypot(lA,lB));

            //计算i点到ah的距离
            float rA = a.y-h.y;
            float rB = h.x-a.x;
            float rC = a.x*h.y-h.x*a.y;
            rPathAShadowDis = Math.abs((rA*i.x+rB*i.y+rC)/(float) Math.hypot(rA,rB));
        } else {
            // 左边 往前翻页
            g.x = (a.x + f.x) / 2;
            g.y = (a.y + f.y) / 2;
            // gf 2 = gm 2 + mf 2  mf/gf = gf/ef  ef = gf2/mf
            e.x = (int)(Math.pow(g.x, 2) + Math.pow(f.y - g.y, 2))/g.x;
            e.y = f.y;
            // mf / gm = hf / ef   hf = mf * ef /gm
            h.x = 0;
            h.y = f.y - (g.x * e.x / (f.y - g.y));

            c.x = e.x + (e.x) / 2;
            c.y = f.y;

            j.x = 0;
            j.y = h.y - (f.y - h.y) / 2;

            b = getIntersectionPoint(a,e,c,j);
            k = getIntersectionPoint(a,h,c,j);


            d.x = (c.x + 2 * e.x + b.x) / 4;
            d.y = (2 * e.y + c.y + b.y) / 4;

            i.x = (j.x + 2 * h.x + k.x) / 4;
            i.y = (2 * h.y + j.y + k.y) / 4;

            //计算d点到ae的距离
            float lA = a.y-e.y;
            float lB = e.x-a.x;
            float lC = a.x*e.y-e.x*a.y;
            lPathAShadowDis = Math.abs((lA*d.x+lB*d.y+lC)/(float) Math.hypot(lA,lB));

            //计算i点到ah的距离
            float rA = a.y-h.y;
            float rB = h.x-a.x;
            float rC = a.x*h.y-h.x*a.y;
            rPathAShadowDis = Math.abs((rA*i.x+rB*i.y+rC)/(float) Math.hypot(rA,rB));

        }

    }

    /**
     * 计算两线段相交点坐标
     * @param lineOne_My_pointOne
     * @param lineOne_My_pointTwo
     * @param lineTwo_My_pointOne
     * @param lineTwo_My_pointTwo
     * @return 返回该点
     */
    private MyPoint getIntersectionPoint(MyPoint lineOne_My_pointOne, MyPoint lineOne_My_pointTwo, MyPoint lineTwo_My_pointOne, MyPoint lineTwo_My_pointTwo){
        float x1,y1,x2,y2,x3,y3,x4,y4;
        x1 = lineOne_My_pointOne.x;
        y1 = lineOne_My_pointOne.y;
        x2 = lineOne_My_pointTwo.x;
        y2 = lineOne_My_pointTwo.y;
        x3 = lineTwo_My_pointOne.x;
        y3 = lineTwo_My_pointOne.y;
        x4 = lineTwo_My_pointTwo.x;
        y4 = lineTwo_My_pointTwo.y;

        float pointX =((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY =((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));

        return  new MyPoint(pointX,pointY);
    }

    /**
     * 计算C点的X值
     * @param a
     * @param f
     * @return
     */
    private float calcPointCX(MyPoint a, MyPoint f){
        MyPoint g,e;
        g = new MyPoint();
        e = new MyPoint();
        g.x = (a.x + f.x) / 2;
        g.y = (a.y + f.y) / 2;

        e.x = g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x);
        e.y = f.y;

        return e.x - (f.x - e.x) / 2;
    }

    public float getViewWidth(){
        return viewWidth;
    }

    public float getViewHeight(){
        return viewHeight;
    }
}
