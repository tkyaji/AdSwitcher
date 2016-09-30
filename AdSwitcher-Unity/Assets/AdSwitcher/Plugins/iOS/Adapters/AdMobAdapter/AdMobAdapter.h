//
//  AdMobAdapter.h
//  AdMobAdapter
//
//  Created by tkyaji on 2016/07/30.
//  Copyright © 2016年 adwitcher. All rights reserved.
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

#import "BannerAdAdapter.h"
#import "InterstitialAdAdapter.h"
#import "Log.h"

@interface AdMobAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, GADInterstitialDelegate, GADBannerViewDelegate>

@end
