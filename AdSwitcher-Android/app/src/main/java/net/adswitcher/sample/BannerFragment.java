package net.adswitcher.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.adswitcher.AdSwitcherBannerView;
import net.adswitcher.adapter.BannerAdSize;
import net.adswitcher.config.AdSwitcherConfigLoader;


public class BannerFragment extends Fragment {

    private Activity activity;

    private AdSwitcherBannerView bannerView_320x50;
    private AdSwitcherBannerView bannerView_320x100;
    private AdSwitcherBannerView bannerView_300x250;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_banner, container, false);
        final FrameLayout layout = (FrameLayout)view.findViewById(R.id.bannerLayout);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        this.bannerView_320x50 = new AdSwitcherBannerView(this.activity, AdSwitcherConfigLoader.getInstance(), "banner_320x50", BannerAdSize.SIZE_320X50, true);
        layout.addView(bannerView_320x50, lp);

        this.bannerView_320x100 = new AdSwitcherBannerView(this.activity, AdSwitcherConfigLoader.getInstance(), "banner_320x100", BannerAdSize.SIZE_320X100, true);
        layout.addView(BannerFragment.this.bannerView_320x100, lp);

        this.bannerView_300x250 = new AdSwitcherBannerView(this.activity, AdSwitcherConfigLoader.getInstance(), "banner_300x250", BannerAdSize.SIZE_300X250, true);
        layout.addView(bannerView_300x250, lp);

        this.bannerView_320x50.load();



        view.findViewById(R.id.button_ShowBanner320x50).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bannerView_320x50.switchAd();
                    }
                });
            }
        });

        view.findViewById(R.id.button_ShowBanner320x100).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bannerView_320x100.switchAd();
                    }
                });
            }
        });

        view.findViewById(R.id.button_ShowBanner300x250).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bannerView_300x250.switchAd();
                    }
                });
            }
        });

        view.findViewById(R.id.button_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bannerView_320x50.hide();
                        bannerView_320x100.hide();
                        bannerView_300x250.hide();
                    }
                });
            }
        });

        return view;
    }

}
