//
//  AdSwitcherPlugin.m
//  Unity-iPhone
//
//  Created by tkyaji on 2016/09/17.
//
//

#import <Foundation/Foundation.h>

#import "AdSwitcherConfigLoader.h"
#import "AdSwitcherInterstitial.h"
#import "AdSwitcherBannerView.h"
#import "AdSwitcherNativeAd.h"


extern "C" {
    
    static inline const char *__copyString(NSString *str) {
        const char *adNameStr = [str UTF8String];
        char* retStr = (char*)malloc(strlen(adNameStr) + 1);
        strcpy(retStr, adNameStr);
        return retStr;
    }
    
    static inline AdConfig *__adConfigFromJson(NSString *adConfigJsonStr) {
        NSData *jsonData = [adConfigJsonStr dataUsingEncoding:NSUTF8StringEncoding];
        NSError *error = nil;
        NSMutableDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&error];
        if (error) {
            _ELOG("%@", error);
            return nil;
        }
        
        AdConfig *adConfig = [AdConfig new];
        adConfig.adName = [dict objectForKey:@"adName"];
        adConfig.className = [dict objectForKey:@"className"];
        adConfig.ratio = ((NSNumber *)[dict objectForKey:@"ratio"]).integerValue;
        
        NSMutableDictionary<NSString *, NSString *> *parameterDict = [NSMutableDictionary<NSString *, NSString *> new];
        for (NSString *paramStr in [dict objectForKey:@"parameters"]) {
            NSArray<NSString *> *paramArr = [paramStr componentsSeparatedByString:@"\t"];
            [parameterDict setObject:[paramArr objectAtIndex:1] forKey:[paramArr objectAtIndex:0]];
        }
        adConfig.parameters = parameterDict;
        
        return adConfig;
    }
    
    static inline AdSwitcherConfig *__adSwitcherConfigFromJson(NSString *adSwitcherConfigJsonStr) {
        NSData *jsonData = [adSwitcherConfigJsonStr dataUsingEncoding:NSUTF8StringEncoding];
        NSError *error = nil;
        NSMutableDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&error];
        if (error) {
            _ELOG("%@", error);
            return nil;
        }
        
        AdSwitcherConfig *adSwitcherConfig = [AdSwitcherConfig new];
        adSwitcherConfig.category = [dict objectForKey:@"category"];
        adSwitcherConfig.switchType = (AdSwitchType)((NSNumber *)[dict objectForKey:@"switchType"]).integerValue;
        adSwitcherConfig.interval = ((NSNumber *)[dict objectForKey:@"interval"]).integerValue;
        
        NSMutableArray<AdConfig *> *adConfigArr = [NSMutableArray<AdConfig *> new];
        NSArray<NSString *> *adConfigStrArr = [dict objectForKey:@"adConfigList"];
        for (NSString *adConfigStr in adConfigStrArr) {
            AdConfig *adConfig = __adConfigFromJson(adConfigStr);
            [adConfigArr addObject:adConfig];
        }
        adSwitcherConfig.adConfigArr = adConfigArr;
        
        return adSwitcherConfig;
    }
    
    static inline NSString *__adConfigToJson(AdConfig *adConfig) {
        NSMutableDictionary *dict = [NSMutableDictionary new];
        
        if (adConfig.adName) {
            [dict setObject:adConfig.adName forKey:@"adName"];
        }
        if (adConfig.className) {
            [dict setObject:adConfig.className forKey:@"className"];
        }
        [dict setObject:@(adConfig.ratio) forKey:@"ratio"];
        
        NSMutableArray *arr = [NSMutableArray new];
        for (NSString *key in adConfig.parameters) {
            NSString *val = [adConfig.parameters objectForKey:key];
            NSString *keyVal = [NSString stringWithFormat:@"%@\t%@", key, val];
            [arr addObject:keyVal];
        }
        [dict setObject:arr forKey:@"parameters"];
        
        NSError *error = nil;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:0 error:&error];
        if (error) {
            _ELOG("%@", error);
            return nil;
        }
        return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    
    static inline CGSize __toBannerAdCGSize(BannerAdSize adSize) {
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
    
    static inline CGRect __toBannerAdFrame(BannerAdSize bannerAdSize, BannerAdAlign bannerAdAlign, BannerAdMargin bannerAdMargin, CGFloat scale) {
        CGSize parentFrameSize = UnityGetGLView().frame.size;
        CGSize adViewSize = __toBannerAdCGSize(bannerAdSize);
        adViewSize.width *= scale;
        adViewSize.height *= scale;
        
        switch (bannerAdAlign) {
            case BannerAdAlignTopLeft:
                return CGRectMake(bannerAdMargin.left,
                                  bannerAdMargin.top,
                                  adViewSize.width, adViewSize.height);
                
            case BannerAdAlignTopRight:
                return CGRectMake(parentFrameSize.width - adViewSize.width - bannerAdMargin.right,
                                  bannerAdMargin.top,
                                  adViewSize.width, adViewSize.height);
                
            case BannerAdAlignTopCenter:
                return CGRectMake((parentFrameSize.width - adViewSize.width) / 2,
                                  bannerAdMargin.top,
                                  adViewSize.width, adViewSize.height);
                
            case BannerAdAlignBottomLeft:
                return CGRectMake(bannerAdMargin.left,
                                  parentFrameSize.height - adViewSize.height - bannerAdMargin.bottom,
                                  adViewSize.width, adViewSize.height);
                
            case BannerAdAlignBottomRight:
                return CGRectMake(parentFrameSize.width - adViewSize.width - bannerAdMargin.right,
                                  parentFrameSize.height - adViewSize.height - bannerAdMargin.bottom,
                                  adViewSize.width, adViewSize.height);
                
            case BannerAdAlignBottomCenter:
                return CGRectMake((parentFrameSize.width - adViewSize.width) / 2,
                                  parentFrameSize.height - adViewSize.height - bannerAdMargin.bottom,
                                  adViewSize.width, adViewSize.height);
        }
    }
    
    
    // AdSwitcherConfigLoader
    
    AdSwitcherConfigLoader* _AdSwitcherConfigLoader_sharedInstance() {
        return [AdSwitcherConfigLoader sharedInstance];
    }
    
    void _AdSwitcherConfigLoader_startLoad(AdSwitcherConfigLoader *configLoader, const char *urlStr) {
        NSURL *url = [NSURL URLWithString:[NSString stringWithUTF8String:urlStr]];
        [configLoader startLoad:url];
    }
    
    void _AdSwitcherConfigLoader_loadJson(AdSwitcherConfigLoader *configLoader, const char *jsonText) {
        NSData *jsonData = [[NSString stringWithUTF8String:jsonText] dataUsingEncoding:NSUTF8StringEncoding];
        [configLoader loadJson:jsonData];
    }
    
    
    // AdSwitcherBannerView
    
    AdSwitcherBannerView *_AdSwitcherBannerView_new(AdSwitcherConfigLoader *configLoader, const char *category,
                                                    int adSize, int adAlign, float *adMarginArr, bool isSizeToFit, bool testMode) {
        
        BannerAdSize bannerAdSize = (BannerAdSize)adSize;
        BannerAdAlign bannerAdAlign = (BannerAdAlign)adAlign;
        BannerAdMargin bannerAdMargin = BannerAdMarginMake(adMarginArr[0], adMarginArr[1], adMarginArr[2], adMarginArr[3]);
        
        AdSwitcherBannerView *bannerView =
        [[AdSwitcherBannerView alloc] initWithConfigLoader:UnityGetGLViewController()
                                              configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                  category:[NSString stringWithUTF8String:category]
                                                    adSize:bannerAdSize
                                                  testMode:testMode];
        bannerView.frame = __toBannerAdFrame(bannerAdSize, bannerAdAlign, bannerAdMargin, 1);
        
        if (isSizeToFit) {
            CGFloat scale = UnityGetGLView().frame.size.width / bannerView.frame.size.width;
            bannerView.transform = CGAffineTransformMakeScale(scale, scale);
            bannerView.frame = __toBannerAdFrame(bannerAdSize, bannerAdAlign, bannerAdMargin, scale);
        }
        
        CFRetain((CFTypeRef)bannerView);
        return bannerView;
    }
    
    AdSwitcherBannerView *_AdSwitcherBannerView_new_config(const char *adSwitcherConfigJsonStr,
                                                           int adSize, int adAlign, float *adMarginArr, bool isSizeToFit, bool testMode) {
        
        AdSwitcherConfig *adSwitcherConfig = __adSwitcherConfigFromJson([NSString stringWithUTF8String:adSwitcherConfigJsonStr]);
        
        BannerAdSize bannerAdSize = (BannerAdSize)adSize;
        BannerAdAlign bannerAdAlign = (BannerAdAlign)adAlign;
        BannerAdMargin bannerAdMargin = BannerAdMarginMake(adMarginArr[0], adMarginArr[1], adMarginArr[2], adMarginArr[3]);
        
        AdSwitcherBannerView *bannerView = [[AdSwitcherBannerView alloc] initWithConfig:UnityGetGLViewController()
                                                                                 config:adSwitcherConfig
                                                                                 adSize:bannerAdSize
                                                                               testMode:testMode];
        bannerView.frame = __toBannerAdFrame(bannerAdSize, bannerAdAlign, bannerAdMargin, 1);
        
        if (isSizeToFit) {
            CGFloat scale = UnityGetGLView().frame.size.width / bannerView.frame.size.width;
            bannerView.transform = CGAffineTransformMakeScale(scale, scale);
            bannerView.frame = __toBannerAdFrame(bannerAdSize, bannerAdAlign, bannerAdMargin, scale);
        }
        
        CFRetain((CFTypeRef)bannerView);
        return bannerView;
    }
    
    void _AdSwitcherBannerView_release(AdSwitcherBannerView *bannerView) {
        CFRelease((CFTypeRef)bannerView);
    }
    
    typedef void (*cs_bannerAdReceivedHandler)(void *, const char *adConfigJson, bool result);
    typedef void (*cs_bannerAdShownHandler)(void *, const char *adConfigJson);
    typedef void (*cs_bannerAdClickedHandler)(void *, const char *adConfigJson);
    
    void _AdSwitcherBannerView_setPosition(AdSwitcherBannerView *bannerView, int adAlign, float *adMarginArr) {
        BannerAdAlign bannerAdAlign = (BannerAdAlign)adAlign;
        BannerAdMargin bannerAdMargin = BannerAdMarginMake(adMarginArr[0], adMarginArr[1], adMarginArr[2], adMarginArr[3]);
        bannerView.frame = __toBannerAdFrame(bannerView.adSize, bannerAdAlign, bannerAdMargin, 1);
    }
    
    void _AdSwitcherBannerView_load(AdSwitcherBannerView *bannerView, bool autoShow) {
        [bannerView load:autoShow];
        if (autoShow && !bannerView.superview) {
            [UnityGetGLView() addSubview:bannerView];
            [bannerView show];
        }
    }
    
    void _AdSwitcherBannerView_show(AdSwitcherBannerView *bannerView) {
        if (bannerView.superview) {
            return;
        }
        if (![bannerView isLoaded]) {
            return;
        }
        [UnityGetGLView() addSubview:bannerView];
        [bannerView show];
    }
    
    void _AdSwitcherBannerView_hide(AdSwitcherBannerView *bannerView) {
        [bannerView hide];
        if (bannerView.superview) {
            [bannerView removeFromSuperview];
        }
    }
    
    void _AdSwitcherBannerView_switchAd(AdSwitcherBannerView *bannerView) {
        if (!bannerView.superview) {
            return;
        }
        [bannerView switchAd];
    }
    
    bool _AdSwitcherBannerView_isLoaded(AdSwitcherBannerView *bannerView) {
        return [bannerView isLoaded];
    }
    
    float _AdSwitcherBannerView_getWidth(AdSwitcherBannerView *bannerView) {
        CGSize size = [bannerView getSize];
        return size.width;
    }
    
    float _AdSwitcherBannerView_getHeight(AdSwitcherBannerView *bannerView) {
        CGSize size = [bannerView getSize];
        return size.height;
    }
    
    float _AdSwitcherBannerView_getScreenWidth(AdSwitcherBannerView *bannerView) {
        CGSize screenSize = [UIScreen mainScreen].bounds.size;
        return screenSize.width;
    }
    
    float _AdSwitcherBannerView_getScreenHeight(AdSwitcherBannerView *bannerView) {
        CGSize screenSize = [UIScreen mainScreen].bounds.size;
        return screenSize.height;
    }
    
    void _AdSwitcherBannerView_setAdReceivedHandler(AdSwitcherBannerView *bannerView,
                                                    void *cs_instance,
                                                    cs_bannerAdReceivedHandler cs_handler) {
        [bannerView setAdReceivedHandler:^(AdConfig *config, BOOL result) {
            NSString *adConfigJson = __adConfigToJson(config);
            cs_handler(cs_instance, [adConfigJson UTF8String], result);
        }];
    }
    
    void _AdSwitcherBannerView_setAdShownHandler(AdSwitcherBannerView *bannerView,
                                                 void *cs_instance,
                                                 cs_bannerAdShownHandler cs_handler) {
        [bannerView setAdShownHandler:^(AdConfig *config) {
            NSString *adConfigJson = __adConfigToJson(config);
            cs_handler(cs_instance, [adConfigJson UTF8String]);
        }];
    }
    
    void _AdSwitcherBannerView_setAdClickedHandler(AdSwitcherBannerView *bannerView,
                                                   void *cs_instance,
                                                   cs_bannerAdClickedHandler cs_handler) {
        [bannerView setAdClickedHandler:^(AdConfig *config) {
            NSString *adConfigJson = __adConfigToJson(config);
            cs_handler(cs_instance, [adConfigJson UTF8String]);
        }];
    }
    
    
    
    // AdSwitcherInterstitial
    
    AdSwitcherInterstitial *_AdSwitcherInterstitial_new(AdSwitcherConfigLoader *configLoader, const char *category, bool testMode) {
        AdSwitcherInterstitial *interstitial =
        [[AdSwitcherInterstitial alloc] initWithConfigLoader:UnityGetGLViewController()
                                                configLoader:configLoader
                                                    category:[NSString stringWithUTF8String:category]
                                                    testMode:testMode];
        CFRetain((CFTypeRef)interstitial);
        return interstitial;
    }
    
    AdSwitcherInterstitial *_AdSwitcherInterstitial_new_config(const char *adSwitcherConfigJsonStr, bool testMode) {
        AdSwitcherConfig *adSwicherConfig = __adSwitcherConfigFromJson([NSString stringWithUTF8String:adSwitcherConfigJsonStr]);
        AdSwitcherInterstitial *interstitial =
        [[AdSwitcherInterstitial alloc] initWithConfig:UnityGetGLViewController() config:adSwicherConfig testMode:testMode];
        CFRetain((CFTypeRef)interstitial);
        return interstitial;
    }
    
    void _AdSwitcherInterstitial_release(AdSwitcherInterstitial *interstitial) {
        CFRelease((CFTypeRef)interstitial);
    }
    
    typedef void (*cs_interstitialAdLoadedHandler)(void *, const char *adConfigJson, bool result);
    typedef void (*cs_interstitialAdShownHandler)(void *, const char *adConfigJson);
    typedef void (*cs_interstitialAdClosedHandler)(void *, const char *adConfigJson, bool result, bool isSkipped);
    typedef void (*cs_interstitialAdClickedHandler)(void *, const char *adConfigJson);
    
    void _AdSwitcherInterstitial_show(AdSwitcherInterstitial *interstitial) {
        [interstitial show];
    }
    
    bool _AdSwitcherInterstitial_isLoaded(AdSwitcherInterstitial *interstitial) {
        return [interstitial isLoaded];
    }
    
    void _AdSwitcherInterstitial_setAdLoadedHandler(AdSwitcherInterstitial *interstitial,
                                                    void *cs_instance,
                                                    cs_interstitialAdLoadedHandler cs_handler) {
        [interstitial setAdLoadedHandler:^(AdConfig *config, BOOL result) {
            NSString *adConfigJson = __adConfigToJson(config);
            cs_handler(cs_instance, [adConfigJson UTF8String], result);
        }];
    }
    
    void _AdSwitcherInterstitial_setAdShownHandler(AdSwitcherInterstitial *interstitial,
                                                   void *cs_instance,
                                                   cs_interstitialAdShownHandler cs_handler) {
        [interstitial setAdShownHandler:^(AdConfig *config) {
            NSString *adConfigJson = __adConfigToJson(config);
            cs_handler(cs_instance, [adConfigJson UTF8String]);
        }];
    }
    
    void _AdSwitcherInterstitial_setAdClosedHandler(AdSwitcherInterstitial *interstitial,
                                                    void *cs_instance,
                                                    cs_interstitialAdClosedHandler cs_handler) {
        [interstitial setAdClosedHandler:^(AdConfig *config, BOOL result, BOOL isSkipped) {
            NSString *adConfigJson = __adConfigToJson(config);
            cs_handler(cs_instance, [adConfigJson UTF8String], result, isSkipped);
        }];
    }
    
    void _AdSwitcherInterstitial_setAdClickedHandler(AdSwitcherInterstitial *interstitial,
                                                     void *cs_instance,
                                                     cs_interstitialAdClickedHandler cs_handler) {
        [interstitial setAdClickedHandler:^(AdConfig *config) {
            NSString *adConfigJson = __adConfigToJson(config);
            cs_handler(cs_instance, [adConfigJson UTF8String]);
        }];
    }
    
    
    
    // AdSwitcherNativeAd
    
    typedef void (*cs_nativeAdReceivedHandler)(void *, const char *adConfigJson, bool result);
    
    AdSwitcherNativeAd *_AdSwitcherNativeAd_new(AdSwitcherConfigLoader *configLoader, const char *category, bool testMode) {
        AdSwitcherNativeAd *nativeAd = [[AdSwitcherNativeAd alloc] initWithConfigLoader:configLoader
                                                                               category:[NSString stringWithUTF8String:category]
                                                                               testMode:testMode];
        CFRetain((CFTypeRef)nativeAd);
        return nativeAd;
    }
    
    AdSwitcherNativeAd *_AdSwitcherNativeAd_new_config(const char *adSwitcherConfigJsonStr, bool testMode) {
        AdSwitcherConfig *adSwicherConfig = __adSwitcherConfigFromJson([NSString stringWithUTF8String:adSwitcherConfigJsonStr]);
        AdSwitcherNativeAd *nativeAd = [[AdSwitcherNativeAd alloc] initWithConfig:adSwicherConfig testMode:testMode];
        CFRetain((CFTypeRef)nativeAd);
        return nativeAd;
    }
    
    void _AdSwitcherNativeAd_release(AdSwitcherNativeAd *nativeAd) {
        CFRelease((CFTypeRef)nativeAd);
    }
    
    void _AdSwitcherNativeAd_load(AdSwitcherNativeAd *nativeAd) {
        [nativeAd load];
    }
    
    AdSwitcherNativeAdData *_AdSwitcherNativeAd_getAdData(AdSwitcherNativeAd *nativeAd) {
        return [nativeAd getAdData];
    }
    
    const char *_AdSwitcherNativeAd_getAdDataProperty(AdSwitcherNativeAdData *nativeAdData, const char *propertyName) {
        NSString *property = [nativeAdData valueForKey:[NSString stringWithUTF8String:propertyName]];
        return __copyString(property);
    }
    
    bool _AdSwitcherNativeAd_isLoaded(AdSwitcherNativeAd *nativeAd) {
        return [nativeAd isLoaded];
    }
    
    void _AdSwitcherNativeAd_openUrl(AdSwitcherNativeAd *nativeAd) {
        [nativeAd openUrl];
    }
    
    void _AdSwitcherNativeAd_sendImpression(AdSwitcherNativeAd *nativeAd) {
        [nativeAd sendImpression];
    }
    
    void _AdSwitcherNativeAd_setAdReceivedHandler(AdSwitcherNativeAd *nativeAd,
                                                  void *cs_instance,
                                                  cs_bannerAdReceivedHandler cs_handler) {
        [nativeAd setAdReceivedHandler:^(AdConfig *config, BOOL result) {
            NSString *adConfigJson = __adConfigToJson(config);
            cs_handler(cs_instance, [adConfigJson UTF8String], result);
        }];
    }
}
