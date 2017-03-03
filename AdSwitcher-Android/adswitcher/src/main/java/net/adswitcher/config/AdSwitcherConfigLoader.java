package net.adswitcher.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tkyaji on 2016/07/15.
 */
public class AdSwitcherConfigLoader {

    private static final String TAG = "AdSwitcherConfigLoader";

    private static final String JSON_CACHE_KEY = "AdSwitcher-jsonCache";

    public interface ConfigLoadHandler {
        public void onLoaded();
    }

    private interface LoadCompleteHandler {
        public void onLoadCompleted(String loadedText);
        public void onFailedToLoad();
    }


    private static final AdSwitcherConfigLoader instance = new AdSwitcherConfigLoader();
    public static AdSwitcherConfigLoader getInstance() { return instance; }

    public boolean loaded;
    public boolean loading;

    private Map<String, AdSwitcherConfig> configMap = new HashMap<>();
    private List<ConfigLoadHandler> configLoadHandlerList = new ArrayList<>();


    public void startLoad(final Context context, final URL url) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String cachedJsonText = preferences.getString(JSON_CACHE_KEY, null);

        this.loading = true;
        this.loadFromUrl(url, new LoadCompleteHandler() {
            @Override
            public void onLoadCompleted(String loadedText) {
                AdSwitcherConfigLoader.this.configMap = AdSwitcherConfigLoader.this.toConfigMap(loadedText);
                if (AdSwitcherConfigLoader.this.configMap != null) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(JSON_CACHE_KEY, loadedText);
                    editor.apply();

                    loadComplete();

                } else {
                    startLoadWithDelay(30000, context, url);
                }
            }

            @Override
            public void onFailedToLoad() {
                Map<String, AdSwitcherConfig> configMap = AdSwitcherConfigLoader.this.toConfigMap(cachedJsonText);

                if (cachedJsonText != null) {
                    AdSwitcherConfigLoader.this.configMap = AdSwitcherConfigLoader.this.toConfigMap(cachedJsonText);
                    if (AdSwitcherConfigLoader.this.configMap == null) {
                        startLoadWithDelay(5000, context, url);
                    }
                    loadComplete();

                } else {
                    startLoadWithDelay(5000, context, url);
                }
            }
        });

    }

    private void startLoadWithDelay(final int waitTime, final Context context, final URL url) {
        HandlerThread handlerThread = new HandlerThread("retryThread");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startLoad(context, url);
            }
        }, waitTime);
    }

    public void loadJson(String jsonText) {
        AdSwitcherConfigLoader.this.configMap = AdSwitcherConfigLoader.this.toConfigMap(jsonText);
        if (AdSwitcherConfigLoader.this.configMap != null) {
            loadComplete();
        }
    }

    private void loadFromUrl(final URL url, final LoadCompleteHandler onLoadCompleted) {
        if (url == null) {
            onLoadCompleted.onFailedToLoad();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);
                    connection.connect();

                    int statusCode = connection.getResponseCode();
                    if (statusCode < 200 || statusCode >= 300) {
                        Log.d(TAG, String.format("Invalid status error (%d)", statusCode));
                        onLoadCompleted.onFailedToLoad();
                        return;
                    }

                    // 正常
                    String responseText = readInputStream(connection.getInputStream());
                    onLoadCompleted.onLoadCompleted(responseText);

                } catch(Exception ex) {
                    // 未接続
                    Log.d(TAG, "No Response");
                    onLoadCompleted.onFailedToLoad();
                    return;
                }

            }

        }).start();
    }

    private Map<String, AdSwitcherConfig> toConfigMap(String jsonText) {
        if (jsonText == null) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonText);
            return toAdSwitcherConfigMap(jsonObject);

        } catch (JSONException ex) {
            // JSONパースエラー
            Log.e(TAG, "Json parse error", ex);
            return null;
        }
    }

    private void loadComplete() {
        synchronized(AdSwitcherConfigLoader.this) {
            for (ConfigLoadHandler handler : configLoadHandlerList) {
                handler.onLoaded();
            }
            configLoadHandlerList.clear();

            loading = false;
            loaded = true;
        }
    }

    private String readInputStream(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

        } finally {
            reader.close();
        }

        return stringBuilder.toString();
    }

    private Map<String, AdSwitcherConfig> toAdSwitcherConfigMap(JSONObject jsonObject) throws JSONException {

        Map<String, AdSwitcherConfig> adSwitcherConfigMap = new HashMap<>();

        Iterator<String> it = jsonObject.keys();
        while (it.hasNext()) {
            String cateogry = it.next();
            JSONObject obj = jsonObject.getJSONObject(cateogry);
            String switchType = obj.has("switch_type") ? obj.getString("switch_type") : "ratio";
            int interval = obj.has("interval") ? obj.getInt("interval") : 0;
            String intervalType = obj.has("interval_type") ? obj.getString("interval_type") : "count";
            JSONArray ads = obj.getJSONArray("ads");

            List<AdConfig> adConfigList = new ArrayList<>();

            for (int i = 0; i < ads.length(); i++) {
                JSONObject ad = ads.getJSONObject(i);
                String adName = ad.getString("name");
                String className = ad.getString("class_name");
                int ratio = ad.getInt("ratio");

                Map<String, String> params = new HashMap<>();
                JSONObject paramJson = ad.getJSONObject("parameters");
                Iterator<String> paramIt = paramJson.keys();
                while (paramIt.hasNext()) {
                    String paramKey = paramIt.next();
                    String paramVal = paramJson.getString(paramKey);
                    params.put(paramKey, paramVal);
                }

                AdConfig adConfig = new AdConfig(adName, className, ratio, params);
                adConfigList.add(adConfig);
            }

            AdSwitcherConfig adSwitcherConfig = new AdSwitcherConfig(cateogry, this.toAdSwitchType(switchType), interval, this.toIntervalType(intervalType), adConfigList);
            adSwitcherConfigMap.put(cateogry, adSwitcherConfig);
        }

        return adSwitcherConfigMap;
    }

    private AdSwitchType toAdSwitchType(String adSwitchTypeStr) {
        if ("rotate".equals(adSwitchTypeStr)) {
            return AdSwitchType.Rotate;
        } else {
            return AdSwitchType.Ratio;
        }
    }

    private IntervalType toIntervalType(String intervalTypeStr) {
        if ("time".equals(intervalTypeStr)) {
            return IntervalType.Time;
        } else {
            return IntervalType.Count;
        }
    }

    public void addConfigLoadedHandler(ConfigLoadHandler handler) {
        synchronized (this) {
            if (this.loaded) {
                handler.onLoaded();
            } else {
                this.configLoadHandlerList.add(handler);
            }
        }
    }

    public AdSwitcherConfig getAdSwitcherConfig(String category) {
        if (this.configMap.size() == 0) {
            return null;
        }
        return this.configMap.get(category);
    }
}
