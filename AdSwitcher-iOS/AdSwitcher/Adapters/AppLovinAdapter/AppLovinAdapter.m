//
//  AppLovinAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/04.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AppLovinAdapter.h"

@implementation AppLovinAdapter {
    ALSdk *_sdk;
    ALAdView *_adView;
    ALInterstitialAd *_interstitialAd;
    NSString *_placement;
    BannerAdSize _adSize;
    BOOL _isSkipped;
    BOOL _loading;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;

#pragma - bannerAdDelegate


- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    NSString *sdkKey = [parameters objectForKey:@"sdk_key"];
    _placement = [parameters objectForKey:@"placement"];
    _DLOG(@"sdkKey:%@, placement=%@", sdkKey, _placement);
    
    _sdk = [ALSdk sharedWithKey:sdkKey];
    [_sdk initializeSdk];
    _adSize = adSize;
}

- (void)bannerAdLoad {
    _DLOG();
    CGRect rect = [self toRect:_adSize];
    ALAdSize *alAdSize = nil;
    if (_adSize == BannerAdSize_300x250) {
        alAdSize = [ALAdSize sizeMRec];
    } else {
        alAdSize = [ALAdSize sizeBanner];
    }
    _adView = [[ALAdView alloc] initWithFrame:rect size:alAdSize sdk:_sdk];
    _adView.adLoadDelegate = self;
    _adView.adDisplayDelegate = self;
    [_adView loadNextAd];
    _loading = YES;
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 5 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_loading) {
                _DLOG("Load Timeout");
                [self.bannerAdDelegate bannerAdReceived:self result:NO];
                _loading = NO;
            }
        });
    });
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [parentView addSubview:_adView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_adView removeFromSuperview];
}


#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    NSString *sdkKey = [parameters objectForKey:@"sdk_key"];
    _placement = [parameters objectForKey:@"placement"];
    _DLOG(@"sdkKey:%@, placement=%@", sdkKey, _placement);
    
    _sdk = [ALSdk sharedWithKey:sdkKey];
    [_sdk initializeSdk];
    _interstitialAd = [[ALInterstitialAd alloc] initWithSdk:_sdk];
    _interstitialAd.adDisplayDelegate = self;
    _interstitialAd.adVideoPlaybackDelegate = self;
}

- (void)interstitialAdLoad {
    if (_interstitialAd.isReadyForDisplay) {
        _DLOG("ready");
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
        
    } else {
        [self adLoad:1];
    }
}

- (void)interstitialAdShow {
    _isSkipped = NO;

    _DLOG();
    if (_placement) {
        [_interstitialAd showOverPlacement:_placement];
    } else {
        [_interstitialAd show];
    }
}


#pragma - ALAdLoadDelegate

- (void)adService:(ALAdService *)adService didLoadAd:(ALAd *)ad {
    _DLOG();
    if (_loading) {
        [self.bannerAdDelegate bannerAdReceived:self result:YES];
    }
    _loading = NO;
}

- (void)adService:(ALAdService *)adService didFailToLoadAdWithError:(int)code {
    _DLOG("error=%d", code);
    if (_loading) {
        [self.bannerAdDelegate bannerAdReceived:self result:NO];
    }
    _loading = NO;
}


#pragma - ALAdDisplayDelegate

- (void)ad:(alnonnull ALAd *)ad wasDisplayedIn:(alnonnull UIView *)view {
    _DLOG();
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdShown:self];
    }
}

- (void)ad:(alnonnull ALAd *)ad wasHiddenIn:(alnonnull UIView *)view {
    _DLOG();
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:_isSkipped];
    }
}

- (void)ad:(alnonnull ALAd *)ad wasClickedIn:(alnonnull UIView *)view {
    _DLOG();
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdClicked:self];
    }
    if (self.bannerAdDelegate) {
        [self.bannerAdDelegate bannerAdClicked:self];
    }
}


#pragma - ALAdVideoPlaybackDelegate

- (void)videoPlaybackBeganInAd:(alnonnull ALAd *)ad {
    _DLOG();
}

- (void)videoPlaybackEndedInAd:(alnonnull ALAd *)ad atPlaybackPercent:(alnonnull NSNumber *)percentPlayed fullyWatched:(BOOL)wasFullyWatched {
    _DLOG();
    _isSkipped = !wasFullyWatched;
}



#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if (_interstitialAd.isReadyForDisplay) {
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
