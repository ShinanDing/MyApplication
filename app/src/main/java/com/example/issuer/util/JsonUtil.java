package com.example.issuer.util;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ISSUSER on 2016/1/6.
 */
public class JsonUtil {

    public static String yhigh, ylow, ydate, ytype, city,wendu;
    public static String[] high, low, date, type;
    public static String[] getHigh, getLow, getDate;

    public static void jsonsax(String result) {
        try {
            if (result.equals("yes")) {
                ytype = "晴";
                ydate = "昨天";
                ylow = "12℃";
                yhigh = "21℃";
                city = "广州";
                type = new String[5];
                getHigh = new String[5];
                getLow = new String[5];
                getDate = new String[5];
                for (int i = 0; i < 5; i++) {
                    type[i] = "晴";
                    getHigh[i] = "22℃";
                    getLow[i] = "14℃";
                }
                getDate[0]="今天";
                getDate[1] = "明天";
                getDate[2] = "星期三";
                getDate[3] = "星期四";
                getDate[4] = "星期五";
            } else {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                city = jsonObject1.getString("city");
                wendu = jsonObject1.getString("wendu");
                Log.i("dsn", city + ":cityname");
                JSONObject jsonObject7 = jsonObject1.getJSONObject("yesterday");
                yhigh = jsonObject7.getString("high").replace("高温 ","");
                ylow = jsonObject7.getString("low").replace("低温 ","");
                ydate = jsonObject7.getString("date");
                ytype = jsonObject7.getString("type");
                JSONArray jsonArray = jsonObject1.getJSONArray("forecast");
                JSONObject jsonObject2 = jsonArray.getJSONObject(0);
                JSONObject jsonObject3 = jsonArray.getJSONObject(1);
                JSONObject jsonObject4 = jsonArray.getJSONObject(2);
                JSONObject jsonObject5 = jsonArray.getJSONObject(3);
                JSONObject jsonObject6 = jsonArray.getJSONObject(4);
                high = new String[5];
                high[0] = jsonObject2.getString("high");
                high[1] = jsonObject3.getString("high");
                high[2] = jsonObject4.getString("high");
                high[3] = jsonObject5.getString("high");
                high[4] = jsonObject6.getString("high");
                getHigh = new String[5];
                getHigh[0] = high[0].replace("高温 ", "");
                getHigh[1] = high[1].replace("高温 ", "");
                getHigh[2] = high[2].replace("高温 ", "");
                getHigh[3] = high[3].replace("高温 ", "");
                getHigh[4] = high[4].replace("高温 ", "");
                low = new String[5];
                low[0] = jsonObject2.getString("low");
                low[1] = jsonObject3.getString("low");
                low[2] = jsonObject4.getString("low");
                low[3] = jsonObject5.getString("low");
                low[4] = jsonObject6.getString("low");
                getLow = new String[5];
                getLow[0] = low[0].replace("低温 ", "");
                getLow[1] = low[1].replace("低温 ", "");
                getLow[2] = low[2].replace("低温 ", "");
                getLow[3] = low[3].replace("低温 ", "");
                getLow[4] = low[4].replace("低温 ", "");
                date = new String[5];
                date[0] = jsonObject2.getString("date");
                date[1] = jsonObject3.getString("date");
                date[2] = jsonObject4.getString("date");
                date[3] = jsonObject5.getString("date");
                date[4] = jsonObject6.getString("date");
                getDate = new String[5];
                getDate[0] = date[0].substring(date[0].length() - 3, date[0].length());
                getDate[1] = date[1].substring(date[1].length() - 3, date[1].length());
                getDate[2] = date[2].substring(date[2].length() - 3, date[2].length());
                getDate[3] = date[3].substring(date[3].length() - 3, date[3].length());
                getDate[4] = date[4].substring(date[4].length() - 3, date[4].length());
                type = new String[5];
                type[0] = jsonObject2.getString("type");
                type[1] = jsonObject3.getString("type");
                type[2] = jsonObject4.getString("type");
                type[3] = jsonObject5.getString("type");
                type[4] = jsonObject6.getString("type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
