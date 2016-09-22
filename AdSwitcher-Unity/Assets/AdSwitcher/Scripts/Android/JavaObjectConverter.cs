#if UNITY_ANDROID

using UnityEngine;
using System;
using System.Collections.Generic;

public class JavaObjectConverter {

	public static AndroidJavaObject AdSwitcherConfigToJavaObject(AdSwitcherConfig adSwitcherConfig) {
		var javaObj_adSwitcherConfig = new AndroidJavaObject("net.adswitcher.config.AdSwitcherConfig");
		javaObj_adSwitcherConfig.Set<string>("category", adSwitcherConfig.Category);

		var javaClass_adSwitchType = new AndroidJavaClass("net.adswitcher.config.AdSwitchType");
		var javaObj_adSwitchType = javaClass_adSwitchType.GetStatic<AndroidJavaObject>(adSwitcherConfig.SwitchType.ToString());
		javaObj_adSwitcherConfig.Set<AndroidJavaObject>("switchType", javaObj_adSwitchType);

		javaObj_adSwitcherConfig.Set<int>("interval", adSwitcherConfig.Interval);

		var javaObj_list = new AndroidJavaObject("java.util.ArrayList");
		IntPtr addId = AndroidJNIHelper.GetMethodID(javaObj_list.GetRawClass(), "add", "(Ljava/lang/Object;)Z");
		foreach (var adConfig in adSwitcherConfig.AdConfigList) {
			var args = new object[] { AConfigToJavaObject(adConfig) };
			AndroidJNI.CallBooleanMethod(javaObj_list.GetRawObject(), addId, AndroidJNIHelper.CreateJNIArgArray(args));
		}
		javaObj_adSwitcherConfig.Set<AndroidJavaObject>("adConfigList", javaObj_list);

		return javaObj_adSwitcherConfig;
	}

	public static AndroidJavaObject AConfigToJavaObject(AdConfig adConfig) {
		var javaObj_adConfig = new AndroidJavaObject("net.adswitcher.config.AdConfig");

		javaObj_adConfig.Set<string>("adName", adConfig.AdName);
		javaObj_adConfig.Set<string>("className", adConfig.ClassName);
		javaObj_adConfig.Set<int>("ratio", adConfig.Ratio);

		var javaObj_map = new AndroidJavaObject("java.util.HashMap");
		IntPtr putId = AndroidJNIHelper.GetMethodID(javaObj_map.GetRawClass(), "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		foreach (var parameter in adConfig.Parameters) {
			var args = new object[] { new AndroidJavaObject("java.lang.String", parameter.Key), new AndroidJavaObject("java.lang.String", parameter.Value) };
			AndroidJNI.CallObjectMethod(javaObj_map.GetRawObject(), putId, AndroidJNIHelper.CreateJNIArgArray(args));
		}
		javaObj_adConfig.Set<AndroidJavaObject>("parameters", javaObj_map);

		return javaObj_adConfig;
	}

	public static AdConfig JavaObjectToAdConfig(AndroidJavaObject javaObj_adConfig) {
		AdConfig adConfig = new AdConfig();

		adConfig.AdName = javaObj_adConfig.Get<string>("adName");
		adConfig.ClassName = javaObj_adConfig.Get<string>("className");
		adConfig.Ratio = javaObj_adConfig.Get<int>("ratio");

		var javaObj_map = javaObj_adConfig.Get<AndroidJavaObject>("parameters");
		var javaObjs_keys = javaObj_map.Call<AndroidJavaObject>("keySet").Call<AndroidJavaObject[]>("toArray");

		var parametersDict = new Dictionary<string, string>();
		foreach (var javaObj_key in javaObjs_keys) {
			var javaObj_val = javaObj_map.Call<AndroidJavaObject>("get", javaObj_key);
			var key = javaObj_key.Call<string>("toString");
			var val = javaObj_val.Call<string>("toString");
			parametersDict.Add(key, val);
		}
		adConfig.Parameters = parametersDict;

		return adConfig;
	}

	public static AndroidJavaObject BannerAdSizeToJavaObject(BannerAdSize adSize) {
		var javaClass_bannerAdSize = new AndroidJavaClass("net.adswitcher.adapter.BannerAdSize");
		switch (adSize) {
			case BannerAdSize.Size_320x50:
				return javaClass_bannerAdSize.GetStatic<AndroidJavaObject>("SIZE_320X50");
				
			case BannerAdSize.Size_320x100:
				return javaClass_bannerAdSize.GetStatic<AndroidJavaObject>("SIZE_320X100");
				
			case BannerAdSize.Size_300x250:
				return javaClass_bannerAdSize.GetStatic<AndroidJavaObject>("SIZE_300X250");
		}
		return javaClass_bannerAdSize.GetStatic<AndroidJavaObject>("SIZE_320X50");
	}

}

#endif
