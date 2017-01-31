//
//  AdColonyAdapter.m
//  AdColonyAdapter
//
//  Created by tkyaji on 2016/08/09.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdColonyAdapter.h"

@implementation AdColonyAdapter {
    NSString *_zoneId;
    UIViewController *_appViewController;
    AdColonyInterstitial *_adColonyInterstitial;
    
    BOOL _isInitialized;
    BOOL _isLoading;
    BOOL _isLoaded;
}

@synthesize interstitialAdDelegate;

# pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    
    NSString *appId = [parameters objectForKey:@"app_id"];
    _zoneId = [parameters objectForKey:@"zone_id"];
    
    _DLOG(@"app_id:%@, zone_id:%@", appId, _zoneId);
    
    [AdColony configureWithAppID:appId zoneIDs:@[_zoneId] options:nil completion:^(NSArray<AdColonyZone *> *zones) {
        _DLOG("%@", appId);
        _isInitialized = YES;
    }];
}

- (void)interstitialAdLoad {
    if (_isInitialized) {
        [self requestLoad];
        
    } else {
        dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
        dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [self requestLoad];
        });
    }
}

- (void)requestLoad {
    _adColonyInterstitial = nil;
    _isLoading = YES;
    _isLoaded = NO;
    
    [AdColony requestInterstitialInZone:_zoneId options:nil success:^(AdColonyInterstitial *ad) {
        _DLOG("success");
        _adColonyInterstitial = ad;
        _isLoading = NO;
        _isLoaded = YES;
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
        
        [ad setOpen:^{
            _DLOG("open");
            [self.interstitialAdDelegate interstitialAdShown:self];
        }];
        [ad setClick:^{
            _DLOG("click");
            [self.interstitialAdDelegate interstitialAdClicked:self];
        }];
        [ad setClose:^{
            _DLOG("close");
            [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:NO];
        }];
        
    } failure:^(AdColonyAdRequestError *error) {
        _DLOG("failure: %@", error);
        [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
        _isLoading = NO;
    }];
    
    [self adLoad:1];
}

- (void)interstitialAdShow {
    if (_isLoaded && [_adColonyInterstitial showWithPresentingViewController:_appViewController]) {
        _DLOG("show:1");
    } else {
        _DLOG("show:0");
        [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
    }
}


#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if (!_isLoading) {
            return;
            
        } else if (count == 5) {
            _isLoading = NO;
            _DLOG("load timeout");
            [self.interstitialAdDelegate interstitialAdLoaded:self result:NO];
        } else {
            [self adLoad:count + 1];
        }
    });
}


@end
