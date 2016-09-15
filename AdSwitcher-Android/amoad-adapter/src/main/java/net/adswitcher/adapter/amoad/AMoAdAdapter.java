package net.adswitcher.adapter.amoad;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;

import com.amoad.AMoAdError;
import com.amoad.AMoAdView;
import com.amoad.AdCallback2;
import com.amoad.AdLoadListener;
import com.amoad.AdResult;
import com.amoad.InterstitialAd;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

/**
 * Created by tkyaji on 2016/08/15.
 */
public class AMoAdAdapter implements BannerAdAdapter, InterstitialAdAdapter, AdCallback2, AdLoadListener, InterstitialAd.OnCloseListener {

    private static final String TAG = "AMoAdAdapter";

    private Activity activity;
    private AMoAdView adView;
    private String sid;
    private BannerAdListener bannerAdListener;
    private InterstitialAdListener interstitialAdListener;
    private boolean isLoaded;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.sid = parameters.get("sid");

        Log.d(TAG, "bannerAdInitialize : sid=" + this.sid);

        this.adView = new AMoAdView(this.activity);
        this.adView.setSid(this.sid);
        this.adView.setCallback(this);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");

        if (this.isLoaded) {
            this.adView.startRotation();
            AMoAdAdapter.this.bannerAdListener.bannerAdReceived(AMoAdAdapter.this, true);

        } else {
            this.adLoad(1);
        }
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "bannerAdShow");
        parentLayout.addView(this.adView);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");
        this.adView.stopRotation();
        this.adView.onFinishTemporaryDetach();
        ((FrameLayout)this.adView.getParent()).removeView(this.adView);
    }



    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;
        this.sid = parameters.get("sid");

        Log.d(TAG, "interstitialAdInitialize : sid=" + this.sid);

        InterstitialAd.register(this.sid);
        InterstitialAd.setAutoReload(this.sid, false);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");
        InterstitialAd.load(this.activity, this.sid, this);
    }

    @Override
    public void interstitialAdShow() {
        if (InterstitialAd.isLoaded(this.sid)) {
            Log.d(TAG, "interstitialAdShow");
            InterstitialAd.show(this.activity, this.sid, this);
            this.interstitialAdListener.interstitialAdShown(this);

        } else {
            Log.d(TAG, "interstitialAdShow");
            this.interstitialAdListener.interstitialAdClosed(this, false, false);
        }
    }



    @Override
    public void didReceiveAd() {
        Log.d(TAG, "banner didReceiveAd");
        this.isLoaded = true;
    }

    @Override
    public void didFailToReceiveAdWithError() {
        Log.d(TAG, "banner didFailToReceiveAdWithError");
        this.isLoaded = false;
    }

    @Override
    public void didReceiveEmptyAd() {
        Log.d(TAG, "banner didReceiveEmptyAd");
        this.isLoaded = false;
    }

    @Override
    public void didClick() {
        Log.d(TAG, "banner didClick");
        this.bannerAdListener.bannerAdClicked(this);
    }

    @Override
    public void didPresentScreen() {
        Log.d(TAG, "banner didPresentScreen");
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void didDismissScreen() {
    }

    @Override
    public void didLeaveApplication() {
    }



    @Override
    public void onLoaded(String s, AdResult adResult, AMoAdError aMoAdError) {
        Log.d(TAG, "interstitial onLoaded : result=" + adResult);
        if (aMoAdError != null) {
            Log.d(TAG, "interstitial error : code=" + aMoAdError.getCode() + ", message=" + aMoAdError.getLocalizedMessage());
        }

        if (adResult == AdResult.Success) {
            this.interstitialAdListener.interstitialAdLoaded(this, true);
        } else {
            this.interstitialAdListener.interstitialAdLoaded(this, false);
        }
    }

    @Override
    public void onClose(InterstitialAd.Result result) {
        Log.d(TAG, "interstitial onClose : result=" + result);
        switch (result) {
            case Click:
                this.interstitialAdListener.interstitialAdClicked(this);
                break;

            case Close:
            case CloseFromApp:
                this.interstitialAdListener.interstitialAdClosed(this, true, false);
                break;

            case Duplicated:
            case Failure:
                this.interstitialAdListener.interstitialAdClosed(this, false, false);
                break;
        }
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AMoAdAdapter.this.isLoaded) {
                    AMoAdAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AMoAdAdapter.this.bannerAdListener.bannerAdReceived(AMoAdAdapter.this, true);
                        }
                    });
                } else if (count == 3) {
                    AMoAdAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AMoAdAdapter.this.bannerAdListener.bannerAdReceived(AMoAdAdapter.this, false);
                        }
                    });
                } else {
                    AMoAdAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }
}
