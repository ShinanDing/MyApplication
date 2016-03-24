package com.example.issuer.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/9.
 */
public class XmlPulllParseUtil {

    public static String shidu;//湿度
    public static String aqi = "";//空气质量指数
    public static String pm25 = "";//pm2.5
    public static String quality = "";//空气质量等级
    public static String pm10 = "";//pm10

//    public static String comfortable = "";//舒适度
//    public static String ganmao = "";//感冒指数
//    public static String sport = "";//运动指数
//    public static String ziwaixian = "";//紫外线强度
    public static List<Just> parseXMLWithPull(String xmlData) {
        List<Just> list = null;
        Just just = null;
        WeatherInfo weath = new WeatherInfo();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String name = "";
            String value = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                        list = new ArrayList<Just>();
                        break;
                    }
                    //开始解析某个节点
                    case XmlPullParser.START_TAG: {
                        if ("shidu".equals(nodeName)) {
                            shidu = xmlPullParser.nextText();
                            weath.setShidu(shidu);
                        } else if ("aqi".equals(nodeName)) {
                            aqi = xmlPullParser.nextText();
                            weath.setAqi(aqi);
                        } else if ("pm25".equals(nodeName)) {
                            pm25 = xmlPullParser.nextText();
                            weath.setPm25(pm25);
                        } else if ("quality".equals(nodeName)) {
                            quality = xmlPullParser.nextText();
                            weath.setQuality(quality);
                        } else if ("pm10".equals(nodeName)) {
                            pm10 = xmlPullParser.nextText();
                            weath.setPm10(pm10);
                        } else if ("name".equals(nodeName)) {
                            just = new Just();
                            name = xmlPullParser.nextText();
                            just.setName(name);
                            Log.i("dsn", "name:" + name);
                        } else if ("value".equals(nodeName)) {
                            value = xmlPullParser.nextText();
                            Log.i("dsn", "value:" + value);
                            just.setValue(value);
                        }
                        break;
                    }
                    //完成某个节点的解析
                    case XmlPullParser.END_TAG: {
                        if ("zhishu".equals(nodeName)) {
                            list.add(just);
                            just = null;
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
