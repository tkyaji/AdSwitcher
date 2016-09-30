package net.adswitcher.adapter.nend;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import net.nend.android.NendAdInterstitial;
import net.nend.android.NendAdListener;
import net.nend.android.NendAdView;

import java.util.Map;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

/**
 * Created by tkyaji on 2016/07/20.
 */
public class NendAdapter implements BannerAdAdapter, InterstitialAdAdapter, NendAdListener {

    private static final String TAG = "NendAdapter";

    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;

    private NendAdView nendAdView;

    private Activity activity;
    private String apiKey;
    private int spotId;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.apiKey = parameters.get("api_key");
        String spotIdStr = parameters.get("spot_id");
        this.spotId = Integer.parseInt(spotIdStr);

        Log.d(TAG, "bannerAdInitialize : api_key=" + this.apiKey + ", spot_id=" + spotIdStr);

        if (testMode) {
            switch (adSize) {
                case SIZE_320X50:
                    this.apiKey = "c5cb8bc474345961c6e7a9778c947957ed8e1e4f";
                    this.spotId = 3174;
                    break;

                case SIZE_320X100:
                    this.apiKey = "8932b68d22d1d32f5d7251f9897a6aa64117995e";
                    this.spotId = 71000;
                    break;

                case SIZE_300X250:
                    this.apiKey = "499f011dbec5d37cfa388b749aed2bfff440a794";
                    this.spotId = 70357;
                    break;
            }
        }
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.nendAdView = new NendAdView(this.activity, this.spotId, this.apiKey);
        this.nendAdView.setListener(this);
        this.nendAdView.loadAd();
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "bannerAdShow");
        parentLayout.addView(this.nendAdView);
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");
        ((FrameLayout)this.nendAdView.getParent()).removeView(this.nendAdView);
        this.nendAdView = null;
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        this.apiKey = parameters.get("api_key");
        String spotIdStr = parameters.get("spot_id");
        this.spotId = Integer.parseInt(spotIdStr);

        if (testMode) {
            this.apiKey = "8c278673ac6f676dae60a1f56d16dad122e23516";
            this.spotId = 213206;
        }

        Log.d(TAG, "interstitialAdInitialize : api_key=" + this.apiKey + ", spot_id=" + spotIdStr);

        NendAdInterstitial.isAutoReloadEnabled = false;
        NendAdInterstitial.setListener(new NendAdInterstitial.OnCompletionListener() {
            @Override
            public void onCompletion(NendAdInterstitial.NendAdInterstitialStatusCode nendAdInterstitialStatusCode) {
                if (nendAdInterstitialStatusCode == NendAdInterstitial.NendAdInterstitialStatusCode.SUCCESS) {
                    NendAdapter.this.interstitialAdListener.interstitialAdLoaded(NendAdapter.this, true);

                } else {
                    NendAdapter.this.interstitialAdListener.interstitialAdLoaded(NendAdapter.this, false);
                }
            }
        });
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");
        NendAdInterstitial.loadAd(this.activity.getApplicationContext(), this.apiKey, this.spotId);
    }

    @Override
    public void interstitialAdShow() {
        NendAdInterstitial.NendAdInterstitialShowResult result = NendAdInterstitial.showAd(this.activity, new NendAdInterstitial.OnClickListener() {
            @Override
            public void onClick(NendAdInterstitial.NendAdInterstitialClickType nendAdInterstitialClickType) {
                if (nendAdInterstitialClickType == NendAdInterstitial.NendAdInterstitialClickType.DOWNLOAD) {
                    NendAdapter.this.interstitialAdListener.interstitialAdClicked(NendAdapter.this);
                }
                NendAdapter.this.interstitialAdListener.interstitialAdClosed(NendAdapter.this, true, false);
            }
        });

        Log.d(TAG, "interstitialAdShow : result=" + result);

        if (result == NendAdInterstitial.NendAdInterstitialShowResult.AD_SHOW_SUCCESS) {
            this.interstitialAdListener.interstitialAdShown(this);

        } else {
            this.interstitialAdListener.interstitialAdClosed(this, false, false);
        }
    }


    @Override
    public void onReceiveAd(NendAdView nendAdView) {
        Log.d(TAG, "banner onReceiveAd");
        this.bannerAdListener.bannerAdReceived(this, true);
    }

    @Override
    public void onFailedToReceiveAd(NendAdView nendAdView) {
        Log.d(TAG, "banner onFailedToReceiveAd");
        this.bannerAdListener.bannerAdReceived(this, false);
    }

    @Override
    public void onClick(NendAdView nendAdView) {
        Log.d(TAG, "banner onClick");
        this.bannerAdListener.bannerAdClicked(this);
    }

    @Override
    public void onDismissScreen(NendAdView nendAdView) {
        Log.d(TAG, "banner onDismissScreen");
    }
}
