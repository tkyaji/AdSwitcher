//
//  VideoViewController.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/09/02.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "VideoViewController.h"

@implementation VideoViewController {
    AdSwitcherInterstitial *_interstitialAdSwitcher;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _interstitialAdSwitcher = [[AdSwitcherInterstitial alloc] initWithConfigLoader:self configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                          category:@"video" testMode:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)showInterstitial:(id)sender {
    [_interstitialAdSwitcher show];
}

@end
