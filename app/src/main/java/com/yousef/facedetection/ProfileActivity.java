package com.yousef.facedetection;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.yousef.facedetection.FaceCenterCircleView.FaceCenterCrop;
import com.yousef.facedetection.FaceCenterCircleView.FaceCenterCrop.FaceCenterCropListener;
import com.yousef.facedetection.Utils.Imageutils;
import com.yousef.facedetection.Utils.Imageutils.ImageAttachmentListener;
import com.yousef.facedetection.Utils.ProgressBarUtil.ProgressBarData;
import com.yousef.facedetection.Utils.ProgressBarUtil.ProgressUtils;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import static com.yousef.facedetection.Utils.Imageutils.GALEERY_REQUEST_CODE;
import static com.yousef.facedetection.Utils.Imageutils.SCANNER_REQUEST_CODE;

public class ProfileActivity extends DrawerActivity  {
    public static SQLiteHelper sqLiteHelper;
    String TAG = "ProfileActivity";
    ImageView imageView;
    static Imageutils imageutils;
    ImageAttachmentListener imageAttachmentListener;
    FaceCenterCrop faceCenterCrop;
    FaceCenterCropListener faceCenterCropListener;
    ProgressUtils progressUtils;
    @BindView(R.id.materialViewPager)
    MaterialViewPager mViewPager;
    NavigationView navigationView;
    Context context;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    static byte[] image ;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dl = (DrawerLayout)findViewById(R.id.drawer_layout);
        imageView = (ImageView) findViewById(R.id.imageee);
        t = new ActionBarDrawerToggle(this, dl,R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        dl.addDrawerListener(t);
        t.syncState();
        sqLiteHelper = new SQLiteHelper(this, "IM.db", null, 1);

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS yousef(Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, price VARCHAR, image BLOB ,num VARCHAR)");
        nv = (NavigationView)findViewById(R.id.na);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch(id)
                {
                    case R.id.search:
                        Intent intent1 = new Intent(ProfileActivity.this, searchlist.class);
                        startActivity(intent1); ;break;
                    case R.id.diplay:
                    Intent intent = new Intent(ProfileActivity.this, imagelist.class);
                    startActivity(intent);    break;
                    case R.id.navItemLogout:
                        finish();   break;
                    default:
                        return true;
                }


                return true;

            }
        });


        context = this;
        setTitle("");
        ButterKnife.bind(this);
        final Toolbar toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }


        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position % 1) {
                    default:
                        return RecyclerViewFragment.newInstance();
                }
            }

            @Override
            public int getCount() {
                return 1;
            }


        });
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorAndDrawable(ContextCompat.getColor(context, R.color.b), getDrawable(R.drawable.backgrond));
                }


                return null;
            }
        });
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());

        imageutils = new Imageutils(this);
        imageutils.setImageAttachment_callBack(getImageAttachmentCallback());

        progressUtils=new ProgressUtils(this);

        faceCenterCrop = new FaceCenterCrop(this, 100, 100, 1);
        final View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();

                    Toast.makeText(getApplicationContext(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
                }
            });
        }


        ///////////////////////////
    }

    private ImageAttachmentListener getImageAttachmentCallback() {
        if (imageAttachmentListener == null)
            imageAttachmentListener = (from, filename, file, uri) -> {
                Log.d(TAG, "getImageAttachmentCallback: " + from);

                if (from == SCANNER_REQUEST_CODE) {
                    if (isExternalStorageWritable()) {
                        saveImage(file);image = imageViewToByte(file) ;



                     Toast.makeText(ProfileActivity.this, "We detected a face", Toast.LENGTH_SHORT).show();          }

                    TestRecyclerViewAdapter.setImage(file);
                }
                else if(from == GALEERY_REQUEST_CODE)
                {
                    Log.d("Time log", "IA callback triggered");

                    ProgressBarData progressBarData= new ProgressBarData.ProgressBarBuilder()
                            .setCancelable(true)
                            .setProgressMessage("Processing")
                            .setProgressMessageColor(Color.parseColor("#4A4A4A"))
                            .setBackgroundViewColor(Color.parseColor("#FFFFFF"))
                            .setProgressbarTintColor(Color.parseColor("#FAC42A")).build();



                    progressUtils.showDialog(progressBarData);

                    faceCenterCrop.detectFace(file, getFaceCropResult());
                }
            };

        return imageAttachmentListener;
    }
    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();

        File myDir = new File(root + "/Studio");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "yousef_"+ timeStamp +".PNG";
        //////////////////////////
        Bitmap immagex=finalBitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        //////////////////////////
        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = Environment.getExternalStorageDirectory()+ "/Studio/yousef_"+ timeStamp +".PNG";

        Person john = new Person("John","12-20-1998","Male",""+path);
        ListView mListView = (ListView) findViewById(R.id.listView);
        ArrayList<Person> peopleList = new ArrayList<>();
        peopleList.add(john);
        PersonListAdapter adapter = new PersonListAdapter(this, R.layout.adapter_view_layout, peopleList);
        mListView.setAdapter(adapter);
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    private FaceCenterCropListener getFaceCropResult() {
        if (faceCenterCropListener == null)
            faceCenterCropListener = new FaceCenterCropListener() {
                @Override
                public void onTransform(Bitmap updatedBitmap) {
                    if (isExternalStorageWritable()) {

                        saveImage(updatedBitmap);

                        //We detected a face
                    }
                    image = imageViewToByte(updatedBitmap);

                        Log.d("Time log", "Output is set");

                    TestRecyclerViewAdapter.setImage(updatedBitmap);
                    /////////////////
                    Toast.makeText(ProfileActivity.this, "We detected a face", Toast.LENGTH_SHORT).show();
                    progressUtils.dismissDialog();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(ProfileActivity.this, "No face was detected", Toast.LENGTH_SHORT).show();
                    progressUtils.dismissDialog();

                }
            };

        return faceCenterCropListener;
    }
    public static byte[] imageViewToByte(Bitmap bitmap) {
       //itmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        try {
            super.onActivityResult(requestCode, resultCode, data);
            imageutils.onActivityResult(requestCode, resultCode, data);
            if (requestCode == SCANNER_REQUEST_CODE && resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + SCANNER_REQUEST_CODE);
            } else if (requestCode == GALEERY_REQUEST_CODE && resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + GALEERY_REQUEST_CODE);

            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imageutils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public static void imagePicker(){
        imageutils.imagepicker(1);
    }

}
