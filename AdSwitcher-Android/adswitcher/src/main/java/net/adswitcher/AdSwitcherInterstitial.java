package net.adswitcher;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import net.adswitcher.adapter.AdAdapter;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;
import net.adswitcher.config.AdConfig;
import net.adswitcher.config.AdSwitcherConfig;
import net.adswitcher.config.AdSwitcherConfigLoader;
import net.adswitcher.config.IntervalType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tkyaji on 2016/09/07.
 */
public class AdSwitcherInterstitial implements InterstitialAdListener {

    private static final String TAG = "AdSwitcherInterstitial";

    private AdLoadedListener adLoadedListener;
    private AdShownListener adShownListener;
    private AdClickedListener adClickedListener;
    private AdClosedListener adClosedListener;

    private Activity activity;
    private AdSwitcherConfig adSwitcherConfig;
    private boolean testMode;

    private InterstitialAdAdapter selectedAdapter;
    private AdConfig selectedAdConfig;
    private AdSelector adSelector;
    private Map<String, InterstitialAdAdapter> adapterCacheMap;
    private Map<String, AdConfig> adConfigMap;
    private boolean loading;
    private int showCalledCount;
    private long showCalledTime;


    public AdSwitcherInterstitial(final Activity activity, final AdSwitcherConfig adSwitcherConfig) {
        this(activity, adSwitcherConfig, false);
    }

    public AdSwitcherInterstitial(final Activity activity, final AdSwitcherConfig adSwitcherConfig, boolean testMode) {
        this.activity = activity;
        this.initialize(adSwitcherConfig, testMode);
    }

    public AdSwitcherInterstitial(Activity activity, AdSwitcherConfigLoader configLoader, String category) {
        this(activity, configLoader, category, false);
    }

