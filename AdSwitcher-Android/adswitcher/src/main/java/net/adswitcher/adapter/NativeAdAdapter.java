package net.adswitcher.adapter;

import android.app.Activity;

import net.adswitcher.AdSwitcherNativeAdData;

import java.util.Map;

/**
 * Created by tkyaji on 2016/10/20.
 */

public interface NativeAdAdapter {
    void nativeAdInitialize(Activity activity, NativeAdListener nativeAdListener, Map<String, String> parameters, boolean testMode);
    void nativeAdLoad();
    AdSwitcherNativeAdData getAdData();
    void openUrl();
    void sendImpression();
}
