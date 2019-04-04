package com.example.wuu.cuhilltown;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Player extends AppCompatActivity
        implements SensorEventListener {
    private static String file_path;
    private VideoView mVideoView;
    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";
    private TextView mBufferingTextView;

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
        registered = false;
        Intent intent = getIntent();
        file_path = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        mVideoView = findViewById(R.id.VideoView );
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
                        Toast.makeText(Player.this, "Playback completed", Toast.LENGTH_SHORT).show();
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

    private void releasePlayer() {
        mVideoView.stopPlayback();
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
        }else if (roll <= -1 * Math.PI/18){
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
