package com.example.administrator.bost;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Network;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn,add,update,delete,query,replace;

    private MyDataBaseHelper myDataBaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDataBaseHelper = new MyDataBaseHelper(this,"BookStore.db",null,1);
        btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDataBaseHelper.getWritableDatabase();
            }
        });

        //add
        add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                //开始组装第一条数据
                values.put("name","The dsn");
                values.put("author","dsn");
                values.put("pages",453);
                values.put("price",48.23);
                db.insert("Book", null, values);
            }
        });

        //query
        query = (Button)findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
                //查询表中的所有数据
                Cursor cursor = db.query("Book",null,null,null,null,null,null);
                if(cursor.moveToFirst()){
                    do{
                        //遍历Cursor对象，取出数据并打印
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int page = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));
                        Log.i("dsn","name:"+name+" author:"+author+" page:"+page+" price:"+price);
                    }while (cursor.moveToNext());
                }
                cursor.close();
            }
        });

        //update
        update = (Button)findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("price",19.32);
                db.update("Book",values,null,null);
            }
        });

        //delete
        delete = (Button)findViewById(R.id.Delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
                db.delete("Book","id>?",new String[]{"1"});
            }
        });

        //使用事物
        replace = (Button)findViewById(R.id.replace);
        replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
                db.beginTransaction();//开启事物
                try{
                    db.delete("Book",null,null);
//                    if(true){
//                        //手动抛出异常，让事务失败
//                        throw new NullPointerException();
//                    }
                    ContentValues values = new ContentValues();
                    values.put("name","sndinga");
                    values.put("author","sss");
                    values.put("price",32.98);
                    values.put("pages",687);
                    db.insert("Book",null,values);
                    db.setTransactionSuccessful();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                }
            }
        });
    }

}
