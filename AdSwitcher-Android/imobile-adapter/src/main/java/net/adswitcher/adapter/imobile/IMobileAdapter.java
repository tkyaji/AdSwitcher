package net.adswitcher.adapter.imobile;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

import jp.co.imobile.sdkads.android.FailNotificationReason;
import jp.co.imobile.sdkads.android.ImobileSdkAd;
import jp.co.imobile.sdkads.android.ImobileSdkAdListener;

/**
 * Created by tkyaji on 2016/08/09.
 */
public class IMobileAdapter extends ImobileSdkAdListener implements BannerAdAdapter, InterstitialAdAdapter {

    private static final String TAG = "IMobileAdapter";

    private Activity activity;
    private String spotId;
    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;
    private FrameLayout adView;
    private boolean isLoaded;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {

        this.activity = activity;
        this.bannerAdListener = bannerAdListener;

        String partnerId = parameters.get("partner_id");
        String mediaId = parameters.get("media_id");
        this.spotId = parameters.get("spot_id");

        ImobileSdkAd.setTestMode(testMode);
        ImobileSdkAd.registerSpotInline(activity, partnerId, mediaId, this.spotId);
        ImobileSdkAd.setImobileSdkAdListener(this.spotId, this);
        ImobileSdkAd.start(this.spotId);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");

        this.adView = new FrameLayout(this.activity);
        ImobileSdkAd.showAd(this.activity, this.spotId, this.adView);

        if (this.isLoaded) {
            this.bannerAdListener.bannerAdReceived(this, true);

        } else {
            this.adLoad(1);
        }
    }

    @Override
    public void bannerAdShow(FrameLayout frameLayout) {
        frameLayout.addView(this.adView);
    }

    @Override
    public void bannerAdHide() {
        ((FrameLayout)this.adView.getParent()).removeView(this.adView);
        this.adView = null;
        this.isLoaded = false;
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {

        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        String partnerId = parameters.get("partner_id");
        String mediaId = parameters.get("media_id");
        this.spotId = parameters.get("spot_id");

        Log.d(TAG, "interstitialAdInitialize : partner_id=" + partnerId + ", media_id=" + mediaId + ", spot_id=" + this.spotId);

        ImobileSdkAd.setTestMode(testMode);
        ImobileSdkAd.registerSpotFullScreen(activity, partnerId, mediaId, this.spotId);
        ImobileSdkAd.setImobileSdkAdListener(this.spotId, this);
        ImobileSdkAd.start(this.spotId);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        if (this.isLoaded) {
            this.interstitialAdListener.interstitialAdLoaded(this, true);

        } else {
            this.adLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        ImobileSdkAd.showAd(this.activity, this.spotId);
        this.interstitialAdListener.interstitialAdShown(this);
    }


    @Override
    public void onAdReadyCompleted() {
        super.onAdReadyCompleted();

        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdReadyCompleted");
            this.isLoaded = true;
        }
        if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdReadyCompleted");
            this.isLoaded = true;
        }
    }

    @Override
    public void onFailed(FailNotificationReason failNotificationReason) {
        super.onFailed(failNotificationReason);

        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onFailed : reson=" + failNotificationReason);
        }
        if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onFailed : reson=" + failNotificationReason);
        }
    }

    @Override
    public void onAdCliclkCompleted() {
        super.onAdCliclkCompleted();
        if (this.bannerAdListener != null) {
            Log.d(TAG, "banner onAdCliclkCompleted");
            this.bannerAdListener.bannerAdClicked(this);
        }
        if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdCliclkCompleted");
            this.interstitialAdListener.interstitialAdClicked(this);
        }
    }

    @Override
    public void onAdCloseCompleted() {
        super.onAdCloseCompleted();

        this.isLoaded = false;
        if (this.interstitialAdListener != null) {
            Log.d(TAG, "interstitial onAdCloseCompleted");
            this.interstitialAdListener.interstitialAdClosed(this, true, false);
        }
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (IMobileAdapter.this.isLoaded) {
                    IMobileAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (IMobileAdapter.this.bannerAdListener != null) {
                                IMobileAdapter.this.bannerAdListener.bannerAdReceived(IMobileAdapter.this, true);
                            }
                            if (IMobileAdapter.this.interstitialAdListener != null) {
                                IMobileAdapter.this.interstitialAdListener.interstitialAdLoaded(IMobileAdapter.this, true);
                            }
                        }
                    });
                } else if (count == 3) {
                    IMobileAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (IMobileAdapter.this.bannerAdListener != null) {
                                IMobileAdapter.this.bannerAdListener.bannerAdReceived(IMobileAdapter.this, false);
                            }
                            if (IMobileAdapter.this.interstitialAdListener != null) {
                                IMobileAdapter.this.interstitialAdListener.interstitialAdLoaded(IMobileAdapter.this, false);
                            }
                        }
                    });
                } else {
                    IMobileAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }
}
