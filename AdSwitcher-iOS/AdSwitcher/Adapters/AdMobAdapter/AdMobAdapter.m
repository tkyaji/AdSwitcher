//
//  AdMobAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/14.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdMobAdapter.h"

#import <AdSupport/ASIdentifierManager.h>
#include <CommonCrypto/CommonDigest.h>

@implementation AdMobAdapter {
    BOOL _testMode;
    BannerAdSize _adSize;
    
    NSString *_adUnitId;
    
    UIViewController *_viewController;
    GADBannerView *_bannerView;
    
    GADInterstitial *_interstitial;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;


#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    _adUnitId = [parameters objectForKey:@"ad_unit_id"];
    _DLOG(@"ad_unit_id:%@", _adUnitId);
    
    _viewController = viewController;
    _adSize = adSize;
    _testMode = testMode;
}

- (void)bannerAdLoad {
    _DLOG();
    GADAdSize gadAdSize = [self toGADAdSize:_adSize];
    _bannerView = [[GADBannerView alloc] initWithAdSize:gadAdSize];
    _bannerView.adUnitID = _adUnitId;
    _bannerView.delegate = self;
    
    _bannerView.rootViewController = _viewController;
    
    GADRequest *request = [GADRequest request];
    if (_testMode) {
        NSString *deviceId = [self getAdMobDeviceId];
        request.testDevices = @[kGADSimulatorID, deviceId];
    }
    [_bannerView loadRequest:request];
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

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode {
    _adUnitId = [parameters objectForKey:@"ad_unit_id"];
    _DLOG(@"ad_unit_id:%@", _adUnitId);
    
    _viewController = viewController;
    _testMode = testMode;
}

- (void)interstitialAdLoad {
    _DLOG();
    _interstitial = [[GADInterstitial alloc] initWithAdUnitID:_adUnitId];
    _interstitial.delegate = self;
    
    GADRequest *request = [GADRequest request];
    if (_testMode) {
        NSString *deviceId = [self getAdMobDeviceId];
        request.testDevices = @[kGADSimulatorID, deviceId];
    }
    [_interstitial loadRequest:request];
}

- (void)interstitialAdShow {
    if (_interstitial.isReady) {
        _DLOG("ready : 1");
        [_interstitial presentFromRootViewController:_viewController];
    } else {
        _DLOG("ready : 0");
        [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
    }
}


#pragma - GADBannerViewDelegate

- (void)adViewDidReceiveAd:(GADBannerView *)bannerView {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:YES];
}

- (void)adView:(GADBannerView *)bannerView didFailToReceiveAdWithError:(GADRequestError *)error {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:NO];
}

- (void)adViewWillLeaveApplication:(GADBannerView *)bannerView {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}


#pragma - GADInterstitialDelegate

- (void)interstitialDidReceiveAd:(GADInterstitial *)ad {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
}

- (void)interstitial:(GADInterstitial *)ad didFailToReceiveAdWithError:(GADRequestError *)error {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
}

- (void)interstitialWillPresentScreen:(GADInterstitial *)ad {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)interstitialDidFailToPresentScreen:(GADInterstitial *)ad {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
}

- (void)interstitialDidDismissScreen:(GADInterstitial *)ad {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
}

- (void)interstitialWillLeaveApplication:(GADInterstitial *)ad {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClicked:self];
}

 
#pragma - Private Method

- (GADAdSize)toGADAdSize:(BannerAdSize)adSize {
    switch (adSize) {
        case BannerAdSize_320x50:
            return kGADAdSizeBanner;
            
        case BannerAdSize_320x100:
            return kGADAdSizeLargeBanner;
            
        case BannerAdSize_300x250:
            return kGADAdSizeMediumRectangle;
    }
    return kGADAdSizeBanner;
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
