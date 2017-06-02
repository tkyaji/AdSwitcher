//
//  AmazonAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2017/06/02.
//  Copyright © 2017年 adwitcher. All rights reserved.
//

#import <UIKit/UIKit.h>

@import Foundation;
@import AdSupport;
@import CoreLocation;
@import SystemConfiguration;
@import CoreTelephony;
@import MediaPlayer;
@import EventKit;
@import EventKitUI;
@import StoreKit;

#import <AmazonAd/AmazonAdInterstitial.h>
#import <AmazonAd/AmazonAdView.h>
#import <AmazonAd/AmazonAdRegistration.h>
#import "AdSwitcherBannerView.h"
#import "AdSwitcherInterstitial.h"
#import "Log.h"

@interface AmazonAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, AmazonAdViewDelegate, AmazonAdInterstitialDelegate>

@end
