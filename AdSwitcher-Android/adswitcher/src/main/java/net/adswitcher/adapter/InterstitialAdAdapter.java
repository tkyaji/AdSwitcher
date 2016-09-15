package net.adswitcher.adapter;

import android.app.Activity;

import java.util.Map;

/**
 * Created by tkyaji on 2016/07/19.
 */
public interface InterstitialAdAdapter extends AdAdapter {
    void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode);
    void interstitialAdLoad();
    void interstitialAdShow();
}
