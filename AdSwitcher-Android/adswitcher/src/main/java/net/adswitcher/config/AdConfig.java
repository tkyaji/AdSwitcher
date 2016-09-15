package net.adswitcher.config;

import java.util.Map;

/**
 * Created by tkyaji on 2016/07/15.
 */
public class AdConfig {

    public String adName;
    public String className;
    public int ratio;
    public Map<String, String> parameters;

    public AdConfig() {}

    public AdConfig(String adName, String className, int ratio, Map<String, String> parameters) {
        this.adName = adName;
        this.className = className;
        this.ratio = ratio;
        this.parameters = parameters;
    }
}
