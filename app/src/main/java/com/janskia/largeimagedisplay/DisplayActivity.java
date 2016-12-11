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
    private Sensor senAccelerometer;

    private boolean isCalibrated=false;
    private float xCalibration;
    private float yCalibration;
    private float zCalibration;

    private float xVelocity=0;
    private float yVelocity=0;

    private float moveMultiplier = 6;
    private ImageView imageView;

    private Options options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);
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
            MoveImage(x,y);
        }
    }

    private void MoveImage(float x,float y){
        xVelocity-=x;
        yVelocity+=y;

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
}
