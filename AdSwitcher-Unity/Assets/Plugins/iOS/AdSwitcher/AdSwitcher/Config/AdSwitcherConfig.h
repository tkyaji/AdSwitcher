//
//  AdSwitcherConfig.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/04.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum {
    AdSwitchTypeRatio,
    AdSwitchTypeRotate
} AdSwitchType;


@interface AdConfig : NSObject

@property (nonatomic) NSString *adName;
@property (nonatomic) NSString *className;
@property (nonatomic) NSInteger ratio;
@property (nonatomic) NSDictionary<NSString *, NSString *> *parameters;

@end


@interface AdSwitcherConfig : NSObject

@property (nonatomic) NSString *category;
@property (nonatomic) AdSwitchType switchType;
@property (nonatomic) NSInteger interval;
@property (nonatomic) NSArray<AdConfig *> *adConfigArr;

@end

