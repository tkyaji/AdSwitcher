#if UNITY_ANDROID && !UNITY_EDITOR

using System;
using UnityEngine;

public class AdSwitcherNativeAd {

	private class AdReceivedListener : AndroidJavaProxy {
		private Action<AdConfig, bool> handler;
		public AdReceivedListener(Action<AdConfig, bool> handler) : base("net.adswitcher.AdSwitcherNativeAd$AdReceivedListener") {
			this.handler = handler;
		}
		void onAdReceived(AndroidJavaObject javaObj_adConfig, bool result) {
			UnityActionExecuter.RunOnUnity(() => {
				var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
				this.handler.Invoke(adConfig, result);
			});
		}
	}

	private AndroidJavaObject javaObj;
	private AndroidJavaObject javaObj_activity;

	public AdSwitcherNativeAd(AdSwitcherConfigLoader configLoader, string category, bool testMode = false) {
		this.javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
		var javaObj_configLoader = new AndroidJavaClass("net.adswitcher.config.AdSwitcherConfigLoader").CallStatic<AndroidJavaObject>("getInstance");
		this.javaObj = new AndroidJavaObject("net.adswitcher.AdSwitcherNativeAd",
											 this.javaObj_activity,
											 javaObj_configLoader,
											 category,
											 testMode);
		UnityActionExecuter.Initialize();
	}

	public AdSwitcherNativeAd(AdSwitcherConfig adSwitcherConfig, bool testMode = false) {
		this.javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
		this.javaObj = new AndroidJavaObject("net.adswitcher.AdSwitcherNativeAd",
											 this.javaObj_activity,
											 JavaObjectConverter.AdSwitcherConfigToJavaObject(adSwitcherConfig),
											 testMode);
		UnityActionExecuter.Initialize();
	}


	public void Load() {
		this.javaObj.Call("load");
	}

	public AdSwitcherNativeAdData GetAdData() {
		var javaObj_adData = this.javaObj.Call<AndroidJavaObject>("getAdData");
		if (javaObj_adData == null) {
			return null;
		}

		var adData = new AdSwitcherNativeAdData();
		adData.shortText = javaObj_adData.Get<string>("shortText");
		adData.longText = javaObj_adData.Get<string>("longText");
		adData.buttonText = javaObj_adData.Get<string>("buttonText");
		adData.imageUrl = javaObj_adData.Get<string>("imageUrl");
		adData.iconImageUrl = javaObj_adData.Get<string>("iconImageUrl");

		return adData;
	}

	public bool IsLoaded() {
		return this.javaObj != null && this.javaObj.Call<bool>("isLoaded");
	}

	public void OpenUrl() {
		this.javaObj.Call("openUrl");
	}

	public void SendImpression() {
		this.javaObj.Call("sendImpression");
	}

	public void SetAdReceivedHandler(Action<AdConfig, bool> handler) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("setAdReceivedListener", new AdReceivedListener((adConfig, result) => {
				handler.Invoke(adConfig, result);
			}));
		}));
	}

	public void LoadImage(Action<Texture2D> onLoaded) {
		var adData = this.GetAdData();
		if (adData == null) {
			return;
		}
		ImageLoader.Load(adData.imageUrl, onLoaded);
	}

	public void LoadIconImage(Action<Texture2D> onLoaded) {
		var adData = this.GetAdData();
		if (adData == null) {
			return;
		}
		ImageLoader.Load(adData.iconImageUrl, onLoaded);
	}

	public void LoadImage(Action<Sprite> onLoaded) {
		this.LoadImage((Texture2D tex2d) => {
			onLoaded.Invoke(ImageLoader.ConvertToSprite(tex2d));
		});
	}

	public void LoadIconImage(Action<Sprite> onLoaded) {
		this.LoadIconImage((Texture2D tex2d) => {
			onLoaded.Invoke(ImageLoader.ConvertToSprite(tex2d));
		});
	}

}

#endif
