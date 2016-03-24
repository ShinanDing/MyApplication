package com.example.issuser.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.issuer.util.JsonUtil;
import com.example.issuer.util.MyHttpClient;
import com.example.issuer.util.Weatherifo;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import static android.R.color.holo_blue_bright;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    LinearLayout chartView, pic;
    private ViewPager viewPager;
    private ArrayList<View> viewList;
    View[] view;
    ChartView chartview;
    private LayoutInflater inflater;
    SharedPreferences spf1;
    SharedPreferences.Editor edit1;
    //    ProgressDialog pd;//进度条
    int a = 1;//a用来判断是否在刷新
    private SwipeRefreshLayout swipe;
    TextView cityTv;
    ImageView refresh, changecity;
    TextView weatherTv, dateTv, temperatureTv;//城市名，天气，日期，温度TextView
    TextView day3, day4, day5, day6;//未来5天的日期TextView
    TextView weather1, weather2, weather3, weather4, weather5, weather6;//未来5天的天气TextView
    ImageView image3, image4, image5, image6, image7, image8;
    String[] low, high;
    String s;//存放定位城市
    private GoogleApiClient client;
    public LocationClient mLocationClient = null;
    public BDLocationListener mylistener = new MyLocationListener();
    StringBuffer sb = new StringBuffer(256);
    PagerAdapter pagerAdapter;
    MyTask myTask;
    Weatherifo weatherifo;
    String url = "http://wthrcdn.etouch.cn/weather_mini?city=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全透明状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        refresh = (ImageView) findViewById(R.id.setting);
        changecity = (ImageView) findViewById(R.id.changecity);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe1);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeResources(new int[]{holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light});
        spf1 = getSharedPreferences("city", MODE_PRIVATE);//缓存手动加入的城市，天气，温度的文件
//        edit.clear().commit();
        edit1 = spf1.edit();//spf1的“写”对象
        edit1.clear().commit();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        /**
         * 百度定位的对象引用
         */
//        pd = new ProgressDialog(this);//进度条
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mylistener);
        initLocation();
        mLocationClient.start();
