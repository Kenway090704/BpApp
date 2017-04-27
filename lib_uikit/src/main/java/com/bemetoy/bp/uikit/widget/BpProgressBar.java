package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by Tom on 2016/5/27.
 */
public class BpProgressBar extends ProgressBar {

    private Paint paint;
    private String text;

    public BpProgressBar(Context context) {
        super(context);
        initText();
    }

    public BpProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    public BpProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initText();
    }

    @Override
    public synchronized void setProgress(int progress) {
        // TODO Auto-generated method stub
        setText(progress);
        super.setProgress(progress);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //this.setText();
        Rect rect = new Rect();
        this.paint.getTextBounds(this.text, 0, this.text.length(), rect);
        paint.setTextSize(42f);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2);
        canvas.drawText(this.text, x, y, this.paint);
    }

    //初始化，画笔
    private void initText(){
        this.paint = new Paint();
        paint.setAntiAlias(true);
        this.paint.setColor(Color.WHITE);

    }

    private void setText(){
        setText(this.getProgress());
    }

    //设置文字内容
    private void setText(int progress){
        this.text = progress + "/" + getMax();
    }
}
