//
//  AdSwitcherBannerView.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/08/28.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdSwitcherConfig.h"
#import "AdSwitcherConfigLoader.h"
#import "BannerAdAdapter.h"
#import "AdSelector.h"

@interface AdSwitcherBannerView : UIView <BannerAdDelegate>

typedef void (^bannerAdReceived)(AdConfig *config, BOOL result);
typedef void (^bannerAdShownHandler)(AdConfig *config);
typedef void (^bannerAdClicked)(AdConfig *config);

@property (nonatomic) UIViewController *viewController;
@property (nonatomic) BOOL testMode;
@property (nonatomic) AdSwitcherConfig *adSwitcherConfig;
@property (nonatomic) BannerAdSize adSize;

- (instancetype)init __attribute__((unavailable("init is not available")));
- (instancetype)initWithCoder:(NSCoder *)aDecoder __attribute__((unavailable("initWithCoder is not available")));
- (instancetype)initWithFrame:(CGRect)frame __attribute__((unavailable("initWithFrame is not available")));

- (instancetype)initWithConfigLoader:(UIViewController *)viewController configLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category testMode:(BOOL)testMode adSize:(BannerAdSize)adSize;

- (instancetype)initWithConfigLoader:(UIViewController *)viewController configLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category testMode:(BOOL)testMode adSize:(BannerAdSize)adSize position:(CGPoint)position;

- (instancetype)initWithConfig:(UIViewController *)viewController config:(AdSwitcherConfig *)adSwitcherConfig
                      testMode:(BOOL)testMode adSize:(BannerAdSize)adSize;

- (instancetype)initWithConfig:(UIViewController *)viewController config:(AdSwitcherConfig *)adSwitcherConfig
                            testMode:(BOOL)testMode adSize:(BannerAdSize)adSize position:(CGPoint)position;

- (void)load;
- (void)hide;
- (void)switchAd;
- (BOOL)isLoaded;
- (CGSize)getSize;

- (void)setAdReceivedHandler:(void (^)(AdConfig *config, BOOL result))handler;
- (void)setAdShownHandler:(void (^)(AdConfig *config))handler;
- (void)setAdClickedHandler:(void (^)(AdConfig *config))handler;

@end
