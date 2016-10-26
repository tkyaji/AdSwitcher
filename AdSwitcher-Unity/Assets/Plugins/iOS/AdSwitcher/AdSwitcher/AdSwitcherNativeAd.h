//
//  AdSwitcherNativeAd.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/10/19.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "UIKit/UIKit.h"
#import "AdSwitcherConfig.h"
#import "AdSwitcherConfigLoader.h"
#import "NativeAdAdapter.h"
#import "AdSelector.h"
#import "AdSwitcherNativeAdData.h"

@interface AdSwitcherNativeAd : NSObject <NativeAdDelegate>

typedef void (^nativeAdReceived)(AdConfig *config, BOOL result);

@property (nonatomic) BOOL testMode;
@property (nonatomic) AdSwitcherConfig *adSwitcherConfig;

- (instancetype)init __attribute__((unavailable("init is not available")));

- (instancetype)initWithConfigLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category;

- (instancetype)initWithConfigLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category testMode:(BOOL)testMode;

- (instancetype)initWithConfig:(AdSwitcherConfig *)adSwitcherConfig;

- (instancetype)initWithConfig:(AdSwitcherConfig *)adSwitcherConfig
                      testMode:(BOOL)testMode;

- (void)load;
- (AdSwitcherNativeAdData *)getAdData;
- (BOOL)isLoaded;
- (void)openUrl;
- (void)sendImpression;
- (void)loadImage:(void (^)(UIImage *uiImage))completion;
- (void)loadIconImage:(void (^)(UIImage *uiImage))completion;

- (void)setAdReceivedHandler:(void (^)(AdConfig *config, BOOL result))handler;

@end
