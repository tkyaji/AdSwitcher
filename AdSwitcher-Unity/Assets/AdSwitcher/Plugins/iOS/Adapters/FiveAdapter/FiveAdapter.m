//
//  FiveAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/21.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "FiveAdapter.h"

static BOOL _initializedSdk;

@implementation FiveAdapter {
    NSString *_slotId;
    FADAdViewW320H180 *_bannerView;
    FADInterstitial *_interstitial;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;


#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    NSString *appId = [parameters objectForKey:@"app_id"];
    _slotId = [parameters objectForKey:@"slot_id"];
    _DLOG(@"appId:%@, slotId:%@", appId, _slotId);
    
    [FiveAdapter initializeSdk:appId testMode:testMode];
}

- (void)bannerAdLoad {
    _DLOG();
    [FADSettings enableLoading:YES];
    // 300x250のレクタングルは用意されていないらしいので、320x180で表示
    _bannerView = [[FADAdViewW320H180 alloc] initWithFrame:CGRectMake(0, 0, 320, 180) slotId:_slotId];
    [_bannerView setDelegate:self];
    [_bannerView loadAd];
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [parentView addSubview:_bannerView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_bannerView removeFromSuperview];
}



#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    NSString *appId = [parameters objectForKey:@"app_id"];
    _slotId = [parameters objectForKey:@"slot_id"];
    _DLOG(@"appId:%@, slotId:%@", appId, _slotId);
    
    [FiveAdapter initializeSdk:appId testMode:testMode];
}

- (void)interstitialAdLoad {
    _DLOG();
    [FADSettings enableLoading:YES];
    _interstitial= [[FADInterstitial alloc] initWithSlotId:_slotId];
    [_interstitial setDelegate:self];
    [_interstitial loadAd];
}

- (void)interstitialAdShow {
    _DLOG();
    [_interstitial show];
    [self.interstitialAdDelegate interstitialAdShown:self];
}


#pragma - FADDelegate

- (void)fiveAdDidLoad:(id<FADAdInterface>)ad {
    _DLOG();
    if (self.bannerAdDelegate) {
        [self.bannerAdDelegate bannerAdReceived:self result:YES];
    }
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
    }
}

- (void)fiveAd:(id<FADAdInterface>)ad didFailedToReceiveAdWithError:(FADErrorCode) errorCode {
    _DLOG(@"error:%d", (int)errorCode);
    if (self.bannerAdDelegate) {
        [self.bannerAdDelegate bannerAdReceived:self result:NO];
    }
    
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
    }
}

- (void)fiveAdDidClick:(id<FADAdInterface>)ad {
    _DLOG();
    if (self.bannerAdDelegate) {
        [self.bannerAdDelegate bannerAdClicked:self];
    }
    
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdClicked:self];
    }
}

- (void)fiveAdDidClose:(id<FADAdInterface>)ad {
    _DLOG();
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
    }
}

- (void)fiveAdDidStart:(id<FADAdInterface>)ad {
}

- (void)fiveAdDidPause:(id<FADAdInterface>)ad {
}

- (void)fiveAdDidResume:(id<FADAdInterface>)ad {
}

- (void)fiveAdDidViewThrough:(id<FADAdInterface>)ad {
}

- (void)fiveAdDidReplay:(id<FADAdInterface>)ad {
}


#pragma - Private mehods

+ (void)initializeSdk:(NSString *)appId testMode:(BOOL)testMode {
    if (_initializedSdk) {
        return;
    }
    
    FADConfig *config = [[FADConfig alloc] initWithAppId:appId];
    
    config.fiveAdFormat = [NSSet setWithObjects:
                           [NSNumber numberWithInt:kFADFormatW320H180],
//                           [NSNumber numberWithInt:kFADFormatW300H250],
                           [NSNumber numberWithInt:kFADFormatInterstitialPortrait],
                           [NSNumber numberWithInt:kFADFormatInterstitialLandscape],
                           nil];
    config.isTest = testMode;
    
    [FADSettings registerConfig:config];
    
    _initializedSdk = YES;
}


@end