//        pd.setMessage("数据加载中，请稍候...");
//        pd.setCancelable(false);
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setIndeterminate(false);
//        pd.show();
    }

    /**
     * 百度定位
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            sb.delete(0,250);
            sb.append(location.getCity());
//            sb.append("\ntime : ");
//            sb.append(location.getTime());
//            sb.append("\nerrorcode : ");
//            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                sb.append("\ndescribe : ");
//                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
//            sb.append("\nlocationdescribe : ");
            mLocationClient.stop();// 停止定位
            s = sb.substring(0, 2);
            if (!s.equals("nu")) {
                edit1.putString("city0", s);
                edit1.commit();
            }
            if(a==1){
                viewPager = (ViewPager) findViewById(R.id.viewpager);
                viewPager.setOnPageChangeListener(MyViewPagerListener);
                viewList = new ArrayList<View>();
                myTask = new MyTask();
                myTask.execute();
            }else{
                refresh();
            }
        }
    }

    public class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            int count = spf1.getInt("i", 1);
            for (int j = 0; j < count; j++) {
                while (spf1.getString("city" + j, null) == null) {
                    j++;
                }
                try {
                    result = MyHttpClient.requestByHttpGet(url, spf1.getString("city" + j, null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                edit1.putString(spf1.getString("city" + j, "null"), result).commit();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            init1();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int count = spf1.getInt("i", 1);
            for (int j = 0; j < count; j++) {
                getdata(spf1.getString(spf1.getString("city" + j, "null"), null));
                chartview = new ChartView(view[j].getContext(), weatherifo);
                chartView.removeAllViews();
                chartView.addView(chartview);
            }
            pagerAdapter.notifyDataSetChanged();
        }
    }

    private void init1() {
        String result;
        inflater = LayoutInflater.from(MainActivity.this);
        //为控件注册
        if (sb.toString().substring(0, 4).equals("null")) {
            Toast.makeText(MainActivity.this, "定位失败，请检查网络是否通畅!", Toast.LENGTH_SHORT).show();
        }
        int count = spf1.getInt("i", 1);
        view = new View[count];
        for (int j = 0; j < count; j++) {
            view[j] = inflater.inflate(R.layout.main, null);
            initview(view[j]);
            result = spf1.getString(spf1.getString("city" + j, "dsn"), "yes");
            getdata(result);
            chartview = new ChartView(view[j].getContext());
            chartView.removeAllViews();
            chartView.addView(chartview);
            viewList.add(view[j]);
        }
        setViewPagerAdapter();
    }

    //ViewPager适配器
    public void setViewPagerAdapter() {
        if (pagerAdapter == null) {
            pagerAdapter = new PagerAdapter() {

                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {

                    return arg0 == arg1;
                }

                @Override
                public int getCount() {

                    return viewList.size();
                }

                @Override
                public void destroyItem(ViewGroup container, int position,
                                        Object object) {
                    container.removeView(viewList.get(position));

                }

                @Override
                public int getItemPosition(Object object) {

                    return super.getItemPosition(object);
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    container.removeView(viewList.get(position));
                    container.addView(viewList.get(position));
                    return viewList.get(position);
                }
            };
            viewPager.setAdapter(pagerAdapter);
        } else {
            pagerAdapter.notifyDataSetChanged();
            if (spf1.getInt("position", 0) != 0) {
                if (spf1.getBoolean("change", false)) {
                    viewPager.setCurrentItem(spf1.getInt("position", 0));
                    edit1.putBoolean("change", false);
                }
            }
        }
    }

    //ViewPager的监听方法
    private ViewPager.OnPageChangeListener MyViewPagerListener = new ViewPager.OnPageChangeListener() {
        // 当当前页面被滑动时调用
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        // 当新的页面被选中时调用
        @Override
        public void onPageSelected(int position) {
            edit1.putString("show",spf1.getString("city"+position,"广州"));
            edit1.commit();
        }

        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    /*
    *实现下拉刷新功能
    */
    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    a = 2;
                    mLocationClient.registerLocationListener(mylistener);
                    initLocation();
                    mLocationClient.start();
                    break;
            }
        }
    };


    public void refresh() {
        new Thread() {
            String result = null;
            public void run() {
                int count = spf1.getInt("i", 1);
                for (int j = 0; j < count; j++) {
                    while (spf1.getString("city" + j, null) == null) {
                        j++;
                    }
                    try {
                        result = MyHttpClient.requestByHttpGet(url, spf1.getString("city" + j, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edit1.putString(spf1.getString("city" + j, "null"), result).commit();
                }
                Message message = new Message();
                message.obj = result;
                handler.sendMessage(message);
            }
        }.start();
        if(isOpenNetwork()){
            Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "刷新失败，请检查网络连接...", Toast.LENGTH_SHORT).show();
        }
        swipe.setRefreshing(false);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int count = spf1.getInt("i", 1);
            for (int j = 0; j < count; j++) {
                getdata(spf1.getString(spf1.getString("city" + j, "null"), null));
                chartview = new ChartView(view[j].getContext(), weatherifo);
                chartView.removeAllViews();
                chartView.addView(chartview);
            }
            pagerAdapter.notifyDataSetChanged();
        }
    };


    //判断网络是否连接
    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    //显示数据
    public void getdata(String result) {
        weatherifo = new Weatherifo();
        JsonUtil.jsonsax(result);
        String ylow = JsonUtil.ylow.replace("℃", "");
        String yhigh = JsonUtil.yhigh.replace("℃", "");
        pic.setBackgroundResource(drawback(JsonUtil.type[0]));
        cityTv.setText(JsonUtil.city);
        weatherTv.setText(JsonUtil.type[0] + " " + JsonUtil.ylow + "~" + JsonUtil.yhigh);//显示当天天气
        dateTv.setText(JsonUtil.getDate[0]);//显示当天日期
        temperatureTv.setText(JsonUtil.wendu + "°C");//显示当天温度
        day3.setText("明天");
        day4.setText(JsonUtil.getDate[2]);
        day5.setText(JsonUtil.getDate[3]);
        day6.setText(JsonUtil.getDate[4]);
        weatherifo.setDay3(JsonUtil.getDate[2]);
        weatherifo.setDay4(JsonUtil.getDate[3]);
        weatherifo.setDay5(JsonUtil.getDate[4]);
        weather1.setText(JsonUtil.ytype);
        weather2.setText(JsonUtil.type[0]);
        weather3.setText(JsonUtil.type[1]);
        weather4.setText(JsonUtil.type[2]);
        weather5.setText(JsonUtil.type[3]);
        weather6.setText(JsonUtil.type[4]);
        image3.setBackgroundResource(draw(JsonUtil.ytype));
        image4.setBackgroundResource(draw(JsonUtil.type[0]));
        image5.setBackgroundResource(draw(JsonUtil.type[1]));
        image6.setBackgroundResource(draw(JsonUtil.type[2]));
        image7.setBackgroundResource(draw(JsonUtil.type[3]));
        image8.setBackgroundResource(draw(JsonUtil.type[4]));
        low = new String[5];
        low[0] = JsonUtil.getLow[0].replace("℃", "");
        low[1] = JsonUtil.getLow[1].replace("℃", "");
        low[2] = JsonUtil.getLow[2].replace("℃", "");
        low[3] = JsonUtil.getLow[3].replace("℃", "");
        low[4] = JsonUtil.getLow[4].replace("℃", "");
        high = new String[5];
        high[0] = JsonUtil.getHigh[0].replace("℃", "");
        high[1] = JsonUtil.getHigh[1].replace("℃", "");
        high[2] = JsonUtil.getHigh[2].replace("℃", "");
        high[3] = JsonUtil.getHigh[3].replace("℃", "");
        high[4] = JsonUtil.getHigh[4].replace("℃", "");
        weatherifo.setTem1(ylow);
        weatherifo.setTem2(low[0]);
        weatherifo.setTem3(low[1]);
        weatherifo.setTem4(low[2]);
        weatherifo.setTem5(low[3]);
        weatherifo.setTem6(low[4]);
        weatherifo.setHtem1(yhigh);
        weatherifo.setHtem2(high[0]);
        weatherifo.setHtem3(high[1]);
        weatherifo.setHtem4(high[2]);
        weatherifo.setHtem5(high[3]);
        weatherifo.setHtem6(high[4]);
        changecity.setOnClickListener(new View.OnClickListener() {
            @Override
            //城市的点击事件，点击后跳转到选择城市页面
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectCity.class);
                Bundle bundle = new Bundle();
                bundle.putString("city", cityTv.getText().toString());
                bundle.putString("temperature", temperatureTv.getText().toString());
                bundle.putString("weather", weatherTv.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowCity.class);
                startActivity(intent);
            }
        });
