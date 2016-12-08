//
//  AdfurikunAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/11/07.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdfurikunAdapter.h"

@implementation AdfurikunAdapter {
    AdfurikunView *_adfurikunView;
    AdfurikunPopupView *_adfurikunPopupView;
    AdfurikunNativeAd *_adfurikunNativeAd;
    AdfurikunPopupDelegateReceiver *_delegateReceiver;
    AdfurikunNativeAdInfo *_adfurikunNativeAdInfo;
    AdSwitcherNativeAdData *_nativeAdData;
    UIViewController *_viewController;
    NSString *_appId;
    BOOL _testMode;
    CGRect _frame;
    BOOL _loading;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;
@synthesize nativeAdDelegate;

#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    _appId = [parameters objectForKey:@"app_id"];
    _DLOG(@"app_id=%@", _appId);
    
    _testMode = testMode;
    _frame = [self toRect:adSize];
}

- (void)bannerAdLoad {
    _DLOG();
    
    _adfurikunView = [[AdfurikunView alloc] initWithFrame:_frame];
    _adfurikunView.appID = _appId;
    _adfurikunView.delegate = self;
    if (_testMode) {
        [_adfurikunView testModeEnable];
    }
    
    [_adfurikunView startShowAd];
    _loading = YES;
    
    // 2秒でタイムアウトしてロード終了。ロードし続けると落ちる。    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 2 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_loading) {
                _DLOG("load timeout.");
                [_adfurikunView stopUpdating];
                _loading = NO;
                [self.bannerAdDelegate bannerAdReceived:self result:NO];
            }
        });
    });
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [parentView addSubview:_adfurikunView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_adfurikunView removeFromSuperview];
}



#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    _viewController = viewController;
    _appId = [parameters objectForKey:@"app_id"];
    _DLOG(@"app_id=%@", _appId);
    
    _adfurikunPopupView = [[AdfurikunPopupView alloc] init];
    _adfurikunPopupView.appID = _appId;
    if (testMode) {
        [_adfurikunPopupView testModeEnable];
    }
    
    _delegateReceiver = [AdfurikunPopupDelegateReceiver new];
    _delegateReceiver.delegate = self;
    _adfurikunPopupView.delegate = _delegateReceiver;
}

- (void)interstitialAdLoad {
    _DLOG();
    [_adfurikunPopupView preloadAd];
}

- (void)interstitialAdShow {
    _DLOG();
    [_viewController.view addSubview:_adfurikunPopupView];
    [_adfurikunPopupView preloadShowAd];
}



#pragma - NativeAdAdapter

- (void)nativeAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode {
    _appId = [parameters objectForKey:@"app_id"];
    _DLOG(@"app_id=%@", _appId);
    
    _viewController = viewController;

    _adfurikunNativeAd = [[AdfurikunNativeAd alloc] init:_appId];
    _adfurikunNativeAd.delegate = self;
}

- (void)nativeAdLoad {
    [_adfurikunNativeAd getNativeAd];
}

- (AdSwitcherNativeAdData *)getAdData {
    return _nativeAdData;
}

- (void)openUrl {
    if (_adfurikunNativeAdInfo) {
        [_adfurikunNativeAdInfo recClick];
        
        SFSafariViewController *safariViewController =
        [[SFSafariViewController alloc] initWithURL:[NSURL URLWithString:_adfurikunNativeAdInfo.link_url]];
        [_viewController presentViewController:safariViewController animated:YES completion:nil];
    }
}

- (void)sendImpression {
    // Adfurikunはインプレッション送信なし
}



#pragma - AdfurikunViewDelegate

-(void)adfurikunViewDidFinishLoad:(AdfurikunView *)view {
    _DLOG();
    if (_loading) {
        [self.bannerAdDelegate bannerAdReceived:self result:YES];
    }
    _loading = NO;
}

-(void)adfurikunViewAdTapped:(AdfurikunView *)view {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}

-(void)adfurikunViewAdFailed:(AdfurikunView *)view {
    _DLOG();
    if (_loading) {
        [self.bannerAdDelegate bannerAdReceived:self result:NO];
    }
    _loading = NO;
}



#pragma - AdfurikunPopupDelegateWrapper

-(void)adfurikunPopup_adfurikunViewDidFinishLoadAdData:(AdfurikunPopupView *)view {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
}

-(void)adfurikunPopup_adfurikunViewDidFinishLoad:(AdfurikunPopupView *)view {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdShown:self];
}

-(void)adfurikunPopup_adfurikunViewAdTapped:(AdfurikunPopupView *)view {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClicked:self];
}

-(void)adfurikunPopup_adfurikunViewAdFailed:(AdfurikunPopupView *)view {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
}

-(void)adfurikunPopup_adfurikunViewAdClose:(AdfurikunPopupView *)view {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
}



#pragma - AdfurikunNativeAdDelegate

-(void)apiDidFinishLoading:(AdfurikunNativeAdInfo *)nativeAdInfo adnetworkKey:(NSString *)adnetworkKey {
    _DLOG(@"adnetworkKey=%@", adnetworkKey);
    
    _adfurikunNativeAdInfo = nativeAdInfo;
    _nativeAdData = [[AdSwitcherNativeAdData alloc] init];
    _nativeAdData.shortText = nativeAdInfo.title;
    _nativeAdData.longText = nativeAdInfo.text;
//    _nativeAdData.imageUrl = nativeAdInfo.img_url;
    _nativeAdData.iconImageUrl = nativeAdInfo.img_url;
    
    [self.nativeAdDelegate nativeAdReceived:self result:YES];
}

-(void)apiDidFailWithError:(int)err adnetworkKey:(NSString *)adnetworkKey {
    _DLOG(@"adnetworkKey=%@, error=%d", adnetworkKey, err);
    [self.nativeAdDelegate nativeAdReceived:self result:NO];
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




@implementation AdfurikunPopupDelegateReceiver

#pragma - AdfurikunPopupViewDelegate

- (void)adfurikunViewDidFinishLoadAdData:(AdfurikunPopupView *)view {
    [self.delegate adfurikunPopup_adfurikunViewDidFinishLoadAdData:view];
}

- (void)adfurikunViewDidFinishLoad:(AdfurikunPopupView *)view {
    [self.delegate adfurikunPopup_adfurikunViewDidFinishLoad:view];
}

- (void)adfurikunViewAdTapped:(AdfurikunPopupView *)view {
    [self.delegate adfurikunPopup_adfurikunViewAdTapped:view];
}

- (void)adfurikunViewAdFailed:(AdfurikunPopupView *)view {
    [self.delegate adfurikunPopup_adfurikunViewAdFailed:view];
}

- (void)adfurikunViewAdClose:(AdfurikunPopupView *)view {
    [self.delegate adfurikunPopup_adfurikunViewAdClose:view];
}

@end
