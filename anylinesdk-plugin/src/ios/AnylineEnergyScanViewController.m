
#import "AnylineEnergyScanViewController.h"
#import <Anyline/Anyline.h>

@interface AnylineEnergyScanViewController ()<AnylineEnergyModuleDelegate>

@end

@implementation AnylineEnergyScanViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    dispatch_async(dispatch_get_main_queue(), ^{
        AnylineEnergyModuleView *energyModuleView = [[AnylineEnergyModuleView alloc] initWithFrame:self.view.bounds];
        
        NSError *error = nil;
        [energyModuleView setupWithLicenseKey:self.key delegate:self error:&error];
//        if(!success) {
//            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Setup failed:" message:error.debugDescription delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
//            [alert show];
//        }
        
        energyModuleView.currentConfiguration = self.conf;
        energyModuleView.scanMode = self.scanMode;
        
        self.moduleView = energyModuleView;
        
        [self.view addSubview:self.moduleView];
        
        [self.view sendSubviewToBack:self.moduleView];
    });
}

#pragma mark - AnylineEnergyModuleDelegate method

- (void)anylineEnergyModuleView:(AnylineEnergyModuleView *)anylineEnergyModuleView
              didFindScanResult:(NSString *)scanResult
                      cropImage:(UIImage *)image
                      fullImage:(UIImage *)fullImage
                         inMode:(ALScanMode)scanMode {
    self.scannedLabel.text = scanResult;
    
    NSMutableDictionary *dictResult = [NSMutableDictionary dictionaryWithCapacity:4];
    
    switch (scanMode) {
        case ALGasMeter:
            [dictResult setObject:@"Gas Meter" forKey:@"meterType"];
            break;
        case ALWaterMeterBlackBackground:
        case ALWaterMeterWhiteBackground:
            [dictResult setObject:@"Water Meter" forKey:@"meterType"];
            break;
        case ALDigitalMeter:
            [dictResult setObject:@"Digital Meter" forKey:@"meterType"];
            break;
        case ALHeatMeter4:
        case ALHeatMeter5:
        case ALHeatMeter6:
            [dictResult setObject:@"Heat Meter" forKey:@"meterType"];
            break;
        case ALSerialNumber:
            [dictResult setObject:@"Serial Number" forKey:@"meterType"];
            break;
        default:
            [dictResult setObject:@"Electric Meter" forKey:@"meterType"];
            break;
    }
    
    [dictResult setObject:scanResult forKey:@"reading"];
    
    NSString *imagePath = [self saveImageToFileSystem:image];
    
    [dictResult setValue:imagePath forKey:@"imagePath"];
    
    NSString *fullImagePath = [self saveImageToFileSystem:fullImage];
    
    [dictResult setValue:fullImagePath forKey:@"fullImagePath"];
    
    [self.delegate anylineBaseScanViewController:self didScan:dictResult continueScanning:!self.moduleView.cancelOnResult];
    
    if (self.moduleView.cancelOnResult) {
        [self dismissViewControllerAnimated:YES completion:NULL];
    }
}

@end
