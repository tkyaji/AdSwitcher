//
//  AdMobAdapter.h
//  AdMobAdapter
//
//  Created by tkyaji on 2016/07/30.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import GoogleMobileAds;

#import "BannerAdAdapter.h"
#import "InterstitialAdAdapter.h"
#import "Log.h"

@interface AdMobAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, GADInterstitialDelegate, GADBannerViewDelegate>

@end
