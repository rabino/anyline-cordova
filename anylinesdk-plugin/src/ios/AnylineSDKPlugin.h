
#import <Foundation/Foundation.h>
#import "Cordova/CDVPlugin.h"

@interface AnylineSDKPlugin : CDVPlugin

- (void)scanBarcode:(CDVInvokedUrlCommand *)command;

- (void)scanMRZ:(CDVInvokedUrlCommand *)command;

- (void)scanElectricMeter:(CDVInvokedUrlCommand *)command;

- (void)scanGasMeter:(CDVInvokedUrlCommand *)command;

- (void)BARCODE:(CDVInvokedUrlCommand *)command;

- (void)MRZ:(CDVInvokedUrlCommand *)command;

- (void)ELECTRIC_METER:(CDVInvokedUrlCommand *)command;

- (void)GAS_METER:(CDVInvokedUrlCommand *)command;

- (void)WATER_METER_WHITE:(CDVInvokedUrlCommand *)command;

- (void)WATER_METER_BLACK:(CDVInvokedUrlCommand *)command;

- (void)ELECTRIC_METER_5_1:(CDVInvokedUrlCommand *)command;

- (void)ELECTRIC_METER_6_1:(CDVInvokedUrlCommand *)command;

- (void)HEAT_METER_4:(CDVInvokedUrlCommand *)command;

- (void)HEAT_METER_5:(CDVInvokedUrlCommand *)command;

- (void)HEAT_METER_6:(CDVInvokedUrlCommand *)command;

- (void)DIGITAL_METER:(CDVInvokedUrlCommand *)command;

- (void)SERIAL_NUMBER:(CDVInvokedUrlCommand *)command;

- (void)ANYLINE_OCR:(CDVInvokedUrlCommand *)command;

@end
