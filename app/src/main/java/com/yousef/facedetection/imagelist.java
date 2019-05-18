package com.yousef.facedetection;

import android.database.Cursor;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;

import android.widget.GridView;

import java.util.ArrayList;



public class imagelist extends AppCompatActivity {

    GridView gridView;
    ArrayList<image> list;
    imageListAdapter adapter = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new imageListAdapter(this, R.layout.image_item, list);
        gridView.setAdapter(adapter);

        // get all data from sqlite
        Cursor cursor = ProfileActivity.sqLiteHelper.getData("SELECT * FROM yousef");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);

            byte[] image = cursor.getBlob(3);
            String num = cursor.getString(4);
            list.add(new image(name, price, image,num, id));
        }
        adapter.notifyDataSetChanged();


    }









}