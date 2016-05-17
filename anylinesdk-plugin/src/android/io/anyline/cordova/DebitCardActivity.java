/*
 * Anyline Cordova Plugin
 * DebitCardActivity.java
 *
 * Copyright (c) 2015 9yards GmbH
 *
 * Created by martin at 2016-01-25
 */
package io.anyline.cordova;

import java.io.IOException;
import java.io.File;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;

import at.nineyards.anyline.models.AnylineImage;
import at.nineyards.anyline.modules.debitcard.DebitCardScanView;
import at.nineyards.anyline.modules.debitcard.DebitCardResult;
import at.nineyards.anyline.modules.debitcard.DebitCardResultListener;
import at.nineyards.anyline.camera.CameraOpenListener;
import at.nineyards.anyline.camera.AnylineViewConfig;
import at.nineyards.anyline.models.AnylineImage;
import at.nineyards.anyline.util.TempFileUtil;

import io.anyline.cordova.Resources;
import io.anyline.cordova.AnylineBaseActivity;

public class DebitCardActivity extends AnylineBaseActivity {
    private static final String TAG = DebitCardActivity.class.getSimpleName();

    private DebitCardScanView scanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scanView = new DebitCardScanView(this, null);
        try{
            JSONObject json = new JSONObject(configJson);
            scanView.setConfig(new AnylineViewConfig(this, json));
        } catch(Exception e) {
            //JSONException or IllegalArgumentException is possible, return it to javascript
            finishWithError(Resources.getString(this, "error_invalid_json_data") + "\n" + e.getLocalizedMessage());
            return;
        }
        setContentView(scanView);

        initAnyline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanView.startScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();

        scanView.cancelScanning();
        scanView.releaseCameraInBackground();
    }

    private void initAnyline() {
        scanView.setCameraOpenListener(this);

        scanView.initAnyline(licenseKey, new DebitCardResultListener() {

            @Override
            public void onResult(DebitCardResult result, AnylineImage anylineImage) {

                JSONObject jsonResult = result.toJSONObject();

                try {
                    File imageFile = TempFileUtil.createTempFileCheckCache(DebitCardActivity.this,
                        UUID.randomUUID().toString(), ".jpg");
                    anylineImage.save(imageFile, 90);
                    jsonResult.put("imagePath", imageFile.getAbsolutePath());

                } catch (IOException e) {
                    Log.e(TAG, "Image file could not be saved.", e);

                } catch (JSONException jsonException) {
                    //should not be possible
                    Log.e(TAG, "Error while putting image path to json.", jsonException);
                }

                if (scanView.getConfig().isCancelOnResult()) {
                    ResultReporter.onResult(jsonResult, true);
                    setResult(AnylinePlugin.RESULT_OK);
                    finish();
                } else {
                    ResultReporter.onResult(jsonResult, false);
                }
            }
        });
        scanView.getAnylineController().setWorkerThreadUncaughtExceptionHandler(this);
    }

}
