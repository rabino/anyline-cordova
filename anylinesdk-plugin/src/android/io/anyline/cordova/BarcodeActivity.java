/*
 * Anyline Cordova Plugin
 * BarcodeActivity.java
 *
 * Copyright (c) 2015 9yards GmbH
 *
 * Created by martin at 2015-07-21
 */
package io.anyline.cordova;

import java.io.IOException;
import java.io.File;
import java.util.UUID;

import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.WindowManager;

import at.nineyards.anyline.modules.barcode.BarcodeScanView;
import at.nineyards.anyline.modules.barcode.BarcodeResultListener;
import at.nineyards.anyline.camera.CameraOpenListener;
import at.nineyards.anyline.camera.AnylineViewConfig;
import at.nineyards.anyline.models.AnylineImage;
import at.nineyards.anyline.util.TempFileUtil;

import io.anyline.cordova.Resources;
import io.anyline.cordova.AnylineBaseActivity;

public class BarcodeActivity extends AnylineBaseActivity {
    private static final String TAG = BarcodeActivity.class.getSimpleName();

    private BarcodeScanView barcodeScanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barcodeScanView = new BarcodeScanView(this, null);
        try{
            JSONObject json = new JSONObject(configJson);
            barcodeScanView.setConfig(new AnylineViewConfig(this, json));
        } catch(Exception e) {
            //JSONException or IllegalArgumentException is possible, return it to javascript
            finishWithError(Resources.getString(this, "error_invalid_json_data") + "\n" + e.getLocalizedMessage());
            return;
        }
        setContentView(barcodeScanView);

        initAnyline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScanView.startScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScanView.cancelScanning();
        barcodeScanView.releaseCameraInBackground();
    }

    private void initAnyline() {
        barcodeScanView.setCameraOpenListener(this);

        barcodeScanView.initAnyline(licenseKey, new BarcodeResultListener() {
            @Override
            public void onResult(String result, BarcodeScanView.BarcodeFormat format, AnylineImage resultImage) {

                JSONObject jsonResult = new JSONObject();
                try {

                    jsonResult.put("value", result);
                    jsonResult.put("format", format.toString());

                    File imageFile = TempFileUtil.createTempFileCheckCache(BarcodeActivity.this,
                        UUID.randomUUID().toString(), ".jpg");

                    resultImage.save(imageFile, 90);
                    jsonResult.put("imagePath", imageFile.getAbsolutePath());

                } catch (IOException e) {
                    Log.e(TAG, "Image file could not be saved.", e);

                } catch (JSONException jsonException) {
                    //should not be possible
                    Log.e(TAG, "Error while putting image path to json.", jsonException);
                }

                if (barcodeScanView.getConfig().isCancelOnResult()) {
                    ResultReporter.onResult(jsonResult, true);
                    setResult(AnylinePlugin.RESULT_OK);
                    finish();
                } else {
                    ResultReporter.onResult(jsonResult, false);
                }
            }
        });
        barcodeScanView.getAnylineController().setWorkerThreadUncaughtExceptionHandler(this);
    }

}
