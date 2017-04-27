package com.bemetoy.bp.uikit.widget;

/**
 * Created by Tom on 2016/5/3.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.TypefaceUtil;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.R;


/**
 * Created by Tom on 2016/3/28.
 * <p>
 * 指定属性的需要按照逆时针排列，因为会按照这个顺序选取属性
 * 这样影响最终view 测量的大小。
 */
public class RadarChartView extends View {

    private static final String TAG = "uikit.RadarChar";

    private static final int PROPERTY_COUNT = 8;

    private float mRadius;
    private float mCenterX;
    private float mCenterY;

    private Paint mCirclePaint = new Paint();
    private Paint mValuePaint = new Paint();
    private Paint mStrokePaint = new Paint();

    private static final float OFFSET = 22.5f;
    private static final float SECTOR_DEGREE = 45f;

    // the padding between the property txt and RadarChart view.
    private int mPadding;
    private int mBgColor;
    private int mCircleColor;

    private static final float[] DEFAULT_DATA = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
    private float data[] = DEFAULT_DATA;
    private float mPropertyImageScale = 1f;
    Drawable stabilityDrawable;
    Drawable outPutPowerDrable;
    Drawable flexbilityDrawable;
    Drawable roadHoldingDrawable;
    Drawable rotateSpeedDrawable;
    Drawable staminaDrawable;
    Drawable torsionDrawable;
    Drawable weightDrawable;

    public RadarChartView(Context context) {
        this(context, null);
    }

