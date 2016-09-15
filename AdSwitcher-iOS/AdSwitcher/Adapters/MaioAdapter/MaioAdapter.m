//
//  MaioAdapter.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/04.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "MaioAdapter.h"

@implementation MaioAdapter {
    BOOL _isSkipped;
}

@synthesize interstitialAdDelegate;

#pragma - InterstitialAdAdapter

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *,NSString *> *)parameters testMode:(BOOL)testMode {

    NSString *mediaId = [parameters objectForKey:@"media_id"];
    
    _DLOG(@"mediaId=%@", mediaId);
    
    [Maio setAdTestMode:testMode];
    [Maio startWithMediaId:mediaId delegate:self];
}

- (void)interstitialAdLoad {
    if ([Maio canShow]) {
        _DLOG("ready");
        [self.interstitialAdDelegate interstitialAdLoaded:self result:YES];
        
    } else {
        [self adLoad:1];
    }
}

- (void)interstitialAdShow {
    _DLOG();
    [Maio show];
    _isSkipped = NO;
}



#pragma - MaioDelegate

- (void)maioWillStartAd:(NSString *)zoneId {
    _DLOG(@"zoneId:%@", zoneId);
    [self.interstitialAdDelegate interstitialAdShown:self];
}

- (void)maioDidFinishAd:(NSString *)zoneId playtime:(NSInteger)playtime skipped:(BOOL)skipped rewardParam:(NSString *)rewardParam {
    _DLOG(@"zoneId:%@ playtime:%zd skipped:%d rewardParam:%@", zoneId, playtime, skipped, rewardParam);
    _isSkipped = skipped;
}

- (void)maioDidClickAd:(NSString *)zoneId {
    _DLOG(@"zoneId:%@", zoneId);
    [self.interstitialAdDelegate interstitialAdClicked:self];
}

- (void)maioDidFail:(NSString *)zoneId reason:(MaioFailReason)reason {
    _DLOG(@"zoneId:%@ reason:%ld", zoneId, (long)reason);
    [self.interstitialAdDelegate interstitialAdClosed:self result:NO isSkipped:NO];
}

- (void)maioDidCloseAd:(NSString *)zoneId {
    _DLOG(@"zoneId:%@", zoneId);
    [self.interstitialAdDelegate interstitialAdClosed:self result:YES isSkipped:_isSkipped];
}


#pragma - Private methods

- (void)adLoad:(int)count {
    _DLOG("count=%d", count);
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if ([Maio canShow]) {
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
