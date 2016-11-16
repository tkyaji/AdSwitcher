//
//  TapjoyAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/11/14.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "TapjoyAdapter.h"

@implementation TapjoyAdapter {
    TJPlacement *_placement;
    UIViewController *_viewController;
    BOOL _result;
    BOOL _isSkipped;
}

@synthesize interstitialAdDelegate;

#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode {
    
    NSString *sdkKey = [parameters objectForKey:@"sdk_key"];
    NSString *placementName = [parameters objectForKey:@"placement"];
    
    _DLOG("sdk_key=%@, placement=%@", sdkKey, placementName);
    
    [Tapjoy setDebugEnabled:testMode];
    [Tapjoy connect:sdkKey];
    
    _placement = [TJPlacement placementWithName:placementName delegate:self];
    _placement.videoDelegate = self;
}

- (void)interstitialAdLoad {
    if (_placement.isContentAvailable && _placement.isContentReady) {
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
        
    } else {
        [_placement requestContent];
        [self adLoad:1];
    }
}

- (void)interstitialAdShow {
    _DLOG();
    [_placement showContentWithViewController:_viewController];
}



#pragma - TJPlacementDelegate

- (void)requestDidSucceed:(TJPlacement*)placement {
    _DLOG("placement=%@", placement.placementName);
}

- (void)requestDidFail:(TJPlacement*)placement error:(NSError*)error {
    _DLOG("placement=%@", placement.placementName);
}

- (void)contentIsReady:(TJPlacement*)placement {
    _DLOG("placement=%@", placement.placementName);
}

- (void)contentDidAppear:(TJPlacement*)placement {
    _DLOG("placement=%@", placement.placementName);
}

- (void)contentDidDisappear:(TJPlacement*)placement {
    _DLOG("placement=%@", placement.placementName);
    [self.interstitialAdDelegate interstitialAdClosed:self result:_result isSkipped:_isSkipped];
}

- (void)placement:(TJPlacement*)placement didRequestPurchase:(TJActionRequest*)request productId:(NSString*)productId {
    _DLOG("placement=%@", placement.placementName);
}

- (void)placement:(TJPlacement*)placement didRequestReward:(TJActionRequest*)request itemId:(NSString*)itemId quantity:(int)quantity {
    _DLOG("placement=%@", placement.placementName);
}



#pragma - TJPlacementVideoDelegate

- (void)videoDidStart:(TJPlacement*)placement {
    _DLOG("placement=%@", placement.placementName);
    [self.interstitialAdDelegate interstitialAdShown:self];
    _result = YES;
    _isSkipped = YES;
}

- (void)videoDidComplete:(TJPlacement*)placement {
    _DLOG("placement=%@", placement.placementName);
    _isSkipped = NO;
}

- (void)videoDidFail:(TJPlacement*)placement error:(NSString*)errorMsg {
    _DLOG("placement=%@, error=%@", placement.placementName, errorMsg);
    [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
}



#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if (_placement.isContentAvailable && _placement.isContentReady) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
            });
        } else if (count == 5) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
            });
        } else {
            [self adLoad:count + 1];
        }
    });
}



@end
