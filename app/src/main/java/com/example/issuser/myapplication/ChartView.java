package com.example.issuser.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.example.issuer.util.Weatherifo;

/**
 * Created by Administrator on 2016/2/15.
 */
public class ChartView extends View {

    private Paint paint;
    private int[] data_total, data_high;
    private String[] date;

    public ChartView(Context context) {
        super(context);
        data_high = new int[]{9, 8, 10, 11, 12, 12};
        data_total = new int[]{5, 5, 3, 7, 5, 6};
        date = new String[]{"02/18", "02/19", "02/20"};
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public ChartView(Context context, Weatherifo weatherifo) {
        super(context);
        //高温
        data_high = new int[]{Integer.valueOf(weatherifo.getHtem1()), Integer.valueOf(weatherifo.getHtem2()), Integer.valueOf(weatherifo.getHtem3()), Integer.valueOf(weatherifo.getHtem4()), Integer.valueOf(weatherifo.getHtem5()), Integer.valueOf(weatherifo.getHtem6())};
        //低温
        data_total = new int[]{Integer.valueOf(weatherifo.getTem1()), Integer.valueOf(weatherifo.getTem2()), Integer.valueOf(weatherifo.getTem3()), Integer.valueOf(weatherifo.getTem4()), Integer.valueOf(weatherifo.getTem5()), Integer.valueOf(weatherifo.getTem6())};
        date = new String[]{weatherifo.getDay3(), weatherifo.getDay4(), weatherifo.getDay5()};
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    //绘制坐标轴
    public void drawAxis(Canvas canvas) {
        paint.setColor(Color.parseColor("#058381"));
        paint.setStrokeWidth(1);
        paint.setTextSize(40);
        canvas.drawLine(0, 0, 1600, 0, paint);
        canvas.drawLine(0,900,1600,900,paint);
    }

    //绘制图表
    public void drawChart(Canvas canvas) {
        //找出低温中的最小值
        int lmin = data_total[0];
        for(int i = 0;i<6;i++){
            if(lmin>data_total[i]){
                lmin = data_total[i];
            }
        }

        //低温折线图
        int[][] lowPoints = new int[6][2];
        lowPoints[0][0] = 110;
        lowPoints[0][1] = data_total[0];
        lowPoints[1][0] = 330;
        lowPoints[1][1] = data_total[1];
        lowPoints[2][0] = 580;
        lowPoints[2][1] = data_total[2];
        lowPoints[3][0] = 830;
        lowPoints[3][1] = data_total[3];
        lowPoints[4][0] = 1100;
        lowPoints[4][1] = data_total[4];
        lowPoints[5][0] = 1400;
        lowPoints[5][1] = data_total[5];
        //绘制Y轴的竖线
        paint.setColor(Color.parseColor("#058381"));
        paint.setStrokeWidth(1);
        for (int i = 0; i < 6; i++) {
            canvas.drawLine(lowPoints[i][0], 0, lowPoints[i][0], 900, paint);
        }
        float[] pts = new float[20];
        for (int i = 0; i < 20; i++) {
            pts[0] = lowPoints[0][0];
            pts[1] = 800 - (lowPoints[0][1]-lmin) * 40;
            pts[2] = lowPoints[1][0];
            pts[3] = 800 - (lowPoints[1][1]-lmin) * 40;
            pts[4] = lowPoints[1][0];
            pts[5] = 800 - (lowPoints[1][1]-lmin) * 40;
            pts[6] = lowPoints[2][0];
            pts[7] = 800 - (lowPoints[2][1]-lmin) * 40;
            pts[8] = lowPoints[2][0];
            pts[9] = 800 - (lowPoints[2][1]-lmin) * 40;
            pts[10] = lowPoints[3][0];
            pts[11] = 800 - (lowPoints[3][1]-lmin) * 40;
            pts[12] = lowPoints[3][0];
            pts[13] = 800 - (lowPoints[3][1]-lmin) * 40;
            pts[14] = lowPoints[4][0];
            pts[15] = 800 - (lowPoints[4][1]-lmin) * 40;
            pts[16] = lowPoints[4][0];
            pts[17] = 800 - (lowPoints[4][1]-lmin) * 40;
            pts[18] = lowPoints[5][0];
            pts[19] = 800 - (lowPoints[5][1]-lmin) * 40;
        }
        paint.setColor(Color.parseColor("#058381"));
        paint.setStrokeWidth(10);
        canvas.drawLines(pts, paint);

        //找出高温中的最大值
        int hmax  = data_high[0];
        for(int i = 0;i<6;i++){
            if(hmax<data_high[i]){
                hmax = data_high[i];
            }
        }
        //高温折线图
        int[][] highPoints = new int[6][2];
        highPoints[0][0] = 110;
        highPoints[0][1] = data_high[0];
        highPoints[1][0] = 330;
        highPoints[1][1] = data_high[1];
        highPoints[2][0] = 580;
        highPoints[2][1] = data_high[2];
        highPoints[3][0] = 830;
        highPoints[3][1] = data_high[3];
        highPoints[4][0] = 1100;
        highPoints[4][1] = data_high[4];
        highPoints[5][0] = 1400;
        highPoints[5][1] = data_high[5];
        float[] pth = new float[20];
        for (int i = 0; i < 20; i++) {
            pth[0] = highPoints[0][0];
            pth[1] = 100 + (hmax-highPoints[0][1]) * 40;
            pth[2] = highPoints[1][0];
            pth[3] = 100 + (hmax-highPoints[1][1]) * 40;
            pth[4] = highPoints[1][0];
            pth[5] = 100 + (hmax-highPoints[1][1]) * 40;
            pth[6] = highPoints[2][0];
            pth[7] = 100 + (hmax-highPoints[2][1]) * 40;
            pth[8] = highPoints[2][0];
            pth[9] = 100 + (hmax-highPoints[2][1]) * 40;
            pth[10] = highPoints[3][0];
            pth[11] = 100 + (hmax-highPoints[3][1]) * 40;
            pth[12] = highPoints[3][0];
            pth[13] = 100 + (hmax-highPoints[3][1]) * 40;
            pth[14] = highPoints[4][0];
            pth[15] = 100 + (hmax-highPoints[4][1]) * 40;
            pth[16] = highPoints[4][0];
            pth[17] = 100 + (hmax-highPoints[4][1]) * 40;
            pth[18] = highPoints[5][0];
            pth[19] = 100 + (hmax-highPoints[5][1]) * 40;
        }
        paint.setColor(Color.parseColor("#FF6633"));
        paint.setStrokeWidth(10);
        canvas.drawLines(pth, paint);
        for (int i = 0; i < 6; i++) {
            //高温点坐标绘制
            paint.setColor(Color.parseColor("#FF6633"));
            canvas.drawCircle(highPoints[i][0] - 1, 100 + (hmax-highPoints[i][1]) * 40, 20, paint);
            //低温点坐标绘制
            paint.setColor(Color.parseColor("#058381"));
            canvas.drawCircle(lowPoints[i][0] - 1, 800-(lowPoints[i][1]-lmin) * 40, 20, paint);
            //高温温度数值显示
            paint.setColor(Color.parseColor("#FF6633"));
            paint.setTextSize(60);
            canvas.drawText(data_high[i] + "℃", highPoints[i][0] - 20,75 + (hmax-highPoints[i][1]) * 40 ,
                    paint);
            //低温温度数值显示
            paint.setColor(Color.parseColor("#058381"));
            paint.setTextSize(60);
            canvas.drawText(data_total[i] + "℃", lowPoints[i][0] - 20, 775-(lowPoints[i][1]-lmin) * 40,
                    paint);
        }
    }

    //view在创建的时候执行这个函数
    @Override

    public void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.parseColor("#88a995"));
        drawAxis(canvas);
        drawChart(canvas);
    }
}
