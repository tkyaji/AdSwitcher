//
//  IMobileAdapter.h
//  IMobileAdapter
//
//  Created by tkyaji on 2016/08/09.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import AdSupport;
@import SystemConfiguration;
@import CoreLocation;

#import "BannerAdAdapter.h"
#import "InterstitialAdAdapter.h"
#import "ImobileSdkAds/ImobileSdkAds.h"
#import "Log.h"

@interface IMobileAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, IMobileSdkAdsDelegate>

@end
