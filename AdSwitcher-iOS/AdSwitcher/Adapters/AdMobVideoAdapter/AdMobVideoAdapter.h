//
//  AdMobVideoAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2017/03/06.
//  Copyright © 2017年 adwitcher. All rights reserved.
//

@import AdSupport;
@import AudioToolbox;
@import AVFoundation;
@import CoreGraphics;
@import CoreTelephony;
@import EventKit;
@import EventKitUI;
@import MediaPlayer;
@import MessageUI;
@import StoreKit;
@import SystemConfiguration;

@import GoogleMobileAds;

#import "InterstitialAdAdapter.h"
#import "Log.h"

@interface AdMobVideoAdapter : NSObject <InterstitialAdAdapter, GADRewardBasedVideoAdDelegate>

@end
