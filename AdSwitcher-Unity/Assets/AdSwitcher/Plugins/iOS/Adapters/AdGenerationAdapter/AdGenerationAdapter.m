//
//  AdGenerationAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/21.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdGenerationAdapter.h"

@implementation AdGenerationAdapter {
    UIViewController *_viewController;
    ADGManagerViewController *_adgViewController;
    NSDictionary *_adgParams;
    UIView *_adgView;
    BannerAdSize _adSize;
    BOOL _testMode;
}

@synthesize bannerAdDelegate;


#pragma - BannerAdAdapter

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize {
    
    NSString *locationId = [parameters objectForKey:@"location_id"];
    _DLOG(@"locationId:%@", locationId);
    
    _viewController = viewController;
    _testMode = testMode;
    _adgParams = @{@"locationid": locationId,
                   @"adtype": @([self toADGAdSize:adSize]),
                   };
}

- (void)bannerAdLoad {
    _DLOG();
    _adgView = [UIView new];
    _adgViewController = [[ADGManagerViewController alloc] initWithAdParams:_adgParams adView:_adgView];
    _adgViewController.delegate = self;
    [_adgViewController setFillerRetry:NO];
    [_adgViewController setEnableTestMode:_testMode];
    [_adgViewController loadRequest];
}

- (void)bannerAdShow:(UIView *)parentView {
    _DLOG();
    [_viewController addChildViewController:_adgViewController];
    [_adgViewController didMoveToParentViewController:_viewController];
    [parentView addSubview:_adgViewController.view];
    [self.bannerAdDelegate bannerAdShown:self];
}

- (void)bannerAdHide {
    _DLOG();
    [_adgView removeFromSuperview];
    [_adgViewController.view removeFromSuperview];
    [_adgViewController removeFromParentViewController];
    
    _adgView = nil;
    _adgViewController = nil;
}



#pragma - ADGManagerViewControllerDelegate

- (void)ADGManagerViewControllerReceiveAd:(ADGManagerViewController *)adgManagerViewController {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:YES];
}

- (void)ADGManagerViewControllerReceiveAd:(ADGManagerViewController *)adgManagerViewController mediationNativeAd:(id)mediationNativeAd {
}

- (void)ADGManagerViewControllerFailedToReceiveAd:(ADGManagerViewController *)adgManagerViewController code:(kADGErrorCode)code {
    _DLOG();
    [self.bannerAdDelegate bannerAdReceived:self result:NO];
}

- (void)ADGManagerViewControllerOpenUrl:(ADGManagerViewController *)adgManagerViewController {
    _DLOG();
    [self.bannerAdDelegate bannerAdClicked:self];
}

- (void)ADGManagerViewControllerFinishImpression:(ADGManagerViewController *)adgManagerViewController {
}

- (void)ADGManagerViewControllerFailInImpression:(ADGManagerViewController *)adgManagerViewController {
}

- (void)ADGManagerViewControllerCompleteRewardAd {
}



#pragma - private methods

- (ADGAdType)toADGAdSize:(BannerAdSize)adSize {
    switch (adSize) {
        case BannerAdSize_320x50:
            return kADG_AdType_Sp;
            
        case BannerAdSize_320x100:
            return kADG_AdType_Large;
            
        case BannerAdSize_300x250:
            return kADG_AdType_Rect;
    }
    return kADG_AdType_Sp;
}

@end
