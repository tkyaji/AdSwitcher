//
//  AMoAdAdapter.m
//  AMoAdAdapter
//
//  Created by tkyaji on 2016/08/15.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AMoAdAdapter.h"

@implementation AMoAdAdapter {
    AMoAdView *_adView;
    NSString *_sid;
    BannerAdSize _adSize;
    BOOL _isLoaded;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;


#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    _sid = [parameters objectForKey:@"sid"];
    _adSize = adSize;
    
    _DLOG(@"sid=%@", _sid);
    
    _adView = [[AMoAdView alloc] initWithFrame:[self toRect:_adSize]];
    _adView.sid = _sid;
    _adView.delegate = self;
}

- (void)bannerAdLoad {
    _DLOG();
    if (_isLoaded) {
        [_adView show];
        [self.bannerAdDelegate bannerAdReceived:self result:YES];
        
    } else {
        [self adLoad:1];
    }
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [parentView addSubview:_adView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_adView hide];
    [_adView removeFromSuperview];
}



#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode {
    
    _sid = [parameters objectForKey:@"sid"];
    
    _DLOG("sid=%@", _sid);

    [AMoAdInterstitial registerAdWithSid:_sid];
    [AMoAdInterstitial setAutoReloadWithSid:_sid autoReload:NO];
}

- (void)interstitialAdLoad {
    _DLOG();
    __block BOOL isFirst = YES;
    [AMoAdInterstitial loadAdWithSid:_sid completion:^(NSString *sid, AMoAdResult result, NSError *err) {
        _DLOG("result=%d", (int)result);
        if (isFirst) {
            [self.interstitialAdDelegate interstitialAdLoaded:self result:(result == AMoAdResultSuccess)];
            isFirst = NO;
        }
    }];
}

- (void)interstitialAdShow {
    _DLOG();
    [AMoAdInterstitial showAdWithSid:_sid completion:^(NSString *sid, AMoAdInterstitialResult result, NSError *err) {
        _DLOG("result=%d", (int)result);
        
        switch (result) {
            case AMoAdInterstitialResultClick:
                [self.interstitialAdDelegate interstitialAdClicked:self];
                [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
                break;
            
            case AMoAdInterstitialResultClose:
            case AMoAdInterstitialResultCloseFromApp:
                [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
                break;
            
            case AMoAdInterstitialResultDuplicated:
            case AMoAdInterstitialResultFailure:
                [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
                break;
        }
        
        [self.interstitialAdDelegate interstitialAdShown:self];
        
    }];
}



#pragma - AMoAdViewDelegate

- (void)AMoAdViewDidReceiveAd:(AMoAdView *)amoadView {
    _DLOG();
    _isLoaded = YES;
}

- (void)AMoAdViewDidFailToReceiveAd:(AMoAdView *)amoadView error:(NSError *)error {
    _DLOG();
    _isLoaded = NO;
}

- (void)AMoAdViewDidReceiveEmptyAd:(AMoAdView *)amoadView {
    _DLOG();
    _isLoaded = NO;
}

- (void)AMoAdViewDidClick:(AMoAdView *)amoadView {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}



#pragma - Private Methods

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

#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if (_isLoaded) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.bannerAdDelegate bannerAdReceived:self result:YES];
            });
        } else if (count == 3) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.bannerAdDelegate bannerAdReceived:self result:NO];
            });
        } else {
            [self adLoad:count + 1];
        }
    });
}


@end
