package net.adswitcher;

import android.util.Log;

import net.adswitcher.config.AdConfig;
import net.adswitcher.config.AdSwitchType;
import net.adswitcher.config.AdSwitcherConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by tkyaji on 2016/09/04.
 */
public class AdSelector {

    private static final String TAG = "AdSelector";

    private static Map<String, Integer> rotateIndexMap = new HashMap<>();

    private AdSwitcherConfig adSwitcherConfig;
    private List<AdConfig> adConfigList;
    private int rotateCount;

    public AdSelector(AdSwitcherConfig adSwitcherConfig) {
        this.adSwitcherConfig = adSwitcherConfig;
        this.adConfigList = new ArrayList<>(adSwitcherConfig.adConfigList);
    }

    public AdConfig selectAd() {
        if (this.adConfigList.size() == 0) {
            return null;
        }

        int index = -1;
        if (this.adSwitcherConfig.switchType == AdSwitchType.Rotate) {
            index = this.selectAdByRotate();
        } else {
            index = this.selectAdByRatio();
        }

        if (index == -1) {
            return null;
        }

        AdConfig config = this.adConfigList.get(index);
        Log.d(TAG, "select:" + config.adName);

        if (this.adSwitcherConfig.switchType == AdSwitchType.Ratio) {
            this.adConfigList.remove(index);
        }

        return config;
    }

    private int selectAdByRatio() {
        Log.d(TAG, "selectAdByRatio");

        int totalRatio = 0;
        for (AdConfig config : this.adConfigList) {
            Log.d(TAG, config.className + ": ratio=" + config.ratio);
            totalRatio += config.ratio;
        }
        if (totalRatio == 0) {
            return -1;
        }

        int randVal = new Random().nextInt(totalRatio);
        Log.d(TAG, randVal + " / " + totalRatio);
        int tmpSumRatio = 0;
        for (int i = 0; i < this.adConfigList.size(); i++) {
            AdConfig config = this.adConfigList.get(i);
            tmpSumRatio += config.ratio;
            if (randVal < tmpSumRatio) {
                return i;
            }
        }

        return -1;
    }

    private int selectAdByRotate() {
        Log.d(TAG, "selectAdByRotate");

        if (this.rotateCount >= this.adConfigList.size()) {
            return -1;
        }

        int rotateIndex = 0;
        if (this.rotateIndexMap.containsKey(this.adSwitcherConfig.category)) {
            rotateIndex = this.rotateIndexMap.get(this.adSwitcherConfig.category);
        }

        if (rotateIndex >= this.adConfigList.size()) {
            rotateIndex = 0;
        }

        this.rotateIndexMap.put(this.adSwitcherConfig.category, rotateIndex + 1);
        this.rotateCount++;

        return rotateIndex;
    }
}
