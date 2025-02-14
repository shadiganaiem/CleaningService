package com.cleaningservice.cleaningservice;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {

    public static String GetProperty(String key,Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("dbconfig.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }
}
