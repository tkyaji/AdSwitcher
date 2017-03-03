//
//  MopubAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2017/03/02.
//  Copyright © 2017年 adwitcher. All rights reserved.
//

@import AdSupport;
@import CoreGraphics;
@import CoreLocation;
@import CoreTelephony;
@import Foundation;
@import MediaPlayer;
@import QuartzCore;
@import StoreKit;
@import SystemConfiguration;
@import UIKit;
@import WebKit;

#import "AdSwitcherBannerView.h"
#import "AdSwitcherInterstitial.h"
#import "AdSwitcherNativeAd.h"
#import "MoPub.h"
#import "MPAdView.h"
#import "MPInterstitialAdController.h"

@interface MopubAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, MPAdViewDelegate, MPInterstitialAdControllerDelegate>

@end
