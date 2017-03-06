//
//  AdMobVideoAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2017/03/06.
//  Copyright © 2017年 adwitcher. All rights reserved.
//

#import "AdMobVideoAdapter.h"
#include <CommonCrypto/CommonDigest.h>

@implementation AdMobVideoAdapter {
    UIViewController *_viewController;
}

@synthesize interstitialAdDelegate;

#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    NSString *adUnitId = [parameters objectForKey:@"ad_unit_id"];
    _DLOG(@"ad_unit_id:%@", adUnitId);
    
    _viewController = viewController;
    
    GADRequest *request = [GADRequest request];
    /*
    if (testMode) {
        NSString *deviceId = [self getAdMobDeviceId];
        request.testDevices = @[kGADSimulatorID, deviceId];
    }
     */
    [GADRewardBasedVideoAd sharedInstance].delegate = self;
    [[GADRewardBasedVideoAd sharedInstance] loadRequest:request withAdUnitID:adUnitId];
}

- (void)interstitialAdLoad {
    if ([GADRewardBasedVideoAd sharedInstance].isReady) {
        _DLOG("ready");
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
        
    } else {
        [self adLoad:1];
    }
}

- (void)interstitialAdShow {
    _DLOG();
    [[GADRewardBasedVideoAd sharedInstance] presentFromRootViewController:_viewController];
}


# pragma - GADRewardBasedVideoAdDelegate

- (void)rewardBasedVideoAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd
   didRewardUserWithReward:(GADAdReward *)reward {
    _DLOG();
}

- (void)rewardBasedVideoAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd
    didFailToLoadWithError:(NSError *)error {
    _DLOG();
}

- (void)rewardBasedVideoAdDidReceiveAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    _DLOG();
}

- (void)rewardBasedVideoAdDidOpen:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)rewardBasedVideoAdDidStartPlaying:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    _DLOG();
}

- (void)rewardBasedVideoAdDidClose:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
}

- (void)rewardBasedVideoAdWillLeaveApplication:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClicked:self];
}


#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if ([GADRewardBasedVideoAd sharedInstance].isReady) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
            });
        } else if (count == 15) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
            });
        } else {
            [self adLoad:count + 1];
        }
    });
}

- (NSString *)getAdMobDeviceId {
    NSString *adUUID = [ASIdentifierManager sharedManager].advertisingIdentifier.UUIDString;
    return [self md5String:adUUID];
}

- (NSString *)md5String:(NSString *)input {
    const char *cStr = [input UTF8String];
    unsigned char digest[16];
    CC_MD5(cStr, (CC_LONG)strlen(cStr), digest);
    
    NSMutableString *output = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    for(int i = 0; i < CC_MD5_DIGEST_LENGTH; i++) {
        [output appendFormat:@"%02x", digest[i]];
    }
    return  output;
}



@end
