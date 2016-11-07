package net.adswitcher.adapter.zucks;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;
import net.zucks.listener.AdBannerListener;
import net.zucks.listener.AdInterstitialListener;
import net.zucks.view.AdBanner;
import net.zucks.view.AdInterstitial;

import java.util.Map;

/**
 * Created by tkyaji on 2016/11/07.
 */
public class ZucksAdapter implements BannerAdAdapter, InterstitialAdAdapter {

    private static final String TAG = "ZucksAdapter";

    private Activity activity;
    private String frameId;
    private FrameLayout layout;
    private AdBanner banner;
    private AdInterstitial interstitial;
    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.frameId = parameters.get("frame_id");

        Log.d(TAG, "bannerAdInitialize : frame_id=" + this.frameId);

        if (testMode) {
            this.frameId = "_833b45aa06";
        }
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.banner = new AdBanner(this.activity, this.frameId, this.createBannerListener());

        this.layout = new FrameLayout(this.activity);
        this.layout.addView(this.banner);
        this.banner.load();
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "bannerAdShow");
        parentLayout.addView(this.layout);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");
        this.layout.removeView(this.banner);
        ((FrameLayout)this.layout.getParent()).removeView(this.layout);
        this.banner = null;
        this.layout = null;
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;
        this.frameId = parameters.get("frame_id");

        Log.d(TAG, "bannerAdInitialize : frame_id=" + this.frameId);

        if (testMode) {
            this.frameId = "_4548cd7e07";
        }
        this.interstitial = new AdInterstitial(this.activity, this.frameId, this.createInterstitialListener());
    }

    @Override
    public void interstitialAdLoad() {
        this.interstitial.load();
    }

    @Override
    public void interstitialAdShow() {
        this.interstitial.show();
    }

    private AdBannerListener createBannerListener() {
        return new AdBannerListener() {
            @Override
            public void onReceiveAd(AdBanner banner) {
                super.onReceiveAd(banner);
                ZucksAdapter.this.bannerAdListener.bannerAdReceived(ZucksAdapter.this, true);
            }
            @Override
            public void onTapAd(AdBanner banner) {
                super.onTapAd(banner);
                ZucksAdapter.this.bannerAdListener.bannerAdClicked(ZucksAdapter.this);
            }
            @Override
            public void onFailure(AdBanner banner, Exception e) {
                super.onFailure(banner, e);
                Log.d(TAG, e.getLocalizedMessage(), e);
                ZucksAdapter.this.bannerAdListener.bannerAdReceived(ZucksAdapter.this, false);
            }
            @Override
            public void onBackApplication(AdBanner banner) {
                super.onBackApplication(banner);
            }
        };
    }

    private AdInterstitialListener createInterstitialListener() {
        return new AdInterstitialListener() {
            @Override
            public void onReceiveAd() {
                super.onReceiveAd();
                ZucksAdapter.this.interstitialAdListener.interstitialAdLoaded(ZucksAdapter.this, true);
            }
            @Override
            public void onShowAd() {
                super.onShowAd();
                ZucksAdapter.this.interstitialAdListener.interstitialAdShown(ZucksAdapter.this);
            }
            @Override
            public void onCancelDisplayRate() {
                super.onCancelDisplayRate();
            }
            @Override
            public void onTapAd() {
                super.onTapAd();
                ZucksAdapter.this.interstitialAdListener.interstitialAdClicked(ZucksAdapter.this);
            }
            @Override
            public void onCloseAd() {
                super.onCloseAd();
                ZucksAdapter.this.interstitialAdListener.interstitialAdClosed(ZucksAdapter.this, true, false);
            }
            @Override
            public void onLoadFailure(Exception e) {
                super.onLoadFailure(e);
                Log.d(TAG, e.getLocalizedMessage(), e);
                ZucksAdapter.this.interstitialAdListener.interstitialAdLoaded(ZucksAdapter.this, false);
            }
            @Override
            public void onShowFailure(Exception e) {
                super.onShowFailure(e);
                Log.d(TAG, e.getLocalizedMessage(), e);
                ZucksAdapter.this.interstitialAdListener.interstitialAdClosed(ZucksAdapter.this, false, false);
            }
        };
    }
}
