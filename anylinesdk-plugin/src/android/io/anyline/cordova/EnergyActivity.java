/*
 * Anyline Cordova Plugin
 * EnergyActivity.java
 *
 * Copyright (c) 2015 9yards GmbH
 *
 * Created by martin at 2015-07-21
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
import at.nineyards.anyline.modules.energy.EnergyResultListener;
import at.nineyards.anyline.modules.energy.EnergyScanView;
import at.nineyards.anyline.camera.CameraOpenListener;
import at.nineyards.anyline.camera.AnylineViewConfig;
import at.nineyards.anyline.models.AnylineImage;
import at.nineyards.anyline.util.TempFileUtil;

import io.anyline.cordova.Resources;
import io.anyline.cordova.AnylineBaseActivity;

public class EnergyActivity extends AnylineBaseActivity  {
    private static final String TAG = EnergyActivity.class.getSimpleName();

    public static final int SCAN_MODE_ELECTIRC = 0;
    public static final int SCAN_MODE_GAS = 1;

    private EnergyScanView energyScanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String scanModeString = getIntent().getStringExtra(AnylinePlugin.EXTRA_SCAN_MODE);

        energyScanView = new EnergyScanView(this, null);

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(configJson);
        } catch(Exception e) {
            //JSONException or IllegalArgumentException is possible, return it to javascript
            finishWithError(Resources.getString(this, "error_invalid_json_data") + "\n" + e.getLocalizedMessage());
            return;
        }

        energyScanView.setConfig(new AnylineViewConfig(this, jsonObject));
        if (jsonObject.has("reportingEnabled")) {
            energyScanView.setReportingEnabled(jsonObject.optBoolean("reportingEnabled", true));
        }
        energyScanView.setScanMode(EnergyScanView.ScanMode.valueOf(scanModeString));

        setContentView(energyScanView);

        initAnyline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        energyScanView.startScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        energyScanView.cancelScanning();
        energyScanView.releaseCameraInBackground();
    }

    private void initAnyline() {
        energyScanView.setCameraOpenListener(this);

        energyScanView.initAnyline(licenseKey, new EnergyResultListener() {

            @Override
            public void onResult(EnergyScanView.ScanMode scanMode, String result,
                                 AnylineImage resultImage, AnylineImage fullImage) {


                JSONObject jsonResult = new JSONObject();

                try {
                    switch (scanMode) {
                        case GAS_METER:
                            jsonResult.put("meterType", "Gas Meter");
                            break;
                        case WATER_METER_WHITE:
                        case WATER_METER_BLACK:
                            jsonResult.put("meterType", "Water Meter");
                            break;
                        case DIGITAL_METER:
                            jsonResult.put("meterType", "Digital Meter");
                            break;
                        case HEAT_METER_4:
                        case HEAT_METER_5:
                        case HEAT_METER_6:
                            jsonResult.put("meterType", "Heat Meter");
                            break;
                        case SERIAL_NUMBER:
                            jsonResult.put("meterType", "Serial Number");
                            break;
                        default:
                            jsonResult.put("meterType", "Electric Meter");
                            break;
                    }

                    jsonResult.put("reading", result);

                    File imageFile = TempFileUtil.createTempFileCheckCache(EnergyActivity.this,
                        UUID.randomUUID().toString(), ".jpg");

                    resultImage.save(imageFile, 90);
                    jsonResult.put("imagePath", imageFile.getAbsolutePath());

                    if (fullImage != null) {
                        imageFile = TempFileUtil.createTempFileCheckCache(EnergyActivity.this,
                            UUID.randomUUID().toString(), ".jpg");
                        fullImage.save(imageFile, 90);
                        jsonResult.put("fullImagePath", imageFile.getAbsolutePath());
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Image file could not be saved.", e);

                } catch (JSONException jsonException) {
                    //should not be possible
                    Log.e(TAG, "Error while putting image path to json.", jsonException);
                }


                if (energyScanView.getConfig().isCancelOnResult()) {
                    ResultReporter.onResult(jsonResult, true);
                    setResult(AnylinePlugin.RESULT_OK);
                    finish();
                } else {
                    ResultReporter.onResult(jsonResult, false);
                }
            }
        });
        energyScanView.getAnylineController().setWorkerThreadUncaughtExceptionHandler(this);
    }

}
