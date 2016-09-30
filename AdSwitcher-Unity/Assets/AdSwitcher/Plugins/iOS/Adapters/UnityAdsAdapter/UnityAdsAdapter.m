//
//  UnityAdsAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/01.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "UnityAdsAdapter.h"

@implementation UnityAdsAdapter {
    UIViewController *_viewController;
    NSString *_zoneId;
}

@synthesize interstitialAdDelegate;

#pragma - VideoAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    NSString *gameId = [parameters objectForKey:@"game_id"];
    _zoneId = [parameters objectForKey:@"zone_id"];
    _DLOG("game_id=%@, zone_id=%@", gameId, _zoneId);
    
    _viewController = viewController;
    
    [UnityAds initialize:gameId delegate:self testMode:testMode];
}

- (void)interstitialAdLoad {
    if ([UnityAds isReady]) {
        _DLOG("ready");
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
        
    } else {
        [self adLoad:1];
    }
}

- (void)interstitialAdShow {
    _DLOG();
    [UnityAds show:_viewController];
}



#pragma - UnityAdsDelegate

- (void)unityAdsReady:(NSString *)placementId {
    _DLOG();
}

- (void)unityAdsDidError:(UnityAdsError)error withMessage:(NSString *)message {
    _DLOG("message=%@", message);
}

- (void)unityAdsDidStart:(NSString *)placementId {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)unityAdsDidFinish:(NSString *)placementId withFinishState:(UnityAdsFinishState)state {
    _DLOG("state=%d", (int)state);
    switch (state) {
        case kUnityAdsFinishStateError:
            [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
            break;
            
        case kUnityAdsFinishStateCompleted:
            [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
            break;
            
        case kUnityAdsFinishStateSkipped:
        [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:YES];
            break;
    }
}


#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if ([UnityAds isReady]) {
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
