#if UNITY_ANDROID && !UNITY_EDITOR

using UnityEngine;
using System;

public class AdSwitcherInterstitial {

	private class AdLoadedListener : AndroidJavaProxy {
		private Action<AdConfig, bool> handler;
		public AdLoadedListener(Action<AdConfig, bool> handler) : base("net.adswitcher.AdSwitcherInterstitial$AdLoadedListener") {
			this.handler = handler;
		}
		void onAdLoaded(AndroidJavaObject javaObj_adConfig, bool result) {
			UnityActionExecuter.RunOnUnity(() => {
				var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
				this.handler.Invoke(adConfig, result);
			});
		}
	}

	private class AdShownListener : AndroidJavaProxy {
		private Action<AdConfig> handler;
		public AdShownListener(Action<AdConfig> handler) : base("net.adswitcher.AdSwitcherInterstitial$AdShownListener") {
			this.handler = handler;
		}
		void onAdShown(AndroidJavaObject javaObj_adConfig) {
			var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
			this.handler.Invoke(adConfig);
		}
	}

	private class AdClosedListener : AndroidJavaProxy {
		private Action<AdConfig, bool, bool> handler;
		public AdClosedListener(Action<AdConfig, bool, bool> handler) : base("net.adswitcher.AdSwitcherInterstitial$AdClosedListener") {
			this.handler = handler;
		}
		void onAdClosed(AndroidJavaObject javaObj_adConfig, bool result, bool isSkipped) {
			UnityActionExecuter.RunOnUnity(() => {
				var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
				this.handler.Invoke(adConfig, result, isSkipped);
			});
		}
	}

	private class AdClickedListener : AndroidJavaProxy {
		private Action<AdConfig> handler;
		public AdClickedListener(Action<AdConfig> handler) : base("net.adswitcher.AdSwitcherInterstitial$AdClickedListener") {
			this.handler = handler;
		}
		void onAdClicked(AndroidJavaObject javaObj_adConfig) {
			var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
			this.handler.Invoke(adConfig);
		}
	}


	private AndroidJavaObject javaObj;
	private AndroidJavaObject javaObj_activity;



	public AdSwitcherInterstitial(AdSwitcherConfigLoader configLoader, string category, bool testMode = false) {
		this.javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj = new AndroidJavaObject("net.adswitcher.AdSwitcherInterstitial",
												 this.javaObj_activity,
												 configLoader.getJavaObject(),
												 category,
												 testMode);
		}));
		UnityActionExecuter.Initialize();
	}

	public AdSwitcherInterstitial(AdSwitcherConfig adSwitcherConfig, bool testMode = false) {
		this.javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj = new AndroidJavaObject("net.adswitcher.AdSwitcherInterstitial",
												 this.javaObj_activity,
			                                     JavaObjectConverter.AdSwitcherConfigToJavaObject(adSwitcherConfig),
												 testMode);
		}));
		UnityActionExecuter.Initialize();
	}


	public void Show() {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("show");
		}));
	}

	public bool IsLoaded() {
		return this.javaObj != null && this.javaObj.Call<bool>("isLoaded");
	}


	public void SetAdLoadedHandler(Action<AdConfig, bool> handler) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("setAdLoadedListener", new AdLoadedListener((adConfig, result) => {
				handler.Invoke(adConfig, result);
			}));
		}));
	}

	public void SetAdShownHandler(Action<AdConfig> handler) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("setAdShownListener", new AdShownListener(adConfig => {
				handler.Invoke(adConfig);
			}));
		}));
	}

	public void SetAdClosedHandler(Action<AdConfig, bool, bool> handler) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("setAdClosedListener", new AdClosedListener((adConfig, result, isSkipped) => {
				handler.Invoke(adConfig, result, isSkipped);
			}));
		}));
	}

	public void SetAdClickedHandler(Action<AdConfig> handler) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("setAdClickedListener", new AdClickedListener(adConfig => {
				handler.Invoke(adConfig);
			}));
		}));
	}

}

#endif
