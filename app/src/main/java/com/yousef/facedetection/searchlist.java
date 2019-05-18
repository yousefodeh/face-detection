package com.yousef.facedetection;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;



public class searchlist extends AppCompatActivity {

    GridView gridView;
    ArrayList<image> list;
    imageListAdapter adapter = null;
    EditText text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selist_activity);



        text=findViewById(R.id.search11);
         Button b=findViewById(R.id.view_button111);
        gridView = (GridView) findViewById(R.id.gridView1);
        list = new ArrayList<>();
        imageListAdapter adapter = null;
        adapter = new imageListAdapter(this, R.layout.image_item, list);
        gridView.setAdapter(adapter);
        imageListAdapter finalAdapter = adapter;
        b.setOnClickListener(new View.OnClickListener() {
             @Override

             public void onClick(View v) {

                 String d= text.getText ().toString ();
                 // get all data from sqlite
                 Cursor cursor = ProfileActivity.sqLiteHelper.getData("SELECT * from yousef where name ='"+d+"'");
                 list.clear();
                 while (cursor.moveToNext()) {
                     int id = cursor.getInt(0);
                     String name = cursor.getString(1);
                     String price = cursor.getString(2);
                     byte[] image = cursor.getBlob(3);
                     String num = cursor.getString(4);
                     list.add(new image(name, price, image,num, id));
                 }
                 finalAdapter.notifyDataSetChanged();
             }
         });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(searchlist.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            String d= text.getText ().toString ();
                            // delete
                            Cursor c = ProfileActivity.sqLiteHelper.getData("SELECT id FROM yousef where name ='"+d+"'");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });



    }

    ImageView imageViewFood;

    private void showDialogDelete(final int idFood){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(searchlist.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ProfileActivity.sqLiteHelper.deleteData(idFood);
                    Toast.makeText(getApplicationContext(), "Delete successfully!!!",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("error", e.getMessage());
                }
              //  updateFoodList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewFood.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}