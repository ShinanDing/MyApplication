package com.example.issuser.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.issuer.util.Just;
import com.example.issuer.util.MyHttpClient;
import com.example.issuer.util.ScreenShot;
import com.example.issuer.util.WeatherInfo;
import com.example.issuer.util.XmlPulllParseUtil;

import java.util.List;

import static android.R.color.holo_blue_bright;

public class ShowCity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    RoundProgressBar roundProgressBar;
    TextView showcity,shidu,ganmao,sport,ziwaixian;
    String url = "http://wthrcdn.etouch.cn/WeatherApi?city=";
    SharedPreferences spf ;
    SharedPreferences.Editor editor;
    String[] name,value;
    WeatherInfo weatherInfo;
    ImageView share;
    SwipeRefreshLayout showsrl;
    MyShowTask myTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //全透明状态栏
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.activity_show_city);
        init();
        myTask = new MyShowTask();
        myTask.execute();
    }
    private void init(){
        weatherInfo = new WeatherInfo();
        spf= getSharedPreferences("city", MODE_PRIVATE);
        editor = spf.edit();//spf1的“写”对象
        showsrl = (SwipeRefreshLayout)findViewById(R.id.showswipe);
        showsrl.setOnRefreshListener(this);
//        showsrl.post(new Runnable() {
//            @Override
//            public void run() {
//                showsrl.setRefreshing(true);
//            }
//        });
//        onRefresh();
        showsrl.setColorSchemeResources(new int[]{holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light});
        showcity = (TextView)findViewById(R.id.showcity);
        shidu = (TextView)findViewById(R.id.shidu);
        ganmao = (TextView)findViewById(R.id.gaomao);
        sport = (TextView)findViewById(R.id.sport);
        ziwaixian = (TextView)findViewById(R.id.ziwaixian);
        share = (ImageView)findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenShot.shoot(ShowCity.this);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, "/mnt/sdcard/apic" + ".png");
                shareIntent.setType("image/*");
                startActivity(shareIntent.createChooser(shareIntent, "请选择"));
            }
        });
    }



    private class MyShowTask extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setData();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isOpenNetwork()){
                showcity.setText(spf.getString("show", "广州"));
                shidu.setText(weatherInfo.getShidu());
                ganmao.setText(value[3]);
                sport.setText(value[8]);
                ziwaixian.setText(value[6]);
                roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
            } else{
                Toast.makeText(ShowCity.this,"网络连接失败...",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                if(isOpenNetwork()){
                    result = MyHttpClient.requestByHttpGet(url,spf.getString("show","广州"));
                    getXml(result);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }
    }

    private void setData(){
        showcity.setText(spf.getString("show","N/A"));
        shidu.setText("N/A");
        ganmao.setText("N/A");
        sport.setText("N/A");
        ziwaixian.setText("N/A");
        weatherInfo.setAqi("0");
        weatherInfo.setPm10("N/A");
        weatherInfo.setQuality("N/A");
        weatherInfo.setPm25("N/A");
        weatherInfo.setShidu("N/A");
        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
    }

    //判断网络是否连接
    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    private void getXml(String result){
        List<Just> lists = XmlPulllParseUtil.parseXMLWithPull(result);
        Log.i("dsn", XmlPulllParseUtil.shidu);
        name = new String[lists.size()];
        value = new String[lists.size()];
        for(int i =0;i<lists.size();i++){
            Just just = lists.get(i);
            name[i] = just.getName();
            value[i] = just.getValue();
            Log.i("dsn",name[i]+value[i]);
        }
    }

    //下拉刷新实现方法
    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    myTask = new MyShowTask();
                    myTask.execute();
                    Toast.makeText(ShowCity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                    showsrl.setRefreshing(false);
                    break;
            }
        }
    };
}
