package com.janskia.largeimagedisplay;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lenovo on 2016-12-11.
 */

public class Options {
    public static final String SELECTED_SELECTED_IMAGE_PATH = "selectedImagePath";
    public static final String GYROSCOPE_ENABLED = "gyroscopeEnabled";
    public static final String SELECTED_SCALE = "selectedScale";

    private String path;
    private boolean gyroscopeEnabled;
    private int scale;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isGyroscopeEnabled() {
        return gyroscopeEnabled;
    }

    public void setGyroscopeEnabled(boolean gyroscopeEnabled) {
        this.gyroscopeEnabled = gyroscopeEnabled;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

}
