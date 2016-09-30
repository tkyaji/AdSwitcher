package net.adswitcher.adapter.five;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import com.five_corp.ad.FiveAd;
import com.five_corp.ad.FiveAdConfig;
import com.five_corp.ad.FiveAdFormat;
import com.five_corp.ad.FiveAdInterface;
import com.five_corp.ad.FiveAdInterstitial;
import com.five_corp.ad.FiveAdListener;
import com.five_corp.ad.FiveAdState;
import com.five_corp.ad.FiveAdW320H180;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

/**
 * Created by tkyaji on 2016/07/20.
 */
public class FiveAdapter implements BannerAdAdapter, InterstitialAdAdapter, FiveAdListener {

    private static final String TAG = "FiveAdapter";

    volatile private static boolean initializedSdk;
    private static Object lock = new Object();

    private Activity activity;

    private BannerAdListener bannerAdListener;
    private FiveAdW320H180 bannerView;
    private String slotId;

    private InterstitialAdListener interstitialAdListener;
    FiveAdInterstitial interstitial;
    private boolean loading;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        if (adSize != BannerAdSize.SIZE_300X250) {
            return;
        }

        this.activity = activity;
        this.bannerAdListener = bannerAdListener;

        String appId = parameters.get("app_id");
        this.slotId = parameters.get("slot_id");
        Log.d(TAG, "bannerAdInitialize : app_id=" + appId + ", slot_id=" + this.slotId);

        initializeSdk(activity, appId, testMode);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.loading = true;

        // 300x250のレクタングルは用意されていないらしいので、320x180で表示
        this.bannerView = new FiveAdW320H180(this.activity, this.slotId);
        this.bannerView.setListener(this);
        this.bannerView.loadAd();
    }

    @Override
    public void bannerAdShow(FrameLayout frameLayout) {
        Log.d(TAG, "bannerAdShow");
        frameLayout.addView(this.bannerView);
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");

        if (this.bannerView == null) {
            return;
        }
        ((FrameLayout)this.bannerView.getParent()).removeView(this.bannerView);
        this.bannerView = null;
    }



    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        String appId = parameters.get("app_id");
        this.slotId = parameters.get("slot_id");
        Log.d(TAG, "interstitialAdInitialize : app_id=" + appId + ", slot_id=" + this.slotId);

        initializeSdk(activity, appId, testMode);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");
        this.loading = true;
        this.interstitial = new FiveAdInterstitial(this.activity, this.slotId);
        this.interstitial.setListener(this);
        this.interstitial.loadAd();
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        boolean result = this.interstitial.show();
        if (result) {
            this.interstitialAdListener.interstitialAdShown(this);
        } else {
            this.interstitialAdListener.interstitialAdClosed(this, false, false);
        }
    }


    private static void initializeSdk(Activity activity, String appId, boolean testMode) {
        synchronized (lock) {
            if (initializedSdk) {
                return;
            }

            FiveAdConfig fiveConfig = new FiveAdConfig(appId);
            fiveConfig.formats = EnumSet.of(
                    FiveAdFormat.W320_H180,
//                    FiveAdFormat.W300_H250,
                    FiveAdFormat.INTERSTITIAL_PORTRAIT,
                    FiveAdFormat.INTERSTITIAL_LANDSCAPE
            );
            fiveConfig.isTest = testMode;

            FiveAd.initialize(activity, fiveConfig);

            FiveAd.getSingleton().enableLoading(true);

            initializedSdk = true;
        }
    }


    @Override
    public void onFiveAdLoad(FiveAdInterface fiveAdInterface) {
        Log.d(TAG, "onFiveAdLoad");

        if (this.loading) {
            if (this.bannerAdListener != null) {
                this.bannerAdListener.bannerAdReceived(this, true);
            }

            if (this.interstitialAdListener != null) {
                this.interstitialAdListener.interstitialAdLoaded(this, true);
            }
            this.loading = false;
        }
    }

    @Override
    public void onFiveAdError(FiveAdInterface fiveAdInterface, ErrorCode errorCode) {
        Log.e(TAG, "onFiveAdError : errorCode=" + errorCode);

        if (this.loading) {
            if (this.bannerAdListener != null) {
                this.bannerAdListener.bannerAdReceived(this, false);
            }

            if (this.interstitialAdListener != null) {
                this.interstitialAdListener.interstitialAdLoaded(this, false);
            }
            this.loading = false;
        }
    }

    @Override
    public void onFiveAdClick(FiveAdInterface fiveAdInterface) {
        Log.d(TAG, "onFiveAdClick");
        if (this.bannerAdListener != null) {
            this.bannerAdListener.bannerAdClicked(this);
        }

        if (this.interstitialAdListener != null) {
            this.interstitialAdListener.interstitialAdClicked(this);
        }
    }

    @Override
    public void onFiveAdClose(FiveAdInterface fiveAdInterface) {
        Log.d(TAG, "onFiveAdClose");
        if (this.interstitialAdListener != null) {
            this.interstitialAdListener.interstitialAdClosed(this, true, false);
        }
    }

    @Override
    public void onFiveAdStart(FiveAdInterface fiveAdInterface) {
        Log.d(TAG, "onFiveAdStart");
    }

    @Override
    public void onFiveAdPause(FiveAdInterface fiveAdInterface) {
        Log.d(TAG, "onFiveAdPause");
    }

    @Override
    public void onFiveAdResume(FiveAdInterface fiveAdInterface) {
        Log.d(TAG, "onFiveAdResume");
    }

    @Override
    public void onFiveAdViewThrough(FiveAdInterface fiveAdInterface) {
        Log.d(TAG, "onFiveAdViewThrough");
    }

    @Override
    public void onFiveAdReplay(FiveAdInterface fiveAdInterface) {
        Log.d(TAG, "onFiveAdReplay");
    }
}
