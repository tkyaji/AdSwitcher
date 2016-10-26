//
//  FirstViewController.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/01.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AdSwitcher/AdSwitcherBannerView.h"

@interface BannerViewController : UIViewController

@property (weak, nonatomic) IBOutlet UISwitch *autoShowSwitch;

- (IBAction)loadBanner_320x50:(id)sender;
- (IBAction)showBanner_320x50:(id)sender;

- (IBAction)loadBanner_320x100:(id)sender;
- (IBAction)showBanner_320x100:(id)sender;

- (IBAction)loadBanner_300x250:(id)sender;
- (IBAction)showBanner_300x250:(id)sender;

- (IBAction)hideBanner:(id)sender;

@end

