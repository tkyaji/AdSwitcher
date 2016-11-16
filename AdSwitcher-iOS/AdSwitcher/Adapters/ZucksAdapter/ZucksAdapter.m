//
//  ZucksAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/11/07.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "ZucksAdapter.h"

@implementation ZucksAdapter {
    UIViewController *_viewController;
    ZADNBannerView *_bannerView;
    NSString *_frameId;
    CGRect _frame;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;

#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    _frameId = [parameters objectForKey:@"frame_id"];
    _DLOG(@"frame_id=%@", _frameId);
    
    if (testMode) {
        switch (adSize) {
            case BannerAdSize_320x50:
                _frameId = @"_1d8ba78682";
                break;
            case BannerAdSize_320x100:
                _frameId = @"_4b551951af";
                break;
            case BannerAdSize_300x250:
                _frameId = @"_4089f6355b";
                break;
        }
    }
    
    _frame = [self toRect:adSize];
}

- (void)bannerAdLoad {
    _DLOG();
    _bannerView = [[ZADNBannerView alloc] initWithFrame:_frame frameId:_frameId];
    _bannerView.delegate = self;
    [_bannerView loadAd];
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [parentView addSubview:_bannerView];
}

- (void)bannerAdHide {
    _DLOG();
    [_bannerView removeFromSuperview];
}



#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    _frameId = [parameters objectForKey:@"frame_id"];
    _DLOG(@"frame_id=%@", _frameId);
    
    if (testMode) {
        _frameId = @"_3e64b0843b";
    }
    [ZADNInterstitialView sharedInstance].frameId = _frameId;
    [ZADNInterstitialView sharedInstance].delegate = self;
}

- (void)interstitialAdLoad {
    _DLOG();
    [[ZADNInterstitialView sharedInstance] loadAd];
}

- (void)interstitialAdShow {
    [[ZADNInterstitialView sharedInstance] show];
}



# pragma - ZADNBannerViewDelegate

- (void)bannerViewDidReceiveAd:(ZADNBannerView *)bannerView {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:YES];
}

- (void)bannerView:(ZADNBannerView *)bannerView didFailAdWithErrorType:(ZADNBannerErrorType)errorType {
    _DLOG("error=%d ", (int)errorType);
    [self.bannerAdDelegate bannerAdReceived:self result:NO];
}

- (void)bannerViewDidTapAd:(ZADNBannerView *)bannerView {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}



#pragma - ZADNInterstitialViewDelegate


- (void)interstitialViewDidReceiveAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
}

- (void)interstitialViewDidLoadFailAdWithErrorType:(ZADNInterstitialLoadErrorType)errorType {
    _DLOG("error=%d", (int)errorType);
    [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
}

- (void)interstitialViewDidShowAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)interstitialViewDidShowFailAdWithErrorType:(ZADNInterstitialShowErrorType)errorType {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
}

- (void)interstitialViewCancelDisplayRate {
    _DLOG();
}

- (void)interstitialViewDidTapAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClicked:self];
}

- (void)interstitialViewDidDismissAd {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
}



#pragma - Private Method

- (CGRect)toRect:(BannerAdSize)adSize {
    switch (adSize) {
        case BannerAdSize_320x50:
            return CGRectMake(0, 0, 320, 50);
            
        case BannerAdSize_320x100:
            return CGRectMake(0, 0, 320, 100);
            
        case BannerAdSize_300x250:
            return CGRectMake(0, 0, 300, 250);
    }
    return CGRectMake(0, 0, 320, 50);
}

@end
