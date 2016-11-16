package net.adswitcher.adapter.tapjoy;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJPlacementVideoListener;
import com.tapjoy.TJVideoListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by tkyaji on 2016/11/15.
 */

public class TapjoyAdapter implements InterstitialAdAdapter, TJPlacementListener, TJPlacementVideoListener, TJVideoListener, TJConnectListener {

    private static final String TAG = "TapjoyAdapter";

    private Activity activity;
    private TJPlacement placement;
    private InterstitialAdListener interstitialAdListener;
    private boolean result;
    private boolean isSkipped;


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        String sdkKey = parameters.get("sdk_key");
        String placementName = parameters.get("placement");

        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        Log.d(TAG, "interstitialAdInitialize : sdk_key=" + sdkKey + ", placement=" + placementName);

        Hashtable<String, Object> connectFlags = new Hashtable<>();
        if (testMode) {
            connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");
            Tapjoy.setDebugEnabled(true);
        }
        Tapjoy.connect(activity.getApplicationContext(), sdkKey, connectFlags, this);
        Tapjoy.setVideoListener(this);

        this.placement = new TJPlacement(activity, placementName, this);
        this.placement.setVideoListener(this);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        if (this.placement.isContentReady() && this.placement.isContentAvailable()) {
            TapjoyAdapter.this.interstitialAdListener.interstitialAdLoaded(TapjoyAdapter.this, true);

        } else {
            this.placement.requestContent();
            this.adLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        this.placement.showContent();
    }


    @Override
    public void onConnectSuccess() {
        Log.d(TAG, "onConnectSuccess");
        this.placement.requestContent();
    }

    @Override
    public void onConnectFailure() {
        Log.d(TAG, "onConnectFailure");
    }


    @Override
    public void onRequestSuccess(TJPlacement tjPlacement) {
        Log.d(TAG, "onRequestSuccess : placement=" + tjPlacement.getName());
    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
        Log.d(TAG, "onRequestFailure : placement=" + tjPlacement.getName());
    }

    @Override
    public void onContentReady(TJPlacement tjPlacement) {
        Log.d(TAG, "onContentReady : placement=" + tjPlacement.getName());
    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {
        Log.d(TAG, "onContentShow : placement=" + tjPlacement.getName());
    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {
        Log.d(TAG, "onContentDismiss : placement=" + tjPlacement.getName());
        TapjoyAdapter.this.interstitialAdListener.interstitialAdClosed(TapjoyAdapter.this, this.result, this.isSkipped);
    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {
        Log.d(TAG, "onPurchaseRequest : placement=" + tjPlacement.getName());
    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {
        Log.d(TAG, "onRewardRequest : placement=" + tjPlacement.getName());
    }


    @Override
    public void onVideoStart(TJPlacement tjPlacement) {
        Log.d(TAG, "onVideoStart : placement=" + tjPlacement.getName());
        TapjoyAdapter.this.interstitialAdListener.interstitialAdShown(TapjoyAdapter.this);
        this.result = true;
        this.isSkipped = false;
    }

    @Override
    public void onVideoError(TJPlacement tjPlacement, String error) {
        Log.d(TAG, "onVideoError : placement=" + tjPlacement.getName() + ", error=" + error);
    }

    @Override
    public void onVideoComplete(TJPlacement tjPlacement) {
        Log.d(TAG, "onVideoComplete : placement=" + tjPlacement.getName());
        this.isSkipped = false;
    }


    @Override
    public void onVideoStart() {
        Log.d(TAG, "onVideoStart");
    }

    @Override
    public void onVideoError(int error) {
        Log.d(TAG, "onVideoError : error=" + error);
    }

    @Override
    public void onVideoComplete() {
        Log.d(TAG, "onVideoComplete");
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TapjoyAdapter.this.placement.isContentAvailable() && TapjoyAdapter.this.placement.isContentReady()) {
                    TapjoyAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TapjoyAdapter.this.interstitialAdListener.interstitialAdLoaded(TapjoyAdapter.this, true);
                        }
                    });
                } else if (count == 5) {
                    TapjoyAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TapjoyAdapter.this.interstitialAdListener.interstitialAdLoaded(TapjoyAdapter.this, false);
                        }
                    });
                } else {
                    TapjoyAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }
}
