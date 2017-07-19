//
//  VungleAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/01.
//  Copyright © 2017年 adwitcher. All rights reserved.
//

#import "VungleAdapter.h"

@implementation VungleAdapter {
    UIViewController *_viewController;
}

@synthesize interstitialAdDelegate;

#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    _viewController = viewController;
    NSString *appId = [parameters objectForKey:@"app_id"];
    
    [[VungleSDK sharedSDK] setDelegate:self];
    [[VungleSDK sharedSDK] setLoggingEnabled:testMode];
    [[VungleSDK sharedSDK] startWithAppId:appId];
}

- (void)interstitialAdLoad {
    _DLOG();
    if ([VungleSDK sharedSDK].isAdPlayable) {
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
    } else {
        [self adLoad:1];
    }
}

- (void)interstitialAdShow {
    _DLOG();
    NSError *error = nil;
    if (![[VungleSDK sharedSDK] playAd:_viewController error:&error]) {
        _DLOG("%@", error);
        [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
    }
}


#pragma - VungleSDKDelegate

- (void)vungleSDKwillShowAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)vungleSDKWillCloseAdWithViewInfo:(NSDictionary *)viewInfo {
    _DLOG();
    NSNumber *completedView = [viewInfo objectForKey:@"completedView"];
    NSNumber *didDownload = [viewInfo objectForKey:@"didDownload"];
    
    if (didDownload.boolValue) {
        [self.interstitialAdDelegate interstitialAdClicked:self];
    }
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:!completedView.boolValue];
}

- (void)vungleSDKAdPlayableChanged:(BOOL)isAdPlayable {
    _DLOG();
}


#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if ([VungleSDK sharedSDK].isAdPlayable) {
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
