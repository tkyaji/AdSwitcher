//
//  AdSwitcherPlugin.h
//  Unity-iPhone
//
//  Created by tkyaji on 2016/12/08.
//
//

@import Foundation;

#import "AdSwitcherConfigLoader.h"
#import "AdSwitcherInterstitial.h"
#import "AdSwitcherBannerView.h"
#import "AdSwitcherNativeAd.h"


#ifdef __cplusplus
extern "C" {
#endif
    
    // AdSwitcherConfigLoader
    
    AdSwitcherConfigLoader* _AdSwitcherConfigLoader_sharedInstance();
    void _AdSwitcherConfigLoader_startLoad(AdSwitcherConfigLoader *configLoader, const char *urlStr);
    void _AdSwitcherConfigLoader_loadJson(AdSwitcherConfigLoader *configLoader, const char *jsonText);
    
    
    // AdSwitcherBannerView
    
    AdSwitcherBannerView *_AdSwitcherBannerView_new(AdSwitcherConfigLoader *configLoader, const char *category,
                                                    int adSize, int adAlign, float *adMarginArr, bool isSizeToFit, bool testMode);
    
    AdSwitcherBannerView *_AdSwitcherBannerView_new_config(const char *adSwitcherConfigJsonStr,
                                                           int adSize, int adAlign, float *adMarginArr, bool isSizeToFit, bool testMode);
    
    void _AdSwitcherBannerView_release(AdSwitcherBannerView *bannerView);
    
    typedef void (*cs_bannerAdReceivedHandler)(void *, const char *adConfigJson, bool result);
    typedef void (*cs_bannerAdShownHandler)(void *, const char *adConfigJson);
    typedef void (*cs_bannerAdClickedHandler)(void *, const char *adConfigJson);
    
    void _AdSwitcherBannerView_setPosition(AdSwitcherBannerView *bannerView, int adAlign, float *adMarginArr);
    
    void _AdSwitcherBannerView_load(AdSwitcherBannerView *bannerView, bool autoShow);

    void _AdSwitcherBannerView_show(AdSwitcherBannerView *bannerView);
    
    void _AdSwitcherBannerView_hide(AdSwitcherBannerView *bannerView);
    
    void _AdSwitcherBannerView_switchAd(AdSwitcherBannerView *bannerView);
    
    bool _AdSwitcherBannerView_isLoaded(AdSwitcherBannerView *bannerView);
    
    float _AdSwitcherBannerView_getWidth(AdSwitcherBannerView *bannerView);
    
    float _AdSwitcherBannerView_getHeight(AdSwitcherBannerView *bannerView);
    
    float _AdSwitcherBannerView_getScreenWidth(AdSwitcherBannerView *bannerView);
    
    float _AdSwitcherBannerView_getScreenHeight(AdSwitcherBannerView *bannerView);
    
    void _AdSwitcherBannerView_setAdReceivedHandler(AdSwitcherBannerView *bannerView,
                                                    void *cs_instance,
                                                    cs_bannerAdReceivedHandler cs_handler);
    
    void _AdSwitcherBannerView_setAdShownHandler(AdSwitcherBannerView *bannerView,
                                                 void *cs_instance,
                                                 cs_bannerAdShownHandler cs_handler);
    
    void _AdSwitcherBannerView_setAdClickedHandler(AdSwitcherBannerView *bannerView,
                                                   void *cs_instance,
                                                   cs_bannerAdClickedHandler cs_handler);
    
    
    
    // AdSwitcherInterstitial
    
    AdSwitcherInterstitial *_AdSwitcherInterstitial_new(AdSwitcherConfigLoader *configLoader, const char *category, bool testMode);
    
    AdSwitcherInterstitial *_AdSwitcherInterstitial_new_config(const char *adSwitcherConfigJsonStr, bool testMode);
    
    void _AdSwitcherInterstitial_release(AdSwitcherInterstitial *interstitial);
    
    typedef void (*cs_interstitialAdLoadedHandler)(void *, const char *adConfigJson, bool result);
    typedef void (*cs_interstitialAdShownHandler)(void *, const char *adConfigJson);
    typedef void (*cs_interstitialAdClosedHandler)(void *, const char *adConfigJson, bool result, bool isSkipped);
    typedef void (*cs_interstitialAdClickedHandler)(void *, const char *adConfigJson);
    
    void _AdSwitcherInterstitial_show(AdSwitcherInterstitial *interstitial);
    
    bool _AdSwitcherInterstitial_isLoaded(AdSwitcherInterstitial *interstitial);
    
    void _AdSwitcherInterstitial_setAdLoadedHandler(AdSwitcherInterstitial *interstitial,
                                                    void *cs_instance,
                                                    cs_interstitialAdLoadedHandler cs_handler);
    
    void _AdSwitcherInterstitial_setAdShownHandler(AdSwitcherInterstitial *interstitial,
                                                   void *cs_instance,
                                                   cs_interstitialAdShownHandler cs_handler);
    
    void _AdSwitcherInterstitial_setAdClosedHandler(AdSwitcherInterstitial *interstitial,
                                                    void *cs_instance,
                                                    cs_interstitialAdClosedHandler cs_handler);
    
    void _AdSwitcherInterstitial_setAdClickedHandler(AdSwitcherInterstitial *interstitial,
                                                     void *cs_instance,
                                                     cs_interstitialAdClickedHandler cs_handler);
    
    
    
    // AdSwitcherNativeAd
    
    typedef void (*cs_nativeAdReceivedHandler)(void *, const char *adConfigJson, bool result);
    
    AdSwitcherNativeAd *_AdSwitcherNativeAd_new(AdSwitcherConfigLoader *configLoader, const char *category, bool testMode);
    
    AdSwitcherNativeAd *_AdSwitcherNativeAd_new_config(const char *adSwitcherConfigJsonStr, bool testMode);
    
    void _AdSwitcherNativeAd_release(AdSwitcherNativeAd *nativeAd);
    
    void _AdSwitcherNativeAd_load(AdSwitcherNativeAd *nativeAd);
    
    AdSwitcherNativeAdData *_AdSwitcherNativeAd_getAdData(AdSwitcherNativeAd *nativeAd);
    
    const char *_AdSwitcherNativeAd_getAdDataProperty(AdSwitcherNativeAdData *nativeAdData, const char *propertyName);
    
    bool _AdSwitcherNativeAd_isLoaded(AdSwitcherNativeAd *nativeAd);
    
    void _AdSwitcherNativeAd_openUrl(AdSwitcherNativeAd *nativeAd);
    
    void _AdSwitcherNativeAd_sendImpression(AdSwitcherNativeAd *nativeAd);
    
    void _AdSwitcherNativeAd_setAdReceivedHandler(AdSwitcherNativeAd *nativeAd,
                                                  void *cs_instance,
                                                  cs_bannerAdReceivedHandler cs_handler);

#ifdef __cplusplus
}
#endif
