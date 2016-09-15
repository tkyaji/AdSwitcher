//
//  AdSelector.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/08/28.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdSelector.h"

static NSMutableDictionary<NSString *, NSNumber *> *rotateIndexDict;

@implementation AdSelector {
    NSMutableArray<AdConfig *> *_adConfigArr;
    NSInteger _rotateCount;
}

- (instancetype)initWithConfig:(AdSwitcherConfig *)adSwitcherConfig {
    if (self = [super init]) {
        self.adSwitcherConfig = adSwitcherConfig;
        
        _adConfigArr = [[NSMutableArray alloc] initWithArray:adSwitcherConfig.adConfigArr];
    }
    return self;
}

- (AdConfig *)selectAd {
    if ([_adConfigArr count] == 0) {
        return nil;
    }
    
    NSInteger index = -1;
    if (self.adSwitcherConfig.switchType == AdSwitchTypeRotate) {
        index = [self selectAdByRotate];
    } else {
        index = [self selectAdByRatio];
    }

    if (index == -1) {
        return nil;
    }
    
    AdConfig *adConfig = [_adConfigArr objectAtIndex:index];
    _DLOG(@"select:%@", adConfig.adName);
    
    if (self.adSwitcherConfig.switchType == AdSwitchTypeRatio) {
        [_adConfigArr removeObjectAtIndex:index];
    }
    
    return adConfig;

}

- (NSInteger)selectAdByRatio {
    _DLOG();
    
    int totalRatio = 0;
    for (AdConfig *config in _adConfigArr) {
        _DLOG(@"%@: ratio=%ld", config.className, (long)config.ratio);
        totalRatio += config.ratio;
    }
    if (totalRatio == 0) {
        return -1;
    }
    
    int randVal = arc4random_uniform(totalRatio);
    _DLOG(@"%d / %d", randVal, totalRatio);
    int tmpSumRatio = 0;
    for (int i = 0; i < _adConfigArr.count; i++) {
        AdConfig *config = [_adConfigArr objectAtIndex:i];
        tmpSumRatio += config.ratio;
        if (randVal < tmpSumRatio) {
            return i;
        }
    }
    
    return -1;
}

- (NSInteger)selectAdByRotate {
    _DLOG();
    
    if (_rotateCount >= _adConfigArr.count) {
        return -1;
    }
    
    if (!rotateIndexDict) {
        rotateIndexDict = [NSMutableDictionary<NSString *, NSNumber *>  new];
    }
    NSInteger rotateIndex = 0;
    if ([rotateIndexDict objectForKey:self.adSwitcherConfig.category] != nil) {
        rotateIndex = ((NSNumber *)[rotateIndexDict objectForKey:self.adSwitcherConfig.category]).integerValue;
    }
    
    if (rotateIndex >= _adConfigArr.count) {
        rotateIndex = 0;
    }
    
    [rotateIndexDict setObject:@(rotateIndex + 1) forKey:self.adSwitcherConfig.category];
    _rotateCount++;
    
    return rotateIndex;
}

@end
