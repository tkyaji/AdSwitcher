//
//  AdfurikunVideoAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/11/11.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdfurikunVideoAdapter.h"


@implementation AdfurikunVideoAdapter {
    NSString *_appId;
    ADFmyMovieInterstitial *_adfurikunMovieInterstitial;
    BOOL _result;
    BOOL _isSkipped;
}

@synthesize interstitialAdDelegate;


#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode {
    
    _appId = [parameters objectForKey:@"app_id"];
    _DLOG(@"app_id=%@", _appId);

    [ADFmyMovieInterstitial initWithAppID:_appId viewController:viewController];
    _adfurikunMovieInterstitial = [ADFmyMovieInterstitial getInstance:_appId delegate:self];
}

- (void)interstitialAdLoad {
    if (_adfurikunMovieInterstitial.isPrepared) {
        _DLOG("ready");
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
    } else {
        [self adLoad:1];
    }
}

- (void)interstitialAdShow {
    _DLOG();
    _result = NO;
    _isSkipped = NO;
    [_adfurikunMovieInterstitial play];
}



#pragma - ADFmyMovieRewardDelegate

- (void)AdsFetchCompleted:(BOOL)isTestMode_inApp {
    _DLOG();
}

- (void)AdsDidShow:(NSString *)adnetworkKey {
    _DLOG("adnetworkKey=%@", adnetworkKey);
    [self.interstitialAdDelegate interstitialAdShown:self];
    _result = YES;
    _isSkipped = YES;
}

- (void)AdsDidCompleteShow {
    _DLOG();
    _isSkipped = NO;
}

- (void)AdsPlayFailed {
    _DLOG();
}

- (void)AdsDidHide {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:_result isSkipped:_isSkipped];
}



#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if (_adfurikunMovieInterstitial.isPrepared) {
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
