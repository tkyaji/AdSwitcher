//
//  AdSwitcherConfigLoader.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/12.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "AdSwitcherConfigLoader.h"

static AdSwitcherConfigLoader *_sharedInstance;

@implementation AdSwitcherConfigLoader {
    NSDictionary<NSString *, AdSwitcherConfig *> *_adSwitcherConfigDict;
    NSMutableArray<configLoadedHandler> *_configLoadedHandlerArr;
}

+ (AdSwitcherConfigLoader *)sharedInstance {
    if (!_sharedInstance) {
        _sharedInstance = [AdSwitcherConfigLoader new];
    }
    return _sharedInstance;
}

- (instancetype)init {
    if (self = [super init]) {
        _configLoadedHandlerArr = [NSMutableArray<configLoadedHandler> new];
    }
    return self;
}

- (void)startLoad:(NSURL *)url {

    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    NSString *key = @"AdSwitcher-jsonCache";
    NSData *cachedJsonData = [userDefault objectForKey:key];
    
    self.loading = YES;
    [self loadFromUrl:url onLoaded:^(NSData *jsonData) {
        if ([self loadToConfigDict:jsonData]) {
            [userDefault setObject:jsonData forKey:key];
            [self loadCompleted];
            
        } else {
            [self startLoadWithDelay:30 url:url];
        }
        
    } onFailedToLoad:^{
        if (cachedJsonData != nil && [self loadToConfigDict:cachedJsonData]) {
            [self loadCompleted];
            
        } else {
            [self startLoadWithDelay:5 url:url];
        }
    }];
}

- (void)startLoadWithDelay:(float)waitTime url:(NSURL *)url {
    
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, waitTime * NSEC_PER_SEC);
    dispatch_after(time, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self startLoad:url];
    });
}

- (void)loadJson:(NSData *)jsonData {
    if ([self loadToConfigDict:jsonData]) {
        [self loadCompleted];
    }
}

- (AdSwitcherConfig *)adSwitchConfig:(NSString *)category {
    if (!_adSwitcherConfigDict) {
        return nil;
    }
    return [_adSwitcherConfigDict objectForKey:category];
}
 
- (void)addConfigLoadedHandler:(void (^)(void))handler {
    @synchronized (self) {
        if (self.loaded) {
            handler();
        } else {
            [_configLoadedHandlerArr addObject:handler];
        }
    }
}


# pragma - private method

- (void)loadFromUrl:(NSURL *)url onLoaded:(void (^)(NSData *))onLoaded onFailedToLoad:(void (^)(void))onFailedToLoad {
    if (!url) {
        onFailedToLoad();
        return;
    }
    
    NSURLSessionConfiguration* config = [NSURLSessionConfiguration defaultSessionConfiguration];
    config.timeoutIntervalForRequest = 2;
    config.timeoutIntervalForResource = 3;
    config.requestCachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
    NSURLSession* session = [NSURLSession sessionWithConfiguration:config];
    
    _DLOG(@"send request: %@", url);
    
    NSURLSessionDataTask *task = [session dataTaskWithURL:url completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        if (!response) {
            // 未接続時を想定
            _DLOG("No Response");
            onFailedToLoad();
            return;
        }
        
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            _DLOG("Invalid Status Error (%ld)", (long)httpResponse.statusCode);
            onFailedToLoad();
            return;
        }
        
        NSLog(@"[AdSwitcherConfigLoader] %@", [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
        
        onLoaded(data);
    }];
    
    [task resume];
}

- (BOOL)loadToConfigDict:(NSData *)jsonData {
    if (!jsonData) {
        return NO;
    }
    
    NSError *error = nil;
    NSDictionary *adDownloadedSwitcherConfigsDict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSUTF8StringEncoding error:&error];
    if (error) {
        // JSONパースエラー
        _DLOG("Json parse error : %@", error);
        
        return NO;
        
    } else {
        _adSwitcherConfigDict = [self toAdSwitcherConfigDict:adDownloadedSwitcherConfigsDict];
        return YES;
    }
}

- (void)loadCompleted {
    @synchronized (self) {
        [_configLoadedHandlerArr enumerateObjectsUsingBlock:^(configLoadedHandler handler, NSUInteger idx, BOOL *stop) {
            handler();
        }];
        [_configLoadedHandlerArr removeAllObjects];
        
        self.loading = NO;
        self.loaded = YES;
    }
}

- (NSDictionary<NSString *, AdSwitcherConfig *> *)toAdSwitcherConfigDict:(NSDictionary *)adSwitcherConfigsDict {
    
    NSMutableDictionary<NSString *, AdSwitcherConfig *> *mDict = [NSMutableDictionary<NSString *, AdSwitcherConfig *> new];
    
    [adSwitcherConfigsDict enumerateKeysAndObjectsUsingBlock:^(NSString *key, NSDictionary *dict, BOOL *stop) {
        NSString *switchType = [dict objectForKey:@"switch_type"];
        NSNumber *interval = [dict objectForKey:@"interval"];
        NSArray *adConfigArr = [dict objectForKey:@"ads"];
        
        AdSwitcherConfig *adSwitcherConfig = [AdSwitcherConfig new];
        
        adSwitcherConfig.category = key;
        adSwitcherConfig.switchType = [self toAdSwitchType:switchType];
        adSwitcherConfig.interval = (interval) ? interval.integerValue : 0;
        
        NSMutableArray<AdConfig *> *mArr = [NSMutableArray<AdConfig *> new];
        for (NSDictionary *adConfigDict in adConfigArr) {
            NSString *adName = [adConfigDict objectForKey:@"name"];
            NSString *className = [adConfigDict objectForKey:@"class_name"];
            NSNumber *ratio = [adConfigDict objectForKey:@"ratio"];
            NSDictionary<NSString *, NSString *> *paramDict = [adConfigDict objectForKey:@"parameters"];
            
            AdConfig *adConfig = [AdConfig new];
            adConfig.adName = adName;
            adConfig.className = className;
            adConfig.ratio = [ratio integerValue];
            adConfig.parameters = paramDict;
            
            [mArr addObject:adConfig];
        }
        
        adSwitcherConfig.adConfigArr = mArr;
        
        [mDict setObject:adSwitcherConfig forKey:key];
    }];
    
    return mDict;
}

- (AdSwitchType)toAdSwitchType:(NSString *)adSwitchTypeStr {
    if ([@"rotate" isEqualToString:adSwitchTypeStr]) {
        return AdSwitchTypeRotate;
    } else if ([@"priority" isEqualToString:adSwitchTypeStr]) {
        return AdSwitchTypePriority;
    } else {
        return AdSwitchTypeRatio;
    }
}


@end
