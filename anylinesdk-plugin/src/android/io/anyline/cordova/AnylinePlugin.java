/*
 * Anyline Cordova Plugin
 * AnylinePlugin.java
 *
 * Copyright (c) 2015 9yards GmbH
 *
 * Created by martin at 2015-07-21
 */

package io.anyline.cordova;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.nineyards.anyline.modules.energy.EnergyScanView;

import io.anyline.cordova.BarcodeActivity;
import io.anyline.cordova.MrzActivity;
import io.anyline.cordova.DebitCardActivity;
import io.anyline.cordova.EnergyActivity;
import io.anyline.cordova.ResultReporter;
import io.anyline.cordova.Resources;


public class AnylinePlugin extends CordovaPlugin implements ResultReporter.OnResultListener {

    private static final String TAG = AnylinePlugin.class.getSimpleName();

    public static final String EXTRA_LICENSE_KEY = "EXTRA_LICENSE_KEY";
    public static final String EXTRA_CONFIG_JSON = "EXTRA_CONFIG_JSON";
    public static final String EXTRA_OCR_CONFIG_JSON = "EXTRA_OCR_CONFIG_JSON";
    public static final String EXTRA_SCAN_MODE = "EXTRA_SCAN_MODE";
    public static final String EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE";

    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 2;
    public static final int RESULT_ERROR_HANDLED = 3;

    public static final int REQUEST_BARCODE = 0;
    public static final int REQUEST_METER = 1;
    public static final int REQUEST_MRZ = 2;
    public static final int REQUEST_DEBIT_CARD = 3;
    public static final int REQUEST_ANYLINE_OCR = 4;

    private CallbackContext mCallbackContext;
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        mCallbackContext = callbackContext;
        PluginResult result = null;

        Log.d(TAG, "Starting action: " + action);

        switch (action) {
            case "scanBarcode":
            case "BARCODE":
                scan(BarcodeActivity.class, REQUEST_BARCODE, args);
                break;
            case "scanMRZ":
            case "MRZ":
                scan(MrzActivity.class, REQUEST_MRZ, args);
                break;
            case "DEBIT_CARD":
                scan(DebitCardActivity.class, REQUEST_DEBIT_CARD, args);
                break;
            case "scanElectricMeter":
            case "ELECTRIC_METER":
                scanEnergy(args, EnergyScanView.ScanMode.ELECTRIC_METER);
                break;
            case "ELECTRIC_METER_6_1":
                scanEnergy(args, EnergyScanView.ScanMode.ELECTRIC_METER_6_1);
                break;
            case "ELECTRIC_METER_5_1":
                scanEnergy(args, EnergyScanView.ScanMode.ELECTRIC_METER_5_1);
                break;
            case "scanGasMeter":
            case "GAS_METER":
                scanEnergy(args, EnergyScanView.ScanMode.GAS_METER);
                break;
            case "WATER_METER_WHITE":
                scanEnergy(args, EnergyScanView.ScanMode.WATER_METER_WHITE);
                break;
            case "WATER_METER_BLACK":
                scanEnergy(args, EnergyScanView.ScanMode.WATER_METER_BLACK);
                break;
            case "DIGITAL_METER":
                scanEnergy(args, EnergyScanView.ScanMode.DIGITAL_METER);
                break;
            case "HEAT_METER_4":
                scanEnergy(args, EnergyScanView.ScanMode.HEAT_METER_4);
                break;
            case "HEAT_METER_5":
                scanEnergy(args, EnergyScanView.ScanMode.HEAT_METER_5);
                break;
            case "HEAT_METER_6":
                scanEnergy(args, EnergyScanView.ScanMode.HEAT_METER_6);
                break;
            case "SERIAL_NUMBER":
                scanEnergy(args, EnergyScanView.ScanMode.SERIAL_NUMBER);
                break;
            case "ANYLINE_OCR":
                scan(AnylineOcrActivity.class, REQUEST_ANYLINE_OCR, args);
                break;
            default:
                result = new PluginResult(Status.INVALID_ACTION);
                callbackContext.error(Resources.getString(cordova.getActivity(),
                                            "error_unkown_scan_mode") + " " + action);
                return false;
        }

        result = new PluginResult(Status.NO_RESULT);
        result.setKeepCallback(true);

        return true;
    }

    private void scan(Class<?> activityToStart, int requestCode, JSONArray data) {
        scan(activityToStart, requestCode, data, null);
    }

    private void scanEnergy(JSONArray data, EnergyScanView.ScanMode modeEnum) {
        scan(EnergyActivity.class, REQUEST_METER, data, modeEnum.name());
    }

    private void scan(Class<?> activityToStart, int requestCode, JSONArray data, String mode) {
        Intent intent = new Intent(cordova.getActivity(), activityToStart);
        try {
            intent.putExtra(EXTRA_LICENSE_KEY, data.getString(0));
            if (data.length() > 1) {
                intent.putExtra(EXTRA_CONFIG_JSON, data.getString(1));
            }
            if (data.length() > 2) {
                intent.putExtra(EXTRA_OCR_CONFIG_JSON, data.getString(2));
            }
            if (mode != null) {
                intent.putExtra(EXTRA_SCAN_MODE, mode);
            }

        } catch (JSONException e) {
            PluginResult result = new PluginResult(Status.INVALID_ACTION);
            mCallbackContext.error(Resources.getString(cordova.getActivity(), "error_invalid_json_data"));
            return;
        }
        ResultReporter.setListener(this);
        cordova.startActivityForResult(this, intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //should always be called, but unclear if the reset of the handler always works
        Thread.setDefaultUncaughtExceptionHandler(mDefaultUncaughtExceptionHandler);

        ResultReporter.setListener(null);
        if (resultCode == RESULT_OK) {
            //nothing todo, handeled with ResultReporter
        } else if (resultCode == RESULT_CANCELED) {
            mCallbackContext.error("Canceled");

        } else if (resultCode == RESULT_ERROR) {
            mCallbackContext.error(data.getStringExtra(EXTRA_ERROR_MESSAGE));
        }
    }

    @Override
    public void onResult(Object result, boolean isFinalResult) {

        PluginResult pluginResult;
        if (result instanceof JSONObject) {
            pluginResult = new PluginResult(Status.OK, (JSONObject) result);
        } else if (result instanceof JSONArray) {
            pluginResult = new PluginResult(Status.OK, (JSONArray) result);
        } else {
            pluginResult = new PluginResult(Status.OK, result.toString());
        }
        if (!isFinalResult) {
            pluginResult.setKeepCallback(true);
        }

        mCallbackContext.sendPluginResult(pluginResult);
    }
}
