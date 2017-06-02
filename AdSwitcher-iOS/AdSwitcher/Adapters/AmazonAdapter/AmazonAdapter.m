//
//  AmazonAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2017/06/02.
//  Copyright © 2017年 adwitcher. All rights reserved.
//

#import "AmazonAdapter.h"

@implementation AmazonAdapter {
    UIViewController *_viewController;
    NSString *_appKey;
    BOOL _testMode;
    AmazonAdView *_amazonAdView;
    CGSize _adSize;
    AmazonAdInterstitial *_interstitial;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;

#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    _viewController = viewController;
    _appKey = [parameters objectForKey:@"app_key"];
    _testMode = testMode;
    _DLOG(@"appKey:%@", _appKey);
    
    switch (adSize) {
        case BannerAdSize_320x50:
        case BannerAdSize_320x100:
            _adSize = AmazonAdSize_320x50;
            break;
        case BannerAdSize_300x250:
            _adSize = AmazonAdSize_300x250;
            break;
    }
    [[AmazonAdRegistration sharedRegistration] setAppKey:_appKey];
}

- (void)bannerAdLoad {
    _DLOG();
    _amazonAdView = [AmazonAdView amazonAdViewWithAdSize:_adSize];
    AmazonAdOptions *options = [AmazonAdOptions options];
    options.isTestRequest = _testMode;
    _amazonAdView.delegate = self;
    [_amazonAdView loadAd:options];
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [parentView addSubview:_amazonAdView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_amazonAdView removeFromSuperview];
}


#pragma - AmazonAdViewDelegate

- (UIViewController *)viewControllerForPresentingModalView {
    return _viewController;
}

- (void)adViewWillExpand:(AmazonAdView *)view {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}

- (void)adViewDidFailToLoad:(AmazonAdView *)view withError:(AmazonAdError *)error {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:NO];
}

- (void)adViewDidLoad:(AmazonAdView *)view {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:YES];
}


#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    _viewController = viewController;
    _appKey = [parameters objectForKey:@"app_key"];
    _testMode = testMode;
    _DLOG(@"appKey:%@", _appKey);

    _interstitial = [AmazonAdInterstitial amazonAdInterstitial];
    _interstitial.delegate = self;
}

- (void)interstitialAdLoad {
    _DLOG();
    AmazonAdOptions *options = [AmazonAdOptions options];
    options.isTestRequest = _testMode;
    [_interstitial load:options];
}

- (void)interstitialAdShow {
    _DLOG();
    if (!_interstitial.isReady) {
        [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
        return;
    }
    [_interstitial presentFromViewController:_viewController];
}


#pragma - AmazonAdInterstitialDelegate

- (void)interstitialDidLoad:(AmazonAdInterstitial *)interstitial {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
}

- (void)interstitialDidFailToLoad:(AmazonAdInterstitial *)interstitial withError:(AmazonAdError *)error {
    _DLOG("error:%@", error);
    [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
}

- (void)interstitialWillPresent:(AmazonAdInterstitial *)interstitial {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)interstitialDidPresent:(AmazonAdInterstitial *)interstitial {
    _DLOG();
}

- (void)interstitialWillDismiss:(AmazonAdInterstitial *)interstitial {
    _DLOG();
}

- (void)interstitialDidDismiss:(AmazonAdInterstitial *)interstitial {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
}

@end
