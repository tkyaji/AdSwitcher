//
//  NativeViewController.m
//  AdSwitcher
//
//  Created by tkyaji on 2016/10/20.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import "NativeViewController.h"

@interface NativeViewController ()

@end

@implementation NativeViewController {
    AdSwitcherNativeAd *_nativeAd;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _nativeAd = [[AdSwitcherNativeAd alloc]initWithConfigLoader:[AdSwitcherConfigLoader sharedInstance]
                                                       category:@"native" testMode:YES];
    
    __block NativeViewController *_self = self;
    __block  AdSwitcherNativeAd *nativeAdInBlock = _nativeAd;
    __block UIView *adView = self.adView;
    __block UILabel *titleLabel = self.titleLabel;
    __block UITextView *contentTextView = self.contentTextView;
    __block UIImageView *imageView = self.imageView;
    __block UIImageView *iconImageView = self.iconImageView;
    
    [_nativeAd setAdReceivedHandler:^(AdConfig *config, BOOL result) {
        if (result) {
            AdSwitcherNativeAdData *adData = [nativeAdInBlock getAdData];
            titleLabel.text = adData.shortText;
            contentTextView.text = adData.longText;
            
            [nativeAdInBlock loadImage:^(UIImage *uiImage) {
                imageView.image = uiImage;
            }];
            [nativeAdInBlock loadIconImage:^(UIImage *uiImage) {
                iconImageView.image = uiImage;
            }];
            
            UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:_self action:@selector(clickAd)];
            [adView addGestureRecognizer:tapGesture];
            
            [nativeAdInBlock sendImpression];
        }
    }];
    
    [_nativeAd load];
}

- (void)clickAd {
    [_nativeAd openUrl];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)loadNativeAd:(id)sender {
    [_nativeAd load];
}

@end
