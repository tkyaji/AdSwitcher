package net.adswitcher.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.adswitcher.AdSwitcherBannerView;
import net.adswitcher.AdSwitcherInterstitial;
import net.adswitcher.AdSwitcherNativeAd;
import net.adswitcher.AdSwitcherNativeAdData;
import net.adswitcher.config.AdConfig;
import net.adswitcher.config.AdSwitcherConfigLoader;

import java.net.URI;

/**
 * Created by tkyaji on 2016/10/20.
 */

public class NativeAdFragment extends Fragment {

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_nativead, container, false);

        AdSwitcherConfigLoader configLoader = AdSwitcherConfigLoader.getInstance();

        final AdSwitcherNativeAd nativeAd = new AdSwitcherNativeAd(this.activity, configLoader, "native", true);
        nativeAd.setAdReceivedListener(new AdSwitcherNativeAd.AdReceivedListener() {
            @Override
            public void onAdReceived(AdConfig config, boolean result) {
                if (result) {
                    AdSwitcherNativeAdData adData = nativeAd.getAdData();
                    ((TextView)view.findViewById(R.id.textView_title)).setText(adData.shortText);
                    ((TextView)view.findViewById(R.id.textView_content)).setText(adData.longText);
                    nativeAd.loadImage(new AdSwitcherNativeAd.ImageLoadedListener() {
                        @Override
                        public void onImageLoaded(Drawable drawable) {
                            ((ImageView)view.findViewById(R.id.imageView_ad)).setImageDrawable(drawable);
                        }
                    });
                    nativeAd.loadIconImage(new AdSwitcherNativeAd.ImageLoadedListener() {
                        @Override
                        public void onImageLoaded(Drawable drawable) {
                            ((ImageView)view.findViewById(R.id.imageView_icon)).setImageDrawable(drawable);
                        }
                    });
                    nativeAd.sendImpression();
                }
            }
        });

        view.findViewById(R.id.layout_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeAd.openUrl();
            }
        });

        view.findViewById(R.id.button_showNativeAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeAdFragment.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity)context;
    }
}
