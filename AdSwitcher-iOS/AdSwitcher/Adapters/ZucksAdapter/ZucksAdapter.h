//
//  ZucksAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/11/07.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import ZucksAdNetworkSDK;

#import "AdSwitcherBannerView.h"
#import "AdSwitcherInterstitial.h"

@interface ZucksAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, ZADNBannerViewDelegate, ZADNInterstitialViewDelegate>

@end
