package net.adswitcher.adapter.applovin;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.applovin.adview.AppLovinAdView;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;

import java.util.Map;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

/**
 * Created by tkyaji on 2016/08/04.
 */
public class AppLovinAdapter implements BannerAdAdapter, InterstitialAdAdapter, AppLovinAdLoadListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener, AppLovinAdClickListener {

    private static final String TAG = "AppLovinAdapter";

    private Activity activity;
    private AppLovinSdk sdk;
    private AppLovinInterstitialAdDialog interstitialAd;
    private AppLovinAdView adView;
    private InterstitialAdListener interstitialAdListener;
    private BannerAdListener bannerAdListener;
    private BannerAdSize bannerAdSize;
    private String placement;
    private boolean isSkipped;

    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        String sdkKey = parameters.get("sdk_key");
        this.placement = parameters.get("placement");
        Log.d(TAG, "interstitialAdInitialize : sdk_key=" + sdkKey + ", placement=" + placement);

        this.sdk = AppLovinSdk.getInstance(sdkKey, AppLovinSdkUtils.retrieveUserSettings(this.activity), this.activity);
        this.sdk.initializeSdk();

        this.interstitialAd = AppLovinInterstitialAd.create(this.sdk, this.activity);
        this.interstitialAd.setAdDisplayListener(this);
        this.interstitialAd.setAdVideoPlaybackListener(this);
        this.interstitialAd.setAdClickListener(this);
        this.interstitialAd.setAdLoadListener(this);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        if (this.interstitialAd.isAdReadyToDisplay()) {
            this.interstitialAdListener.interstitialAdLoaded(AppLovinAdapter.this, true);

        } else {
            this.adLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        this.isSkipped = false;

        Log.d(TAG, "interstitialAdShow");

        if (this.placement != null) {
            this.interstitialAd.show(this.placement);
        } else {
            this.interstitialAd.show();
        }
    }


    @Override
    public void adReceived(AppLovinAd appLovinAd) {
        Log.d(TAG, "adReceived");
        if (this.bannerAdListener != null) {
            this.bannerAdListener.bannerAdReceived(this, true);
        }
    }

    @Override
    public void failedToReceiveAd(int errorCode) {
        Log.d(TAG, "failedToReceiveAd : errorCode=" + errorCode);
        if (this.bannerAdListener != null) {
            this.bannerAdListener.bannerAdReceived(this, false);
        }
    }


    @Override
    public void adDisplayed(AppLovinAd appLovinAd) {
        Log.d(TAG, "adDisplayed");
        if (this.interstitialAdListener != null) {
            this.interstitialAdListener.interstitialAdShown(this);
        }
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        Log.d(TAG, "adHidden");
        if (this.interstitialAdListener != null) {
            this.interstitialAdListener.interstitialAdClosed(this, true, this.isSkipped);
        }
    }

    @Override
    public void videoPlaybackBegan(AppLovinAd appLovinAd) {
        Log.d(TAG, "videoPlaybackBegan");
    }

    @Override
    public void videoPlaybackEnded(final AppLovinAd ad, final double percentViewed, final boolean fullyWatched) {
        Log.d(TAG, "videoPlaybackEnded percentViewed=" + percentViewed + ", fullyWatched=" + fullyWatched);
        this.isSkipped = !fullyWatched;
    }

    @Override
    public void adClicked(AppLovinAd appLovinAd) {
        Log.d(TAG, "adClicked");
        if (this.interstitialAdListener != null) {
            this.interstitialAdListener.interstitialAdClicked(this);
        }
        if (this.bannerAdListener != null) {
            this.bannerAdListener.bannerAdClicked(this);
        }
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppLovinAdapter.this.interstitialAd.isAdReadyToDisplay()) {
                    AppLovinAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppLovinAdapter.this.interstitialAdListener.interstitialAdLoaded(AppLovinAdapter.this, true);
                        }
                    });
                } else if (count == 5) {
                    AppLovinAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppLovinAdapter.this.interstitialAdListener.interstitialAdLoaded(AppLovinAdapter.this, false);
                        }
                    });
                } else {
                    AppLovinAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }


    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.bannerAdSize = bannerAdSize;

        String sdkKey = parameters.get("sdk_key");
        this.placement = parameters.get("placement");
        Log.d(TAG, "bannerAdInitialize : sdk_key=" + sdkKey + ", placement=" + placement);

//        this.sdk = AppLovinSdk.getInstance(sdkKey, AppLovinSdkUtils.retrieveUserSettings(this.activity), this.activity);
//        this.sdk.initializeSdk();

//        AppLovinSdk.initializeSdk(this.activity);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        AppLovinAdSize alAdSize;
        if (this.bannerAdSize == BannerAdSize.SIZE_300X250) {
            alAdSize = AppLovinAdSize.MREC;
        } else {
            alAdSize = AppLovinAdSize.BANNER;
        }

        this.adView = new AppLovinAdView(alAdSize, this.activity);
        this.adView.setAdLoadListener(this);
        this.adView.setAdClickListener(this);
        this.adView.setAdDisplayListener(this);
        this.adView.setLayoutParams(new FrameLayout.LayoutParams(
                AppLovinSdkUtils.dpToPx(this.activity, alAdSize.getWidth()),
                AppLovinSdkUtils.dpToPx(this.activity, alAdSize.getHeight())));
        this.adView.loadNextAd();
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "bannerAdShow");
        parentLayout.addView(this.adView);
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");
        ((FrameLayout)this.adView.getParent()).removeView(this.adView);
    }
}
