//
//  AMoAdAdapter.h
//  AMoAdAdapter
//
//  Created by tkyaji on 2016/08/15.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import AdSupport;
@import ImageIO;
@import StoreKit;
@import AVFoundation;
@import CoreMedia;
@import AdSupport;

#import "AdSwitcherBannerView.h"
#import "AdSwitcherInterstitial.h"
#import "Log.h"
#import "AMoAdView.h"
#import "AMoAdInterstitial.h"

@interface AMoAdAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, AMoAdViewDelegate>

@end
