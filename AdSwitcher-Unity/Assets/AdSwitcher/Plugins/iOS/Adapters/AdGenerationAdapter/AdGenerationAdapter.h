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

#import <ADG/ADGManagerViewController.h>
#import "BannerAdAdapter.h"
#import "Log.h"

@interface AdGenerationAdapter : NSObject <BannerAdAdapter, ADGManagerViewControllerDelegate>

@end