    public RadarChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpRadarChart, defStyleAttr, 0);

        mCirclePaint.setAntiAlias(true);
        mValuePaint.setAntiAlias(true);
        mStrokePaint.setAntiAlias(true);

        mRadius = a.getDimensionPixelSize(R.styleable.BpRadarChart_radar_radius, 0);

        int circleStrokeWidth = a.getDimensionPixelOffset(R.styleable.BpRadarChart_radar_circle_stroke_width, 3);
        mCirclePaint.setStrokeWidth(circleStrokeWidth);

        mCircleColor = a.getColor(R.styleable.BpRadarChart_radar_circle_color, Color.CYAN);

        int circleColorValue = a.getColor(R.styleable.BpRadarChart_radar_value_color, Color.LTGRAY);
        mValuePaint.setColor(circleColorValue);

        int circleColorValueTrans = a.getInteger(R.styleable.BpRadarChart_radar_value_color_transparent, 120);
        mValuePaint.setAlpha(circleColorValueTrans);

        mBgColor = a.getColor(R.styleable.BpRadarChart_radar_bg_color, Color.TRANSPARENT);

        int strokeValueColor = a.getColor(R.styleable.BpRadarChart_radar_value_stroke_color, Color.WHITE);
        mStrokePaint.setColor(strokeValueColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        int strokeWidth = a.getDimensionPixelSize(R.styleable.BpRadarChart_radar_value_stroke_width, 0);
        mStrokePaint.setStrokeWidth(strokeWidth);

        mPadding = a.getDimensionPixelSize(R.styleable.BpRadarChart_property_view_padding, 16);
        mPropertyImageScale = a.getFloat(R.styleable.BpRadarChart_radar_property_scale, 1f);
        stabilityDrawable = getContext().getResources().getDrawable(R.drawable.stability);
        outPutPowerDrable = getContext().getResources().getDrawable(R.drawable.output_power);
        flexbilityDrawable = getContext().getResources().getDrawable(R.drawable.flexbility);
        roadHoldingDrawable = getContext().getResources().getDrawable(R.drawable.road_holding);
        rotateSpeedDrawable = getContext().getResources().getDrawable(R.drawable.rotate_speed);
        staminaDrawable = getContext().getResources().getDrawable(R.drawable.stamina);
        torsionDrawable = getContext().getResources().getDrawable(R.drawable.torsion);
        weightDrawable = getContext().getResources().getDrawable(R.drawable.weight);
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {

            int weightDrawableActualWidth = (int) (weightDrawable.getIntrinsicWidth() * mPropertyImageScale);
            int rotateSpeedDrawableActualWidth = (int) (rotateSpeedDrawable.getIntrinsicWidth() * mPropertyImageScale);
            int imageLeftWidth = weightDrawableActualWidth > rotateSpeedDrawableActualWidth ?
                    weightDrawableActualWidth : rotateSpeedDrawableActualWidth;
            int roadHoldingDrawableActualWidth = (int) (roadHoldingDrawable.getIntrinsicWidth() * mPropertyImageScale);
            int staminaDrawableActualWidth = (int) (staminaDrawable.getIntrinsicWidth() * mPropertyImageScale);
            int imageRightWith = roadHoldingDrawableActualWidth > staminaDrawableActualWidth ?
                    roadHoldingDrawableActualWidth : staminaDrawableActualWidth;

            int w = (int) (mRadius * (float) Math.cos(Math.toRadians(OFFSET)) * 2 + imageLeftWidth + imageRightWith + mPadding * 2);
            width = Math.min(w, width);
        }

        if (heightMode == MeasureSpec.AT_MOST) {

            int stabilityDrawableActualHeight = (int) (stabilityDrawable.getIntrinsicHeight() * mPropertyImageScale);
            int flexbilityDrawableActualHeight = (int) (flexbilityDrawable.getIntrinsicHeight() * mPropertyImageScale);
            int imageTopHeight = stabilityDrawableActualHeight > flexbilityDrawableActualHeight ?
                    stabilityDrawableActualHeight : flexbilityDrawableActualHeight;

            int outPutPowerDrawableActualHeight = (int) (outPutPowerDrable.getIntrinsicHeight() * mPropertyImageScale);
            int torsionDrawableActualHeight = (int)(torsionDrawable.getIntrinsicHeight() * mPropertyImageScale);
            int imageBottomHeight = outPutPowerDrawableActualHeight > torsionDrawableActualHeight ?
                    outPutPowerDrawableActualHeight : torsionDrawableActualHeight;
            int h = (int) (mRadius * (float) Math.cos(Math.toRadians(OFFSET)) * 2 + imageTopHeight + imageBottomHeight + mPadding * 2);
            height = Math.min(height, h);
        }
        Log.i(TAG, "measured width is %d, height is %d", width, height);
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        float widthRadius = mRadius * (float) Math.cos(Math.toRadians(OFFSET));

        int weightDrawableActualWidth = (int) (weightDrawable.getIntrinsicWidth() * mPropertyImageScale);
        int rotateSpeedDrawableActualWidth = (int) (rotateSpeedDrawable.getIntrinsicWidth() * mPropertyImageScale);
        int imageLeftWidth = weightDrawableActualWidth > rotateSpeedDrawableActualWidth ?
                weightDrawableActualWidth : rotateSpeedDrawableActualWidth;
        mCenterX = imageLeftWidth +  mPadding + widthRadius;

        int stabilityDrawableActualHeight = (int) (stabilityDrawable.getIntrinsicHeight() * mPropertyImageScale);
        int flexbilityDrawableActualHeight = (int) (flexbilityDrawable.getIntrinsicHeight() * mPropertyImageScale);
        int imageTopHeight = stabilityDrawableActualHeight > flexbilityDrawableActualHeight ?
                stabilityDrawableActualHeight : flexbilityDrawableActualHeight;
        mCenterY = imageTopHeight + mPadding + widthRadius;

        drawBackground(canvas);
        drawRadar(canvas);
        drawLine(canvas);

        Path valuePath = getValuePath();
        canvas.drawPath(valuePath, mValuePaint);
        canvas.drawPath(valuePath, mStrokePaint);
        drawPropertiesImage(canvas);
    }

    private void drawLine(Canvas canvas) {
        canvas.save();
        canvas.rotate(OFFSET, mCenterX, mCenterY);
        for (int i = 0; i <= 3; i++) {
            canvas.rotate(i * SECTOR_DEGREE, mCenterX, mCenterY);
            canvas.drawLine(mCenterX, mCenterY - mRadius, mCenterX, mCenterY + mRadius, mCirclePaint);
        }
        canvas.restore();
    }


    private void drawRadar(Canvas canvas) {
        canvas.save();
        canvas.rotate(OFFSET * 2, mCenterX, mCenterY);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i <= 8; i++) {
            canvas.rotate(i * 45, mCenterX, mCenterY);
            Path p1 = new Path();
            p1.moveTo(mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET)), mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET)));
            p1.lineTo(mCenterX - mRadius * (float) Math.sin(Math.toRadians(OFFSET)), mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET)));
            canvas.drawPath(p1, mCirclePaint);


            Path p2 = new Path();
            p2.moveTo(mCenterX + mRadius * 0.75f * (float) Math.sin(Math.toRadians(OFFSET)), mCenterY - mRadius * 0.75f * (float) Math.cos(Math.toRadians(OFFSET)));
            p2.lineTo(mCenterX - mRadius * 0.75f * (float) Math.sin(Math.toRadians(OFFSET)), mCenterY - mRadius * 0.75f * (float) Math.cos(Math.toRadians(OFFSET)));
            canvas.drawPath(p2, mCirclePaint);

            Path p3 = new Path();
            p3.moveTo(mCenterX + mRadius * 0.5f * (float) Math.sin(Math.toRadians(OFFSET)), mCenterY - mRadius * 0.5f * (float) Math.cos(Math.toRadians(OFFSET)));
            p3.lineTo(mCenterX - mRadius * 0.5f * (float) Math.sin(Math.toRadians(OFFSET)), mCenterY - mRadius * 0.5f * (float) Math.cos(Math.toRadians(OFFSET)));
            canvas.drawPath(p3, mCirclePaint);

            Path p4 = new Path();
            p4.moveTo(mCenterX + mRadius * 0.25f * (float) Math.sin(Math.toRadians(OFFSET)), mCenterY - mRadius * 0.25f * (float) Math.cos(Math.toRadians(OFFSET)));
            p4.lineTo(mCenterX - mRadius * 0.25f * (float) Math.sin(Math.toRadians(OFFSET)), mCenterY - mRadius * 0.25f * (float) Math.cos(Math.toRadians(OFFSET)));
            canvas.drawPath(p4, mCirclePaint);
        }
        canvas.restore();
    }


    private void drawBackground(Canvas canvas) {
        Path path = new Path();

        int startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET)));
        int startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET)));
        path.moveTo(startX, startY);

        for (int i = 0; i < 7; i++) {
            int x = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(-OFFSET - i * SECTOR_DEGREE)));
            int y = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(-OFFSET - i * SECTOR_DEGREE)));
            path.lineTo(x, y);
        }

        path.lineTo(startX, startY);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mBgColor);
        canvas.drawPath(path, mCirclePaint);
    }


    /**
     *
     * @param canvas
     */
    private void drawPropertiesImage(Canvas canvas) {
        /**
         * 因为为了适配不同的设备，所以会对图片有所缩放。
         */
        int flexbilityDrawableActualHeight = (int) (flexbilityDrawable.getIntrinsicHeight() * mPropertyImageScale);
        int flexbilityDrawableActualWidth = (int) (flexbilityDrawable.getIntrinsicWidth() * mPropertyImageScale);

        int startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET)));
        int startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET))) - mPadding;
        flexbilityDrawable.setBounds(startX - flexbilityDrawableActualWidth / 3,
                startY - flexbilityDrawableActualHeight , startX + flexbilityDrawableActualWidth * 2 / 3
                , startY);
        flexbilityDrawable.draw(canvas);

        int stabilityDrawableActualWidth = (int) (stabilityDrawable.getIntrinsicWidth() * mPropertyImageScale);
        int stabilityDrawableActualHeight = (int) (stabilityDrawable.getIntrinsicHeight() * mPropertyImageScale);

        startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET - SECTOR_DEGREE)));
        startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET - SECTOR_DEGREE))) - mPadding;
        stabilityDrawable.setBounds(startX - stabilityDrawableActualWidth * 2 / 3,
                startY - stabilityDrawableActualHeight, startX + stabilityDrawableActualWidth / 3
                , startY);
        stabilityDrawable.draw(canvas);

        int weightDrawableActualWidth = (int) (weightDrawable.getIntrinsicWidth() * mPropertyImageScale);
        int weightDrawableActualHeight = (int) (weightDrawable.getIntrinsicHeight() * mPropertyImageScale);

        startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET - 2 * SECTOR_DEGREE))) - mPadding;
        startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET - 2 * SECTOR_DEGREE)));
        weightDrawable.setBounds(startX - weightDrawableActualWidth,
                startY - weightDrawableActualHeight / 2, startX
                , startY + weightDrawableActualHeight / 2);
        weightDrawable.draw(canvas);

        int rotateSpeedDrawableActualWidth = (int) (rotateSpeedDrawable.getIntrinsicWidth() * mPropertyImageScale);
        int rotateSpeedDrawableActualHeight = (int) (rotateSpeedDrawable.getIntrinsicHeight() * mPropertyImageScale);

        startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET - 3 * SECTOR_DEGREE))) - mPadding;
        startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET - 3 * SECTOR_DEGREE)));
        rotateSpeedDrawable.setBounds(startX - rotateSpeedDrawableActualWidth,
                startY - rotateSpeedDrawableActualHeight / 2, startX
                , startY + rotateSpeedDrawableActualHeight / 2);
        rotateSpeedDrawable.draw(canvas);

        int outPutPowerDrawableActualWidth = (int) (outPutPowerDrable.getIntrinsicWidth() * mPropertyImageScale);
        int outPutPowerDrawableActualHeight = (int) (outPutPowerDrable.getIntrinsicHeight() * mPropertyImageScale);

        startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET - 4 * SECTOR_DEGREE)));
        startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET - 4 * SECTOR_DEGREE))) + mPadding;
        outPutPowerDrable.setBounds(startX - outPutPowerDrawableActualWidth * 2 / 3,
                startY, startX + outPutPowerDrawableActualWidth / 3
                , startY + outPutPowerDrawableActualHeight);
        outPutPowerDrable.draw(canvas);


        int torsionDrawableActualWidth = (int) (torsionDrawable.getIntrinsicWidth() * mPropertyImageScale);
        int torsionDrawableActualHeight = (int) (torsionDrawable.getIntrinsicHeight() * mPropertyImageScale);

        startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET - 5 * SECTOR_DEGREE)));
        startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET - 5 * SECTOR_DEGREE))) + mPadding;
        torsionDrawable.setBounds(startX - torsionDrawableActualWidth / 3,
                startY, startX + torsionDrawableActualWidth * 2 / 3
                , startY + torsionDrawableActualHeight);
        torsionDrawable.draw(canvas);

        int staminaDrawableActualWidth = (int) (staminaDrawable.getIntrinsicWidth() * mPropertyImageScale);
        int staminaDrawableActualHeight = (int) (staminaDrawable.getIntrinsicHeight() * mPropertyImageScale);

        startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET - 6 * SECTOR_DEGREE))) + mPadding;
        startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET - 6 * SECTOR_DEGREE)));
        staminaDrawable.setBounds(startX,
                startY - staminaDrawableActualHeight / 2, startX + staminaDrawableActualWidth
                , startY + staminaDrawableActualHeight / 2);
        staminaDrawable.draw(canvas);

        int roadHoldingDrawableActualWidth = (int) (roadHoldingDrawable.getIntrinsicWidth() * mPropertyImageScale);
        int roadHoldingDrawableActualHeight = (int) (roadHoldingDrawable.getIntrinsicHeight() * mPropertyImageScale);

        startX = (int) (mCenterX + mRadius * (float) Math.sin(Math.toRadians(OFFSET - 7 * SECTOR_DEGREE))) + mPadding;
        startY = (int) (mCenterY - mRadius * (float) Math.cos(Math.toRadians(OFFSET - 7 * SECTOR_DEGREE)));
        roadHoldingDrawable.setBounds(startX,
                startY - roadHoldingDrawableActualHeight / 2, startX + roadHoldingDrawableActualWidth
                , startY + roadHoldingDrawableActualHeight / 2);
        roadHoldingDrawable.draw(canvas);

    }


    private Path getValuePath() {
        Path path = new Path();

        if (isEmpty(data)) {
            Log.i(TAG, "data is empty");
            return path;
        }

        int startX = (int) (mCenterX + data[0] / 100f * mRadius * (float) Math.sin(Math.toRadians(OFFSET)));
        int startY = (int) (mCenterY - data[0] / 100f * mRadius * (float) Math.cos(Math.toRadians(OFFSET)));
        path.moveTo(startX, startY);

        for (int i = 1; i <= 7; i++) {
            int x = (int) (mCenterX + data[i] / 100f * mRadius * (float) Math.sin(Math.toRadians(OFFSET - i * SECTOR_DEGREE)));
            int y = (int) (mCenterY - data[i] / 100f * mRadius * (float) Math.cos(Math.toRadians(OFFSET - i * SECTOR_DEGREE)));
            path.lineTo(x, y);
        }
        path.lineTo(startX, startY);

        return path;
    }

    public void setData(float[] array) {
        if (!isDataValid(array)) {
            Log.e(TAG, "data is invalid.");
            array = DEFAULT_DATA;
        }

        data = array;
        invalidate();
    }


    /**
     * Whether all of the array is biger than zero and the length of the array is PROPERTY_COUNT
     *
     * @param array
     * @return
     */
    private boolean isDataValid(float[] array) {

        if (array == null || array.length != PROPERTY_COUNT) {
            Log.e(TAG, "data is invalid.");
            return false;
        }

        for (float i : array) {
            if (i < 0) {
                Log.e(TAG, "data is invalid.");
                return false;
            }
        }

        return true;
    }

    /**
     * Whether all of the arrys is zero.
     *
     * @param array
     * @return
     */
    private boolean isEmpty(float[] array) {
        if (!isDataValid(array)) {
            return true;
        }

        for (float i : array) {
            if (i > 0) {
                return false;
            }
        }
        Log.d(TAG, "data is empty.");
        return true;
    }

}