//        pd.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_view_pager_demo, menu);
        return true;
    }

    private void initview(View view) {
        pic = (LinearLayout) view.findViewById(R.id.pic);
        chartView = (LinearLayout) view.findViewById(R.id.chartView);
        cityTv = (TextView) view.findViewById(R.id.cityTv);
        weatherTv = (TextView) view.findViewById(R.id.weatherTv);
        dateTv = (TextView) view.findViewById(R.id.dateTv);
        temperatureTv = (TextView) view.findViewById(R.id.temperatureTv);
        day3 = (TextView) view.findViewById(R.id.day3);
        day4 = (TextView) view.findViewById(R.id.day4);
        day5 = (TextView) view.findViewById(R.id.day5);
        day6 = (TextView) view.findViewById(R.id.day6);
        weather1 = (TextView) view.findViewById(R.id.weather1);
        weather2 = (TextView) view.findViewById(R.id.weather2);
        weather3 = (TextView) view.findViewById(R.id.weather3);
        weather4 = (TextView) view.findViewById(R.id.weather4);
        weather5 = (TextView) view.findViewById(R.id.weather5);
        weather6 = (TextView) view.findViewById(R.id.weather6);
        image3 = (ImageView) view.findViewById(R.id.imageView3);
        image4 = (ImageView) view.findViewById(R.id.imageView4);
        image5 = (ImageView) view.findViewById(R.id.imageView5);
        image6 = (ImageView) view.findViewById(R.id.imageView6);
        image7 = (ImageView) view.findViewById(R.id.imageView7);
        image8 = (ImageView) view.findViewById(R.id.imageView8);
    }

    private int draw(String weather) {
        if (weather == null)
            return R.drawable.p001;
        if ("晴".equals(weather) || weather.equals("多云转晴"))
            return R.drawable.p001;
        if ("多云".equals(weather) || weather.equals("晴转多云"))
            return R.drawable.p002;
        if ("阴".equals(weather) || weather.equals("阴转多云"))
            return R.drawable.p004;
        if ("暴雨".equals(weather) || weather.equals("大雨到暴雨"))
            return R.drawable.p009;
        if ("雷阵雨".equals(weather))
            return R.drawable.p010;
        if ("雨夹雪".equals(weather))
            return R.drawable.p015;
        if ("小雨".equals(weather) || weather.equals("小到中雨"))
            return R.drawable.p007;
        if ("中雨".equals(weather) || "中到大雨".equals(weather))
            return R.drawable.p008;
        if ("大雨".equals(weather) || weather.equals("暴雨到大雨"))
            return R.drawable.p009;
        if ("阵雨".equals(weather))
            return R.drawable.p009;
        if ("小雪".equals(weather))
            return R.drawable.p011;
        if ("中雪".equals(weather))
            return R.drawable.p012;
        if ("大雪".equals(weather))
            return R.drawable.p014;
        if ("暴雪".equals(weather))
            return R.drawable.p013;
        if ("雾".equals(weather))
            return R.drawable.p023;
        if ("沙暴".equals(weather))
            return R.drawable.p024;
        if ("冰雹".equals(weather))
            return R.drawable.p017;
        if ("霾".equals(weather))
            return R.drawable.p006;
        return R.drawable.p004;
    }

    public int drawback(String weather) {
        if (weather == null) {
            return R.drawable.yin;
        }
        if ("晴".equals(weather) || weather.equals("多云转晴"))
            return R.drawable.qing;
        if ("多云".equals(weather) || weather.equals("晴转多云"))
            return R.drawable.duoyun;
        if ("阴".equals(weather) || weather.equals("阴转多云"))
            return R.drawable.yin;
        if ("暴雨".equals(weather) || weather.equals("大雨-暴雨"))
            return R.drawable.yu;
        if ("雷阵雨".equals(weather))
            return R.drawable.yu;
        if ("雨夹雪".equals(weather))
            return R.drawable.zhongyu;
        if ("小雨".equals(weather) || weather.equals("小雨-中雨"))
            return R.drawable.yu;
        if ("中雨".equals(weather) || "中雨-大雨".equals(weather))
            return R.drawable.yu;
        if ("大雨".equals(weather) || weather.equals("暴雨-大雨"))
            return R.drawable.yu;
        if ("阵雨".equals(weather))
            return R.drawable.yu;
        if ("小雪".equals(weather))
            return R.drawable.xue;
        if ("中雪".equals(weather))
            return R.drawable.xue;
        if ("大雪".equals(weather))
            return R.drawable.xue;
        if ("暴雪".equals(weather))
            return R.drawable.xue;
        if ("雾".equals(weather))
            return R.drawable.wumai;
        if ("沙暴".equals(weather))
            return R.drawable.shabao;
        if ("冰雹".equals(weather))
            return R.drawable.bingbao;
        if ("霾".equals(weather))
            return R.drawable.wumai;
        return R.drawable.yin;
    }

    protected void onRestart() {
        super.onRestart();
        if (!viewList.isEmpty()) {
            viewList.clear();
        }
        myTask = new MyTask();
        myTask.execute();
    }
}
