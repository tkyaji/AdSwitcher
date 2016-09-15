//
//  AppLovinAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/04.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AppLovinAdapter.h"

@implementation AppLovinAdapter {
    ALInterstitialAd *_interstitialAd;
    NSString *_placement;
    BOOL _isSkipped;
}

@synthesize interstitialAdDelegate;

#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {
    NSString *sdkKey = [parameters objectForKey:@"sdk_key"];
    _placement = [parameters objectForKey:@"placement"];
    _DLOG(@"sdkKey:%@, placement=%@", sdkKey, _placement);
    
    NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];
    [infoDict setValue:sdkKey forKey:@"AppLovinSdkKey"];

    [ALSdk initializeSdk];

    _interstitialAd = [[ALInterstitialAd alloc] initWithSdk:[ALSdk shared]];
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



#pragma - ALAdDisplayDelegate

- (void)ad:(alnonnull ALAd *)ad wasDisplayedIn:(alnonnull UIView *)view {
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)ad:(alnonnull ALAd *)ad wasHiddenIn:(alnonnull UIView *)view {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:_isSkipped];
}

- (void)ad:(alnonnull ALAd *)ad wasClickedIn:(alnonnull UIView *)view {
    _DLOG();
    [self.interstitialAdDelegate interstitialAdClicked:self];
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

@end
