//
//  InterstitialAdSwitcher.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/13.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdSwitcherInterstitial.h"

@implementation AdSwitcherInterstitial {
    interstitialAdLoadedHandler _interstitialAdLoadedHandler;
    interstitialAdShownHandler _interstitialAdShownHandler;
    interstitialAdClosedHandler _interstitialAdClosedHandler;
    interstitialAdClickedHandler _interstitialAdClickedHandler;
    
    NSMutableDictionary<NSString *, NSObject<InterstitialAdAdapter> *> *_adapterCacheDict;
    NSMutableDictionary<NSString *, AdConfig *> *_adConfigDict;
    AdSelector *_adSelector;
    
    NSObject<InterstitialAdAdapter> *_selectedAdapter;
    AdConfig *_selectedAdConfig;
    BOOL _loading;
    int _show_called_count;
}

- (instancetype)initWithConfigLoader:(UIViewController *)viewController configLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category {
    return [self initWithConfigLoader:viewController configLoader:configLoader category:category testMode:NO];
}

- (instancetype)initWithConfigLoader:(UIViewController *)viewController configLoader:(AdSwitcherConfigLoader *)configLoader
                            category:(NSString *)category testMode:(BOOL)testMode {
    
    if (self = [super init]) {
        self.viewController = viewController;
        self.testMode = testMode;

        __block AdSwitcherConfigLoader *configLoaderInBlock = configLoader;
        [configLoader addConfigLoadedHandler:^{
            AdSwitcherConfig *adSwitcherConfig = [configLoaderInBlock adSwitchConfig:category];
            [self initialize:adSwitcherConfig];
        }];
    }
    
    return self;

}

- (instancetype)initWithConfig:(UIViewController *)viewController config:(AdSwitcherConfig *)adSwitcherConfig {
    return [self initWithConfig:viewController config:adSwitcherConfig testMode:NO];
}

- (instancetype)initWithConfig:(UIViewController *)viewController config:(AdSwitcherConfig *)adSwitcherConfig
                      testMode:(BOOL)testMode {
    
    if (self = [super init]) {
        self.viewController = viewController;
        self.testMode = testMode;

        [self initialize:adSwitcherConfig];
    }
    
    return self;
}

- (void)show {
    _DLOG("interval : %d/%d", _show_called_count, (int)self.adSwitcherConfig.interval);
    
    if (++_show_called_count < self.adSwitcherConfig.interval) {
        if (_interstitialAdClosedHandler) {
            _interstitialAdClosedHandler(nil, NO, NO);
        }
        return;
    }
    _show_called_count = 0;
    
    if (_selectedAdapter) {
        [_selectedAdapter interstitialAdShow];
        
    } else {
        if (_interstitialAdClosedHandler) {
            _interstitialAdClosedHandler(nil, NO, NO);
        }
        [self load];
    }
}

- (BOOL)isLoaded {
    return _selectedAdapter;
}

- (void)setAdLoadedHandler:(void (^)(AdConfig *config, BOOL result))handler {
    interstitialAdLoadedHandler handlerWrapper = ^(AdConfig *config, BOOL result) {
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(config, result);
        });
    };
    _interstitialAdLoadedHandler = handlerWrapper;
}

- (void)setAdShownHandler:(void (^)(AdConfig *config))handler {
    interstitialAdShownHandler handlerWrapper = ^(AdConfig *config) {
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(config);
        });
    };
    _interstitialAdShownHandler = handlerWrapper;
}

- (void)setAdClosedHandler:(void (^)(AdConfig *config, BOOL result, BOOL isSkipped))handler {
    interstitialAdClosedHandler handlerWrapper = ^(AdConfig *config, BOOL result, BOOL isSkipped) {
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(config, result, isSkipped);
        });
    };
    _interstitialAdClosedHandler = handlerWrapper;
}

- (void)setAdClickedHandler:(void (^)(AdConfig *config))handler {
    interstitialAdClickedHandler handlerWrapper = ^(AdConfig *config) {
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(config);
        });
    };
    _interstitialAdClickedHandler = handlerWrapper;
}



#pragma - AdSwitcher

- (BOOL)initAdapter:(NSObject<AdAdapter> *)adAdapter adConfig:(AdConfig *)adConfig {

    if (![adAdapter conformsToProtocol:@protocol(InterstitialAdAdapter)]) {
        _DLOG(@"%@ class is not conform InterstitialAdAdapter.", adConfig.className);
        return NO;
    }
    
    NSObject<InterstitialAdAdapter> *interstitialAdAdapter = (NSObject<InterstitialAdAdapter> *)adAdapter;
    [interstitialAdAdapter interstitialAdInitialize:self.viewController parameters:adConfig.parameters testMode:self.testMode];
    [interstitialAdAdapter interstitialAdLoad];
    
    @try {
        [interstitialAdAdapter setValue:self forKey:@"interstitialAdDelegate"];
        
    } @catch (NSException *exception) {
        _DLOG(@"property is not found. : %@", adConfig.className);
        return NO;
    }
    
    return YES;
}



