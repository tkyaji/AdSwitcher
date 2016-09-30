//
//  FacebookAdapter.m
//  FacebookAdapter
//
//  Created by tkyaji on 2016/08/03.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "FacebookAdapter.h"

@implementation FacebookAdapter {
    FBAdView *_adView;
    FBInterstitialAd *_interstitialAd;
    UIViewController *_viewController;
    NSString *_placementId;
    BannerAdSize _adSize;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;

#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    _placementId = [parameters objectForKey:@"placement_id"];
    _viewController = viewController;
    _adSize = adSize;
    _DLOG(@"placement_id:%@", _placementId);
    
    if (testMode) {
        // TODO: testDeviceIdの算出と設定
        [FBAdSettings addTestDevice:@""];
    }
}

- (void)bannerAdLoad {
    _DLOG();
    
    _adView = [[FBAdView alloc] initWithPlacementID:_placementId adSize:[self toFBAdSize:_adSize] rootViewController:_viewController];
    
    switch (_adSize) {
        case BannerAdSize_320x50:
        case BannerAdSize_320x100:
            _adView.frame = CGRectMake(0, 0, 320, _adView.frame.size.height);
            break;
            
        case BannerAdSize_300x250:
            _adView.frame = CGRectMake(0, 0, 300, _adView.frame.size.height);
            break;
    }
    _adView.delegate = self;

    [_adView loadAd];
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [parentView addSubview:_adView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_adView removeFromSuperview];
    _adView = nil;
}


#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    _placementId = [parameters objectForKey:@"placement_id"];
    _DLOG(@"placement_id:%@", _placementId);
    
    _viewController = viewController;
    
    if (testMode) {
        // TODO: testDeviceIdの取得
        [FBAdSettings addTestDevice:@""];
    }
}

- (void)interstitialAdLoad {
    _DLOG();
    _interstitialAd = [[FBInterstitialAd alloc] initWithPlacementID:_placementId];
    _interstitialAd.delegate = self;
    [_interstitialAd loadAd];
}

- (void)interstitialAdShow {
    _DLOG();
    [_interstitialAd showAdFromRootViewController:_viewController];
    [self.interstitialAdDelegate interstitialAdShown:self];
}



#pragma - FBAdViewDelegate

- (void)adViewDidClick:(FBAdView *)adView {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}

- (void)adViewDidFinishHandlingClick:(FBAdView *)adView {
    _DLOG();
}

- (void)adViewDidLoad:(FBAdView *)adView {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:YES];
}

- (void)adView:(FBAdView *)adView didFailWithError:(NSError *)error {
    _DLOG(@"error:%@", error);
    [self.bannerAdDelegate bannerAdReceived:self result:NO];
    [_adView disableAutoRefresh];
}



#pragma - FBInterstitialAdDelegate

- (void)interstitialAdDidClick:(FBInterstitialAd *)interstitialAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClicked:self];
}

- (void)interstitialAdDidClose:(FBInterstitialAd *)interstitialAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
    [_adView disableAutoRefresh];
}

- (void)interstitialAdDidLoad:(FBInterstitialAd *)interstitialAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
}

- (void)interstitialAd:(FBInterstitialAd *)interstitialAd didFailWithError:(NSError *)error {
    _DLOG(@"error:%@", error);
    [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
}



#pragma - Private Method

- (FBAdSize)toFBAdSize:(BannerAdSize)adSize {
    switch (adSize) {
        case BannerAdSize_320x50:
            return kFBAdSizeHeight50Banner;
            
        case BannerAdSize_320x100:
            return kFBAdSizeHeight90Banner;
            
        case BannerAdSize_300x250:
            return kFBAdSizeHeight250Rectangle;
    }
    return kFBAdSizeHeight50Banner;
}


@end
