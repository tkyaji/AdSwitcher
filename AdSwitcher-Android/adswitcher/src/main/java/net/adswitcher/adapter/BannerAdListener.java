package net.adswitcher.adapter;

/**
 * Created by tkyaji on 2016/07/17.
 */
public interface BannerAdListener {
    void bannerAdReceived(AdAdapter adAdapter, boolean result);
    void bannerAdShown(AdAdapter adAdapter);
    void bannerAdClicked(AdAdapter adAdapter);
}
