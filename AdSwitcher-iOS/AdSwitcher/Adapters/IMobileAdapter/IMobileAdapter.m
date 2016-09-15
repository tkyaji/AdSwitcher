//
//  IMobileAdapter.m
//  IMobileAdapter
//
//  Created by tkyaji on 2016/08/09.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "IMobileAdapter.h"

@implementation IMobileAdapter {
    NSString *_spotId;
    UIViewController *_viewController;
    BannerAdSize _adSize;
    UIView *_adView;
    BOOL _isLoaded;
}

@synthesize bannerAdDelegate;
@synthesize interstitialAdDelegate;


# pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    _viewController = viewController;
    _adSize = adSize;
    
    NSString *publiisherId = [parameters objectForKey:@"publisher_id"];
    NSString *mediaId = [parameters objectForKey:@"media_id"];
    _spotId = [parameters objectForKey:@"spot_id"];
    
    _DLOG(@"publisher_id:%@, media:id:%@, spot_id:%@", publiisherId, mediaId, _spotId);
    
    [ImobileSdkAds setTestMode:testMode];
    [ImobileSdkAds registerWithPublisherID:publiisherId MediaID:mediaId SpotID:_spotId];
    [ImobileSdkAds setSpotDelegate:_spotId delegate:self];
}

- (void)bannerAdLoad {
    _DLOG();
    [ImobileSdkAds startBySpotID:_spotId];
    
    CGSize imobileAdSize = [self toCGSize:_adSize];
    _adView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, imobileAdSize.width, imobileAdSize.height)];
    [ImobileSdkAds showBySpotID:_spotId View:_adView];
    _adView.hidden = YES;
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    _adView.hidden = NO;
    [parentView addSubview:_adView];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_adView removeFromSuperview];
    [ImobileSdkAds stopBySpotID:_spotId];
}


# pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode {
    
    _viewController = viewController;
    
    NSString *publiisherId = [parameters objectForKey:@"publisher_id"];
    NSString *mediaId = [parameters objectForKey:@"media_id"];
    _spotId = [parameters objectForKey:@"spot_id"];

    _DLOG("publisher_id=%@, media_id=%@, spot_id=%@", publiisherId, mediaId, _spotId);
    
    [ImobileSdkAds setTestMode:testMode];
    [ImobileSdkAds registerWithPublisherID:publiisherId MediaID:mediaId SpotID:_spotId];
    [ImobileSdkAds setSpotDelegate:_spotId delegate:self];
}

- (void)interstitialAdLoad {
    _DLOG();
    [ImobileSdkAds startBySpotID:_spotId];
}

- (void)interstitialAdShow {
    _DLOG();
    [ImobileSdkAds showBySpotID:_spotId];
    [self.interstitialAdDelegate interstitialAdShown:self];
}


#pragma - IMobileSdkAdsDelegate

- (void)imobileSdkAdsSpot:(NSString *)spotId didReadyWithValue:(ImobileSdkAdsReadyResult)value {
    _DLOG();
    
    if (self.bannerAdDelegate) {
        [bannerAdDelegate bannerAdReceived:self result:YES];
    }
    
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
    }
}

- (void)imobileSdkAdsSpot:(NSString *)spotId didFailWithValue:(ImobileSdkAdsFailResult)value {
    _DLOG(@"result:%d", (int)value);

    [ImobileSdkAds stopBySpotID:_spotId];
    if (self.bannerAdDelegate) {
        [self.bannerAdDelegate bannerAdReceived:self result:NO];
    }
    
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
    }
}

- (void)imobileSdkAdsSpotDidClick:(NSString *)spotId {
    _DLOG();
    if (self.bannerAdDelegate) {
        [self.bannerAdDelegate bannerAdClicked:self];
    }
    
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdClicked:self];
    }
}

- (void)imobileSdkAdsSpotDidClose:(NSString *)spotId {
    _DLOG();
    [ImobileSdkAds stopBySpotID:_spotId];
    if (self.interstitialAdDelegate) {
        [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
    }
}


#pragma - private methods

- (CGSize)toCGSize:(BannerAdSize)adSize {
    switch (adSize) {
        case BannerAdSize_320x50:
            return CGSizeMake(320, 50);
            
        case BannerAdSize_320x100:
            return CGSizeMake(320, 100);
            
        case BannerAdSize_300x250:
            return CGSizeMake(300, 250);
    }
    return CGSizeMake(320, 50);
}


@end
