package com.bemetoy.bp.plugin.ranking.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bemetoy.bp.plugin.ranking.R;
import com.bemetoy.bp.sdk.utils.TypefaceUtil;
import com.bemetoy.bp.uikit.widget.BpTabStrip;

/**
 * Created by Tom on 2016/8/24.
 */
public class RankingTitleTextView extends TextView implements BpTabStrip.OnItemChecked {

    private static final int TEXT_CHECK_STROKE_BOTTOM_COLOR = 0XFFdf0a00;//酒红色
    private static final int TEXT_CHECK_STROKE_TOP_COLOR = 0XFFe77200;//蛋黄色
    private static final int TEXT_CHECK_STROKE_CENTER_COLOR = 0XFF000000;


    private static final int TEXT_STROKE_BOTTOM_COLOR = 0XFF0073b5;
    private static final int TEXT_STROKE_TOP_COLOR = 0XFF45c8ff;
    private static final int TEXT_STROKE_CENTER_COLOR = 0XFF000000;


    private static final int TEXT_STEP_1_COLOR = 0XFFfffde9;
    private static final int TEXT_STEP_2_COLOR = 0X44cbcbcb;
    private static final int TEXT_STEP_3_COLOR = 0XFFffffff;

    private boolean isChecked = false;


    public RankingTitleTextView(Context context) {
        super(context);
        init(context);
    }

    public RankingTitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RankingTitleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        Typeface tf = TypefaceUtil.get(context, "fonts/yahei.ttf"); //set YAHEI as the default font.
        if (tf != null && this.getTypeface() != null) {
            tf = Typeface.create(tf, this.getTypeface().getStyle()); // set the style
            this.setTypeface(tf);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint strokePaint = new TextPaint(getPaint());
        Shader shader = null;
        if(isChecked) {
            shader = new LinearGradient(
                    0, 0, 0, strokePaint.getTextSize(),
                    new int[]{TEXT_CHECK_STROKE_TOP_COLOR, TEXT_CHECK_STROKE_CENTER_COLOR, TEXT_CHECK_STROKE_BOTTOM_COLOR},
                    new float[]{0.3f,0.6f,1f},
                    Shader.TileMode.CLAMP);
        } else {
            shader = new LinearGradient(
                    0, 0, 0, strokePaint.getTextSize(),
                    new int[]{TEXT_STROKE_TOP_COLOR, TEXT_STROKE_CENTER_COLOR, TEXT_STROKE_BOTTOM_COLOR},
                    new float[]{0.3f,0.6f,1f},
                    Shader.TileMode.CLAMP);
        }

//        strokePaint.setStyle(Paint.Style.STROKE);
//        strokePaint.setStrokeWidth(1.5f);
//        strokePaint.setShader(shader);
//        strokePaint.setAntiAlias(true);
//        canvas.drawText(getText().toString(), (getWidth() - strokePaint.measureText(getText().toString())) / 2 + getCompoundDrawablePadding(),
//                getBaseline(), strokePaint);

        shader = new LinearGradient(
                0, 0, 0, strokePaint.getTextSize(),
                new int[]{TEXT_STEP_3_COLOR, TEXT_STEP_1_COLOR , TEXT_STEP_1_COLOR},
                new float[]{0.7f, 0.75f, 1f},
                Shader.TileMode.CLAMP);
        getPaint().setShader(shader);
        super.onDraw(canvas);
    }

    @Override
    public void onCheckedChange(boolean isCheck) {
        isChecked = isCheck;
        if(isCheck) {
            this.setBackgroundResource(R.drawable.tab_check_bg);
        } else {
            this.setBackgroundResource(R.drawable.tab_uncheck_bg);
        }
        invalidate();
    }
}
