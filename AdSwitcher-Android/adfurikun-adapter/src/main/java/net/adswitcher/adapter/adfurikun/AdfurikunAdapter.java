package net.adswitcher.adapter.adfurikun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;

import net.adswitcher.AdSwitcherNativeAdData;
import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;
import net.adswitcher.adapter.NativeAdAdapter;
import net.adswitcher.adapter.NativeAdListener;

import java.lang.reflect.Field;
import java.util.Map;

import jp.tjkapp.adfurikunsdk.AdWebView;
import jp.tjkapp.adfurikunsdk.AdfurikunIntersAd;
import jp.tjkapp.adfurikunsdk.AdfurikunLayout;
import jp.tjkapp.adfurikunsdk.AdfurikunNativeAd;
import jp.tjkapp.adfurikunsdk.FileUtil;
import jp.tjkapp.adfurikunsdk.LayoutBase;
import jp.tjkapp.adfurikunsdk.OnAdfurikunIntersAdFinishListener;

/**
 * Created by tkyaji on 2016/11/11.
 */

public class AdfurikunAdapter implements BannerAdAdapter, InterstitialAdAdapter, NativeAdAdapter {

    private static final String TAG = "AdfurikunAdapter";

    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;
    private NativeAdListener nativeAdListener;

    private Activity activity;
    private String appId;
    private BannerAdSize adSize;
    private AdfurikunLayoutWrapper adfurikunLayout;
    private FrameLayout layout;
    private AdfurikunNativeAd.AdfurikunNativeAdInfo adfurikunNativeAdInfo;
    private AdfurikunNativeAd adfurikunNativeAd;
    private AdSwitcherNativeAdData nativeAdData;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.adSize = adSize;

        if (testMode) {
            FileUtil.setTestMode(this.activity.getApplicationContext(), 0);
        } else {
            FileUtil.setTestMode(this.activity.getApplicationContext(), -1);
        }

        this.appId = parameters.get("app_id");
        Log.d(TAG, "bannerAdInitialize : app_id=" + this.appId);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.adfurikunLayout = new AdfurikunLayoutWrapper(this.activity);
        this.adfurikunLayout.setAdfurikunAppKey(this.appId);
        this.adfurikunLayout.setOnAdClickListener(new OnAdClickListener() {
            @Override
            public void adClickListener() {
                Log.d(TAG, "bannerAdClicked");
                AdfurikunAdapter.this.bannerAdListener.bannerAdClicked(AdfurikunAdapter.this);
            }
        });

        this.layout = new FrameLayout(this.activity);
        this.layout.addView(this.adfurikunLayout, this.toLayoutParams(this.adSize));
        this.adfurikunLayout.nextAd();

