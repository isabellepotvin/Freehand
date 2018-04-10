package com.team06.freehand.Chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.team06.freehand.R;


/**
 * Created by isabellepotvin on 2018-01-29.
 */

public class DrawingView extends View {

    private static final String TAG = "DrawingView";

    Context mContext;

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    //close button
    private ImageView closeBtn;

    private float brushSize, lastBrushSize;
    private boolean erase=false;



    //CONSTRUCTOR
    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();

        mContext = context;

        Log.d(TAG, "DrawingView: creating drawing view");
        Log.d(TAG, "DrawingView: context: " + context + " , " + ((Activity) context));
    }





    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        brushSize = 20;
        lastBrushSize = brushSize;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                closeBtn.setVisibility(GONE);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                closeBtn.setVisibility(VISIBLE);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }


    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    //updates brush size
    public void setBrushSize(float newSize){
        Log.d(TAG, "setBrushSize: new brush size: " + newSize);
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
        Log.d(TAG, "setBrushSize: brush size (pixels): " + brushSize);

    }

    //setter
    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }

    //getter
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public int getPaintColor() {
        return paintColor;
    }

    //ERASER
    public void setErase(boolean isErase){
        erase=isErase;

        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    //NEW DRAWING
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setupCloseBtn(ImageView button){
        closeBtn = button;
    }

}
