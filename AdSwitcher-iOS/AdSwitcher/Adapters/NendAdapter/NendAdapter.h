//
//  NendAdapter.h
//  NendAdapter
//
//  Created by tkyaji on 2016/07/30.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import <UIKit/UIKit.h>

@import Foundation;
@import AdSupport;
@import Security;
@import ImageIO;

#import "AdSwitcherBannerView.h"
#import "AdSwitcherInterstitial.h"
#import "NADView.h"
#import "NADInterstitial.h"

@interface NendAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, NADViewDelegate, NADInterstitialDelegate>

@end
