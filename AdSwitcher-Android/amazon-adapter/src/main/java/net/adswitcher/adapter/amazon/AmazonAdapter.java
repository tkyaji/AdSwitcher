package net.adswitcher.adapter.amazon;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.InterstitialAd;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

/**
 * Created by tkyaji on 2017/06/02.
 */

public class AmazonAdapter implements BannerAdAdapter, InterstitialAdAdapter, AdListener {

    private static final String TAG = "AmazonAdapter";

    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;

    private AdLayout adView;
    private InterstitialAd interstitialAd;

    private Activity activity;
    private String appKey;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.appKey = parameters.get("app_key");

        Log.d(TAG, "bannerAdInitialize : app_key=" + this.appKey);

        AdRegistration.enableTesting(testMode);
        AdRegistration.enableLogging(testMode);
        AdRegistration.setAppKey(this.appKey);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.adView = new AdLayout(this.activity);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        this.adView.setLayoutParams(lp);
        this.adView.setListener(this);
        this.adView.loadAd();
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
        this.adView = null;
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;
        this.appKey = parameters.get("app_key");

        Log.d(TAG, "interstitialAdInitialize : app_key=" + this.appKey);

        AdRegistration.enableTesting(testMode);
        AdRegistration.enableLogging(testMode);
        AdRegistration.setAppKey(this.appKey);

        this.interstitialAd = new InterstitialAd(activity);
        this.interstitialAd.setListener(this);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");
        this.interstitialAd.loadAd();
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        if (!this.interstitialAd.isReady()) {
            this.interstitialAdListener.interstitialAdClosed(this, false, false);
            return;
        }
        this.interstitialAd.showAd();
    }


    @Override
    public void onAdLoaded(Ad ad, AdProperties adProperties) {
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdLoaded");
            this.bannerAdListener.bannerAdReceived(this, true);

        } else if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdLoaded");
            this.interstitialAdListener.interstitialAdLoaded(this, true);
        }
    }

    @Override
    public void onAdFailedToLoad(Ad ad, AdError adError) {
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdFailedToLoad : " + adError.getMessage());
            this.bannerAdListener.bannerAdReceived(this, false);

        } else if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdFailedToLoad");
            this.interstitialAdListener.interstitialAdLoaded(this, false);
        }
    }

    @Override
    public void onAdExpanded(Ad ad) {
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdExpanded");
            this.bannerAdListener.bannerAdClicked(this);

        } else if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdExpanded");
            this.interstitialAdListener.interstitialAdClicked(this);
        }
    }

    @Override
    public void onAdCollapsed(Ad ad) {
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdCollapsed");

        } else if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdCollapsed");
        }
    }

    @Override
    public void onAdDismissed(Ad ad) {
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdDismissed");

        } else if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdDismissed");
            this.interstitialAdListener.interstitialAdClosed(this, true, false);
        }
    }
}
