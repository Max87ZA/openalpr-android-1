package com.openalpr.jni;

import android.content.Context;
import android.util.Log;

import com.openalpr.jni.json.JSONException;
import com.openalpr.jni.utils.Utils;

import java.io.File;

public class Alpr {

    static {
        System.loadLibrary("pngt");
        System.loadLibrary("jpgt");
        System.loadLibrary("lept");
        System.loadLibrary("tess");
        System.loadLibrary("opencv_java3");
        System.loadLibrary("openalpr");
        System.loadLibrary("openalprjni");
        nativeClassInit();
    }

    private native void initialize(String country, String configFile, String runtimeDir);
    private native void dispose();

    private native boolean is_loaded();
    private native String native_recognize(String imageFile);
    @SuppressWarnings("JniMissingFunction")
    private native String native_recognize(byte[] imageBytes);
    private native String native_recognize(long imageData, int bytesPerPixel, int imgWidth, int imgHeight);

    private native void set_default_region(String region);
    private native void detect_region(boolean detectRegion);
    private native void set_top_n(int topN);
    private native String get_version();
    private static final String LCAT = "Alpr";

    public Alpr(Context context, String androidDataDir, String country, String configFile, String runtimeDir) {

        Utils.copyAssetFolder(context.getAssets(), "runtime_data", androidDataDir + File.separatorChar + "runtime_data");

        initialize(country, configFile, runtimeDir);
    }

    public void unload() {
        dispose();
    }

    public boolean isLoaded() {
        return is_loaded();
    }

    public AlprResults recognize(String imageFile) throws AlprException {
        try {
            String json = native_recognize(imageFile);
            Log.i(LCAT, "recognize result: "+json);
            return new AlprResults(json);
        } catch (JSONException e)
        {
            throw new AlprException("Unable to parse ALPR results");
        }
    }


    public AlprResults recognize(byte[] imageBytes) throws AlprException {
        try {
            String json = native_recognize(imageBytes);

            return new AlprResults(json);
        } catch (JSONException e)
        {
            throw new AlprException("Unable to parse ALPR results");
        }
    }


    public AlprResults recognize(long imageData, int bytesPerPixel, int imgWidth, int imgHeight) throws AlprException {
        try {
            String json = native_recognize(imageData, bytesPerPixel, imgWidth, imgHeight);
            return new AlprResults(json);
        } catch (JSONException e)
        {
            throw new AlprException("AlprException Unable to parse ALPR results");
        }
    }


    public void setTopN(int topN)
    {
        set_top_n(topN);
    }

    public void setDefaultRegion(String region)
    {
        set_default_region(region);
    }

    public void setDetectRegion(boolean detectRegion)
    {
        detect_region(detectRegion);
    }

    public String getVersion()
    {
        return get_version();
    }
}
