package net.adswitcher.adapter;

/**
 * Created by tkyaji on 2016/07/19.
 */
public interface InterstitialAdListener {
    void interstitialAdLoaded(AdAdapter adAdapter, boolean result);
    void interstitialAdShown(AdAdapter adAdapter);
    void interstitialAdClicked(AdAdapter adAdapter);
    void interstitialAdClosed(AdAdapter adAdapter, boolean result, boolean isSkipped);
}
