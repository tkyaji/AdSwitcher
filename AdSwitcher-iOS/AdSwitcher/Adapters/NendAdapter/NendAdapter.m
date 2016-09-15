//
//  NendAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/13.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "NendAdapter.h"

@implementation NendAdapter {
    UIViewController *_viewController;
    NADView *_nadView;
    NSString *_apiKey;
    NSString *_spotId;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;

#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    _apiKey = [parameters objectForKey:@"api_key"];
    _spotId = [parameters objectForKey:@"spot_id"];
    _DLOG(@"apiKey:%@, spotId:%@", _apiKey, _spotId);
    
    if (testMode) {
        switch (adSize) {
            case BannerAdSize_320x50:
                _apiKey = @"a6eca9dd074372c898dd1df549301f277c53f2b9";
                _spotId = @"3172";
                break;
                
            case BannerAdSize_320x100:
                _apiKey = @"eb5ca11fa8e46315c2df1b8e283149049e8d235e";
                _spotId = @"70996";
                break;
                
            case BannerAdSize_300x250:
                _apiKey = @"88d88a288fdea5c01d17ea8e494168e834860fd6";
                _spotId = @"70356";
                break;
        }
    }
}

- (void)bannerAdLoad {
    _nadView = [NADView new];
    [_nadView setNendID:_apiKey spotID:_spotId];
    [_nadView setDelegate:self];
#ifdef DEBUG
    [_nadView setIsOutputLog:YES];
#else
    [_nadView setIsOutputLog:NO];
#endif
    [_nadView load];
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [parentView addSubview:_nadView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_nadView removeFromSuperview];
}



#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    _apiKey = [parameters objectForKey:@"api_key"];
    _spotId = [parameters objectForKey:@"spot_id"];
    _DLOG(@"apiKey:%@, spotId:%@", _apiKey, _spotId);
    
    _viewController = viewController;
    if (testMode) {
        _apiKey = @"308c2499c75c4a192f03c02b2fcebd16dcb45cc9";
        _spotId = @"213208";
    }
    
    [NADInterstitial sharedInstance].enableAutoReload = NO;
    [NADInterstitial sharedInstance].delegate = self;
#ifdef DEBUG
    [NADInterstitial sharedInstance].isOutputLog = YES;
#else
    [NADInterstitial sharedInstance].isOutputLog = NO;
#endif
}

- (void)interstitialAdLoad {
    _DLOG();
    [[NADInterstitial sharedInstance] loadAdWithApiKey:_apiKey spotId:_spotId];
}

- (void)interstitialAdShow {
    NADInterstitialShowResult result = [[NADInterstitial sharedInstance] showAdFromViewController:_viewController];
    _DLOG(@"result:%ld", (long)result);
    switch (result) {
        case AD_SHOW_SUCCESS:
        case AD_LOAD_INCOMPLETE:
            [self.interstitialAdDelegate interstitialAdShown:self];
            break;
            
        case AD_REQUEST_INCOMPLETE:
        case AD_DOWNLOAD_INCOMPLETE:
        case AD_FREQUENCY_NOT_REACHABLE:
        case AD_SHOW_ALREADY:
        case AD_CANNOT_DISPLAY:
            break;
    }
}



#pragma - NADViewDelegate

- (void)nadViewDidReceiveAd:(NADView *)adView {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:YES];
}

- (void)nadViewDidFailToReceiveAd:(NADView *)adView {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:NO];
}

- (void)nadViewDidClickAd:(NADView *)adView {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}



#pragma - NADInterstitialDelegate

- (void)didFinishLoadInterstitialAdWithStatus:(NADInterstitialStatusCode)status {
    _DLOG(@"status:%ld", (long)status);
    [self.interstitialAdDelegate interstitialAdLoaded:self result:(status == SUCCESS)];
}

- (void)didClickWithType:(NADInterstitialClickType)type {
    _DLOG(@"type:%ld", (long)type);
    if (type == CLOSE) {
        [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
    } else if (type == DOWNLOAD) {
        [self.interstitialAdDelegate interstitialAdClicked:self];
    }
}


@end
