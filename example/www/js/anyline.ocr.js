/*
 * Anyline Cordova Plugin
 * anyline.ocr.js
 *
 * Copyright (c) 2016 9yards GmbH
 */

if (anyline === undefined) {
    var anyline = {};
}
anyline.ocr = {
    onResult: function(result) {
        //this is called for every mrz scan result
        //the result is a json-object containing all the scaned values and check-digits

        console.log("Result: " + JSON.stringify(result));
        var div = document.getElementById('results');

        div.innerHTML = "<p>" + "<img src=\"" + result.imagePath + "\" width=\"100%\" height=\"auto\"/><br/>" +
            "<b>Result: </b> " + result.text + "</p>" + div.innerHTML;

        document.getElementById("details_scan_modes").removeAttribute("open");
        document.getElementById("details_results").setAttribute("open", "");
    },

    onError: function(error) {
        //called if an error occurred or the user canceled the scanning
        if (error == "Canceled") {
            //do stuff when user has canceled
            // this can be used as an indicator that the user finished the scanning if canclelOnResult is false
            console.log("AnylineOcr scanning canceled");
            return;
        }

        alert(error);
    },

    licenseKey: "" +
        "eyJzY29wZSI6WyJCQVJDT0RFIiwiTVJaIiwiRU5FUkdZIiwiQU5ZTElORV9PQ1IiLCJET0NVTUVOVCIs" +
        "IkFMTCJdLCJwbGF0Zm9ybSI6WyJpT1MiLCJBbmRyb2lkIiwiV2luZG93cyJdLCJ2YWxpZCI6IjIwMTct" +
        "MDMtMTUiLCJtYWpvclZlcnNpb24iOiIzIiwiaXNDb21tZXJjaWFsIjpmYWxzZSwidG9sZXJhbmNlRGF5" +
        "cyI6NjAsImlvc0lkZW50aWZpZXIiOlsiaW8uYW55bGluZS5leGFtcGxlcy5jb3Jkb3ZhIl0sImFuZHJv" +
        "aWRJZGVudGlmaWVyIjpbImlvLmFueWxpbmUuZXhhbXBsZXMuY29yZG92YSJdLCJ3aW5kb3dzSWRlbnRp" +
        "ZmllciI6WyJpby5hbnlsaW5lLmV4YW1wbGVzLmNvcmRvdmEiXX0KVThWSHd6U295d3RGZ0oxL1pOY011" +
        "ZnBZT0F6eXRoVzA5ZUhIRDEzZ0RwSTUzUXhuMk52bExyWnllZEsvd2t1UUU0My9MUUMreUVSVlZHS2g1" +
        "N29IVlhuOVdqODE2cDQxYnh0cWx0ODk3WGkzcjhxVUdQekhaNzNnYWlsUEtPYmM5TlYyY0x1ZjVNK01m" +
        "RTQ2ZFJoeGlSUDZPcnk2dXB3U0laS1VEVDBDTjBMQWZxcXd2dW5IOFpIdk5HZE4vSmdlbFRkVlNSckNJ" +
        "bHVPaXliVC82N1ZRMVQ4QzVaWWs1VUdSdEFydW0yRmpGQWdiN1BPbnZ6bmhYeTl3NTVrcC9MUFpDM21E" +
        "OTVCdVNCUFZOSFZNdi9taHFlbUlTcGhTVjBpL0hWU3ZtYlp4VnY4ZVRJT29pM2YwNUJIREcrYWoyaDJJ" +
        "aUJBN1VaK0RZSVZrWjhpYXF2Wk5BPT0=",

    ibanViewConfig: {
        "captureResolution": "1080",
        "cutout": {
            "style": "rect",
            "maxWidthPercent": "80%",
            "maxHeightPercent": "80%",
            "alignment": "top_half",
            "width": 870,
            "ratioFromSize": {
                "width": 5,
                "height": 1
            },
            "strokeWidth": 2,
            "cornerRadius": 10,
            "strokeColor": "FFFFFF",
            "outerColor": "000000",
            "outerAlpha": 0.3
        },
        "flash": {
            "mode": "manual",
            "alignment": "bottom_right"
        },
        "beepOnResult": true,
        "vibrateOnResult": true,
        "blinkAnimationOnResult": true,
        "cancelOnResult": true
    },

    ibanOcrConfig: {
        "scanMode": "LINE",
        "minCharHeight": 30,
        "maxCharHeight": 60,
        "traineddataFiles": ["assets/eng_no_dict.traineddata"],
        "charWhitelist": "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890",
        "validationRegex": "^[A-Z]{2}([0-9A-Z]\\s*){13,32}$",
        "minConfidence": 65,
        "minSharpness": 66,
        "removeSmallContours": true,
        "charCountX": 4,
        "charCountY": 2,
        "charPaddingXFactor": 1.0,
        "charPaddingYFactor": 1.0,
        "isBrightTextOnDark": false,
        "drawTextOutline": true
    },

    anylineVoucherCodesViewConfig: {
        "captureResolution": "1080",
        "cutout": {
            "style": "rect",
            "maxWidthPercent": "80%",
            "maxHeightPercent": "80%",
            "alignment": "center",
            "width": 540,
            "ratioFromSize": {
                "width": 4,
                "height": 1
            },
            "strokeWidth": 2,
            "cornerRadius": 10,
            "strokeColor": "FFFFFF",
            "outerColor": "000000",
            "outerAlpha": 0.3
        },
        "flash": {
            "mode": "manual",
            "alignment": "bottom_right"
        },
        "beepOnResult": true,
        "vibrateOnResult": true,
        "blinkAnimationOnResult": true,
        "cancelOnResult": true
    },

    anylineVoucherCodesOcrConfig: {
        "scanMode": "GRID",
        "minCharHeight": 45,
        "maxCharHeight": 85,
        "traineddataFiles": ["assets/anyline_capitals.traineddata"],
        "charWhitelist": "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
        "validationRegex": "[A-Z0-9]{8}$",
        "minConfidence": 85,
        "removeSmallContours": true,
        "charCountX": 8,
        "charCountY": 1,
        "charPaddingXFactor": 0.5,
        "isBrightTextOnDark": false,
        "drawTextOutline": true
    },

    scanIban: function() {
        // start the Anyline OCR scanning
        // pass the success and error callbacks, as well as the license key and the config to the plugin
        // see http://documentation.anyline.io/#anyline-config for config details
        // and http://documentation.anyline.io/#anylineOcrModule for module details

        cordova.exec(this.onResult, this.onError, "AnylineSDK", "ANYLINE_OCR", [this.licenseKey, this.ibanViewConfig,
            this.ibanOcrConfig
        ]);
    },

    scanAnylineVoucherCodes: function() {
        // start the Anyline OCR scanning
        // pass the success and error callbacks, as well as the license key and the config to the plugin
        // see http://documentation.anyline.io/#anyline-config for config details
        // and http://documentation.anyline.io/#anylineOcrModule for module details

        cordova.exec(this.onResult, this.onError, "AnylineSDK", "ANYLINE_OCR", [this.licenseKey,
            this.anylineVoucherCodesViewConfig, this.anylineVoucherCodesOcrConfig
        ]);
    }
};
