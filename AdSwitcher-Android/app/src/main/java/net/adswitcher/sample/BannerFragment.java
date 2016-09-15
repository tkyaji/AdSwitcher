package net.adswitcher.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        view.findViewById(R.id.button_ShowBanner320x50).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (BannerFragment.this.bannerView_320x50 == null) {
                            BannerFragment.this.bannerView_320x50 = new AdSwitcherBannerView(BannerFragment.this.activity, AdSwitcherConfigLoader.getInstance(), "banner_320x50", true, BannerAdSize.SIZE_320X50);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                            layout.addView(BannerFragment.this.bannerView_320x50, lp);

                        } else {
                            BannerFragment.this.bannerView_320x50.switchAd();
                        }
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
                        if (BannerFragment.this.bannerView_320x100 == null) {
                            BannerFragment.this.bannerView_320x100 = new AdSwitcherBannerView(BannerFragment.this.activity, AdSwitcherConfigLoader.getInstance(), "banner_320x100", true, BannerAdSize.SIZE_320X100);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                            layout.addView(BannerFragment.this.bannerView_320x100, lp);

                        } else {
                            BannerFragment.this.bannerView_320x100.switchAd();
                        }
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
                        if (BannerFragment.this.bannerView_300x250 == null) {
                            BannerFragment.this.bannerView_300x250 = new AdSwitcherBannerView(BannerFragment.this.activity, AdSwitcherConfigLoader.getInstance(), "banner_300x250", true, BannerAdSize.SIZE_300X250);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                            layout.addView(BannerFragment.this.bannerView_300x250, lp);

                        } else {
                            BannerFragment.this.bannerView_300x250.switchAd();
                        }
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
                        if (BannerFragment.this.bannerView_320x50 != null) {
                            BannerFragment.this.bannerView_320x50.hide();
                        }
                        if (BannerFragment.this.bannerView_320x100 != null) {
                            BannerFragment.this.bannerView_320x100.hide();
                        }
                        if (BannerFragment.this.bannerView_300x250 != null) {
                            BannerFragment.this.bannerView_300x250.hide();
                        }
                    }
                });
            }
        });

        view.findViewById(R.id.button_ShowBanner320x50).performClick();

        return view;
    }

}
