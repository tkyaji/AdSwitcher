package net.adswitcher.adapter.facebook;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.internal.util.AdInternalSettings;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

/**
 * Created by tkyaji on 2016/08/04.
 */
public class FacebookAdapter implements BannerAdAdapter, InterstitialAdAdapter, AdListener, com.facebook.ads.InterstitialAdListener {

    private static final String TAG = FacebookAdapter.class.getSimpleName();

    private Activity activity;
    private String placementId;
    private BannerAdSize adSize;

    private AdView adView;
    private InterstitialAd interstitialAd;

    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.placementId = parameters.get("placement_id");
        this.activity = activity;
        this.adSize = adSize;
        this.bannerAdListener = bannerAdListener;

        Log.d(TAG, "bannerAdInitialize : placement_id=" + this.placementId);

        AdInternalSettings.setTestMode(testMode);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.adView = new AdView(this.activity, this.placementId, this.toFBAdSize(this.adSize));
        this.adView.setAdListener(this);
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
        this.adView.disableAutoRefresh();
        this.adView = null;
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.placementId = parameters.get("placement_id");
        this.interstitialAdListener = interstitialAdListener;

        Log.d(TAG, "interstitialAdInitialize : placement_id=" + this.placementId);

        AdInternalSettings.setTestMode(testMode);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");
        this.interstitialAd = new InterstitialAd(this.activity, this.placementId);
        this.interstitialAd.setAdListener(this);
        this.interstitialAd.loadAd();
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow : canShow=true");
        this.interstitialAd.show();
        this.interstitialAdListener.interstitialAdShown(this);
    }


    @Override
    public void onError(Ad ad, AdError adError) {
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onError : error:(" + adError.getErrorCode() + ")" + adError.getErrorMessage());
            this.bannerAdListener.bannerAdReceived(this, false);
            this.adView.disableAutoRefresh();
            this.adView = null;
        }

        if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onError : error:(" + adError.getErrorCode() + ")" + adError.getErrorMessage());
            this.interstitialAdListener.interstitialAdLoaded(this, false);
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdLoaded");
            this.bannerAdListener.bannerAdReceived(this, true);
        }

        if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdLoaded");
            this.interstitialAdListener.interstitialAdLoaded(this, true);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdClicked");
            this.bannerAdListener.bannerAdClicked(this);
        }

        if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdClicked");
            this.interstitialAdListener.interstitialAdClicked(this);
        }
    }


    @Override
    public void onInterstitialDisplayed(Ad ad) {
        Log.d(TAG, "interstitial onInterstitialDisplayed");
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        Log.d(TAG, "interstitial onInterstitialDismissed");
        this.interstitialAdListener.interstitialAdClosed(this, true, false);
        this.interstitialAd.destroy();
        this.interstitialAd = null;
    }


    private AdSize toFBAdSize(BannerAdSize adSize) {
        switch (adSize) {
            case SIZE_320X50:
                return AdSize.BANNER_HEIGHT_50;

            case SIZE_320X100:
                return AdSize.BANNER_HEIGHT_90;

            case SIZE_300X250:
                return AdSize.RECTANGLE_HEIGHT_250;
        }

        return AdSize.BANNER_HEIGHT_50;
    }

}
