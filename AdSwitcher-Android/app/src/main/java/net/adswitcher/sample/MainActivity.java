package net.adswitcher.sample;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.adswitcher.config.AdSwitcherConfigLoader;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        String jsonText = this.readJsonTextFromAsset();
        AdSwitcherConfigLoader.getInstance().loadJson(jsonText);

        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.container);

        TabHost.TabSpec bannerTabSpec = tabHost.newTabSpec("banner_tab")
                .setIndicator("Banner");
        tabHost.addTab(bannerTabSpec, BannerFragment.class, null);

        TabHost.TabSpec interstitialTabSpec = tabHost.newTabSpec("interstitial_tab")
                .setIndicator("Interstitial");
        tabHost.addTab(interstitialTabSpec, InterstitialFragment.class, null);

        TabHost.TabSpec videoTabSpec = tabHost.newTabSpec("video_tab")
                .setIndicator("Video");
        tabHost.addTab(videoTabSpec, VideoFragment.class, null);
    }


    private String readJsonTextFromAsset() {
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = this.getAssets().open("adswitcher.json");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();

        } catch (Exception ex) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
