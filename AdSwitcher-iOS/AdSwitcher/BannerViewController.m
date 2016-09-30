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
    
    _bannerView_320x50 = [[AdSwitcherBannerView alloc] initWithConfigLoader:self
                                                               configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                   category:@"banner_320x50"
                                                                   testMode:YES
                                                                     adSize:BannerAdSize_320x50];
    
    _bannerView_320x100 = [[AdSwitcherBannerView alloc] initWithConfigLoader:self
                                                                configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                    category:@"banner_320x100"
                                                                    testMode:YES
                                                                      adSize:BannerAdSize_320x100];

    _bannerView_300x250 = [[AdSwitcherBannerView alloc] initWithConfigLoader:self
                                                                configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                    category:@"banner_300x250"
                                                                    testMode:YES
                                                                      adSize:BannerAdSize_300x250];

    _bannerView_320x50.frame = CGRectMake((self.view.frame.size.width - _bannerView_320x50.frame.size.width) / 2, 0,
                                          _bannerView_320x50.frame.size.width, _bannerView_320x50.frame.size.height);
    _bannerView_320x100.frame = CGRectMake((self.view.frame.size.width - _bannerView_320x100.frame.size.width) / 2, 0,
                                          _bannerView_320x100.frame.size.width, _bannerView_320x100.frame.size.height);
    _bannerView_300x250.frame = CGRectMake((self.view.frame.size.width - _bannerView_300x250.frame.size.width) / 2, 0,
                                          _bannerView_300x250.frame.size.width, _bannerView_300x250.frame.size.height);
    
    [self.view addSubview:_bannerView_320x50];
    [self.view addSubview:_bannerView_320x100];
    [self.view addSubview:_bannerView_300x250];
    
    [_bannerView_320x50 load];
}

- (IBAction)showBanner_320x50:(id)sender {
    [_bannerView_320x50 switchAd];
}

- (IBAction)showBanner_320x100:(id)sender {
    [_bannerView_320x100 switchAd];
}

- (IBAction)showBanner_300x250:(id)sender {
    [_bannerView_300x250 switchAd];
}

- (IBAction)hideBanner:(id)sender {
    [_bannerView_320x50 hide];
    [_bannerView_320x100 hide];
    [_bannerView_300x250 hide];
}

@end
