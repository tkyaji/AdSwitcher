package net.adswitcher.config;

import java.util.List;

/**
 * Created by tkyaji on 2016/07/15.
 */
public class AdSwitcherConfig {

    private static final String TAG = "AdSwitcherConfig";

    public String category;
    public AdSwitchType switchType;
    public int interval;
    public List<AdConfig> adConfigList;

    public AdSwitcherConfig() {}

    public AdSwitcherConfig(String category, AdSwitchType switchType, int interval, List<AdConfig> adConfigList) {
        this.category = category;
        this.switchType = switchType;
        this.interval = interval;
        this.adConfigList = adConfigList;
    }
}
