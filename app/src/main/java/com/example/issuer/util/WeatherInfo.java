package com.example.issuer.util;

/**
 * Created by Administrator on 2016/3/15.
 */
public class WeatherInfo {
    public static String shidu;//湿度
    public static String aqi = "";//空气质量指数
    public static String pm25 = "";//pm2.5
    public static String quality = "";//空气质量等级
    public static String pm10 = "";//pm10

    public static String getShidu() {
        return shidu;
    }

    public static void setShidu(String shidu) {
        WeatherInfo.shidu = shidu;
    }

    public static String getAqi() {
        return aqi;
    }

    public static void setAqi(String aqi) {
        WeatherInfo.aqi = aqi;
    }

    public static String getPm25() {
        return pm25;
    }

    public static void setPm25(String pm25) {
        WeatherInfo.pm25 = pm25;
    }

    public static String getQuality() {
        return quality;
    }

    public static void setQuality(String quality) {
        WeatherInfo.quality = quality;
    }

    public static String getPm10() {
        return pm10;
    }

    public static void setPm10(String pm10) {
        WeatherInfo.pm10 = pm10;
    }

}
