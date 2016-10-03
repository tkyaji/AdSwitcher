//
//  BannerAdAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/13.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#ifndef BannerAdAdapter_h
#define BannerAdAdapter_h


#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "AdAdapter.h"


typedef enum {
    BannerAdSize_320x50,
    BannerAdSize_320x100,
    BannerAdSize_300x250,
} BannerAdSize;

typedef enum {
    BannerAdAlignTopLeft,
    BannerAdAlignTopCenter,
    BannerAdAlignTopRight,
    BannerAdAlignBottomLeft,
    BannerAdAlignBottomCenter,
    BannerAdAlignBottomRight,
} BannerAdAlign;

typedef struct {
    float top;
    float bottom;
    float left;
    float right;
} BannerAdMargin;

static inline BannerAdMargin BannerAdMarginMake(float left, float top, float right, float bottom) {
    BannerAdMargin adMargin;
    adMargin.left = left;
    adMargin.top = top;
    adMargin.right = right;
    adMargin.bottom = bottom;
    return adMargin;
}

static const BannerAdMargin BannerAdMarginZero = {0, 0, 0, 0};



@protocol BannerAdDelegate

@required

- (void)bannerAdReceived:(NSObject<AdAdapter> *)adAdapter result:(BOOL)result;
- (void)bannerAdShown:(NSObject<AdAdapter> *)adAdapter;
- (void)bannerAdClicked:(NSObject<AdAdapter> *)adAdapter;

@end



@protocol BannerAdAdapter <AdAdapter>

@required

@property (nonatomic) id<BannerAdDelegate> bannerAdDelegate;

- (void)bannerAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode adSize:(BannerAdSize)adSize;
- (void)bannerAdLoad;
- (void)bannerAdShow:(UIView *)parentView;
- (void)bannerAdHide;

@end


#endif  /* BannerAdAdapter_h */
