package com.example.issuser.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.issuer.util.JsonUtil;
import com.example.issuer.util.MyHttpClient;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.R.color.holo_blue_bright;

public class SelectCity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    ImageView editor;
    ImageView back;
    GridView gridView;
    MyAdapter adapter;
    SharedPreferences spf;
    SharedPreferences.Editor edit;
    String[] weather, temperature, city, result,temperaturel;
    String[] weather1, temperature1, city1,temperaturel1;
    int i;
    private ArrayList<HashMap<Object, Object>> myList1;
    ArrayList<HashMap<Object, Object>> newList;
    private View deleteView;
    private boolean isShowDelete = false;//根据这个变量来判断是否显示删除图标，true是显示，false是不显示
    TextView weatherTv, temperatureTv, cityTv,temperaturelow;
    ImageView weatherimage;
    private SwipeRefreshLayout srl;
    private boolean isentry = false;
    String url = "http://wthrcdn.etouch.cn/weather_mini?city=";
    MyTask myTask;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        spf = getSharedPreferences("city", MODE_PRIVATE);
        edit = spf.edit();
        init();
        back = (ImageView) findViewById(R.id.imageView8);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editor = (ImageView) findViewById(R.id.editor);
        srl = (SwipeRefreshLayout) findViewById(R.id.swipe);
        srl.setOnRefreshListener(this);
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(true);
            }
        });
        onRefresh();
        srl.setColorSchemeResources(new int[]{holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light});
        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenNetwork()) {
                    init();
                } else {
                    Toast.makeText(SelectCity.this, "网络连接不可用...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.i("dsn", i + "   aaaaa");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(listener);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (isShowDelete) {
                    isShowDelete = false;
                    isentry = false;
                } else {
                    isShowDelete = true;
                    isentry = true;
                    adapter.setIsShowDelete();
                }
                adapter.setIsShowDelete();
                return true;
            }
        });
        i = spf.getInt("i", 1);
        weather1 = new String[i + 1];
        temperature1 = new String[i + 1];
        temperaturel1 = new String[i+1];
        city1 = new String[i + 1];
        result = new String[i];
        city = new String[i + 1];
        weather = new String[i + 1];
        temperature = new String[i + 1];
        temperaturel = new String[i + 1];
        myTask = new MyTask();
        myTask.execute();
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    init();
                    if(isOpenNetwork()){
                        Toast.makeText(SelectCity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                        srl.setRefreshing(false);
                    }else{
                        Toast.makeText(SelectCity.this,"刷新失败，网络异常...",Toast.LENGTH_SHORT).show();
                        srl.setRefreshing(false);
                    }
                    break;
            }
        }
    };

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    public class MyAdapter extends BaseAdapter {
        LayoutInflater lf;

        public MyAdapter(Context context) {
            lf = LayoutInflater.from(context);
        }

        public int getCount() {
            return myList1.size();
        }

        @Override
        public Object getItem(int position) {
            return myList1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setIsShowDelete() {
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (position < i) {
                convertView = lf.inflate(R.layout.layout, null);
                weatherTv = (TextView) convertView.findViewById(R.id.weather2);
                temperatureTv = (TextView) convertView.findViewById(R.id.temperatrue2);
                temperaturelow = (TextView) convertView.findViewById(R.id.temperatrue3);
                cityTv = (TextView) convertView.findViewById(R.id.cityname2);
                weatherimage = (ImageView) convertView.findViewById(R.id.weahterimage);
                Log.i("nds", myList1.get(position).get("weather").toString());
                weatherTv.setText(myList1.get(position).get("weather").toString());
                temperatureTv.setText(myList1.get(position).get("temperature").toString());
                temperaturelow.setText(myList1.get(position).get("temperaturel").toString());
                cityTv.setText(myList1.get(position).get("city").toString());
                weatherimage.setBackgroundResource(draw(myList1.get(position).get("weather").toString()));
                deleteView = convertView.findViewById(R.id.delete_markView);
                deleteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isShowDelete){
                            if(position!=0){
                                i--;
                                edit.putInt("i", i);
                                edit.remove("city" + position);
                                edit.remove("weather" + position);
                                edit.remove("temperature" + position);
                                edit.commit();
                                Log.i("nsd", spf.getString("city" + position, "已删除"));
                                delete(position);
                                isShowDelete = false;
                                isentry = false;
                                adapter = new MyAdapter(SelectCity.this);
                                gridView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
                deleteView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);//设置删除按钮是否显示
            } else {
                convertView = lf.inflate(R.layout.add, null);
            }
            return convertView;
        }
    }

    private GridView.OnItemClickListener listener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            if (position == parent.getCount() - 1) {
                if (isOpenNetwork()) {
                    int j = spf.getInt("i", 1);
                    Intent intent = new Intent(SelectCity.this, findCity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SelectCity.this, "网络异常...", Toast.LENGTH_SHORT).show();
                }
            } else {
//                if (isShowDelete) {
//                    if (position != 0) {
//                        i--;
//                        edit.putInt("i", i);
//                        edit.remove("city" + position);
//                        edit.remove("weather" + position);
//                        edit.remove("temperature" + position);
//                        edit.commit();
//                        Log.i("nsd", spf.getString("city" + position, "已删除"));
//                        delete(position);
//                        isShowDelete = false;
//                        isentry = false;
//                        adapter = new MyAdapter(SelectCity.this);
//                        gridView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//                    }
//                } else {
                    if (!isentry) {
                        edit.putBoolean("change", true);
                        edit.putInt("position", position);
                        edit.commit();
                        finish();
//                    }
                }
            }
        }
    };


    public void update() {
        for (int j = 0; j < i; j++) {
            try {
                result[j] = MyHttpClient.requestByHttpGet(url,spf.getString("city" + j, "广州"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonUtil.jsonsax(result[j]);
            city[j] = spf.getString("city" + j, "广州");
            weather[j] = JsonUtil.type[0];
            temperature[j] = JsonUtil.getHigh[0];
            temperaturel[j] = JsonUtil.getLow[0];
            edit.putString("weather" + j, weather[j]);
            edit.putString("temperature" + j, temperature[j]);
            edit.putString("temperaturel"+j,temperaturel[j]);
            edit.commit();
        }
    }

    public void getdata() {
        for (int j = 0; j < i; j++) {
            weather1[j] = spf.getString("weather" + j, "晴");
            temperature1[j] = spf.getString("temperature" + j, "17");
            temperaturel1[j] = spf.getString("temperaturel"+j,"15");
            city1[j] = spf.getString("city" + j, "广州");
            Log.i("dsn", weather1[j] + "zzzzz");
            Log.i("dsn", temperature[j] + "qqqqq");
        }
        myList1 = getMenuAdapter(weather1, temperature1, city1,temperaturel1);
        adapter = new MyAdapter(SelectCity.this);
        gridView.setAdapter(adapter);
    }

    private void delete(int position) {
        newList = new ArrayList<HashMap<Object, Object>>();
        if (isShowDelete) {
            for (int i = 0; i < myList1.size(); i++) {
                if (i == position) {

                } else {
                    newList.add(myList1.get(i));
                }
            }
        }
        myList1.clear();
        myList1.addAll(newList);
        Log.i("nsd", myList1.get(0).toString() + myList1.size());
    }

    private ArrayList<HashMap<Object, Object>> getMenuAdapter(String[] menuImageArray, String[] menuNameArray, String[] city1,String[] temlow) {
        ArrayList<HashMap<Object, Object>> data = new ArrayList<HashMap<Object, Object>>();
        for (int i = 0; i < menuImageArray.length; i++) {
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            map.put("weather", menuImageArray[i]);
            map.put("temperature", menuNameArray[i]);
            map.put("city", city1[i]);
            map.put("temperaturel",temlow[i]);
            data.add(map);
        }
        return data;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (myList1 != null) {
            myList1.clear();
        }
        init();
    }

    //判读网络是否可用
    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
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
        if ("暴雨".equals(weather) || weather.equals("大到暴雨"))
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
        return R.drawable.p002;
    }

    public class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            update();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getdata();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getdata();
            if (!isOpenNetwork()) {
                Toast.makeText(SelectCity.this, "网络异常...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}