package com.janskia.largeimagedisplay;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OptionsActivity extends Activity {
    private final String SHARED_PREF_NAME = "com.janskia.largeImageDisplay";

    public static final String TAG = "LargeImageDiplay";
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static int RESULT_LOAD_IMAGE = 1;

    private Options options;

    private Button buttonLoadImage;
    private ToggleButton buttonGyroscopeEnabled;
    private TextView editTextScale;
    private Button buttonGoToDisplay ;
    private ImageView imageView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        options = new Options();
        loadFromSharedPreferences();

        super.onCreate(savedInstanceState);
        isStoragePermissionGranted();

        setContentView(R.layout.activity_options);

        buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonGyroscopeEnabled = (ToggleButton) findViewById(R.id.toggleButtonGyroscopeEnabled);
        editTextScale = (TextView) findViewById(R.id.editTextScale);
        buttonGoToDisplay = (Button) findViewById(R.id.buttonGoToDisplay);
        imageView = (ImageView) findViewById(R.id.imageView);

        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Toast.makeText(getApplicationContext(),"started loading bitmap",Toast.LENGTH_SHORT);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        buttonGyroscopeEnabled.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                options.setGyroscopeEnabled(toggleButton.isChecked());
            }
        });

        editTextScale.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    options.setScale(Integer.parseInt(s.toString()));
                }catch (Exception e){
                    options.setScale(1);
                }
            }
        });

        final Activity optionsActivity = this;
        buttonGoToDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                saveToSharedPreferences();
                Intent i = new Intent(optionsActivity, DisplayActivity.class);
                i.putExtra(Options.SELECTED_SELECTED_IMAGE_PATH, options.getPath());
                i.putExtra(Options.GYROSCOPE_ENABLED, options.isGyroscopeEnabled());
                i.putExtra(Options.SELECTED_SCALE, options.getScale());
                startActivity(i);
            }
        });

        restoreUIValues();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            if(isStoragePermissionGranted()) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                options.setPath(cursor.getString(columnIndex));
                cursor.close();


                Bitmap bitmap = BitmapFactory.decodeFile(options.getPath());
                imageView.setImageBitmap(bitmap);
                imageView.setBackgroundColor(Color.rgb(255, 0, 0));
            }else{
                Log.v(TAG,"no permission!");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }


    public void saveToSharedPreferences(){
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Options.SELECTED_SELECTED_IMAGE_PATH,options.getPath());
        editor.putInt(Options.SELECTED_SCALE,options.getScale());
        editor.putBoolean(Options.GYROSCOPE_ENABLED,options.isGyroscopeEnabled());
        editor.commit();
    }

    public void loadFromSharedPreferences(){
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        options.setPath(prefs.getString(Options.SELECTED_SELECTED_IMAGE_PATH,""));
        options.setScale(prefs.getInt(Options.SELECTED_SCALE, 1));
        options.setGyroscopeEnabled(prefs.getBoolean(Options.GYROSCOPE_ENABLED,false));
    }

    private void restoreUIValues(){
        buttonGyroscopeEnabled.setChecked(options.isGyroscopeEnabled());
        editTextScale.setText(Integer.toString(options.getScale()));
        try {
            if(isStoragePermissionGranted()) {
                Bitmap bitmap = BitmapFactory.decodeFile(options.getPath());
                imageView.setImageBitmap(bitmap);
            }
        }
        catch(Exception e){
            Log.i("EXCEPTION",e.toString());
        }
    }
}