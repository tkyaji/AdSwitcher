package net.adswitcher.adapter.admob;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

/**
 * Created by tkyaji on 2016/07/19.
 */
public class AdMobAdapter implements BannerAdAdapter, InterstitialAdAdapter {

    private static final String TAG = "AdMobAdapter";

    private AdView adView;
    private Activity activity;
    private BannerAdListener bannerAdListener;
    private String adUnitId;
    private boolean testMode;
    private BannerAdSize adSize;

    private InterstitialAd interstitialAd;
    private InterstitialAdListener interstitialAdListener;


    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.testMode = testMode;
        this.adSize = adSize;
        this.adUnitId = parameters.get("ad_unit_id");

        Log.d(TAG, "bannerAdCreateView : ad_unit_id=" + adUnitId);
    }

    @Override
    public void bannerAdLoad() {
        this.adView = new AdView(this.activity);
        this.adView.setAdSize(this.toGADAdSize(this.adSize));
        this.adView.setAdUnitId(this.adUnitId);
        this.setBannerListener(this.adView, this.bannerAdListener);

        AdRequest.Builder builder = new AdRequest.Builder();
        if (testMode) {
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            builder.addTestDevice(this.getAdMobDeviceId());
        }
        this.adView.loadAd(builder.build());
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "banenrAdShow");
        parentLayout.addView(this.adView);
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerHide");
        ((FrameLayout)this.adView.getParent()).removeView(this.adView);
    }

    private void setBannerListener(final AdView adView, final BannerAdListener bannerAdListener) {

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "banner onAdLoaded");
                bannerAdListener.bannerAdReceived(AdMobAdapter.this, true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "banner onAdFailedToLoad : errorCode=" + errorCode);
                bannerAdListener.bannerAdReceived(AdMobAdapter.this, false);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "banner onAdLeftApplication");
                bannerAdListener.bannerAdClicked(AdMobAdapter.this);
            }
        });
    }


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;
        this.testMode = testMode;
        this.adUnitId = parameters.get("ad_unit_id");

        Log.d(TAG, "interstitialAdInitialize : ad_unit_id=" + this.adUnitId);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        this.interstitialAd = new InterstitialAd(this.activity);
        this.interstitialAd.setAdUnitId(this.adUnitId);
        this.setInterstitialAdListener(this.interstitialAd, this.interstitialAdListener);

        AdRequest.Builder builder = new AdRequest.Builder();
        if (this.testMode) {
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            builder.addTestDevice(this.getAdMobDeviceId());
        }
        this.interstitialAd.loadAd(builder.build());
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        this.interstitialAd.show();
    }

    private void setInterstitialAdListener(final InterstitialAd interstitialAd, final InterstitialAdListener interstitialAdListener) {
        this.interstitialAdListener = interstitialAdListener;

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "interstitial onAdClosed");
                interstitialAdListener.interstitialAdClosed(AdMobAdapter.this, true, false);
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "interstitial onAdLoaded");
                interstitialAdListener.interstitialAdLoaded(AdMobAdapter.this, true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "interstitial onAdFailedToLoad : errorCode=" + errorCode);
                interstitialAdListener.interstitialAdLoaded(AdMobAdapter.this, false);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "interstitial onAdLeftApplication");
                interstitialAdListener.interstitialAdClicked(AdMobAdapter.this);
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "interstitial onAdOpened");
                interstitialAdListener.interstitialAdShown(AdMobAdapter.this);
            }
        });
    }


    private AdSize toGADAdSize(BannerAdSize adSize) {
        switch (adSize) {
            case SIZE_320X50:
                return AdSize.BANNER;

            case SIZE_320X100:
                return AdSize.LARGE_BANNER;

            case SIZE_300X250:
                return AdSize.MEDIUM_RECTANGLE;
        }

        return AdSize.BANNER;
    }

    private String getAdMobDeviceId() {
        String androidId =  Settings.Secure.getString(this.activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return this.md5String(androidId).toUpperCase();
    }

    public String md5String(final String s) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        return "";
    }

}
