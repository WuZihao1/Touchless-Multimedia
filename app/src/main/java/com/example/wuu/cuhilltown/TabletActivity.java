package com.example.wuu.cuhilltown;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.HashMap;
import java.util.*;

import static java.lang.System.exit;


public class TabletActivity extends AppCompatActivity
        implements SensorEventListener {

    private static final String Humble_Cottage = "http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/humble_cottage_cuhk.mp4";
    private static final String Green_Building_Awards = "http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/green_bldg_cuhk.mp4";
    private static final String Space_and_Earth = "http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/connecting_space_cuhk.mp4";
    private static final String infraRed_Spots = "http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/infrared_cuhk.mp4";
    private static final String Soaring_CUHK = "soaring_cuhk";

    private VideoView mVideoView;
    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";
    private TextView mBufferingTextView;
    private String file_path;
    private Stack<String> movies_stack;
    private Bundle instanceState;


    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    private Display mDisplay;


    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    private ImageView pause;
    private ImageView forward;
    private ImageView backward;
    boolean registered;


    private static final float VALUE_DRIFT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instanceState = savedInstanceState;
        movies_stack = new Stack<String>();
        file_path = "@";
        setContentView(R.layout.activity_tablet);
        mVideoView = findViewById(R.id.videoView);
        setThumbnail(savedInstanceState);

        pause = findViewById(R.id.pause);
        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.backward);


        mBufferingTextView = findViewById(R.id.buffering_textview);
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }
        MediaController controller = new MediaController(this);
        controller.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(controller);


        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();
    }

    private void setController(Bundle savedInstanceState){
        mBufferingTextView = findViewById(R.id.buffering_textview);


        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }
        MediaController controller = new MediaController(this);
        controller.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(controller);
    }


    public void setThumbnail(final Bundle saveInstanceState){
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
                file_path = Soaring_CUHK;
                setController(saveInstanceState);
                onStart();
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

                file_path = Humble_Cottage;
                setController(saveInstanceState);
                onStart();

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

                file_path = Green_Building_Awards;
                setController(saveInstanceState);
                onStart();
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

                file_path = Space_and_Earth;
                setController(saveInstanceState);
                onStart();
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
                file_path = infraRed_Spots;
                setController(saveInstanceState);
                onStart();
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

    @Override
    protected void onStart() {
        super.onStart();
        if(file_path == "@"){
            return;
        }
        movies_stack.push(file_path);
        initializePlayer();
        regis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
        mSensorManager.unregisterListener(this);
        registered = false;
    }

    private void initializePlayer() {
        Uri videoUri = getMedia(file_path);
        mVideoView.setVideoURI(videoUri);
        if (mCurrentPosition > 0) {
            mVideoView.seekTo(mCurrentPosition);
        } else {
            mVideoView.seekTo(1);
        }
        mVideoView.start();

        mVideoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) { // Implementation here.
                        mBufferingTextView.setVisibility(View.INVISIBLE);
                    }
                });

        mVideoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Toast.makeText(TabletActivity.this, "Playback completed", Toast.LENGTH_SHORT).show();
                        mVideoView.seekTo(1);
                    }
                });

    }

    @Override protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mVideoView.pause();
        }
    }


    protected void regis(){
        if(registered == true) {
            return;
        }
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        registered = true;
    }

    private void releasePlayer() {
        mVideoView.stopPlayback();
    }


    @Override
    public void onBackPressed(){
        if(movies_stack.empty()){
            exit(0);
        }else {
            String temp = movies_stack.pop();
            if(temp != file_path) {
                file_path = temp;
                setController(instanceState);
                onStart();
            }else{
                onBackPressed();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerData, mMagnetometerData);
        float[] rotationMatrixAdjusted = new float[9];

        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                rotationMatrixAdjusted = rotationMatrix.clone();
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                        rotationMatrixAdjusted);
                break;
        }

        float orientationValues[] = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrixAdjusted, orientationValues);
        }

        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }






        if(Math.abs(roll) < Math.PI/18 && Math.abs(pitch) <Math.PI/3){
            mVideoView.start();
            pause.setVisibility(View.INVISIBLE);
            forward.setVisibility(View.INVISIBLE);
            backward.setVisibility(View.INVISIBLE);
        }else if(Math.abs(pitch) >= Math.PI/3){
            mVideoView.pause();
            pause.setVisibility(View.VISIBLE);
            forward.setVisibility(View.INVISIBLE);
            backward.setVisibility(View.INVISIBLE);
        }else if (roll <= -1* Math.PI/18){
            int current_position = mVideoView.getCurrentPosition();
            if(current_position - 5*1000 <= 0){
                mVideoView.seekTo(1);
            }else {
                mVideoView.seekTo(current_position - 5 * 1000);
            }
            pause.setVisibility(View.INVISIBLE);
            forward.setVisibility(View.INVISIBLE);
            backward.setVisibility(View.VISIBLE);

        } else if(roll >= Math.PI/18){
            int current_position = mVideoView.getCurrentPosition();

            if(current_position + 5*1000 > mVideoView.getDuration()){
                mVideoView.seekTo(1);
            }else {
                mVideoView.seekTo(current_position + 5 * 1000);
            }

            pause.setVisibility(View.INVISIBLE);
            forward.setVisibility(View.VISIBLE);
            backward.setVisibility(View.INVISIBLE);

        }else {
            pause.setVisibility(View.INVISIBLE);
            forward.setVisibility(View.INVISIBLE);
            backward.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }





}
