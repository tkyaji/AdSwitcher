//
//  AdSwitcherBannerView.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/08/28.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdSwitcherBannerView.h"

@implementation AdSwitcherBannerView {
    bannerAdShownHandler _bannerAdShownHandler;
    bannerAdReceived _bannerAdReceived;
    bannerAdClicked _bannerAdClicked;
    
    NSMutableDictionary<NSString *, NSObject<BannerAdAdapter> *> *_adapterCacheDict;
    NSMutableDictionary<NSString *, AdConfig *> *_adConfigDict;
    AdSelector *_adSelector;
    
    NSObject<BannerAdAdapter> *_selectedAdapter;
    AdConfig *_selectedAdConfig;
    BOOL _loading;
    BOOL _loadCancel;
    BOOL _showing;
    BOOL _autoShow;
}

- (instancetype)initWithConfigLoader:(UIViewController *)viewController configLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category adSize:(BannerAdSize)adSize {
    return [self initWithConfigLoader:viewController configLoader:configLoader category:category adSize:adSize testMode:NO];
}

- (instancetype)initWithConfigLoader:(UIViewController *)viewController configLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category adSize:(BannerAdSize)adSize testMode:(BOOL)testMode {
    
    CGSize cgSize = [self toCGSize:adSize];
    CGRect frame = CGRectMake(0, 0, cgSize.width, cgSize.height);
    if (self = [super initWithFrame:frame]) {
        __block AdSwitcherConfigLoader *configLoaderInBlock = configLoader;
        [configLoader addConfigLoadedHandler:^{
            AdSwitcherConfig *adSwitcherConfig = [configLoaderInBlock adSwitchConfig:category];
            [self initialize:viewController config:adSwitcherConfig adSize:adSize testMode:testMode];
        }];
    }
    
    return self;
}

- (instancetype)initWithConfig:(UIViewController *)viewController config:(AdSwitcherConfig *)adSwitcherConfig
                        adSize:(BannerAdSize)adSize {
    return [self initWithConfig:viewController config:adSwitcherConfig adSize:adSize testMode:NO];
}

- (instancetype)initWithConfig:(UIViewController *)viewController config:(AdSwitcherConfig *)adSwitcherConfig
                        adSize:(BannerAdSize)adSize testMode:(BOOL)testMode {
    
    CGSize cgSize = [self toCGSize:adSize];
    CGRect frame = CGRectMake(0, 0, cgSize.width, cgSize.height);
    if (self = [super initWithFrame:frame]) {
        [self initialize:viewController config:adSwitcherConfig adSize:adSize testMode:testMode];
    }
    
    return self;
}

- (void)load {
    [self load:false];
}

- (void)load:(BOOL)autoShow {
    _DLOG("autoShow=%d", (int)autoShow);
    if (_loading) {
        _DLOG("already started to load.");
        return;
    }
    if (_showing) {
        _DLOG("ad showing.");
        return;
    }
    _autoShow = autoShow;
    _adSelector = [[AdSelector alloc] initWithConfig:self.adSwitcherConfig];
    [self selectLoad];
}

- (void)show {
    if (![self isLoaded]) {
        _DLOG("also not loaded.");
        return;
    }
    if (_showing) {
        _DLOG("already show.");
        return;
    }
    [_selectedAdapter bannerAdShow:self];
    _showing = YES;
}

- (void)hide {
    _DLOG();
    if (_showing) {
        [_selectedAdapter bannerAdHide];
        _selectedAdapter = nil;
        _selectedAdConfig = nil;
        _showing = NO;
        
    }
    if (_loading) {
        _loadCancel = YES;
    }
}

- (void)switchAd {
    _DLOG();
    [self hide];
    [self load];
}

- (BOOL)isLoaded {
    return _selectedAdapter;
}

- (CGSize)getSize {
    return [self toCGSize:self.adSize];
}

- (void)setAdReceivedHandler:(void (^)(AdConfig *config, BOOL result))handler {
    bannerAdReceived handlerWrapper = ^(AdConfig *config, BOOL result) {
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(config, result);
        });
    };
    _bannerAdReceived = handlerWrapper;
}

- (void)setAdShownHandler:(void (^)(AdConfig *config))handler {
    bannerAdShownHandler handlerWrapper = ^(AdConfig *config) {
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(config);
        });
    };
    _bannerAdShownHandler = handlerWrapper;
}

- (void)setAdClickedHandler:(void (^)(AdConfig *config))handler {
    bannerAdClicked handlerWrapper = ^(AdConfig *config) {
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(config);
        });
    };
    _bannerAdClicked = handlerWrapper;
}


#pragma - BannerAdDelegate

