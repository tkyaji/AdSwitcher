package net.adswitcher.adapter.adgeneration;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGListener;
import com.socdm.d.adgeneration.ADGSettings;
import com.socdm.d.adgeneration.interstitial.ADGInterstitial;
import com.socdm.d.adgeneration.interstitial.ADGInterstitialListener;

import java.util.Map;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

/**
 * Created by tkyaji on 2016/08/04.
 */
public class AdGenerationAdapter implements BannerAdAdapter, InterstitialAdAdapter {

    private static final String TAG = "AdGenerationAdapter";

    private Activity activity;
    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;
    private boolean testMode;
    private String locationId;
    private ADG.AdFrameSize adSize;
    private ADGInterstitial adgInterstitial;

    private ADG adg;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        ADGSettings.setGeolocationEnabled(false);
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.testMode = testMode;
        this.locationId = parameters.get("location_id");
        this.adSize = toADGAdSize(adSize);

        Log.d(TAG, "bannerAdInitialize location_id=" + this.locationId);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.adg = new ADG(this.activity);
        this.adg.setLocationId(this.locationId);
        this.adg.setAdFrameSize(this.adSize);
        this.adg.setAdListener(this.getBannerListener());
        this.adg.setReloadWithVisibilityChanged(false);
        this.adg.setFillerRetry(false);
        this.adg.setEnableTestMode(this.testMode);
        this.adg.start();
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "bannerAdShow");
        parentLayout.addView(this.adg);
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");
        ((FrameLayout)this.adg.getParent()).removeView(this.adg);
        this.adg.destroyAdView();
        this.adg = null;
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        ADGSettings.setGeolocationEnabled(false);
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;
        this.testMode = testMode;
        this.locationId = parameters.get("location_id");

        Log.d(TAG, "interstitialAdInitialize location_id=" + this.locationId);

        this.adgInterstitial = new ADGInterstitial(activity);
        this.adgInterstitial.setLocationId(this.locationId);
        this.adgInterstitial.setAdListener(this.getInterstitialListener());
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");
        this.adgInterstitial.preload();
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        this.adgInterstitial.show();
    }


    private ADG.AdFrameSize toADGAdSize(BannerAdSize adSize) {
        switch (adSize) {
            case SIZE_320X50:
                return ADG.AdFrameSize.SP;

            case SIZE_320X100:
                return ADG.AdFrameSize.LARGE;

            case SIZE_300X250:
                return ADG.AdFrameSize.RECT;
        }
        return ADG.AdFrameSize.SP;
    }

    private ADGListener getBannerListener() {
        return new ADGListener() {
            @Override
            public void onReceiveAd() {
                Log.d(TAG, "banner onReceiveAd");
                AdGenerationAdapter.this.bannerAdListener.bannerAdReceived(AdGenerationAdapter.this, true);
            }

            @Override
            public void onFailedToReceiveAd(ADGConsts.ADGErrorCode code) {
                Log.d(TAG, "banner onFailedToReceiveAd code=" + code);
                AdGenerationAdapter.this.bannerAdListener.bannerAdReceived(AdGenerationAdapter.this, false);
            }

            @Override
            public void onOpenUrl() {
                Log.d(TAG, "banner onOpenUrl");
                AdGenerationAdapter.this.bannerAdListener.bannerAdClicked(AdGenerationAdapter.this);
            }
        };
    }

    private ADGInterstitialListener getInterstitialListener() {
        return new ADGInterstitialListener() {
            @Override
            public void onReceiveAd() {
                Log.d(TAG, "onReceiveAd");
                AdGenerationAdapter.this.interstitialAdListener.interstitialAdLoaded(AdGenerationAdapter.this, true);
            }

            @Override
            public void onFailedToReceiveAd(ADGConsts.ADGErrorCode code) {
                Log.d(TAG, "onFailedToReceiveAd : errorCode=" + code);
                AdGenerationAdapter.this.interstitialAdListener.interstitialAdLoaded(AdGenerationAdapter.this, false);
            }

            @Override
            public void onCloseInterstitial() {
                Log.d(TAG, "onCloseInterstitial");
                AdGenerationAdapter.this.interstitialAdListener.interstitialAdClosed(AdGenerationAdapter.this, true, false);
            }

            @Override
            public void onOpenUrl() {
                Log.d(TAG, "onOpenUrl");
                AdGenerationAdapter.this.interstitialAdListener.interstitialAdClicked(AdGenerationAdapter.this);
            }
        };
    }
}
