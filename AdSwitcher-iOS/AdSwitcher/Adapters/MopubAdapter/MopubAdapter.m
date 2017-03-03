//
//  MopubAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2017/03/02.
//  Copyright © 2017年 adwitcher. All rights reserved.
//

#import "MopubAdapter.h"

@implementation MopubAdapter {
    UIViewController *_viewController;
    MPAdView *_mpAdView;
    MPInterstitialAdController *_mpInterstitial;
    NSString *_adUnitId;
    CGSize _bannerSize;
    AdSwitcherNativeAdData *_nativeAdData;
    BOOL _testMode;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;

#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    _adUnitId = [parameters objectForKey:@"ad_unit_id"];
    _viewController = viewController;
    
    _DLOG(@"adUnitId:%@", _adUnitId);
    
    _testMode = testMode;
    
    _bannerSize = MOPUB_BANNER_SIZE;
    if (adSize == BannerAdSize_300x250) {
        _bannerSize = MOPUB_MEDIUM_RECT_SIZE;
    }
}

- (void)bannerAdLoad {
    _DLOG();
    _mpAdView = [[MPAdView alloc] initWithAdUnitId:_adUnitId size:_bannerSize];
    _mpAdView.delegate = self;
    _mpAdView.frame = CGRectMake(0, 0, _bannerSize.width, _bannerSize.height);
    [_mpAdView loadAd];
    [_mpAdView setTesting:_testMode];
    [_mpAdView stopAutomaticallyRefreshingContents];
    [_mpAdView setHidden:YES];
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [_mpAdView startAutomaticallyRefreshingContents];
    [_mpAdView setHidden:NO];
    
    [parentView addSubview:_mpAdView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_mpAdView removeFromSuperview];
}


#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    _adUnitId = [parameters objectForKey:@"ad_unit_id"];
    _viewController = viewController;
    
    _DLOG(@"adUnitId:%@", _adUnitId);
    
    _mpInterstitial = [MPInterstitialAdController interstitialAdControllerForAdUnitId:_adUnitId];
    [_mpInterstitial setTesting:testMode];
    _mpInterstitial.delegate = self;
}

- (void)interstitialAdLoad {
    _DLOG();
    [_mpInterstitial loadAd];
}

- (void)interstitialAdShow {
    if (_mpInterstitial.ready) {
        _DLOG("ready:1");
        [_mpInterstitial showFromViewController:_viewController];
        
    } else {
        _DLOG("ready:0");
        [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
    }
}


#pragma - MPAdViewDelegate

- (UIViewController *)viewControllerForPresentingModalView {
    return _viewController;
}

- (void)adViewDidLoadAd:(MPAdView *)view {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:YES];
}

- (void)adViewDidFailToLoadAd:(MPAdView *)view {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:NO];
}

- (void)willPresentModalViewForAd:(MPAdView *)view {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}

- (void)didDismissModalViewForAd:(MPAdView *)view {
    _DLOG();
}

- (void)willLeaveApplicationFromAd:(MPAdView *)view {
    _DLOG();
}


# pragma - MPInterstitialAdControllerDelegate

- (void)interstitialDidLoadAd:(MPInterstitialAdController *)interstitial {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
}

- (void)interstitialDidFailToLoadAd:(MPInterstitialAdController *)interstitial {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
}

- (void)interstitialWillAppear:(MPInterstitialAdController *)interstitial {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)interstitialDidAppear:(MPInterstitialAdController *)interstitial {
    _DLOG();
}

- (void)interstitialWillDisappear:(MPInterstitialAdController *)interstitial {
    _DLOG();
}

- (void)interstitialDidDisappear:(MPInterstitialAdController *)interstitial {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
}

- (void)interstitialDidExpire:(MPInterstitialAdController *)interstitial {
    _DLOG();
}

- (void)interstitialDidReceiveTapEvent:(MPInterstitialAdController *)interstitial {
    [self.interstitialAdDelegate interstitialAdClicked:self];
}

@end
