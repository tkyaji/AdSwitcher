package net.adswitcher.adapter.applovin;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;

import java.util.Map;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

/**
 * Created by tkyaji on 2016/08/04.
 */
public class AppLovinAdapter implements InterstitialAdAdapter, AppLovinAdLoadListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener, AppLovinAdClickListener {

    private static final String TAG = "AppLovinAdapter";

    private Activity activity;
    private AppLovinSdk sdk;
    private AppLovinInterstitialAdDialog interstitialAd;
    private InterstitialAdListener interstitialAdListener;
    private boolean isSkipped;

    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        String sdkKey = parameters.get("sdk_key");
        Log.d(TAG, "interstitialAdInitialize : sdk_key=" + sdkKey);

        this.initSdk(sdkKey);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        if (this.interstitialAd.isAdReadyToDisplay()) {
            this.interstitialAdListener.interstitialAdLoaded(AppLovinAdapter.this, true);

        } else {
            this.adLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        this.isSkipped = false;

        Log.d(TAG, "interstitialAdShow");
        this.interstitialAd.show();
    }

    private void initSdk(String sdkKey) {
        this.sdk = AppLovinSdk.getInstance(sdkKey, AppLovinSdkUtils.retrieveUserSettings(this.activity), this.activity);
        this.interstitialAd = AppLovinInterstitialAd.create(this.sdk, this.activity);
        this.interstitialAd.setAdDisplayListener(this);
        this.interstitialAd.setAdVideoPlaybackListener(this);
        this.interstitialAd.setAdClickListener(this);
        this.interstitialAd.setAdLoadListener(this);
    }


    @Override
    public void adReceived(AppLovinAd appLovinAd) {
        Log.d(TAG, "adReceived");
    }

    @Override
    public void failedToReceiveAd(int errorCode) {
        Log.d(TAG, "failedToReceiveAd : errorCode=" + errorCode);
    }


    @Override
    public void adDisplayed(AppLovinAd appLovinAd) {
        Log.d(TAG, "adDisplayed");
        this.interstitialAdListener.interstitialAdShown(this);
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        Log.d(TAG, "adHidden");
        this.interstitialAdListener.interstitialAdClosed(this, true, this.isSkipped);
    }

    @Override
    public void videoPlaybackBegan(AppLovinAd appLovinAd) {
        Log.d(TAG, "videoPlaybackBegan");
    }

    @Override
    public void videoPlaybackEnded(final AppLovinAd ad, final double percentViewed, final boolean fullyWatched) {
        Log.d(TAG, "videoPlaybackEnded percentViewed=" + percentViewed + ", fullyWatched=" + fullyWatched);
        this.isSkipped = !fullyWatched;
    }

    @Override
    public void adClicked(AppLovinAd appLovinAd) {
        Log.d(TAG, "adClicked");
        this.interstitialAdListener.interstitialAdClicked(this);
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppLovinAdapter.this.interstitialAd.isAdReadyToDisplay()) {
                    AppLovinAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppLovinAdapter.this.interstitialAdListener.interstitialAdLoaded(AppLovinAdapter.this, true);
                        }
                    });
                } else if (count == 5) {
                    AppLovinAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppLovinAdapter.this.interstitialAdListener.interstitialAdLoaded(AppLovinAdapter.this, false);
                        }
                    });
                } else {
                    AppLovinAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }

}
