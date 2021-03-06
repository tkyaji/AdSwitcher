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
    private boolean loadCancel;
    private boolean showing;
    private boolean autoShow;


    public AdSwitcherBannerView(final Activity activity, final AdSwitcherConfigLoader configLoader,
                                final String category, final BannerAdSize adSize) {
        this(activity, configLoader, category, adSize, false);
    }

    public AdSwitcherBannerView(final Activity activity, final AdSwitcherConfigLoader configLoader,
                                final String category, final BannerAdSize adSize, final boolean testMode) {
        super(activity);
        this.activity = activity;
        this.adSize = adSize;
        this.testMode = testMode;

        configLoader.addConfigLoadedHandler(new AdSwitcherConfigLoader.ConfigLoadHandler() {
            @Override
            public void onLoaded() {
                final AdSwitcherConfig adSwitcherConfig = configLoader.getAdSwitcherConfig(category);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdSwitcherBannerView.this.initialize(adSwitcherConfig);
                    }
                });
            }
        });
    }

    public AdSwitcherBannerView(final Activity activity, final AdSwitcherConfig adSwitcherConfig,
                                final BannerAdSize adSize) {
        this(activity, adSwitcherConfig, adSize, false);
    }

    public AdSwitcherBannerView(final Activity activity, final AdSwitcherConfig adSwitcherConfig,
                                final BannerAdSize adSize, final boolean testMode) {
        super(activity);
        this.activity = activity;
        this.testMode = testMode;
        this.adSize = adSize;

        this.initialize(adSwitcherConfig);
    }

    public void load() {
        this.load(false);
    }

    public void load(boolean autoShow) {
        Log.d(TAG, "load : autoShow=" + autoShow);
        if (this.loading) {
            Log.d(TAG, "already started to load.");
            return;
        }
        if (this.showing) {
            Log.d(TAG, "ad showing.");
            return;
        }
        this.autoShow = autoShow;
        this.adSelector = new AdSelector(this.adSwitcherConfig);
        this.selectLoad();
    }

    public void show() {
        if (!this.isLoaded()) {
            Log.d(TAG, "also not loaded.");
            return;
        }
        if (this.showing) {
            Log.d(TAG, "already shown.");
            return;
        }
        this.selectedAdapter.bannerAdShow(this);
        this.showing = true;
    }

    public void hide() {
        Log.d(TAG, "hide");
        if (this.showing) {
            this.selectedAdapter.bannerAdHide();
            this.selectedAdapter = null;
            this.selectedAdConfig = null;
            this.showing = false;
        }
        if (this.loading) {
            this.loadCancel = true;
        }
    }

    public void switchAd() {
        Log.d(TAG, "switchAd");
        this.hide();
        this.load(true);
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


    private void initialize(AdSwitcherConfig adSwitcherConfig) {
        Log.d(TAG, "testMode=" + testMode + ", adSize=" + adSize);

        this.adSwitcherConfig = adSwitcherConfig;

        this.adapterCacheMap = new HashMap<>();
        this.adConfigMap = new HashMap<>();

        if (this.adSwitcherConfig != null) {
            for (AdConfig config : this.adSwitcherConfig.adConfigList) {
                this.adConfigMap.put(config.className, config);
            }
        }
    }

    private void selectLoad() {
        if (this.loadCancel) {
            this.loading = false;
            this.loadCancel = false;
            return;
        }
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
            final BannerAdAdapter _bannerAdAdapter = bannerAdAdapter;
            AdSwitcherBannerView.this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _bannerAdAdapter.bannerAdLoad();
                }
            });

        } else {
            Log.w(TAG, "It will not be able to display all.");

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdSwitcherBannerView.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "load retry");
                            AdSwitcherBannerView.this.adSelector = new AdSelector(AdSwitcherBannerView.this.adSwitcherConfig);
                            AdSwitcherBannerView.this.selectLoad();
                        }
                    });
                }
            }, 5000);
        }
    }

    private void initAdapter(final AdConfig adConfig) {
        if (this.adapterCacheMap.containsKey(adConfig.className)) {
            return;
        }

        try {
            Class<?> adAdapterClass = Class.forName(adConfig.className);
            final BannerAdAdapter bannerAdAdapter = (BannerAdAdapter) adAdapterClass.newInstance();

            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bannerAdAdapter.bannerAdInitialize(AdSwitcherBannerView.this.activity,
                            AdSwitcherBannerView.this, adConfig.parameters,
                            AdSwitcherBannerView.this.testMode, AdSwitcherBannerView.this.adSize);
                }
            });
            this.adapterCacheMap.put(adConfig.className, bannerAdAdapter);

        } catch (ClassNotFoundException ex) {
            Log.w(TAG, adConfig.className + " class is not found.");
            return;

        } catch (Throwable e) {
            Log.w(TAG, adConfig.className + " class is not conform BannerAdAdapter.");
            return;
        }
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

        if (this.selectedAdapter == null && result) {
            this.selectedAdapter = (BannerAdAdapter) adAdapter;
        }

        if (this.adReceivedListener != null) {
            this.adReceivedListener.onAdReceived(this.adConfigMap.get(className), result);
        }

        if (this.showing) {
            return;
        }

        if (result) {
            if (this.autoShow) {
                AdSwitcherBannerView.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdSwitcherBannerView.this.show();
                    }
                });
            }
        } else {
            this.selectLoad();
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
