package net.adswitcher;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;

import net.adswitcher.adapter.AdAdapter;
import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.config.AdConfig;
import net.adswitcher.config.AdSwitcherConfig;
import net.adswitcher.config.AdSwitcherConfigLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tkyaji on 2016/09/04.
 */
public class AdSwitcherBannerView extends FrameLayout implements BannerAdListener {

    private static final String TAG = "AdSwitcherBannerView";

    private AdReceivedListener adReceivedListener;
    private AdShownListener adShownListener;
    private AdClickedListener adClickedListener;

    private Activity activity;
    private AdSwitcherConfig adSwitcherConfig;
    private boolean testMode;
    private BannerAdSize adSize;

    private BannerAdAdapter selectedAdapter;
    private AdConfig selectedAdConfig;
    private AdSelector adSelector;
    private Map<String, BannerAdAdapter> adapterCacheMap;
    private Map<String, AdConfig> adConfigMap;
    private boolean loading;


    public AdSwitcherBannerView(final Activity activity, final AdSwitcherConfigLoader configLoader,
                                final String category, final BannerAdSize adSize, final boolean testMode) {
        super(activity);

        configLoader.addConfigLoadedHandler(new AdSwitcherConfigLoader.ConfigLoadHandler() {
            @Override
            public void onLoaded() {
                AdSwitcherConfig adSwitcherConfig = configLoader.getAdSwitcherConfig(category);
                AdSwitcherBannerView.this.initialize(activity, adSwitcherConfig, testMode, adSize);
            }
        });
    }

    public AdSwitcherBannerView(final Activity activity, final AdSwitcherConfig adSwitcherConfig,
                                final BannerAdSize adSize, final boolean testMode) {
        super(activity);

        this.initialize(activity, adSwitcherConfig, testMode, adSize);
    }

    public void load() {
        Log.d(TAG, "load");
        if (this.loading) {
            Log.d(TAG, "Already started to load");
            return;
        }
        this.adSelector = new AdSelector(this.adSwitcherConfig);
        this.selectLoad();
    }

    public void hide() {
        Log.d(TAG, "hide");
        if (this.selectedAdapter != null) {
            this.selectedAdapter.bannerAdHide();
            this.selectedAdapter = null;
            this.selectedAdConfig = null;
        }
    }

    public void switchAd() {
        Log.d(TAG, "switchAd");
        this.hide();
        this.load();
    }

    public boolean isLoaded() {
        return (this.selectedAdapter != null);
    }

    public float getDpiWidth() {
        switch (this.adSize) {
            case SIZE_320X50:
            case SIZE_320X100:
                return 320;
            case SIZE_300X250:
                return 300;
        }
        return 0;
    }

    public float getDpiHeight() {
        switch (this.adSize) {
            case SIZE_320X50:
                return 50;
            case SIZE_320X100:
                return 100;
            case SIZE_300X250:
                return 250;
        }
        return 0;
    }

    public float getPxWidth() {
        float dpiW = this.getDpiWidth();
        DisplayMetrics metrics = this.getMetrics();
        return dpiW * metrics.density;
    }

    public float getPxHeight() {
        float dpiH = this.getDpiHeight();
        DisplayMetrics metrics = this.getMetrics();
        return dpiH * metrics.density;
    }

