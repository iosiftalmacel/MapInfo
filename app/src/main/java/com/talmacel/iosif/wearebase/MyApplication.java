package com.talmacel.iosif.wearebase;

import android.app.Application;
import android.util.Log;
import com.talmacel.iosif.wearebase.models.BaseData;
import com.talmacel.iosif.wearebase.utils.JsonLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iosif on 17/03/2018.
 */

public class MyApplication extends Application {
    public static String URL = "https://api.opendevicelab.com/";

    @Override
    public void onCreate() {
        super.onCreate();
        JsonLoader.CreateStaticInstance();
        JsonLoader.instance.downloadParsedJson(this, URL, BaseData[].class, true, null);
        Log.e("sfdasf", "entessssssss");
    }
}