        if (this.adfurikunLayout.isFinishedFirstLoad()) {
            this.bannerAdListener.bannerAdReceived(this, true);

        } else {
            this.bannerAdLoad(1);
        }
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "bannerAdShow");
        parentLayout.addView(this.layout);
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");
        this.layout.removeView(this.adfurikunLayout);
        ((FrameLayout)this.layout.getParent()).removeView(this.layout);
        this.adfurikunLayout = null;
        this.layout = null;
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        this.appId = parameters.get("app_id");
        Log.d(TAG, "interstitialAdInitialize : app_id=" + this.appId);

        if (testMode) {
            FileUtil.setTestMode(this.activity.getApplicationContext(), 0);
        }

        AdfurikunIntersAd.addIntersAdSetting(this.activity, this.appId, null, 1, 0, null, null);
    }

    @Override
    public void interstitialAdLoad() {
        if (AdfurikunIntersAd.isLoadFinished(0)) {
            this.interstitialAdListener.interstitialAdLoaded(this, true);

        } else {
            this.interstitialAdLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        AdfurikunIntersAd.showIntersAd(this.activity, 0, new OnAdfurikunIntersAdFinishListener() {
            @Override
            public void onAdfurikunIntersAdClose(int i) {
                AdfurikunAdapter.this.interstitialAdListener.interstitialAdClosed(AdfurikunAdapter.this, true, false);
            }

            @Override
            public void onAdfurikunIntersAdCustomClose(int i) {
                AdfurikunAdapter.this.interstitialAdListener.interstitialAdClosed(AdfurikunAdapter.this, true, false);
            }

            @Override
            public void onAdfurikunIntersAdSkip(int i) {
                AdfurikunAdapter.this.interstitialAdListener.interstitialAdClosed(AdfurikunAdapter.this, false, false);
            }

            @Override
            public void onAdfurikunIntersAdMaxEnd(int i) {
                AdfurikunAdapter.this.interstitialAdListener.interstitialAdClosed(AdfurikunAdapter.this, false, false);
            }

            @Override
            public void onAdfurikunIntersAdError(int i, int i1) {
                AdfurikunAdapter.this.interstitialAdListener.interstitialAdClosed(AdfurikunAdapter.this, false, false);
            }
        });
    }


    @Override
    public void nativeAdInitialize(Activity activity, NativeAdListener nativeAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.nativeAdListener = nativeAdListener;

        this.appId = parameters.get("app_id");
        Log.d(TAG, "nativeAdInitialize : app_id=" + this.appId);

        if (testMode) {
            FileUtil.setTestMode(this.activity.getApplicationContext(), 0);
        } else {
            FileUtil.setTestMode(this.activity.getApplicationContext(), -1);
        }

        this.adfurikunNativeAd = new AdfurikunNativeAd(this.activity, this.appId, new AdfurikunNativeAd.OnAdfurikunNativeAdListener() {
            @Override
            public void onNativeAdLoadFinish(AdfurikunNativeAd.AdfurikunNativeAdInfo adfurikunNativeAdInfo, String adnetworkKey) {
                Log.d(TAG, "onNativeAdLoadFinish : adnetworkKey=" + adnetworkKey);
                AdfurikunAdapter.this.adfurikunNativeAdInfo = adfurikunNativeAdInfo;
                AdfurikunAdapter.this.nativeAdData = new AdSwitcherNativeAdData();
                AdfurikunAdapter.this.nativeAdData.shortText = adfurikunNativeAdInfo.title;
                AdfurikunAdapter.this.nativeAdData.longText = adfurikunNativeAdInfo.text;
                AdfurikunAdapter.this.nativeAdData.iconImageUrl = adfurikunNativeAdInfo.img_url;
                // AdfurikunAdapter.this.nativeAdData.imageUrl = adfurikunNativeAdInfo.img_url;

                AdfurikunAdapter.this.nativeAdListener.nativeAdReceived(AdfurikunAdapter.this, true);
            }

            @Override
            public void onNativeAdLoadError(int error, String adnetworkKey) {
                Log.d(TAG, "onNativeAdLoadError : error=" + error + ", adnetworkKey=" + adnetworkKey);
                AdfurikunAdapter.this.nativeAdListener.nativeAdReceived(AdfurikunAdapter.this, false);
            }
        });
    }

    @Override
    public void nativeAdLoad() {
        this.adfurikunNativeAd.getNativeAd();
    }

    @Override
    public AdSwitcherNativeAdData getAdData() {
        return this.nativeAdData;
    }

    @Override
    public void openUrl() {
        if (this.adfurikunNativeAdInfo != null) {
            this.adfurikunNativeAd.recClick();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.adfurikunNativeAdInfo.link_url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.activity.startActivity(intent);
        }
    }

    @Override
    public void sendImpression() {
        // Adfurikunはインプレッション送信なし
    }


    private FrameLayout.LayoutParams toLayoutParams(BannerAdSize adSize) {

        Display display = this.activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealMetrics(metrics);
        } else {
            display.getMetrics(metrics);
        }

        switch (adSize) {
            case SIZE_320X100:
                return new FrameLayout.LayoutParams(Math.round(320 * metrics.density), Math.round(100 * metrics.density));

            case SIZE_300X250:
                return new FrameLayout.LayoutParams(Math.round(300 * metrics.density), Math.round(250 * metrics.density));

            default:
                return new FrameLayout.LayoutParams(Math.round(320 * metrics.density), Math.round(50 * metrics.density));
        }
    }

    private void bannerAdLoad(final int count) {
        Log.d(TAG, "bannerAdLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AdfurikunAdapter.this.adfurikunLayout.isFinishedFirstLoad()) {
                    AdfurikunAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdfurikunAdapter.this.bannerAdListener.bannerAdReceived(AdfurikunAdapter.this, true);
                        }
                    });

                } else if (count == 5) {
                    AdfurikunAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdfurikunAdapter.this.bannerAdListener.bannerAdReceived(AdfurikunAdapter.this, false);
                        }
                    });

                } else {
                    AdfurikunAdapter.this.bannerAdLoad(count + 1);
                }
            }
        }, 1000);
    }

    private void interstitialAdLoad(final int count) {
        Log.d(TAG, "interstitialAdLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AdfurikunIntersAd.isLoadFinished(0)) {
                    AdfurikunAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdfurikunAdapter.this.interstitialAdListener.interstitialAdLoaded(AdfurikunAdapter.this, true);
                        }
                    });

                } else if (count == 5) {
                    AdfurikunAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdfurikunAdapter.this.interstitialAdListener.interstitialAdLoaded(AdfurikunAdapter.this, false);
                        }
                    });

                } else {
                    AdfurikunAdapter.this.interstitialAdLoad(count + 1);
                }
            }
        }, 1000);
    }


    // クリックイベントが設定できないため、リフレクションで無理矢理設定する
    private class AdfurikunLayoutWrapper extends AdfurikunLayout {

        private AdWebView.OnActionListener originalOnActionListener;
        private OnAdClickListener onAdClickListener;

        public AdfurikunLayoutWrapper(Context context) {
            super(context);
            this.initListener();
        }

        public void setOnAdClickListener(OnAdClickListener listener) {
            this.onAdClickListener = listener;
        }

        private void initListener() {
            this.originalOnActionListener = this.mAdViewActionListener;

            try {
                Field field = LayoutBase.class.getDeclaredField("mAdViewActionListener");
                field.setAccessible(true);

                field.set(this, new AdWebView.OnActionListener() {
                    @Override
                    public void clickAd(AdWebView adWebView) {
                        if (onAdClickListener != null) {
                            onAdClickListener.adClickListener();
                        }
                        originalOnActionListener.clickAd(adWebView);
                    }
                    @Override
                    public void closeWindow(AdWebView adWebView) {
                        originalOnActionListener.closeWindow(adWebView);
                    }
                    @Override
                    public void loadError(AdWebView adWebView) {
                        originalOnActionListener.loadError(adWebView);
                    }
                    @Override
                    public void loadSuccess(AdWebView adWebView) {
                        originalOnActionListener.loadSuccess(adWebView);
                    }
                });

            } catch (NoSuchFieldException e) {
                Log.d(TAG, e.getLocalizedMessage(), e);

            } catch (IllegalAccessException e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    private interface OnAdClickListener {
        void adClickListener();
    }
}
