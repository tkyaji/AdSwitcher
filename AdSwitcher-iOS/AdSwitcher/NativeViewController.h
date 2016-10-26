//
//  NativeViewController.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/10/20.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AdSwitcher/AdSwitcherNativeAd.h"

@interface NativeViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIView *adView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UITextView *contentTextView;

- (IBAction)loadNativeAd:(id)sender;

@end
