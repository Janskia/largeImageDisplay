package com.janskia.largeimagedisplay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DisplayActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer,senGyroscope;

    private boolean isCalibrated=false;
    private float xCalibration;
    private float yCalibration;
    private float zCalibration;

    private float xVelocity,yVelocity;

    private float moveMultiplier = 1f;
    private float rotationMultiplier = 1f;
    private ImageView imageView;

    private Options options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xVelocity=0;
        yVelocity=0;
        options = new Options();
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        options.setPath(intent.getStringExtra(Options.SELECTED_SELECTED_IMAGE_PATH));
        options.setGyroscopeEnabled(intent.getBooleanExtra(Options.GYROSCOPE_ENABLED,false));
        options.setScale(intent.getIntExtra(Options.SELECTED_SCALE, 1));
        Log.v(OptionsActivity.TAG, Integer.toString(options.getScale()));
        //update image
        imageView = (ImageView) findViewById(R.id.largeImageDisplay);
        Bitmap bitmap = BitmapFactory.decodeFile(options.getPath());
        imageView.setImageBitmap(bitmap);
        imageView.setScaleX(options.getScale());
        imageView.setScaleY(options.getScale());

        //setup accelerometer
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senGyroscope = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senGyroscope , SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            if(!isCalibrated){
                xCalibration = x;
                yCalibration = y;
                zCalibration = z;
                isCalibrated=true;
            }
            Log.d("ACCEREROMETERx","acceleration x:"+x);
            MoveImage(x,y);
        }
        if(options.isGyroscopeEnabled()){
            if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
                RotateImage(sensorEvent.values[2]);
            }
        }
    }

    private void RotateImage(float rotation){
        imageView.setRotation(imageView.getRotation()+rotation*rotationMultiplier);
        //imageView.setRotationY(imageView.getRotationY()+y*rotationMultiplier);
    }

    private void MoveImage(float x,float y){
        xVelocity-=x;
        yVelocity+=y;
        Log.d("VELOCITYx","velocity x:"+x);
        Log.d("VELOCITYy","velocity y:"+y);
        imageView.setTranslationX(imageView.getTranslationX()+xVelocity*moveMultiplier);
        imageView.setTranslationY(imageView.getTranslationY()+yVelocity*moveMultiplier);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch(action){
            case (MotionEvent.ACTION_DOWN) :
                resetImagePosition();
                Log.d("DEBUG_TAG","DOWN");
                break;
            case(MotionEvent.ACTION_BUTTON_PRESS):
                Log.d("DEBUG_TAG","PREDD");
                break;
            case(MotionEvent.ACTION_MOVE):
                Log.d("DEBUG_TAG","MOVE!");
                break;
        }
        return super.onTouchEvent(event);
    }

    private void resetImagePosition(){
        xVelocity=0;
        yVelocity=0;
        imageView.setTranslationX(0);
        imageView.setTranslationY(0);
    }
}
