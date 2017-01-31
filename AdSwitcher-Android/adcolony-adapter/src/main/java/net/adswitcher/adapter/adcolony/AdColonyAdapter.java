package net.adswitcher.adapter.adcolony;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.adcolony.sdk.*;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

/**
 * Created by tkyaji on 2016/08/12.
 */
public class AdColonyAdapter implements InterstitialAdAdapter {

    private static final String TAG = "AdColonyAdapter";

    private String zoneId;
    private InterstitialAdListener interstitialAdListener;
    AdColonyInterstitial adColonyInterstitial;
    private Activity activity;
    private boolean isLoading;
    private boolean isLoaded;


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.interstitialAdListener = interstitialAdListener;
        this.activity = activity;

        String appId = parameters.get("app_id");
        this.zoneId = parameters.get("zone_id");

        Log.d(TAG, "videoAdInitialize : app_id=" + appId + ", zone_id=" + this.zoneId);
        AdColony.configure(activity, appId, this.zoneId);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        this.adColonyInterstitial = null;
        this.isLoading = true;
        this.isLoaded = false;

        AdColony.requestInterstitial(this.zoneId, this.getListener());

        this.adLoad(1);
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "videoAdShow");
        if (this.isLoaded && this.adColonyInterstitial.show()) {
        } else {
            this.interstitialAdListener.interstitialAdClosed(this, false, false);
        }
    }

    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AdColonyAdapter.this.isLoading) {
                    return;

                } else if (count == 5) {
                    AdColonyAdapter.this.isLoading = false;
                    Log.d(TAG, "load timeout");
                    AdColonyAdapter.this.interstitialAdListener.interstitialAdLoaded(AdColonyAdapter.this, false);

                } else {
                    AdColonyAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }

    private AdColonyInterstitialListener getListener() {
        return new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
                Log.d(TAG, "onRequestFilled");
                AdColonyAdapter.this.adColonyInterstitial = adColonyInterstitial;
                AdColonyAdapter.this.isLoading = false;
                AdColonyAdapter.this.isLoaded = true;
                AdColonyAdapter.this.interstitialAdListener.interstitialAdLoaded(AdColonyAdapter.this, true);
            }

            @Override
            public void onClicked(AdColonyInterstitial ad) {
                Log.d(TAG, "onClicked");
                AdColonyAdapter.this.interstitialAdListener.interstitialAdClicked(AdColonyAdapter.this);
            }

            @Override
            public void onClosed(AdColonyInterstitial ad) {
                Log.d(TAG, "onClosed");
                AdColonyAdapter.this.interstitialAdListener.interstitialAdClosed(AdColonyAdapter.this, true, false);
            }

            @Override
            public void onOpened(AdColonyInterstitial ad) {
                Log.d(TAG, "onOpened");
                AdColonyAdapter.this.interstitialAdListener.interstitialAdShown(AdColonyAdapter.this);
            }
        };
    }
}
