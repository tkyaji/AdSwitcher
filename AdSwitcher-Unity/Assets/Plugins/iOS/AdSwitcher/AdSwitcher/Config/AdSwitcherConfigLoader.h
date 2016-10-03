//
//  AdSwitcherConfigLoader.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/12.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AdSwitcherConfig.h"
#import "Log.h"

@interface AdSwitcherConfigLoader : NSObject

typedef void (^configLoadedHandler)(void);

@property (atomic) BOOL loading;
@property (atomic) BOOL loaded;

- (instancetype)init __attribute__((unavailable("init is not available")));

+ (AdSwitcherConfigLoader *)sharedInstance;

- (void)startLoad:(NSURL *)url;
- (void)loadJson:(NSData *)jsonData;

- (AdSwitcherConfig *)adSwitchConfig:(NSString *)category;
- (void)addConfigLoadedHandler:(void (^)(void))handler;

@end
