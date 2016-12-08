//
//  AdfurikunAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/11/07.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import SystemConfiguration;
@import SafariServices;

#import "AdSwitcherBannerView.h"
#import "AdSwitcherInterstitial.h"
#import "AdSwitcherNativeAd.h"
#import <adfurikunsdk/AdfurikunView.h>
#import <adfurikunsdk/AdfurikunPopupView.h>
#import <adfurikunsdk/AdfurikunNativeAd.h>


/*
 * AdfurikunViewDelegate と AdfurikunPopupViewDelegate でメソッド名が同一のものがあり、両方を定義できないため、
 * AdfurikunPopupViewDelegateの方はラップクラスをかます
 */
@protocol AdfurikunPopupDelegateWrapper

@optional

-(void)adfurikunPopup_adfurikunViewDidFinishLoadAdData:(AdfurikunPopupView *)view;
-(void)adfurikunPopup_adfurikunViewDidFinishLoad:(AdfurikunPopupView *)view;
-(void)adfurikunPopup_adfurikunViewAdTapped:(AdfurikunPopupView *)view;
-(void)adfurikunPopup_adfurikunViewAdFailed:(AdfurikunPopupView *)view;
-(void)adfurikunPopup_adfurikunViewAdClose:(AdfurikunPopupView *)view;

@end

@interface AdfurikunPopupDelegateReceiver : NSObject<AdfurikunPopupViewDelegate>
@property NSObject<AdfurikunPopupDelegateWrapper> *delegate;

- (void)adfurikunViewDidFinishLoadAdData:(AdfurikunPopupView *)view;
- (void)adfurikunViewDidFinishLoad:(AdfurikunPopupView *)view;
- (void)adfurikunViewAdTapped:(AdfurikunPopupView *)view;
- (void)adfurikunViewAdFailed:(AdfurikunPopupView *)view;
- (void)adfurikunViewAdClose:(AdfurikunPopupView *)view;

@end


@interface AdfurikunAdapter : NSObject <BannerAdAdapter, InterstitialAdAdapter, NativeAdAdapter, AdfurikunViewDelegate, AdfurikunPopupDelegateWrapper, AdfurikunNativeAdDelegate>

@end
