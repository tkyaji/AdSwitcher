//
//  AdColonyAdapter.h
//  AdColonyAdapter
//
//  Created by tkyaji on 2016/08/09.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import AdSupport;
@import AudioToolbox;
@import AVFoundation;
@import CoreGraphics;
@import CoreMedia;
@import CoreTelephony;
@import EventKit;
@import EventKitUI;
@import MediaPlayer;
@import MessageUI;
@import QuartzCore;
@import Social;
@import StoreKit;
@import SystemConfiguration;
@import WebKit;

#import "AdSwitcherInterstitial.h"
#import "Log.h"
#import <AdColony/AdColony.h>

@interface AdColonyAdapter : NSObject <InterstitialAdAdapter, AdColonyDelegate, AdColonyAdDelegate>

@end
