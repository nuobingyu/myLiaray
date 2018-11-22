package com.nby.circleimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Matrix;
import android.graphics.RadialGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static android.graphics.Shader.TileMode.CLAMP;


public class CircleImageView extends android.support.v7.widget.AppCompatImageView
        implements View.OnClickListener{

    private Paint mPaint;
    private Canvas mCanvas;
    private int width;
    private int height;
    private int mRadius = 10; //半径
    private float mScale = 1.0f; //图与布局的比例
    private int cx,cy;
    private android.graphics.Matrix matrix ;
    private float[] matrixValues = new float[9];
    private static boolean haveBorder ;
    private final int borderWidth = 2;
    private Context mContext;


    public CircleImageView(Context context) {
        super(context,null);
    }

    @SuppressLint("CustomViewStyleable")
    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    @SuppressLint({"ClickableViewAccessibility","CustomViewStyleable"})
    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPaint = new Paint();
        matrix = new Matrix();
        mCanvas = new Canvas();

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CircleView);
        if(typedArray!=null) {
            haveBorder = typedArray.getBoolean(R.styleable.CircleView_have_border,false);
            mRadius = typedArray.getInt(R.styleable.CircleView_radius ,0);
            Log.e("typeArray"," "+haveBorder);
            typedArray.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup.LayoutParams lp = getLayoutParams();
        if(lp.width == ViewGroup.LayoutParams.WRAP_CONTENT && lp.height == ViewGroup.LayoutParams.WRAP_CONTENT){
            lp.width = dip2px(50);
            lp.height = dip2px(50);
        }else if(lp.width == ViewGroup.LayoutParams.WRAP_CONTENT){
            lp.width = dip2px(50);
        }else if(lp.height == ViewGroup.LayoutParams.WRAP_CONTENT){
            lp.height = dip2px(50);
        }
    }



    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        height = getHeight() - getPaddingTop() - getPaddingBottom();
        width = getWidth() - getPaddingLeft() - getPaddingRight();
        int size = Math.min(height ,width);
        mRadius=size/2;
        //Log.i("onMeasure",width+","+height);
        // Log.i("onMeasure","mRadius="+mRadius);
        mCanvas = canvas;
        Drawable d = getDrawable();
        if(d != null){
            cx = mRadius;
            cy = mRadius;
            if(haveBorder){
                drawBorder();
                mRadius -= dip2px(borderWidth);
            }
            Bitmap bitmap =((BitmapDrawable)d).getBitmap();
            BitmapShader bitmapShader = new BitmapShader(bitmap , CLAMP , CLAMP);
            mScale = (mRadius * 2.0f) / Math.min(bitmap.getHeight() ,bitmap.getWidth());
            matrix.setScale(mScale,mScale);
            bitmapShader.setLocalMatrix(matrix);
            mPaint.setShader(bitmapShader);

            Path path = new Path();
            path.addCircle(cx,cy,mRadius, Path.Direction.CW);
            canvas.clipPath(path);
            canvas.setMatrix(matrix);
            canvas.drawARGB(0,0,0,0);

            Log.i("Bitmap",bitmap.getWidth() +","+bitmap.getHeight());
            if(haveBorder) {
                //选取图片中间位置
                int drawY = (int) (bitmap.getHeight()/2-(mRadius-dip2px(borderWidth))/mScale );
                canvas.drawBitmap(bitmap,0, drawY , mPaint);
            }else{
                canvas.drawBitmap(bitmap, 0 , 0, mPaint);
            }
        }else{

            super.onDraw(canvas);
        }
    }

    public int dip2px(int px){
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int)(px*scale +0.5f);
    }


    @Override
    public void onClick(View v) {
//        post(new Runnable( ) {
//            @Override
//            public void run() {
//                Toast.makeText(mContext ,"点击了这个圆形图片",Toast.LENGTH_SHORT ).show();
//            }
//        });

    }

    public void drawBorder(){
        Log.i("drawBorder","画边界了~~~半径为："+mRadius);
        RadialGradient gradient = new RadialGradient(cx,cy,mRadius,new int[]{Color.GRAY,0x80000000}
        ,new float[]{0.980f,1.0f} , CLAMP);
        Paint paint = new Paint();

        paint.setStrokeWidth(dip2px(borderWidth));
        Log.i("画笔的宽度",paint.getStrokeWidth()+"");
        paint.setShader(gradient);
        paint.setAntiAlias(true); //抗锯齿
        paint.setStyle(Paint.Style.STROKE);
        mCanvas.drawCircle(cx ,cy,(mRadius-dip2px(borderWidth/2)),paint);
    }

    public void setBorderVisible(){
        haveBorder = true;
        invalidate();
    }

    public void setBorderGone(){
        haveBorder = false;
        invalidate();
    }
}