//
//  AdSelector.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/08/28.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdSwitcherConfig.h"
#import "Log.h"

@interface AdSelector : NSObject

@property (nonatomic) AdSwitcherConfig *adSwitcherConfig;

- (instancetype)init __attribute__((unavailable("init is not available")));
- (instancetype)initWithConfig:(AdSwitcherConfig *)adSwitcherConfig;

- (AdConfig *)selectAd;

@end
