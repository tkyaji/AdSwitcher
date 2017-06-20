package net.adswitcher;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.adswitcher.adapter.AdAdapter;
import net.adswitcher.adapter.NativeAdAdapter;
import net.adswitcher.adapter.NativeAdListener;
import net.adswitcher.config.AdConfig;
import net.adswitcher.config.AdSwitcherConfig;
import net.adswitcher.config.AdSwitcherConfigLoader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tkyaji on 2016/10/20.
 */

public class AdSwitcherNativeAd implements NativeAdListener {

    private static final String TAG = "AdSwitcherNativeAd";

    private AdSwitcherNativeAd.AdReceivedListener adReceivedListener;

    private Activity activity;
    private AdSwitcherConfig adSwitcherConfig;
    private boolean testMode;

    private NativeAdAdapter selectedAdapter;
    private AdConfig selectedAdConfig;
    private AdSelector adSelector;
    private Map<String, NativeAdAdapter> adapterCacheMap;
    private Map<String, AdConfig> adConfigMap;
    private boolean loading;

    public AdSwitcherNativeAd(final Activity activity, final AdSwitcherConfigLoader configLoader,
                                final String category) {
        this(activity, configLoader, category, false);
    }

    public AdSwitcherNativeAd(final Activity activity, final AdSwitcherConfigLoader configLoader,
                                final String category, final boolean testMode) {
        this.activity = activity;
        configLoader.addConfigLoadedHandler(new AdSwitcherConfigLoader.ConfigLoadHandler() {
            @Override
            public void onLoaded() {
                final AdSwitcherConfig adSwitcherConfig = configLoader.getAdSwitcherConfig(category);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdSwitcherNativeAd.this.initialize(adSwitcherConfig, testMode);
                    }
                });
            }
        });
    }

    public AdSwitcherNativeAd(final Activity activity, final AdSwitcherConfig adSwitcherConfig) {
        this(activity, adSwitcherConfig, false);
    }

    public AdSwitcherNativeAd(final Activity activity, final AdSwitcherConfig adSwitcherConfig, final boolean testMode) {
        this.activity = activity;
        this.initialize(adSwitcherConfig, testMode);
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

    public AdSwitcherNativeAdData getAdData() {
        if (this.selectedAdapter == null) {
            return null;
        }
        return this.selectedAdapter.getAdData();
    }

    public boolean isLoaded() {
        return this.selectedAdapter != null;
    }

    public void sendImpression() {
        if (this.selectedAdapter == null) {
            return;
        }
        this.selectedAdapter.sendImpression();
    }

    public void openUrl() {
        if (this.selectedAdapter == null) {
            return;
        }
        this.selectedAdapter.openUrl();
    }

    public void loadImage(ImageLoadedListener listener) {
        AdSwitcherNativeAdData adData = this.getAdData();
        if (adData == null) {
            listener.onImageLoaded(null);
        }
        this.loadImageAsync(adData.imageUrl, listener);
    }

    public void loadIconImage(ImageLoadedListener listener) {
        AdSwitcherNativeAdData adData = this.getAdData();
        if (adData == null) {
            listener.onImageLoaded(null);
        }
        this.loadImageAsync(adData.iconImageUrl, listener);
    }

    private void loadImageAsync(final String url, final ImageLoadedListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Drawable drawable = AdSwitcherNativeAd.this.loadImageToDrawable(url);
                AdSwitcherNativeAd.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onImageLoaded(drawable);
                    }
                });
            }
        }).start();
    }

    private Drawable loadImageToDrawable(String url)  {
        try {
            InputStream is = (InputStream)new URL(url).getContent();
            return Drawable.createFromStream(is, "src name");

        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        }
    }


    public void setAdReceivedListener(final AdSwitcherNativeAd.AdReceivedListener listener) {
        this.adReceivedListener = new AdSwitcherNativeAd.AdReceivedListener() {
            @Override
            public void onAdReceived(final AdConfig config, final boolean result) {
                AdSwitcherNativeAd.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdReceived(config, result);
                    }
                });
            }
        };
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
    }

    private void selectLoad() {
        this.loading = true;

        NativeAdAdapter nativeAdAdapter = null;
        while (nativeAdAdapter == null) {
            this.selectedAdConfig = this.adSelector.selectAd();
            if (this.selectedAdConfig == null) {
                break;
            }

            this.initAdapter(this.selectedAdConfig);
            nativeAdAdapter = this.adapterCacheMap.get(this.selectedAdConfig.className);
        }

        if (nativeAdAdapter != null) {
            Log.d(TAG, nativeAdAdapter.getClass().getName());
            final NativeAdAdapter _nativeAdAdapter = nativeAdAdapter;
            AdSwitcherNativeAd.this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _nativeAdAdapter.nativeAdLoad();
                }
            });

        } else {
            this.loading = false;
            Log.w(TAG, "It will not be able to display all.");

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdSwitcherNativeAd.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdSwitcherNativeAd.this.load();
                        }
                    });
                }
            }, 10000);
        }
    }

    private void initAdapter(final AdConfig adConfig) {
        if (this.adapterCacheMap.containsKey(adConfig.className)) {
            return;
        }

        try {
            Class<?> adAdapterClass = Class.forName(adConfig.className);
            final NativeAdAdapter nativeAdAdapter = (NativeAdAdapter) adAdapterClass.newInstance();

            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nativeAdAdapter.nativeAdInitialize(AdSwitcherNativeAd.this.activity,
                            AdSwitcherNativeAd.this, adConfig.parameters,
                            AdSwitcherNativeAd.this.testMode);
                }
            });
            this.adapterCacheMap.put(adConfig.className, nativeAdAdapter);

        } catch (ClassNotFoundException ex) {
            Log.w(TAG, adConfig.className + " class is not found.");
            return;

        } catch (Throwable e) {
            Log.w(TAG, adConfig.className + " class is not conform NativeAdAdapter.");
            return;
        }
    }


    @Override
    public void nativeAdReceived(AdAdapter adAdapter, boolean result) {
        String className = adAdapter.getClass().getName();
        Log.d(TAG, "nativeAdReceived : " + className + ", result=" + result);

        if (this.selectedAdConfig == null) {
            Log.d(TAG, "class name is invalid.(null)");
            return;
        } else if (!this.selectedAdConfig.className.equals(className)) {
            Log.d(TAG, "class name is invalid. selectedClass=" + this.selectedAdConfig.className);
            return;
        }

        this.loading = false;

        if (this.selectedAdapter == null && result) {
            this.selectedAdapter = (NativeAdAdapter) adAdapter;
        }

        if (this.adReceivedListener != null) {
            this.adReceivedListener.onAdReceived(this.adConfigMap.get(className), result);
        }

        if (this.selectedAdapter == null) {
            if (!result) {
                this.selectLoad();
            }
        }
    }



    public interface AdReceivedListener {
        void onAdReceived(AdConfig config, boolean result);
    }

    public interface ImageLoadedListener {
        void onImageLoaded(Drawable drawable);
    }
}
