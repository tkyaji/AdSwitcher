package net.adswitcher.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.adswitcher.AdSwitcherInterstitial;
import net.adswitcher.config.AdSwitcherConfigLoader;


public class VideoFragment extends Fragment {

    private Activity activity;
    private static AdSwitcherInterstitial video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        if (video == null) {
            video = new AdSwitcherInterstitial(this.activity, AdSwitcherConfigLoader.getInstance(), "video", true);
        }

        view.findViewById(R.id.button_showVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoFragment.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        video.show();
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
