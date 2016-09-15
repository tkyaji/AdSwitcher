//
//  UnityAdsAdapter.h
//  UnityAdsAdapter
//
//  Created by tkyaji on 2016/07/30.
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

#import "InterstitialAdAdapter.h"
#import "Log.h"
#import <UnityAds/UnityAds.h>

@interface UnityAdsAdapter : NSObject <InterstitialAdAdapter, UnityAdsDelegate>

@end