    public AdSwitcherInterstitial(final Activity activity, final AdSwitcherConfigLoader configLoader, final String category, final boolean testMode) {
        this.activity = activity;
        configLoader.addConfigLoadedHandler(new AdSwitcherConfigLoader.ConfigLoadHandler() {
            @Override
            public void onLoaded() {
                final AdSwitcherConfig adSwitcherConfig = configLoader.getAdSwitcherConfig(category);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdSwitcherInterstitial.this.initialize(adSwitcherConfig, testMode);
                    }
                });
            }
        });

    }

    public void show() {
        if (adSwitcherConfig == null) {
            this.showFailedClose();
            return;
        }

        if (adSwitcherConfig.intervalType == IntervalType.Time) {
            long now = System.currentTimeMillis();
            Log.d(TAG, "show : interval_time=" + ((now - this.showCalledTime) / 1000) + "/" + this.adSwitcherConfig.interval);

            if (now - this.showCalledTime <= adSwitcherConfig.interval * 1000) {
                this.showFailedClose();
                return;
            }
            this.showCalledTime = now;

        } else {
            Log.d(TAG, "show : interval_count=" + this.showCalledCount + "/" + this.adSwitcherConfig.interval);

            if (++this.showCalledCount < this.adSwitcherConfig.interval) {
                this.showFailedClose();
                return;
            }
            this.showCalledCount = 0;
        }

        if (this.selectedAdapter != null) {
            this.selectedAdapter.interstitialAdShow();

        } else {
            this.showFailedClose();
            this.load();
        }
    }

    private void showFailedClose() {
        if (this.adClosedListener != null) {
            this.adClosedListener.onAdClosed(new AdConfig(), false, false);
        }
    }

    public boolean isLoaded() {
        return this.selectedAdapter != null;
    }

    public void setAdLoadedListener(final AdLoadedListener listener) {
        this.adLoadedListener = new AdLoadedListener() {
            @Override
            public void onAdLoaded(final AdConfig config, final boolean result) {
                AdSwitcherInterstitial.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdLoaded(config, result);
                    }
                });
            }
        };
    }

    public void setAdShownListener(final AdShownListener listener) {
        this.adShownListener = new AdShownListener() {
            @Override
            public void onAdShown(final AdConfig config) {
                AdSwitcherInterstitial.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdShown(config);
                    }
                });
            }
        };
    }

    public void setAdClosedListener(final AdClosedListener listener) {
        this.adClosedListener = new AdClosedListener() {
            @Override
            public void onAdClosed(final AdConfig config, final boolean result, final boolean isSkipped) {
                AdSwitcherInterstitial.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdClosed(config, result, isSkipped);
                    }
                });
            }
        };
    }

    public void setAdClickedListener(final AdClickedListener listener) {
        this.adClickedListener = new AdClickedListener() {
            @Override
            public void onAdClicked(final AdConfig config) {
                AdSwitcherInterstitial.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdClicked(config);
                    }
                });
            }
        };
    }


    @Override
    public void interstitialAdLoaded(AdAdapter adAdapter, boolean result) {
        String className = adAdapter.getClass().getName();
        Log.d(TAG, "interstitialAdLoaded : " + className + ", result=" + result);

        if (this.selectedAdConfig == null) {
            Log.d(TAG, "class name is invalid.(null)");
            return;
        } else if (!this.selectedAdConfig.className.equals(className)) {
            Log.d(TAG, "class name is invalid. selectedClass=" + this.selectedAdConfig.className);
            return;
        }

        this.loading = false;

        if (this.selectedAdapter == null && result) {
            this.selectedAdapter = (InterstitialAdAdapter) adAdapter;
        }

        if (this.adLoadedListener != null) {
            this.adLoadedListener.onAdLoaded(this.adConfigMap.get(className), result);
        }

        if (!result) {
            this.selectLoad();
        }
    }

    @Override
    public void interstitialAdShown(AdAdapter adAdapter) {
        String className = adAdapter.getClass().getName();
        Log.d(TAG, "interstitialAdShown : " + className);

        if (this.adShownListener != null) {
            this.adShownListener.onAdShown(this.adConfigMap.get(className));
        }
    }

    @Override
    public void interstitialAdClicked(AdAdapter adAdapter) {
        String className = adAdapter.getClass().getName();
        Log.d(TAG, "interstitialAdClicked : " + className);

        if (this.adClickedListener != null) {
            this.adClickedListener.onAdClicked(this.adConfigMap.get(className));
        }
    }

    @Override
    public void interstitialAdClosed(AdAdapter adAdapter, boolean result, boolean isSkipped) {
        String className = adAdapter.getClass().getName();
        Log.d(TAG, "interstitialAdClosed : " + className + ", result=" + result + ", isSkipped=" + isSkipped);

        if (this.adClosedListener != null) {
            this.adClosedListener.onAdClosed(this.adConfigMap.get(className), result, isSkipped);
        }

        this.selectedAdapter = null;
        this.selectedAdConfig = null;
        this.load();
    }


    private void initialize(AdSwitcherConfig adSwitcherConfig, boolean testMode) {
        Log.d(TAG, "testMode=" + testMode);

        this.adSwitcherConfig = adSwitcherConfig;
        this.testMode = testMode;

        this.adapterCacheMap = new HashMap<>();
        this.adConfigMap = new HashMap<>();

        if (this.adSwitcherConfig != null) {
            for (AdConfig config : this.adSwitcherConfig.adConfigList) {
                this.adConfigMap.put(config.className, config);
            }
        }

        this.load();
    }

    private void selectLoad() {
        this.loading = true;

        InterstitialAdAdapter interstitialAdAdapter = null;
        while (interstitialAdAdapter == null) {
            this.selectedAdConfig = this.adSelector.selectAd();
            if (this.selectedAdConfig == null) {
                break;
            }

            this.initAdapter(this.selectedAdConfig);
            interstitialAdAdapter = this.adapterCacheMap.get(this.selectedAdConfig.className);
        }

        if (interstitialAdAdapter != null) {
            Log.d(TAG, interstitialAdAdapter.getClass().getName());
            interstitialAdAdapter.interstitialAdLoad();

        } else {
            this.loading = false;
            Log.w(TAG, "It will not be able to display all.");

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdSwitcherInterstitial.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdSwitcherInterstitial.this.load();
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

        InterstitialAdAdapter interstitialAdAdapter = null;
        try {
            Class<?> adAdapterClass = Class.forName(adConfig.className);
            interstitialAdAdapter = (InterstitialAdAdapter) adAdapterClass.newInstance();

        } catch (ClassNotFoundException ex) {
            Log.w(TAG, adConfig.className + " class is not found.");
            return;

        } catch (Throwable e) {
            Log.w(TAG, adConfig.className + " class is not conform BannerAdAdapter.");
            return;
        }

        interstitialAdAdapter.interstitialAdInitialize(this.activity, this, adConfig.parameters, this.testMode);

        this.adapterCacheMap.put(adConfig.className, interstitialAdAdapter);
    }

    private void load() {
        Log.d(TAG, "load");
        if (this.loading) {
            Log.d(TAG, "Already started to load");
            return;
        }
        this.adSelector = new AdSelector(this.adSwitcherConfig);
        this.selectLoad();
    }


    public interface AdLoadedListener {
        void onAdLoaded(AdConfig config, boolean result);
    }

    public interface AdShownListener {
        void onAdShown(AdConfig config);
    }

    public interface AdClickedListener {
        void onAdClicked(AdConfig config);
    }

    public interface AdClosedListener {
        void onAdClosed(AdConfig config, boolean result, boolean isSkipped);
    }

}
