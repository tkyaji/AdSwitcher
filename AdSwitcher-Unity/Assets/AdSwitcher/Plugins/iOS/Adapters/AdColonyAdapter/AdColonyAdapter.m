//
//  AdColonyAdapter.m
//  AdColonyAdapter
//
//  Created by tkyaji on 2016/08/09.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdColonyAdapter.h"

@implementation AdColonyAdapter {
    NSString *_zoneId;
    UIViewController* _appViewController;
}

@synthesize interstitialAdDelegate;

# pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    NSString *appId = [parameters objectForKey:@"app_id"];
    _zoneId = [parameters objectForKey:@"zone_id"];
    
    _DLOG(@"app_id:%@, zone_id:%@", appId, _zoneId);
    
    [AdColony configureWithAppID:appId zoneIDs:@[_zoneId] delegate:self logging:testMode];
}

- (void)interstitialAdLoad {
    if ([AdColony zoneStatusForZone:_zoneId] == ADCOLONY_ZONE_STATUS_ACTIVE) {
        _DLOG("ready");
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
        
    } else {
        [self adLoad:1];
    }
}

- (void)interstitialAdShow {
    UIWindow* window = [[UIApplication sharedApplication] keyWindow];
    UIViewController* viewController = [UIViewController new];
    [viewController.view setBackgroundColor:[UIColor whiteColor]];
    _appViewController = window.rootViewController;
    window.rootViewController = viewController;
    
    [AdColony playVideoAdForZone:_zoneId withDelegate:self];
}


# pragma - AdColonyAdDelegate

- (void)onAdColonyAdStartedInZone:(NSString *)zoneID {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)onAdColonyAdAttemptFinished:(BOOL)shown inZone:(NSString *)zoneID {
    _DLOG(@"show:%d", (int)shown);
    [self.interstitialAdDelegate interstitialAdClosed:self result:shown isSkipped:NO];
    [[UIApplication sharedApplication] keyWindow].rootViewController = _appViewController;
}


#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if ([AdColony zoneStatusForZone:_zoneId] == ADCOLONY_ZONE_STATUS_ACTIVE) {
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
