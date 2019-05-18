package com.yousef.facedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.yousef.facedetection.ProfileActivity.sqLiteHelper;


public class TestRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    TextView tv;
    List<Object> contents;
    String[] textContent;
    private Context mContext;
    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    static final int TYPE_BUTTON = 2;
    static View imageViewHolder=null;
   static  String name;
    public TestRecyclerViewAdapter(List<Object> contents,Context context) {
        mContext = context;
        this.contents = contents;
        textContent = new String[contents.size()];
        for(int i=0;i<textContent.length;i++){
            textContent[i]="";
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            case 4:
                return TYPE_BUTTON;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_big, parent, false);
                imageViewHolder=view;
                return new RecyclerView.ViewHolder(view) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            case TYPE_BUTTON: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_button, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
        }
        return null;


    }
    public static void setImage(Bitmap bitmap){
        ImageView image = imageViewHolder.findViewById(R.id.image_view);
        image.setImageBitmap(bitmap);
    }
    public  void imagename(String N){
        this.name=N;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ImageView image = holder.itemView.findViewById(R.id.image_view);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(v.getContext(), "Image Clicked", Toast.LENGTH_SHORT).show()
                        ProfileActivity.imagePicker();

                    }
                });
                break;
            case TYPE_CELL:
                tv = holder.itemView.findViewById(R.id.card_view);
                tv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        textContent[position]=s.toString();

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                if(position==1)
                    tv.setHint("Enter the person's name");
                if(position==2)
                    tv.setHint("Enter a email");
                if (position==3)
                    tv.setHint("Enter a phone number");


                break;
            case TYPE_BUTTON:
                Button button = holder.itemView.findViewById(R.id.view_button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = "";
                        EditText text;

                        if(textContent[1] ==""||textContent[2] ==""||textContent[3] ==""){
                            Toast.makeText(v.getContext(), "Not insert", Toast.LENGTH_SHORT).show();
                        }
                        if(textContent[1] !=""&&textContent[2] !=""&&textContent[3] !="") {

                            sqLiteHelper.insertData("" + textContent[1], "" + textContent[2],
                                    ProfileActivity.image, "" + textContent[3]);
                            Toast.makeText(v.getContext(), "Insert", Toast.LENGTH_SHORT).show();
                            for(int i=0;i<textContent.length;i++){
                                textContent[i]="";
                            }
                        }
                    }
                });

        }
    }
}