//
//  NativeAdAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/10/19.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#ifndef NativeAdAdapter_h
#define NativeAdAdapter_h

#import "AdAdapter.h"
#import "AdSwitcherNativeAdData.h"

@protocol NativeAdDelegate

@required

- (void)nativeAdReceived:(NSObject<AdAdapter> *)adAdapter result:(BOOL)result;

@end



@protocol NativeAdAdapter <AdAdapter>

@required

@property (nonatomic) id<NativeAdDelegate> nativeAdDelegate;

- (void)nativeAdInitialize:(NSDictionary<NSString *, NSString *> *)parameters testMode:(BOOL)testMode;
- (void)nativeAdLoad;
- (AdSwitcherNativeAdData *)getAdData;
- (void)openUrl;
- (void)sendImpression;

@end


#endif /* NativeAdAdapter_h */
