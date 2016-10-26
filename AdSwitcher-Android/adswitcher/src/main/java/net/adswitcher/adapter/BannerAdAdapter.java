package net.adswitcher.adapter;

import android.app.Activity;
import android.widget.FrameLayout;

import java.util.Map;

/**
 * Created by tkyaji on 2016/07/17.
 */
public interface BannerAdAdapter extends AdAdapter {
    void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize);
    void bannerAdLoad();
    void bannerAdShow(FrameLayout parentLayout);
    void bannerAdHide();
}
