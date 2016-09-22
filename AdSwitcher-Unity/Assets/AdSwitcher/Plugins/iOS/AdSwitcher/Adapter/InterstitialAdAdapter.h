//
//  InterstitialAdAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/13.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#ifndef InterstitialAdAdapter_h
#define InterstitialAdAdapter_h


#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "AdAdapter.h"

@protocol InterstitialAdDelegate

@required

- (void)interstitialAdLoaded:(NSObject<AdAdapter> *)adAdapter result:(BOOL)result;
- (void)interstitialAdShown:(NSObject<AdAdapter> *)adAdapter;
- (void)interstitialAdClosed:(NSObject<AdAdapter> *)adAdapter result:(BOOL)result isSkipped:(BOOL)isSkipped;
- (void)interstitialAdClicked:(NSObject<AdAdapter> *)adAdapter;

@end


@protocol InterstitialAdAdapter <AdAdapter>

@required

@property (nonatomic) id<InterstitialAdDelegate> interstitialAdDelegate;

- (void)interstitialAdInitialize:(UIViewController *)viewController parameters:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode;
- (void)interstitialAdLoad;
- (void)interstitialAdShow;

@end

#endif /* InterstitialAdAdapter_h */
