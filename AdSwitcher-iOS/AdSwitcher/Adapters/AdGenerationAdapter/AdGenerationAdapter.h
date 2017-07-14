//
//  AdGenerationAdapter.h
//  AdGenerationAdapter
//
//  Created by tkyaji on 2016/07/30.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import SystemConfiguration;
@import CoreTelephony;
@import MediaPlayer;
@import Security;
@import QuartzCore;
@import AdSupport;
@import CoreGraphics;

#import <ADG/ADGSettings.h>
#import <ADG/ADGManagerViewController.h>
#import <ADG/ADGInterstitial.h>
#import "BannerAdAdapter.h"
#import "InterstitialAdAdapter.h"
#import "Log.h"

@interface AdGenerationAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, ADGManagerViewControllerDelegate, ADGInterstitialDelegate>

@end
