//
//  TapjoyAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/11/14.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "AdSwitcherInterstitial.h"
#import <Tapjoy/Tapjoy.h>

@interface TapjoyAdapter : NSObject <InterstitialAdAdapter, TJPlacementDelegate, TJPlacementVideoDelegate>

@end
