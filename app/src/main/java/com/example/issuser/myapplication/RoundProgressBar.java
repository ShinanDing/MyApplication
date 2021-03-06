package com.example.issuser.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.issuer.util.Just;
import com.example.issuer.util.WeatherInfo;
import com.example.issuer.util.Weatherifo;

/**
 * Created by Administrator on 2016/3/10.
 */
public class RoundProgressBar extends View {
    private Paint paint;
    private int roundColor;//圆环颜色
    private int roundProgressColor;//圆环进度的颜色
    private int textColor;//中间进度百分比的字符串的颜色
    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;
    /**
     * 圆环的宽度
     */
    private float roundWidth;
    /**
     * 最大进度
     */
    private int max;
    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;
    /**
     * 进度的风格，实心或者空心
     */
    private int style;
    public static final int STROKE = 0;
    public static final int FILL = 1;
    WeatherInfo weatherInfo;
    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context);
        paint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 5);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 200);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
        mTypedArray.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        weatherInfo = new WeatherInfo();
        setProgress(Integer.parseInt(weatherInfo.getAqi()));
        setMax(200);
        //画外层的大圆环
        int centre = getWidth() / 2;//获取圆心X的坐标
        int radius = (int) (centre - roundWidth / 2) - 2;//圆环的半径
        paint.setStrokeWidth(2);
        paint.setColor(roundColor);
        canvas.drawLine(50, centre, centre - 200, centre, paint);
        canvas.drawLine(centre + 200, centre, 2 * centre - 50, centre, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(60);
        paint.setColor(Color.parseColor("#7e7e7e"));
        float textWidth2 = paint.measureText("PM2.5");
        canvas.drawText("PM2.5", (centre - textWidth2 / 2)-150, centre + 200, paint);
        canvas.drawText("PM10", (centre + textWidth2 / 2), centre + 200, paint);
        canvas.drawText(weatherInfo.getPm25(),(centre - textWidth2 / 2)-100,centre+300,paint);
        canvas.drawText(weatherInfo.getPm10(),(centre + textWidth2 / 2)+50,centre+300,paint);
        paint.setColor(Color.parseColor("#2f2f2f"));
        setTextSize(320);
        float textWidth1 = paint.measureText(String.valueOf(weatherInfo.getQuality()));
        canvas.drawText(weatherInfo.getQuality(), centre - (textWidth1 / 2), centre + 20, paint);
        paint.setColor(roundColor);//设置圆环颜色
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(roundWidth - 2);//设置圆环的宽度
        paint.setAntiAlias(true);//消除锯齿
        canvas.drawCircle(centre, centre, radius, paint);//画出圆环
        /*
        *画进度百分比
        */
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体
        int percent = (int) (((float) getProgress() / (float) getMax()) * 100);//中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText(String.valueOf(percent));//测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        if (textIsDisplayable && style == STROKE) {
            //canvas.drawText(String.valueOf(percent), centre - textWidth / 2, (centre + textSize / 2), paint);//画出进度百分比
//            canvas.drawText(String.valueOf(percent), centre - textWidth / 2, 380, paint);
            canvas.drawText(weatherInfo.getAqi(), centre - textWidth / 2, 380, paint);
        }

        /**
         * 画圆弧 ，画圆环的进度
         */
        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth);//设置圆环的宽度
        paint.setColor(roundProgressColor);//设置进度的颜色
        RectF oval = new RectF(centre - radius - 1, centre - radius - 1, centre
                + radius + 1, centre + radius + 1);  //用于定义的圆弧的形状和大小的界限
        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 90, 360 * getProgress() / getMax(), false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.STROKE);
                if (progress != 0) {
                    canvas.drawArc(oval, 90, 360 * getProgress() / getMax(), true, paint);  //根据进度画圆弧
                    break;
                }
            }
        }
    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }


    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

}
