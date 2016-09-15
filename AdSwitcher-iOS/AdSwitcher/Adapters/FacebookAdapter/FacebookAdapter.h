//
//  FacebookAdapter.h
//  FacebookAdapter
//
//  Created by tkyaji on 2016/08/03.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import AdSupport;
@import AVFoundation;
@import CFNetwork;
@import CoreFoundation;
@import CoreMedia;
@import CoreTelephony;
@import StoreKit;
@import SystemConfiguration;

#import "AdSwitcherBannerView.h"
#import "AdSwitcherInterstitial.h"
#import "Log.h"
@import FBAudienceNetwork;

@interface FacebookAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, FBAdViewDelegate, FBInterstitialAdDelegate>

@end
