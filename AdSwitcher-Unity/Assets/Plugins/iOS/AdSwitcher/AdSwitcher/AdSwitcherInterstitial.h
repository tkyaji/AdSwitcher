//
//  InterstitialAdSwitcher.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/13.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdSwitcherConfig.h"
#import "AdSwitcherConfigLoader.h"
#import "InterstitialAdAdapter.h"
#import "AdSelector.h"

@interface AdSwitcherInterstitial : NSObject <InterstitialAdDelegate>

typedef void (^interstitialAdLoadedHandler)(AdConfig *config, BOOL result);
typedef void (^interstitialAdShownHandler)(AdConfig *config);
typedef void (^interstitialAdClosedHandler)(AdConfig *config, BOOL result, BOOL isSkipped);
typedef void (^interstitialAdClickedHandler)(AdConfig *config);

@property (nonatomic) UIViewController *viewController;
@property (nonatomic) BOOL testMode;
@property (nonatomic) AdSwitcherConfig *adSwitcherConfig;

- (instancetype)init __attribute__((unavailable("init is not available")));

- (instancetype)initWithConfigLoader:(UIViewController *)viewController configLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category testMode:(BOOL)testMode;

- (instancetype)initWithConfig:(UIViewController *)viewController config:(AdSwitcherConfig *)adSwitcherConfig
                      testMode:(BOOL)testMode;

- (void)show;
- (BOOL)isLoaded;

- (void)setAdLoadedHandler:(void (^)(AdConfig *config, BOOL result))handler;
- (void)setAdShownHandler:(void (^)(AdConfig *config))handler;
- (void)setAdClosedHandler:(void (^)(AdConfig *config, BOOL result, BOOL isSkipped))handler;
- (void)setAdClickedHandler:(void (^)(AdConfig *config))handler;

@end
