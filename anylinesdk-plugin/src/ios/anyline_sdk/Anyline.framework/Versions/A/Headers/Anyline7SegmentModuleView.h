
#import <Anyline/Anyline.h>

/**
 *  The possible run error codes for this module.
 *  You can listen to the error codes for each run via the delegate method anylineOCRModuleView:reportsRunFailure:
 */
typedef NS_ENUM(NSInteger, AL7SegmentError) {
    /**
     *  An unknown error occurred.
     */
    AL7SegmentErrorUnkown                   = -1,
    /**
     *  No text lines found in imag
     */
    AL7SemgentErrorNoDisplayFound           = -2,
    /**
     *  No text found in lines
     */
    AL7SemgentErrorNotEnoughEqualResults    = -3,
    /**
     *  The required min confidence is not reached for this run
     */
    AL7SemgentErrorResultNotValid    = -4,
};

@protocol Anyline7SegmentModuleDelegate;
/**
 * The Anyline7SegmentModuleView class declares the programmatic interface for an object that manages easy access to Anylines 7-segment scanning mode. All its capabilities are bundled into this AnylineAbstractModuleView subclass. Management of the scanning process happens within the view object. It is configurable via interface builder or the config json.
 *
 * Communication with the host application is managed with a delegate that conforms to Anyline7SegmentModuleDelegate.
 *
 */
@interface Anyline7SegmentModuleView : AnylineAbstractModuleView

/**
 *  Sets the license key and delegate.
 *
 *  @param licenseKey               The Anyline license key for this application bundle
 *  @param delegate                 The delegate that will receive the Anyline results (hast to conform to <Anyline7SegmentModuleDelegate>)
 *  @param cmdFilePath              The cmd file to load. This tells Anyline what to do.
 *  @param uiConfigurationFilePath  The ui configuration tells Anyline how to draw the cutout, flash buttons & how to behave
 *  @param error                    The error that occured
 *
 *  @return Boolean indicating the success / failure of the call.
 */
- (BOOL)setupWithLicenseKey:(NSString *)licenseKey
                   delegate:(id<Anyline7SegmentModuleDelegate>)delegate
                cmdFilePath:(NSString *)cmdFilePath
    uiConfigurationFilePath:(NSString *)uiConfigurationFilePath
                      error:(NSError **)error;
/**
 *  Sets a new Cmd file and returns an Error if something failed.
 *
 *  @param cmdFilePath The cmd to set
 *  @param error     The Error object if something fails
 *
 *  @return Boolean indicating the success / failure of the call.
 */
- (BOOL)setCmdFilePath:(NSString *)cmdFilePath error:(NSError **)error;

/**
 *  Sets a parameter with a key in the Interpreter. To customize the behaviour of
 *  the Cmd file you can set different parameters.
 *  Ex. date, time settings, ...
 *
 *  @param parameter Parameter to set.
 *  @param key       The key for the parameter.
 */
- (void)setParameter:(id)parameter forKey:(NSString *)key;

@end

/**
 *  The delegate for the Anyline7SegmentModuleView.
 */
@protocol Anyline7SegmentModuleDelegate <NSObject>

@required

/**
 *  Called when a result is found.
 *
 *  @param anyline7SegmentModuleView    The Anyline7SegmentModuleView.
 *  @param result                       The result object. You can retrieve the results for every datapoint with resultForIdentifier.
 *  @param image                        The Image where the result was found.
 *
 *  @return Boolean indicating if the result is accepted. Returning YES will invoke the Anyline result feedback activated in the config.
 */
- (BOOL)anyline7SegmentModuleView:(Anyline7SegmentModuleView *)anyline7SegmentModuleView
                    didFindResult:(ALResult *)result
                          atImage:(UIImage *)image;

@optional
/**
 * <p>Called with interesting values, that arise during processing.</p>
 * <p>
 * Some possibly reported values:
 * <ul>
 * <li>$transformedImage - The transformed image from the found display square </li>
 * <li>$displayResult - the result where every single 7-segment status is stored </li>
 * <li>$resultCount - the acual equal result count </li>
 * </ul>
 * </p>
 *
 *  @param anyline7SegmentModuleView The Anyline7SegmentModuleView
 *  @param variableName         The variable name of the reported value
 *  @param value                The reported value
 */
- (void)anyline7SegmentModuleView:(Anyline7SegmentModuleView *)anyline7SegmentModuleView
                  reportsVariable:(NSString *)variableName
                            value:(id)value;

/**
 *  Called when the outline of a possible text is detected. The provided List of points contains the outline
 *  in clockwise order starting with the most top left point. 
 *
 *  @warning When not implemented Anyline will handle the drawing. Deactivate it by implementing this delegate
 *           and return YES
 *
 *  @param anyline7SegmentModuleView The Anyline7SegmentModuleView
 *  @param outline              The ALSquare with the 4 points.
 *
 *  @return YES if you handle drawing by yourself, NO if Anyline should draw the outline.
 */
- (BOOL)anyline7SegmentModuleView:(Anyline7SegmentModuleView *)anyline7SegmentModuleView
                  outlineDetected:(ALSquare *)outline;

/**
 *  Is called when the processing is aborted for the current image before reaching return.
 *  (If no display is found, result not valid, etc.)
 *
 *  @param anyline7SegmentModuleView    The Anyline7SegmentModuleView
 *  @param error                        The error enum
 */
- (void)anyline7SegmentModuleView:(Anyline7SegmentModuleView *)anyline7SegmentModuleView
                reportsRunFailure:(AL7SegmentError)error;

@end
