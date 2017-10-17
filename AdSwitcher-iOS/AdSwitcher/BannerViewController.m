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
                                                                     adSize:BannerAdSize_320x50
                                                                   testMode:YES];
    
    _bannerView_320x100 = [[AdSwitcherBannerView alloc] initWithConfigLoader:self
                                                                configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                    category:@"banner_320x100"
                                                                      adSize:BannerAdSize_320x100
                                                                    testMode:YES];

    _bannerView_300x250 = [[AdSwitcherBannerView alloc] initWithConfigLoader:self
                                                                configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                    category:@"banner_300x250"
                                                                      adSize:BannerAdSize_300x250
                                                                    testMode:YES];

    _bannerView_320x50.frame = CGRectMake((self.view.frame.size.width - _bannerView_320x50.frame.size.width) / 2, 0,
                                          _bannerView_320x50.frame.size.width, _bannerView_320x50.frame.size.height);
    _bannerView_320x100.frame = CGRectMake((self.view.frame.size.width - _bannerView_320x100.frame.size.width) / 2, 0,
                                          _bannerView_320x100.frame.size.width, _bannerView_320x100.frame.size.height);
    _bannerView_300x250.frame = CGRectMake((self.view.frame.size.width - _bannerView_300x250.frame.size.width) / 2, 0,
                                          _bannerView_300x250.frame.size.width, _bannerView_300x250.frame.size.height);
}

- (IBAction)loadBanner_320x50:(id)sender {
    if (self.autoShowSwitch.on) {
        [self.view addSubview:_bannerView_320x50];
    }
    [_bannerView_320x50 load:self.autoShowSwitch.on];
}

- (IBAction)showBanner_320x50:(id)sender {
    [self.view addSubview:_bannerView_320x50];
    [_bannerView_320x50 show];
}

- (IBAction)loadBanner_320x100:(id)sender {
    if (self.autoShowSwitch.on) {
        [self.view addSubview:_bannerView_320x100];
    }
    [_bannerView_320x100 load:self.autoShowSwitch.on];
}

- (IBAction)showBanner_320x100:(id)sender {
    [self.view addSubview:_bannerView_320x100];
    [_bannerView_320x100 show];
}

- (IBAction)loadBanner_300x250:(id)sender {
    if (self.autoShowSwitch.on) {
        [self.view addSubview:_bannerView_300x250];
    }
    [_bannerView_300x250 load:self.autoShowSwitch.on];
}

- (IBAction)showBanner_300x250:(id)sender {
    [self.view addSubview:_bannerView_300x250];
    [_bannerView_300x250 show];
}

- (IBAction)hideBanner:(id)sender {
    [_bannerView_320x50 hide];
    [_bannerView_320x50 removeFromSuperview];
    [_bannerView_320x100 hide];
    [_bannerView_320x100 removeFromSuperview];
    [_bannerView_300x250 hide];
    [_bannerView_300x250 removeFromSuperview];
}

@end
