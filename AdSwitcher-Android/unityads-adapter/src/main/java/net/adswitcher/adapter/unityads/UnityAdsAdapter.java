package net.adswitcher.adapter.unityads;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.Map;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

/**
 * Created by tkyaji on 2016/07/19.
 */
public class UnityAdsAdapter implements InterstitialAdAdapter, IUnityAdsListener {

    private static final String TAG = "UnityAdsAdapter";

    private Activity activity;
    private String placementId;
    private InterstitialAdListener interstitialAdListener;


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        String gameId = parameters.get("game_id");
        this.placementId = parameters.get("placement_id");
        Log.d(TAG, "videoAdInitialize : game_id=" + gameId + ", placement_id=" + this.placementId);

        UnityAds.initialize(activity, gameId, this, testMode);
        UnityAds.setDebugMode(testMode);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        if (this.isReady()) {
            this.interstitialAdListener.interstitialAdLoaded(UnityAdsAdapter.this, true);

        } else {
            this.adLoad(1);
        }
    }

    private boolean isReady() {
        if (this.placementId == null) {
            return UnityAds.isReady();
        } else {
            return UnityAds.isReady(this.placementId);
        }
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");

        if (this.placementId == null) {
            UnityAds.show(this.activity);

        } else {
            UnityAds.show(this.activity, this.placementId);
        }
    }


    @Override
    public void onUnityAdsReady(String s) {
        Log.d(TAG, "onUnityAdsReady");
    }

    @Override
    public void onUnityAdsStart(String s) {
        Log.d(TAG, "onUnityAdsStart");
        this.interstitialAdListener.interstitialAdShown(this);
    }

    @Override
    public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
        Log.d(TAG, "onUnityAdsFinish : finishState=" + finishState);

        boolean result = (finishState == UnityAds.FinishState.COMPLETED || finishState == UnityAds.FinishState.SKIPPED);
        boolean isSkipped = (finishState == UnityAds.FinishState.SKIPPED);

        this.interstitialAdListener.interstitialAdClosed(this, result, isSkipped);
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
        Log.d(TAG, "onUnityAdsError : unityAdsError=" + unityAdsError);

    }



    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (UnityAdsAdapter.this.isReady()) {
                    UnityAdsAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UnityAdsAdapter.this.interstitialAdListener.interstitialAdLoaded(UnityAdsAdapter.this, true);
                        }
                    });
                } else if (count == 5) {
                    UnityAdsAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UnityAdsAdapter.this.interstitialAdListener.interstitialAdLoaded(UnityAdsAdapter.this, false);
                        }
                    });
                } else {
                    UnityAdsAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }

}
