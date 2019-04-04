package com.example.wuu.cuhilltown;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.URL;
import java.util.HashMap;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST;
import static android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC;
import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;
import static android.media.ThumbnailUtils.extractThumbnail;

public class MainActivity extends AppCompatActivity {
    private static final String Humble_Cottage = "http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/humble_cottage_cuhk.mp4";
    private static final String Green_Building_Awards = "http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/green_bldg_cuhk.mp4";
    private static final String Space_and_Earth = "http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/connecting_space_cuhk.mp4";
    private static final String infraRed_Spots = "http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/infrared_cuhk.mp4";
    private static final String Soaring_CUHK = "soaring_cuhk";
    public static final String EXTRA_MESSAGE = "com.example.wuu.chuhilltown";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isPad(this.getApplicationContext())==false ) {
            setContentView(R.layout.activity_main);
            setThumbnail();
        } else{
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(this, TabletActivity.class);
            startActivity(intent);
        }


    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void setThumbnail(){
        ImageView iv1 = findViewById(R.id.soar_cuhk);
        Bitmap bm1 = createVideoThumbnail(this.getApplicationContext(), getMedia(Soaring_CUHK));
        if(bm1==null) {
            iv1.setBackgroundColor(Color.BLACK);
        }else {
            iv1.setImageBitmap(bm1);
        }
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Player.class);
                intent.putExtra(EXTRA_MESSAGE, Soaring_CUHK);
                startActivity(intent);
            }
        });

        ImageView iv2 = findViewById(R.id.humble_cottage);
        Bitmap bm2 = createVideoThumbnail(Humble_Cottage);
        if(bm2==null) {
            iv2.setBackgroundColor(Color.BLACK);
        }else {
            iv2.setImageBitmap(bm2);
        }
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Player.class);
                intent.putExtra(EXTRA_MESSAGE, Humble_Cottage);
                startActivity(intent);
            }
        });

        ImageView iv3 = findViewById(R.id.green_building_awards);
        Bitmap bm3 = createVideoThumbnail(Green_Building_Awards);
        if(bm3==null) {
            iv3.setBackgroundColor(Color.BLACK);
        }else {
            iv3.setImageBitmap(bm3);
        }
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Player.class);
                intent.putExtra(EXTRA_MESSAGE, Green_Building_Awards);
                startActivity(intent);
            }
        });


        ImageView iv4 = findViewById(R.id.space_and_earth);
        Bitmap bm4 = createVideoThumbnail(Space_and_Earth);
        if(bm4==null) {
            iv4.setBackgroundColor(Color.BLACK);
        }else {
            iv4.setImageBitmap(bm4);
        }
        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Player.class);
                intent.putExtra(EXTRA_MESSAGE, Space_and_Earth);
                startActivity(intent);
            }
        });

        ImageView iv5 = findViewById(R.id.infraRed_spots);
        Bitmap bm5 = createVideoThumbnail(infraRed_Spots);
        if(bm5==null) {
            iv5.setBackgroundColor(Color.BLACK);
        }else {
            iv5.setImageBitmap(bm5);
        }
       iv5.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(v.getContext(), Player.class);
               intent.putExtra(EXTRA_MESSAGE, infraRed_Spots);
               startActivity(intent);
           }
       });

    }






    public static Bitmap createVideoThumbnail(Context context, Uri filePath) {
        Bitmap bitmap = null;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(context, filePath);

        bitmap = retriever.getFrameAtTime(-1);
        retriever.release();

        return bitmap;

    }


    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(filePath, new HashMap<String, String>());

        bitmap = retriever.getFrameAtTime();
        retriever.release();

        return bitmap;

    }

    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            return Uri.parse(mediaName);
        } else {
            return Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName);
        }
    }








}