- (void)bannerAdReceived:(NSObject<AdAdapter> *)adAdapter result:(BOOL)result {
    NSString *className = NSStringFromClass(adAdapter.class);
    _DLOG(@"%@, result=%d", className, result);
    
    if (!_selectedAdConfig || ![_selectedAdConfig.className isEqualToString:className]) {
        _DLOG(@"class name is invalid. selectedClass=%@", _selectedAdConfig.className);
        return;
    }
    
    _loading = false;
    
    if (!_selectedAdapter && result) {
        _selectedAdapter = (NSObject<BannerAdAdapter> *)adAdapter;
    }
    
    if (_bannerAdReceived) {
        _bannerAdReceived([_adConfigDict objectForKey:className], result);
    }
    
    if (result) {
        if (_autoShow) {
            [self show];
        }
    } else {
        [self selectLoad];
    }
}

- (void)bannerAdShown:(NSObject<AdAdapter> *)adAdapter {
    NSString *className = NSStringFromClass(adAdapter.class);
    _DLOG(@"%@", className);
    if (_bannerAdShownHandler) {
        _bannerAdShownHandler([_adConfigDict objectForKey:className]);
    }
}

- (void)bannerAdClicked:(NSObject<AdAdapter> *)adAdapter {
    NSString *className = NSStringFromClass(adAdapter.class);
    _DLOG(@"%@", className);
    if (_bannerAdClicked) {
        _bannerAdClicked([_adConfigDict objectForKey:className]);
    }
}


#pragma - UIView

- (void)removeFromSuperview {
    if (_selectedAdapter) {
        [_selectedAdapter bannerAdHide];
    }
    [super removeFromSuperview];
}


#pragma - private methods

- (void)initialize:(UIViewController *)viewController config:(AdSwitcherConfig *)adSwitcherConfig
            adSize:(BannerAdSize)adSize testMode:(BOOL)testMode {
    
    _DLOG("testMode=%d, adSize=%d, switchType=%d", testMode, (int)adSize, (int)adSwitcherConfig.switchType);

    self.viewController = viewController;
    self.adSwitcherConfig = adSwitcherConfig;
    self.testMode = testMode;
    self.adSize = adSize;
    
    _adapterCacheDict = [NSMutableDictionary<NSString *, NSObject<BannerAdAdapter> *> new];
    
    _adConfigDict = [NSMutableDictionary new];
    for (AdConfig *adConfig in adSwitcherConfig.adConfigArr) {
        [_adConfigDict setObject:adConfig forKey:adConfig.className];
    }
}

- (void)selectLoad {
    if (_loadCancel) {
        _loading = NO;
        _loadCancel = NO;
        return;
    }
    _loading = YES;
    
    NSObject<BannerAdAdapter> *bannerAdAdapter = nil;
    while (!bannerAdAdapter) {
        _selectedAdConfig = [_adSelector selectAd];
        if (!_selectedAdConfig) {
            break;
        }
        
        [self initAdapter:_selectedAdConfig];
        bannerAdAdapter = [_adapterCacheDict objectForKey:_selectedAdConfig.className];
    }
    
    if (bannerAdAdapter) {
        _DLOG("%@", NSStringFromClass(bannerAdAdapter.class));
        [bannerAdAdapter bannerAdLoad];
        
    } else {
        _DLOG("It will not be able to display all.");
        dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 5 * NSEC_PER_SEC);
        dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                _DLOG("retry load.");
                _adSelector = [[AdSelector alloc] initWithConfig:self.adSwitcherConfig];
                [self selectLoad];
            });
        });
    }
}

- (void)initAdapter:(AdConfig *)adConfig {
    
    if ([_adapterCacheDict objectForKey:adConfig.className]) {
        return;
    }
    
    Class adAdapterClass = NSClassFromString(adConfig.className);
    if (!adAdapterClass) {
        _DLOG("%@ class is not found.", adConfig.className);
        return;
    }
    
    NSObject<AdAdapter> *adAdapter = [adAdapterClass new];
    
    if (![adAdapter conformsToProtocol:@protocol(BannerAdAdapter)]) {
        _DLOG(@"%@ class is not conform BannerAdAdapter.", adConfig.className);
        return;
    }
    
    NSObject<BannerAdAdapter> *bannerAdAdapter = (NSObject<BannerAdAdapter> *)adAdapter;
    
    @try {
        [bannerAdAdapter setValue:self forKey:@"bannerAdDelegate"];
        
    } @catch (NSException *exception) {
        _DLOG(@"bannerAdDelegate property is not found. : %@", adConfig.className);
        return;
    }
    
    [bannerAdAdapter bannerAdInitialize:self.viewController parameters:adConfig.parameters testMode:self.testMode adSize:self.adSize];
    
    [_adapterCacheDict setObject:bannerAdAdapter forKey:adConfig.className];
}

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
