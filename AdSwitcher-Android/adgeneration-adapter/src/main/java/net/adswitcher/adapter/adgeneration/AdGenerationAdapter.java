package net.adswitcher.adapter.adgeneration;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGListener;

import java.util.Map;

import net.adswitcher.adapter.BannerAdAdapter;
import net.adswitcher.adapter.BannerAdListener;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.adapter.InterstitialAdListener;

/**
 * Created by tkyaji on 2016/08/04.
 */
public class AdGenerationAdapter implements BannerAdAdapter {

    private static final String TAG = "AdGenerationAdapter";

    private Activity activity;
    private BannerAdListener bannerAdListener;
    private boolean testMode;
    private String locationId;
    private ADG.AdFrameSize adSize;

    private ADG adg;

    @Override
    public void bannerAdInitialize(Activity activity, BannerAdListener bannerAdListener, Map<String, String> parameters, boolean testMode, BannerAdSize adSize) {
        this.activity = activity;
        this.bannerAdListener = bannerAdListener;
        this.testMode = testMode;
        this.locationId = parameters.get("location_id");
        this.adSize = toADGAdSize(adSize);

        Log.d(TAG, "bannerAdInitialize location_id=" + this.locationId);
    }

    @Override
    public void bannerAdLoad() {
        Log.d(TAG, "bannerAdLoad");
        this.adg = new ADG(this.activity);
        this.adg.setLocationId(this.locationId);
        this.adg.setAdFrameSize(this.adSize);
        this.setBannerListener(this.adg);
        this.adg.setReloadWithVisibilityChanged(false);
        this.adg.setFillerRetry(false);
        this.adg.setEnableTestMode(this.testMode);
        this.adg.start();
    }

    @Override
    public void bannerAdShow(FrameLayout parentLayout) {
        Log.d(TAG, "bannerAdShow");
        parentLayout.addView(this.adg);
        this.bannerAdListener.bannerAdShown(this);
    }

    @Override
    public void bannerAdHide() {
        Log.d(TAG, "bannerAdHide");
        ((FrameLayout)this.adg.getParent()).removeView(this.adg);
        this.adg.destroyAdView();
        this.adg = null;
    }

    private ADG.AdFrameSize toADGAdSize(BannerAdSize adSize) {
        switch (adSize) {
            case SIZE_320X50:
                return ADG.AdFrameSize.SP;

            case SIZE_320X100:
                return ADG.AdFrameSize.LARGE;

            case SIZE_300X250:
                return ADG.AdFrameSize.RECT;
        }
        return ADG.AdFrameSize.SP;
    }

    private void setBannerListener(ADG adg) {
        adg.setAdListener(new ADGListener() {
            @Override
            public void onReceiveAd() {
                Log.d(TAG, "banner onReceiveAd");
                AdGenerationAdapter.this.bannerAdListener.bannerAdReceived(AdGenerationAdapter.this, true);
            }

            @Override
            public void onFailedToReceiveAd(ADGConsts.ADGErrorCode code) {
                Log.d(TAG, "banner onFailedToReceiveAd code=" + code);
                AdGenerationAdapter.this.bannerAdListener.bannerAdReceived(AdGenerationAdapter.this, false);
            }

            @Override
            public void onOpenUrl() {
                Log.d(TAG, "banner onOpenUrl");
                AdGenerationAdapter.this.bannerAdListener.bannerAdClicked(AdGenerationAdapter.this);
            }
        });
    }
}
