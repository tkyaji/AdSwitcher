//
//  FirstViewController.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/01.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "BannerViewController.h"

@implementation BannerViewController {
    AdSwitcherBannerView *_bannerView_320x50;
    AdSwitcherBannerView *_bannerView_320x100;
    AdSwitcherBannerView *_bannerView_300x250;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self showBanner_320x50:nil];
}

- (IBAction)showBanner_320x50:(id)sender {
    if (_bannerView_320x50) {
        [_bannerView_320x50 switchAd];
        
    } else {
        _bannerView_320x50 = [[AdSwitcherBannerView alloc] initWithConfigLoader:self
                                                                   configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                       category:@"banner_320x50"
                                                                       testMode:YES
                                                                         adSize:BannerAdSize_320x50];
        
        _bannerView_320x50.frame = CGRectMake((self.view.frame.size.width - 320) / 2, 0,
                                              _bannerView_320x50.frame.size.width, _bannerView_320x50.frame.size.height);
        [self.view addSubview:_bannerView_320x50];
    }
}

- (IBAction)showBanner_320x100:(id)sender {
    if (_bannerView_320x100) {
        [_bannerView_320x100 switchAd];
        
    } else {
        _bannerView_320x100 = [[AdSwitcherBannerView alloc] initWithConfigLoader:self
                                                                    configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                        category:@"banner_320x100"
                                                                        testMode:YES
                                                                          adSize:BannerAdSize_320x100];
        
        _bannerView_320x100.frame = CGRectMake((self.view.frame.size.width - 320) / 2, 0,
                                              _bannerView_320x100.frame.size.width, _bannerView_320x100.frame.size.height);
        [self.view addSubview:_bannerView_320x100];
    }
}

- (IBAction)showBanner_300x250:(id)sender {
    if (_bannerView_300x250) {
        [_bannerView_300x250 switchAd];
        
    } else {
        _bannerView_300x250 = [[AdSwitcherBannerView alloc] initWithConfigLoader:self
                                                                    configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                        category:@"banner_300x250"
                                                                        testMode:YES
                                                                          adSize:BannerAdSize_300x250];
        
        _bannerView_300x250.frame = CGRectMake((self.view.frame.size.width - 300) / 2, 0,
                                               _bannerView_300x250.frame.size.width, _bannerView_300x250.frame.size.height);
        [self.view addSubview:_bannerView_300x250];
    }
}

- (IBAction)hideBanner:(id)sender {
    if (_bannerView_320x50) {
        [_bannerView_320x50 hide];
    }

    if (_bannerView_320x100) {
        [_bannerView_320x100 hide];
    }

    if (_bannerView_300x250) {
        [_bannerView_300x250 hide];
    }
}

@end
