package net.adswitcher.adapter.admobvideo;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by tkyaji on 2017/03/06.
 */

public class AdMobVideoAdapter implements InterstitialAdAdapter, RewardedVideoAdListener {

    private static final String TAG = "AdMobVideoAdapter";

    private RewardedVideoAd rewardedVideoAd;
    private Activity activity;
    private InterstitialAdListener interstitialAdListener;
    private boolean testMode;
    private String adUnitId;
    private boolean isRewarded;


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;
        this.testMode = testMode;

        this.adUnitId = parameters.get("ad_unit_id");
        Log.d(TAG, "interstitialAdInitialize : ad_unit_id=" + adUnitId);

        this.rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
        this.rewardedVideoAd.setRewardedVideoAdListener(this);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        AdRequest.Builder builder = new AdRequest.Builder();
        if (this.testMode) {
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            builder.addTestDevice(this.getAdMobDeviceId());
        }
        this.rewardedVideoAd.loadAd(this.adUnitId, builder.build());
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        this.isRewarded = false;
        this.rewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        this.interstitialAdListener.interstitialAdLoaded(this, true);
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d(TAG, "onRewardedVideoAdFailedToLoad");
        this.interstitialAdListener.interstitialAdLoaded(this, false);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        this.interstitialAdListener.interstitialAdShown(this);
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        this.interstitialAdListener.interstitialAdClosed(this, true, !this.isRewarded);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        this.isRewarded = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        this.interstitialAdListener.interstitialAdClicked(this);
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
