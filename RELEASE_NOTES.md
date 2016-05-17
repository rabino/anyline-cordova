## Anyline-Cordova-Plugin

A plugin to make the Anyline-Modules available for Cordova.  
See: http://documentation.anyline.io/#modules

### Release Notes
## Anyline Cordova 3.4.1 Alpha
Release Date 2016-03-31

### New ###
- added Anyline OCR module (generic module for custom use cases)
- added scanning of heat meters
- added scanning of water meters
- added electric meter scanning with decimal place
- added scanning of generic digital meters

### Improved ###
- Camera and Focus settings
- MRZ speed
- barcode scanning accuracy improved
- MRZ scanning: higher tolerance for targeting the IDs


## Anyline Cordova 3.3.1 Alpha 1
Release Date 2016-01-11

### Fix ###
- MRZ speed improvements, especially on older devices
- Android preview view scaling fix

## Anyline Cordova 3.2.2 Alpha 2
Release Date 2015-12-10

### Fix ###
- Android: SDK asserts are now alerts. (i.e. wrong license, no camera access allowed, ...)

### Known Issues ###
- iOS: (community license only) flashOnResult:true is currently unstable. Workaround: set flashOnResult:false
- Localization

## Anyline Cordova 3.2.2 Alpha 1
Release Date 2015-12-02

### Fix ###
- iOS: flash-mode auto not working
- iOS: SDK asserts are now UIAlerts without crashing (i.e. wrong license, no camera access allowed, ...)

### Improved ###
- iOS: done button customisable for iOS (see barcode.js for example)
- iOS: done button offset
- iOS: done button font
- iOS: done button style (fullwidth,rect)
- iOS: done button background
- iOS: done button rounded corners                    

### Known Issues ###
- iOS: (community license only) flashOnResult:true is currently unstable. Workaround: set flashOnResult:false

## Anyline Cordova 3.2.1 Alpha 3

### Fix ###
- onResult was not called on iOS

## Anyline Cordova 3.2.1 Alpha 2

### Fix ###
- Configuration not applied correctly
- Threading issue
- MRZ Module wrong type issue

## Anyline Cordova 3.2.1 Alpha 1

### Improved ###
- Barcode Module
    - Better and faster scanning
    - Same barcode can now be scanned again (after 2 second timeout)
- MRZ Module
    - Refined scanning of TD1 size MROTDs
    - Refined scanning of MRPs and other TD3 size MRTDs
    - Added support for TD2 size MROTDs
    - Added support for French ID cards
        - Known Limitations: check digit validation does not work on French ID cards

### Disclaimer

This plugin is an alpha stage and API, names and stuff may still change, however we try not to.