    private DisplayMetrics getMetrics() {
        Display display = this.activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealMetrics(metrics);
        } else {
            display.getMetrics(metrics);
        }
        return metrics;
    }

    public void setAdReceivedListener(final AdReceivedListener listener) {
        this.adReceivedListener = new AdReceivedListener() {
            @Override
            public void onAdReceived(final AdConfig config, final boolean result) {
                AdSwitcherBannerView.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdReceived(config, result);
                    }
                });
            }
        };
    }

    public void setAdShownListener(final AdShownListener listener) {
        this.adShownListener = new AdShownListener() {
            @Override
            public void onAdShown(final AdConfig config) {
                AdSwitcherBannerView.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdShown(config);
                    }
                });
            }
        };
    }

    public void setAdClickedListener(final AdClickedListener listener) {
        this.adClickedListener = new AdClickedListener() {
            @Override
            public void onAdClicked(final AdConfig config) {
                AdSwitcherBannerView.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdClicked(config);
                    }
                });
            }
        };
    }


    private void initialize(Activity activity, AdSwitcherConfig adSwitcherConfig, boolean testMode, BannerAdSize adSize) {
        Log.d(TAG, "testMode=" + testMode + ", adSize=" + adSize);

        this.activity = activity;
        this.adSwitcherConfig = adSwitcherConfig;
        this.testMode = testMode;
        this.adSize = adSize;

        this.adapterCacheMap = new HashMap<>();
        this.adConfigMap = new HashMap<>();
        for (AdConfig config : this.adSwitcherConfig.adConfigList) {
            this.adConfigMap.put(config.className, config);
        }
    }

    private void selectLoad() {
        this.loading = true;

        BannerAdAdapter bannerAdAdapter = null;
        while (bannerAdAdapter == null) {
            this.selectedAdConfig = this.adSelector.selectAd();
            if (this.selectedAdConfig == null) {
                break;
            }

            this.initAdapter(this.selectedAdConfig);
            bannerAdAdapter = this.adapterCacheMap.get(this.selectedAdConfig.className);
        }

        if (bannerAdAdapter != null) {
            Log.d(TAG, bannerAdAdapter.getClass().getName());
            bannerAdAdapter.bannerAdLoad();

        } else {
            this.loading = false;
            Log.w(TAG, "It will not be able to display all.");

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdSwitcherBannerView.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdSwitcherBannerView.this.load();
                        }
                    });
                }
            }, 10000);
        }
    }

    private void initAdapter(AdConfig adConfig) {
        if (this.adapterCacheMap.containsKey(adConfig.className)) {
            return;
        }

        BannerAdAdapter bannerAdAdapter = null;
        try {
            Class<?> adAdapterClass = Class.forName(adConfig.className);
            bannerAdAdapter = (BannerAdAdapter) adAdapterClass.newInstance();

        } catch (ClassNotFoundException ex) {
            Log.w(TAG, adConfig.className + " class is not found.");
            return;

        } catch (Throwable e) {
            Log.w(TAG, adConfig.className + " class is not conform BannerAdAdapter.");
            return;
        }

        bannerAdAdapter.bannerAdInitialize(this.activity, this, adConfig.parameters, this.testMode, this.adSize);

        this.adapterCacheMap.put(adConfig.className, bannerAdAdapter);
    }


    @Override
    public void bannerAdReceived(AdAdapter adAdapter, boolean result) {
        String className = adAdapter.getClass().getName();
        Log.d(TAG, "bannerAdReceived : " + className + ", result=" + result);

        if (this.selectedAdConfig == null) {
            Log.d(TAG, "class name is invalid.(null)");
            return;
        } else if (!this.selectedAdConfig.className.equals(className)) {
            Log.d(TAG, "class name is invalid. selectedClass=" + this.selectedAdConfig.className);
            return;
        }

        this.loading = false;

        if (this.adReceivedListener != null) {
            this.adReceivedListener.onAdReceived(this.adConfigMap.get(className), result);
        }

        if (this.selectedAdapter == null) {
            if (result) {
                this.selectedAdapter = (BannerAdAdapter) adAdapter;
                this.selectedAdapter.bannerAdShow(this);
            } else {
                this.selectLoad();
            }
        }
    }

    @Override
    public void bannerAdShown(AdAdapter adAdapter) {
        String className = adAdapter.getClass().getName();
        Log.d(TAG, "bannerAdShown : " + className);

        if (this.adShownListener != null) {
            this.adShownListener.onAdShown(this.adConfigMap.get(className));
        }
    }

    @Override
    public void bannerAdClicked(AdAdapter adAdapter) {
        String className = adAdapter.getClass().getName();
        Log.d(TAG, "bannerAdClicked : " + className);

        if (this.adClickedListener != null) {
            this.adClickedListener.onAdClicked(this.adConfigMap.get(className));
        }
    }


    public interface AdReceivedListener {
        void onAdReceived(AdConfig config, boolean result);
    }

    public interface AdShownListener {
        void onAdShown(AdConfig config);
    }

    public interface AdClickedListener {
        void onAdClicked(AdConfig config);
    }

}
