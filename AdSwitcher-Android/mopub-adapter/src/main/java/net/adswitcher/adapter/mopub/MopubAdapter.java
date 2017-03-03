package net.adswitcher.adapter.mopub;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

/**
 * Created by tkyaji on 2017/03/02.
 */

public class MopubAdapter implements BannerAdAdapter, InterstitialAdAdapter, MoPubView.BannerAdListener, MoPubInterstitial.InterstitialAdListener {

    private static final String TAG = "MopubAdapter";

    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;

    private Activity activity;
    private String adUnitId;
    private boolean testMode;
    private MoPubView mpAdView;
    private MoPubInterstitial mpInterstitial;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.adUnitId = parameters.get("ad_unit_id");
        this.testMode = testMode;

        Log.d(TAG, "bannerAdInitialize : ad_unit_id=" + this.adUnitId);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.mpAdView = new MoPubView(this.activity);
        this.mpAdView.setAdUnitId(this.adUnitId);
        this.mpAdView.setTesting(this.testMode);
        this.mpAdView.setBannerAdListener(this);
        this.mpAdView.loadAd();
        this.mpAdView.setAutorefreshEnabled(false);
        this.mpAdView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "bannerAdShow");
        this.mpAdView.setAutorefreshEnabled(true);
        this.mpAdView.setVisibility(View.VISIBLE);
        parentLayout.addView(this.mpAdView);
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");
        this.mpAdView.destroy();
        ((FrameLayout)this.mpAdView.getParent()).removeView(this.mpAdView);
        this.mpAdView = null;
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        this.adUnitId = parameters.get("ad_unit_id");

        Log.d(TAG, "interstitialAdInitialize : ad_unit_id=" + this.adUnitId);

        this.mpInterstitial = new MoPubInterstitial(this.activity, this.adUnitId);
        this.mpInterstitial.setTesting(testMode);
        this.mpInterstitial.setInterstitialAdListener(this);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");
        this.mpInterstitial.load();
    }

    @Override
    public void interstitialAdShow() {
        if (this.mpInterstitial.isReady()) {
            Log.d(TAG, "interstitialAdShow : ready=true");
            this.mpInterstitial.show();

        } else {
            Log.d(TAG, "interstitialAdShow : ready=false");
            this.interstitialAdListener.interstitialAdClosed(this, false, false);
        }
    }


    @Override
    public void onBannerLoaded(MoPubView moPubView) {
        Log.d(TAG, "onBannerLoaded");
        this.bannerAdListener.bannerAdReceived(this, true);
    }

    @Override
    public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
        Log.d(TAG, "onBannerFailed : errorCode=" + moPubErrorCode);
        this.bannerAdListener.bannerAdReceived(this, false);
    }

    @Override
    public void onBannerClicked(MoPubView moPubView) {
        Log.d(TAG, "onBannerClicked");
        this.bannerAdListener.bannerAdClicked(this);
    }

    @Override
    public void onBannerExpanded(MoPubView moPubView) {
        Log.d(TAG, "onBannerExpanded");
    }

    @Override
    public void onBannerCollapsed(MoPubView moPubView) {
        Log.d(TAG, "onBannerCollapsed");
    }


    @Override
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        Log.d(TAG, "onInterstitialLoaded");
        this.interstitialAdListener.interstitialAdLoaded(this, true);
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
        Log.d(TAG, "onInterstitialFailed : errorCode=" + moPubErrorCode);
        this.interstitialAdListener.interstitialAdLoaded(this, false);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {
        Log.d(TAG, "onInterstitialShown");
        this.interstitialAdListener.interstitialAdShown(this);
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {
        Log.d(TAG, "onInterstitialClicked");
        this.interstitialAdListener.interstitialAdClicked(this);
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
        Log.d(TAG, "onInterstitialDismissed");
        this.interstitialAdListener.interstitialAdClosed(this, true, false);
    }
}
