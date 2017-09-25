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
    BOOL _isRewarded;
    BOOL _testMode;
    NSString *_adUnitId;
}

@synthesize interstitialAdDelegate;

#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    _adUnitId = [parameters objectForKey:@"ad_unit_id"];
    _DLOG(@"ad_unit_id:%@", _adUnitId);
    
    _viewController = viewController;
    _testMode = testMode;
    
    [GADRewardBasedVideoAd sharedInstance].delegate = self;
}

- (void)interstitialAdLoad {
    GADRequest *request = [GADRequest request];
    if (_testMode) {
        NSString *deviceId = [self getAdMobDeviceId];
        request.testDevices = @[kGADSimulatorID, deviceId];
    }
    [[GADRewardBasedVideoAd sharedInstance] loadRequest:request withAdUnitID:_adUnitId];
}

- (void)interstitialAdShow {
    _DLOG();
    _isRewarded = NO;
    [[GADRewardBasedVideoAd sharedInstance] presentFromRootViewController:_viewController];
}


# pragma - GADRewardBasedVideoAdDelegate

- (void)rewardBasedVideoAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd didRewardUserWithReward:(GADAdReward *)reward {
    _DLOG();
    _isRewarded = YES;
}

- (void)rewardBasedVideoAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd didFailToLoadWithError:(NSError *)error {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:rewardBasedVideoAd.isReady];
}

- (void)rewardBasedVideoAdDidReceiveAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:rewardBasedVideoAd.isReady];
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
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:!_isRewarded];
}

- (void)rewardBasedVideoAdWillLeaveApplication:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClicked:self];
}


#pragma - Private methods

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