#pragma - InterstitialAdDelegate

- (void)interstitialAdLoaded:(NSObject<AdAdapter> *)adAdapter result:(BOOL)result {
    NSString *className = NSStringFromClass(adAdapter.class);
    _DLOG(@"%@, result=%d", className, result);
    
    if (!_selectedAdConfig || ![_selectedAdConfig.className isEqualToString:className]) {
        _DLOG(@"class name is invalid. selectedClass=%@", _selectedAdConfig.className);
        return;
    }
    
    _loading = NO;

    if (_interstitialAdLoadedHandler) {
        _interstitialAdLoadedHandler([_adConfigDict objectForKey:className], result);
    }
    
    if (result) {
        if (!_selectedAdapter) {
            _selectedAdapter = (NSObject<InterstitialAdAdapter> *)adAdapter;
        }
    } else {
        [self selectLoad];
    }
}

- (void)interstitialAdShown:(NSObject<AdAdapter> *)adAdapter {
    NSString *className = NSStringFromClass(adAdapter.class);
    _DLOG(@"%@", className);
    if (_interstitialAdShownHandler) {
        _interstitialAdShownHandler([_adConfigDict objectForKey:className]);
    }
}

- (void)interstitialAdClicked:(NSObject<AdAdapter> *)adAdapter {
    NSString *className = NSStringFromClass(adAdapter.class);
    _DLOG(@"%@", className);
    if (_interstitialAdClickedHandler) {
        _interstitialAdClickedHandler([_adConfigDict objectForKey:className]);
    }
}

- (void)interstitialAdClosed:(NSObject<AdAdapter> *)adAdapter result:(BOOL)result isSkipped:(BOOL)isSkipped {
    NSString *className = NSStringFromClass(adAdapter.class);
    _DLOG(@"%@, result=%d, isSkipped=%d", className, result, isSkipped);
    if (_interstitialAdClosedHandler) {
        _interstitialAdClosedHandler([_adConfigDict objectForKey:className], result, isSkipped);
    }
    
    _selectedAdapter = nil;
    _selectedAdConfig = nil;
    [self load];
}



#pragma - private methods

- (void)initialize:(AdSwitcherConfig *)adSwitcherConfig {

    _DLOG("switchType=%d", (int)adSwitcherConfig.switchType);

    self.adSwitcherConfig = adSwitcherConfig;
    
    _adapterCacheDict = [NSMutableDictionary<NSString *, NSObject<InterstitialAdAdapter> *> new];
    
    _adConfigDict = [NSMutableDictionary new];
    for (AdConfig *adConfig in adSwitcherConfig.adConfigArr) {
        [_adConfigDict setObject:adConfig forKey:adConfig.className];
    }
    
    [self load];
}

- (void)selectLoad {
    _loading = YES;
    
    NSObject<InterstitialAdAdapter> *interstitialAdAdapter = nil;
    while (!interstitialAdAdapter) {
        _selectedAdConfig = [_adSelector selectAd];
        if (!_selectedAdConfig) {
            break;
        }
        
        [self initAdapter:_selectedAdConfig];
        interstitialAdAdapter = [_adapterCacheDict objectForKey:_selectedAdConfig.className];
    }
    
    if (interstitialAdAdapter) {
        _DLOG("%@", NSStringFromClass(interstitialAdAdapter.class));
        [interstitialAdAdapter interstitialAdLoad];
        
    } else {
        _loading = NO;
        _DLOG("It will not be able to display all.");
        dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, 10 * NSEC_PER_SEC);
        dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self load];
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
    
    if (![adAdapter conformsToProtocol:@protocol(InterstitialAdAdapter)]) {
        _DLOG(@"%@ class is not conform InterstitialAdAdapter.", adConfig.className);
        return;
    }
    
    NSObject<InterstitialAdAdapter> *interstitialAdAdapter = (NSObject<InterstitialAdAdapter> *)adAdapter;
    
    @try {
        [interstitialAdAdapter setValue:self forKey:@"interstitialAdDelegate"];
        
    } @catch (NSException *exception) {
        _DLOG(@"interstitialAdDelegate property is not found. : %@", adConfig.className);
        return;
    }
    
    [interstitialAdAdapter interstitialAdInitialize:self.viewController parameters:adConfig.parameters testMode:self.testMode];
    
    [_adapterCacheDict setObject:interstitialAdAdapter forKey:adConfig.className];
}

- (void)load {
    _DLOG();
    if (_loading) {
        _DLOG("Already started to load");
        return;
    }
    _adSelector = [[AdSelector alloc] initWithConfig:self.adSwitcherConfig];
    [self selectLoad];
}


@end
