//
//  FiveAdapter.h
//  FiveAdapter
//
//  Created by tkyaji on 2016/07/30.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import AdSupport;
@import AVFoundation;
@import CoreMedia;
@import CoreTelephony;
@import SystemConfiguration;

#import "BannerAdAdapter.h"
#import "InterstitialAdAdapter.h"
#import "Log.h"
#import <FiveAd/FiveAd.h>


@interface FiveAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, FADDelegate>

@end
