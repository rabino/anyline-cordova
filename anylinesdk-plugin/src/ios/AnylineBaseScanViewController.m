
#import "AnylineBaseScanViewController.h"
#import <Anyline/Anyline.h>

@interface AnylineBaseScanViewController ()

@property (nonatomic,strong) UIButton *doneButton;

@end

@implementation AnylineBaseScanViewController

-(instancetype)initWithKey:(NSString*)key configuration:(ALUIConfiguration *)conf cordovaConfiguration:(ALCordovaUIConfiguration*)cordovaConf delegate:(id<AnylineBaseScanViewControllerDelegate>)delegate {
    self = [super init];
    if(self) {
        _key = key;
        _delegate = delegate;
        _conf = conf;
        _cordovaConfig = cordovaConf;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    dispatch_async(dispatch_get_main_queue(), ^{
        
        self.doneButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [self.doneButton setTitle:self.cordovaConfig.buttonDoneTitle
                         forState:UIControlStateNormal];

        [self.doneButton addTarget:self action:@selector(doneButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:self.doneButton];
        
        [self updateButtonPosition:self.doneButton withConfiguration:self.cordovaConfig];
        
        self.scannedLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 44)];
        self.scannedLabel.center = CGPointMake(self.view.center.x, self.view.center.y+166);
        
        self.scannedLabel.alpha = 0.0;
        self.scannedLabel.font = [UIFont fontWithName:@"HelveticaNeue" size:33];
        self.scannedLabel.textColor = [UIColor whiteColor];
        self.scannedLabel.textAlignment = NSTextAlignmentCenter;
        
        [self.view addSubview:self.scannedLabel];
        
    });
    
}

-(void) updateButtonPosition:(UIButton*)button withConfiguration:(ALCordovaUIConfiguration*)conf {

    CGPoint center = CGPointMake(0, 0);
    button.titleLabel.font = [UIFont fontWithName:conf.buttonDoneFontName size:conf.buttonDoneFontSize];
    [button setTitleColor:conf.buttonDoneTextColor forState:UIControlStateNormal];
    [button setTitleColor:conf.buttonDoneTextColorHighlighted forState:UIControlStateHighlighted];
    
    button.backgroundColor = conf.buttonDoneBackgroundColor;
    button.translatesAutoresizingMaskIntoConstraints = NO;
    button.layer.cornerRadius = conf.buttonDoneCornerRadius;
    
    switch (conf.buttonType) {
        case ALButtonTypeFullWidth:
            // Width constraint
            [self.view addConstraint:[NSLayoutConstraint constraintWithItem:button
                                                             attribute:NSLayoutAttributeWidth
                                                             relatedBy:NSLayoutRelationEqual
                                                                toItem:self.view
                                                             attribute:NSLayoutAttributeWidth
                                                            multiplier:1.0
                                                              constant:0]];
            break;
        
        case ALButtonTypeRect:
            [button sizeToFit];
            break;
        
        default:
            break;
    }
    
    switch (conf.buttonDoneXAlignment) {
        case ALButtonXAlignmentCenter:
            [self.view addConstraint:[NSLayoutConstraint constraintWithItem:button
                                                             attribute:NSLayoutAttributeCenterX
                                                             relatedBy:NSLayoutRelationEqual
                                                                toItem:self.view
                                                             attribute:NSLayoutAttributeCenterX
                                                            multiplier:1.0
                                                              constant:0.0]];
            break;
        case ALButtonXAlignmentLeft:
            center.x=button.frame.size.width/2+button.frame.size.height/2;
            break;
        case ALButtonXAlignmentRight:
            center.x=self.view.frame.size.width-button.frame.size.width/2-button.frame.size.height/2;
            break;
            
        default:
            break;
    }
    
    switch (conf.buttonDoneYAlignment) {
        case ALButtonYAlignmentTop:
            // Align Top
            [self.view addConstraint:[NSLayoutConstraint constraintWithItem:button
                                                                  attribute:NSLayoutAttributeTop
                                                                  relatedBy:NSLayoutRelationEqual
                                                                     toItem:self.view
                                                                  attribute:NSLayoutAttributeTop
                                                                 multiplier:1.0
                                                                   constant:MAX(conf.buttonDoneYPositionOffset,0)]];
            break;
        case ALButtonYAlignmentBottom:
            // Align Bottom
            [self.view addConstraint:[NSLayoutConstraint constraintWithItem:button
                                                             attribute:NSLayoutAttributeBottom
                                                             relatedBy:NSLayoutRelationEqual
                                                                toItem:self.view
                                                             attribute:NSLayoutAttributeBottom
                                                            multiplier:1.0
                                                              constant:MIN(conf.buttonDoneYPositionOffset,0)]];
            
            break;
        case ALButtonYAlignmentCenter:
            // Center vertically
            [self.view addConstraint:[NSLayoutConstraint constraintWithItem:button
                                                             attribute:NSLayoutAttributeCenterY
                                                             relatedBy:NSLayoutRelationEqual
                                                                toItem:self.view
                                                             attribute:NSLayoutAttributeCenterY
                                                            multiplier:1.0
                                                              constant:conf.buttonDoneYPositionOffset]];
            break;
            
        default:
            break;
    }
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    [UIApplication sharedApplication].idleTimerDisabled = YES;
    
    NSError *error;
    BOOL success = [self.moduleView startScanningAndReturnError:&error];
    if(!success) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Could not start scanning" message:error.localizedDescription delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }

    self.moduleView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [[self view] addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[moduleView]|" options:0 metrics:nil views:@{@"moduleView" : self.moduleView}]];
    
    id topGuide = self.topLayoutGuide;
    [[self view] addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[topGuide]-0-[moduleView]|" options:0 metrics:nil views:@{@"moduleView" : self.moduleView, @"topGuide" : topGuide}]];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    [self dismissViewControllerAnimated:YES completion:^{
        [self.delegate anylineBaseScanViewController:self didStopScanning:self];
    }];
}

- (void)viewDidDisappear:(BOOL)animated {
    [UIApplication sharedApplication].idleTimerDisabled = NO;
}

- (BOOL)shouldAutorotate {
    return NO;
}

- (void)doneButtonPressed:(id)sender {
    [self.moduleView cancelScanningAndReturnError:nil];
    [self dismissViewControllerAnimated:YES completion:^{
        [self.delegate anylineBaseScanViewController:self didStopScanning:sender];
    }];
}

- (NSString *)saveImageToFileSystem:(UIImage *)image {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *basePath = ([paths count] > 0) ? [paths objectAtIndex:0] : nil;
    
    NSData *binaryImageData = UIImageJPEGRepresentation(image, 0.9);
    NSString *uuid = [NSUUID UUID].UUIDString;
    NSString *imagePath = [NSString stringWithFormat:@"%@.jpg",uuid];
    
    NSString *fullPath = [basePath stringByAppendingPathComponent:imagePath];
    [binaryImageData writeToFile:fullPath atomically:YES];
    
    return fullPath;
}

-(void)flashResultFor:(NSTimeInterval) duration {    
    [UIView animateWithDuration:duration/3 animations:^{
        self.scannedLabel.alpha = 1.0;
    } completion:^(BOOL finished) {
        [UIView animateWithDuration:duration/3*2 animations:^{
            self.scannedLabel.alpha = 0.0;
        } completion:^(BOOL finished) {
            // self.scannedLabel.alpha = 0.0;
        }];
    }];
}



@end