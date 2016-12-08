//
//  AdSwitcherNativeAd.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/10/19.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdSwitcherNativeAd.h"

@implementation AdSwitcherNativeAd {
    NSMutableDictionary<NSString *, NSObject<NativeAdAdapter> *> *_adapterCacheDict;
    NSMutableDictionary<NSString *, AdConfig *> *_adConfigDict;
    AdSelector *_adSelector;
    
    NSObject<NativeAdAdapter> *_selectedAdapter;
    AdConfig *_selectedAdConfig;
    BOOL _loading;
    
    nativeAdReceived _nativeAdReceived;
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

- (void)setAdReceivedHandler:(void (^)(AdConfig *config, BOOL result))handler {
    nativeAdReceived handlerWrapper = ^(AdConfig *config, BOOL result) {
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(config, result);
        });
    };
    _nativeAdReceived = handlerWrapper;
}

- (AdSwitcherNativeAdData *)getAdData {
    if (!_selectedAdapter) {
        return nil;
    }
    return [_selectedAdapter getAdData];
}

- (void)loadImage:(void (^)(UIImage *uiImage))completion {
    AdSwitcherNativeAdData *adData = [self getAdData];
    if (!adData) {
        completion(nil);
        return;
    }
    [self loadToUIImage:adData.imageUrl completion:completion];
}

- (void)loadIconImage:(void (^)(UIImage *uiImage))completion {
    AdSwitcherNativeAdData *adData = [self getAdData];
    if (!adData) {
        completion(nil);
        return;
    }
    [self loadToUIImage:adData.iconImageUrl completion:completion];
}

- (void)loadToUIImage:(NSString *)imageUrl completion:(void (^)(UIImage *))completion {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        UIImage *uiImage = [UIImage imageWithData:[NSData dataWithContentsOfURL: [NSURL URLWithString: imageUrl]]];
        dispatch_async(dispatch_get_main_queue(), ^{
            completion(uiImage);
        });
    });
}

- (BOOL)isLoaded {
    return (_selectedAdapter);
}

- (void)openUrl {
    if (!_selectedAdapter) {
        return;
    }
    [_selectedAdapter openUrl];
}

- (void)sendImpression {
    if (!_selectedAdapter) {
        return;
    }
    [_selectedAdapter sendImpression];
}



#pragma - NativeAdDelegate

- (void)nativeAdReceived:(NSObject<AdAdapter> *)adAdapter result:(BOOL)result {
    NSString *className = NSStringFromClass(adAdapter.class);
    _DLOG(@"%@, result=%d", className, result);
    
    if (!_selectedAdConfig || ![_selectedAdConfig.className isEqualToString:className]) {
        _DLOG(@"class name is invalid. selectedClass=%@", _selectedAdConfig.className);
        return;
    }
    
    _loading = false;
    
    if (_nativeAdReceived) {
        _nativeAdReceived([_adConfigDict objectForKey:className], result);
    }
    
    if (!_selectedAdapter) {
        if (result) {
            _selectedAdapter = (NSObject<NativeAdAdapter> *)adAdapter;
        } else {
            [self selectLoad];
        }
    }
}



#pragma - private methods

- (void)selectLoad {
    _loading = YES;
    
    NSObject<NativeAdAdapter> *nativeAdAdapter = nil;
    while (!nativeAdAdapter) {
        _selectedAdConfig = [_adSelector selectAd];
        if (!_selectedAdConfig) {
            break;
        }
        
        [self initAdapter:_selectedAdConfig];
        nativeAdAdapter = [_adapterCacheDict objectForKey:_selectedAdConfig.className];
    }
    
    if (nativeAdAdapter) {
        _DLOG("%@", NSStringFromClass(nativeAdAdapter.class));
        [nativeAdAdapter nativeAdLoad];
        
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
    
    if (![adAdapter conformsToProtocol:@protocol(NativeAdAdapter)]) {
        _DLOG(@"%@ class is not conform NativeAdAdapter.", adConfig.className);
        return;
    }
    
    NSObject<NativeAdAdapter> *nativeAdAdapter = (NSObject<NativeAdAdapter> *)adAdapter;
    
    @try {
        [nativeAdAdapter setValue:self forKey:@"nativeAdDelegate"];
        
    } @catch (NSException *exception) {
        _DLOG(@"interstitialAdDelegate property is not found. : %@", adConfig.className);
        return;
    }
    
    [nativeAdAdapter nativeAdInitialize:self.viewController parameters:adConfig.parameters testMode:self.testMode];
    
    [_adapterCacheDict setObject:nativeAdAdapter forKey:adConfig.className];
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

- (void)initialize:(AdSwitcherConfig *)adSwitcherConfig {
    
    _DLOG("switchType=%d", (int)adSwitcherConfig.switchType);
    
    self.adSwitcherConfig = adSwitcherConfig;
    
    _adapterCacheDict = [NSMutableDictionary<NSString *, NSObject<NativeAdAdapter> *> new];
    
    _adConfigDict = [NSMutableDictionary new];
    for (AdConfig *adConfig in adSwitcherConfig.adConfigArr) {
        [_adConfigDict setObject:adConfig forKey:adConfig.className];
    }
}

@end
