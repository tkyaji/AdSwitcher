//
//  VungleAdapter.h
//  VungleAdapter
//
//  Created by tkyaji on 2017/07/19.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import AdSupport;
@import AudioToolbox;
@import AVFoundation;
@import CFNetwork;
@import CoreGraphics;
@import CoreMedia;
@import MediaPlayer;
@import QuartzCore;
@import StoreKit;
@import SystemConfiguration;
@import UIKit;
@import WebKit;

#import "InterstitialAdAdapter.h"
#import "Log.h"
#import <VungleSDK/VungleSDK.h>

@interface VungleAdapter : NSObject <InterstitialAdAdapter, VungleSDKDelegate>

@end
