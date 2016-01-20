package com.example.bezeka.testtaskbezeka.app;

import android.os.Environment;

/**
 * Created by Bezeka on 19.01.2016.
 */
public class AppConfig {
    public static final String FOLDER_FOR_IMAGES = "Test Task Images";
    public static final String IMAGE_PATH = Environment
            .getExternalStorageDirectory().toString()
            + "/"+"download"+".jpg";
}
