package net.adswitcher.adapter.adcolony;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

/**
 * Created by tkyaji on 2016/08/12.
 */
public class AdColonyAdapter implements InterstitialAdAdapter, AdColonyAdListener {

    private static final String TAG = "AdColonyAdapter";

    private String zoneId;
    private AdColonyVideoAd videoAd;
    private InterstitialAdListener interstitialAdListener;
    private Activity activity;


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.interstitialAdListener = interstitialAdListener;
        this.activity = activity;

        String appId = parameters.get("app_id");
        this.zoneId = parameters.get("zone_id");

        Log.d(TAG, "videoAdInitialize : app_id=" + appId + ", zone_id=" + this.zoneId);

        String versionName = "";
        try {
            versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (Throwable ex) {
            Log.e(TAG, ex.getLocalizedMessage(), ex);
        }

        String option = "version:" + versionName + ",store:google";
        AdColony.configure(activity, option, appId, this.zoneId);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        this.videoAd = new AdColonyVideoAd(this.zoneId);
        this.videoAd.withListener(this);

        if (this.videoAd.isReady()) {
            this.interstitialAdListener.interstitialAdLoaded(AdColonyAdapter.this, true);

        } else {
            this.adLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "videoAdShow");
        this.videoAd.show();
    }


    @Override
    public void onAdColonyAdStarted(AdColonyAd adColonyAd) {
        Log.d(TAG, "onAdColonyAdStarted");
        this.interstitialAdListener.interstitialAdShown(this);
    }

    @Override
    public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {
        if (adColonyAd.shown()) {
            if (adColonyAd.skipped()) {
                Log.d(TAG, "onAdColonyAdAttemptFinished : status=skipped");
                this.interstitialAdListener.interstitialAdClosed(this, true, true);

            } else {
                Log.d(TAG, "onAdColonyAdAttemptFinished : status=completed");
                this.interstitialAdListener.interstitialAdClosed(this, true, false);
            }

        } else {
            Log.d(TAG, "onAdColonyAdAttemptFinished : status=failed");
            this.interstitialAdListener.interstitialAdClosed(this, false, false);
        }
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AdColonyAdapter.this.videoAd.isReady()) {
                    AdColonyAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdColonyAdapter.this.interstitialAdListener.interstitialAdLoaded(AdColonyAdapter.this, true);
                        }
                    });
                } else if (count == 5) {
                    AdColonyAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdColonyAdapter.this.interstitialAdListener.interstitialAdLoaded(AdColonyAdapter.this, false);
                        }
                    });
                } else {
                    AdColonyAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }

}
