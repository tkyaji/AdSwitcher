package net.adswitcher.adapter.maio;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.util.Map;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;
import jp.maio.sdk.android.FailNotificationReason;
import jp.maio.sdk.android.MaioAds;
import jp.maio.sdk.android.MaioAdsListener;

/**
 * Created by tkyaji on 2016/07/20.
 */
public class MaioAdapter extends MaioAdsListener implements InterstitialAdAdapter {

    private static final String TAG = "MaioAdapter";

    private Activity activity;
    private InterstitialAdListener interstitialAdListener;
    private String zoneId;
    private boolean isSkipped;


    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.interstitialAdListener = interstitialAdListener;
        this.activity = activity;

        String mediaId = parameters.get("media_id");
        this.zoneId = parameters.get("zone_id");

        Log.d(TAG, "videoAdInitialize : media_id=" + mediaId + ", zone_id=" + this.zoneId);

        MaioAds.setAdTestMode(testMode);
        MaioAds.init(activity, mediaId, this);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        if (this.zoneId == null) {
            if (MaioAds.canShow(this.zoneId)) {
                this.interstitialAdListener.interstitialAdLoaded(MaioAdapter.this, true);

            } else {
                this.adLoad(1);
            }
        } else {
            if (MaioAds.canShow()) {
                this.interstitialAdListener.interstitialAdLoaded(MaioAdapter.this, true);

            } else {
                this.adLoad(1);
            }
        }
    }

    @Override
    public void interstitialAdShow() {
        this.isSkipped = false;

        if (this.zoneId == null) {
            MaioAds.show();
        } else {
            MaioAds.show(this.zoneId);
        }
    }

    private boolean canShow() {
        if (this.zoneId == null) {
            return MaioAds.canShow();
        } else {
            return MaioAds.canShow(this.zoneId);
        }
    }

    private void show() {
        if (this.zoneId == null) {
            MaioAds.show();
        } else {
            MaioAds.show(this.zoneId);
        }
    }


    @Override
    public void onInitialized() {
        Log.d(TAG, "onInitialized");
    }

    @Override
    public void onChangedCanShow(String zoneId, boolean newValue) {
        Log.d(TAG, "onChangedCanShow : zone_id=" + zoneId + ", new_value=" + newValue);
    }

    @Override
    public void onOpenAd(String zoneId) {
        Log.d(TAG, "onOpenAd : zone_id=" + zoneId);
    }

    @Override
    public void onStartedAd(String zoneId) {
        Log.d(TAG, "onStartedAd : zone_id=" + zoneId);
        this.interstitialAdListener.interstitialAdShown(this);
    }

    @Override
    public void onFinishedAd(int playtime, boolean skipped, int duration, String zoneId) {
        Log.d(TAG, "onFinishedAd : playtime=" + playtime + ", skipped=" + skipped + " duration=" + duration + ", zone_id=" + zoneId);
        this.isSkipped = skipped;
    }

    @Override
    public void onClosedAd(String zoneId) {
        Log.d(TAG, "onClosedAd : zone_id=" + zoneId);
        this.interstitialAdListener.interstitialAdClosed(this, true, this.isSkipped);
    }

    @Override
    public void onClickedAd(String zoneId){
        Log.d(TAG, "onClickedAd : zone_id=" + zoneId);
        this.interstitialAdListener.interstitialAdClicked(this);
    }

    @Override
    public void onFailed(FailNotificationReason reason, String zoneId) {
        Log.d(TAG, "onFailed : reason=" + reason + ", zone_id=" + zoneId);
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MaioAds.canShow()) {
                    MaioAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MaioAdapter.this.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MaioAdapter.this.interstitialAdListener.interstitialAdLoaded(MaioAdapter.this, true);
                                }
                            });
                        }
                    });
                } else if (count == 5) {
                    MaioAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MaioAdapter.this.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MaioAdapter.this.interstitialAdListener.interstitialAdLoaded(MaioAdapter.this, false);
                                }
                            });
                        }
                    });
                } else {
                    MaioAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }

}
