package net.adswitcher.adapter.vungle;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

/**
 * Created by tkyaji on 2017/07/19.
 */

public class VungleAdapter implements InterstitialAdAdapter, EventListener {

    private static final String TAG = "VungleAdapter";

    private Activity activity;
    private InterstitialAdListener interstitialAdListener;


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        String appId = parameters.get("app_id");
        VunglePub.getInstance().init(activity, appId);
        VunglePub.getInstance().setEventListeners();
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");
        if (VunglePub.getInstance().isAdPlayable()) {
            this.interstitialAdListener.interstitialAdLoaded(this, true);
        } else {
            this.adLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        VunglePub.getInstance().playAd();
    }


    @Override
    public void onAdEnd(boolean wasSuccessfulView, boolean wasCallToActionClicked) {
        Log.d(TAG, "onAdEnd");

        if (wasCallToActionClicked) {
            this.interstitialAdListener.interstitialAdClicked(this);
        }
        this.interstitialAdListener.interstitialAdClosed(this, true, !wasSuccessfulView);
    }

    @Override
    public void onAdStart() {
        Log.d(TAG, "onAdStart");
        this.interstitialAdListener.interstitialAdShown(this);
    }

    @Override
    public void onAdUnavailable(String reason) {
        Log.d(TAG, "onAdUnavailable : " + reason);
    }

    @Override
    public void onAdPlayableChanged(boolean isAdPlayable) {
        Log.d(TAG, "onAdPlayableChanged");
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (VunglePub.getInstance().isAdPlayable()) {
                    VungleAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            VungleAdapter.this.interstitialAdListener.interstitialAdLoaded(VungleAdapter.this, true);
                        }
                    });
                } else if (count == 5) {
                    VungleAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            VungleAdapter.this.interstitialAdListener.interstitialAdLoaded(VungleAdapter.this, false);
                        }
                    });
                } else {
                    VungleAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }
}
