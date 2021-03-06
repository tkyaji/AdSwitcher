//
//  AdfurikunVideoAdapter.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/11/11.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

@import Foundation;
@import WebKit;

#import "AdSwitcherInterstitial.h"
#import <ADFMovieReward/ADFmyMovieReward.h>
#import <ADFMovieReward/ADFmyMovieInterstitial.h>

@interface AdfurikunVideoAdapter : NSObject <InterstitialAdAdapter, ADFmyMovieRewardDelegate>

@end
