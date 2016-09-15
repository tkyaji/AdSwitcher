package net.adswitcher.sample;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.adswitcher.AdSwitcherInterstitial;
import net.adswitcher.config.AdSwitcherConfigLoader;


public class InterstitialFragment extends Fragment {

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interstitial, container, false);

        AdSwitcherConfigLoader configLoader = AdSwitcherConfigLoader.getInstance();

        final AdSwitcherInterstitial interstitial = new AdSwitcherInterstitial(this.activity, configLoader, "interstitial", true);

        view.findViewById(R.id.button_showInterstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterstitialFragment.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        interstitial.show();
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
