//
//  AppLovinAdapter.h
//  AppLovinAdapter
//
//  Created by tkyaji on 2016/07/30.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import AdSupport;
@import AVFoundation;
@import CoreTelephony;
@import CoreGraphics;
@import CoreMedia;
@import StoreKit;
@import SystemConfiguration;
@import UIKit;

#import "InterstitialAdAdapter.h"
#import "Log.h"
#import "ALSdk.h"
#import "ALInterstitialAd.h"

@interface AppLovinAdapter : NSObject <InterstitialAdAdapter, ALAdDisplayDelegate, ALAdVideoPlaybackDelegate>

@end
