//
//  SecondViewController.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/01.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "InterstitialViewController.h"

@implementation InterstitialViewController {
    AdSwitcherInterstitial *_interstitialAdSwitcher;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _interstitialAdSwitcher = [[AdSwitcherInterstitial alloc] initWithConfigLoader:self configLoader:[AdSwitcherConfigLoader sharedInstance]
                                                                          category:@"interstitial" testMode:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)showInterstitial:(id)sender {
    if (_interstitialAdSwitcher.isLoaded) {
        [_interstitialAdSwitcher show];
        
    } else {
        NSLog(@"Not loaded.");
    }
}

@end
