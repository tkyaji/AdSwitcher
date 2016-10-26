#if UNITY_EDITOR || (!UNITY_IOS && !UNITY_ANDROID)

using System;
using UnityEngine;

public class AdSwitcherNativeAd {

	private static AdConfig _adConfig;
	private static AdConfig adConfig {
		get {
			if (_adConfig == null) {
				_adConfig = new AdConfig();
				_adConfig.AdName = "Stub";
				_adConfig.ClassName = "Stub";
				_adConfig.Parameters = new System.Collections.Generic.Dictionary<string, string>();
			}
			return _adConfig;
		}
	}

	private Action<AdConfig, bool> adReceivedHandler;

	private bool loaded;


	public AdSwitcherNativeAd(AdSwitcherConfigLoader configLoader, string category, bool testMode = false) {
	}

	public AdSwitcherNativeAd(AdSwitcherConfig adSwitcherConfig, bool testMode = false) {
	}


	public void Load() {
		WithWaitInvoker.Register(() => {
			this.loaded = (Application.internetReachability != NetworkReachability.NotReachable);
			if (this.adReceivedHandler != null) {
				if (this.loaded) {
					this.adReceivedHandler.Invoke(adConfig, this.loaded);
				} else {
					Debug.Log("load failed -> reload");
					this.Load();
				}
			}
		}, 0.5f);

	}

	public AdSwitcherNativeAdData GetAdData() {
		var adData = new AdSwitcherNativeAdData();
		adData.shortText = "広告 SHORT";
		adData.longText = "広告LONG";
		adData.buttonText = "クリック";
		return adData;
	}

	public bool IsLoaded() {
		return this.loaded;
	}

	public void OpenUrl() {
		Application.OpenURL("http://google.com");
	}

	public void SendImpression() {
	}

	public void SetAdReceivedHandler(Action<AdConfig, bool> handler) {
		this.adReceivedHandler = handler;
	}

	public void LoadImage(Action<Texture2D> onLoaded) {
	}

	public void LoadImage(Action<Sprite> onLoaded) {
	}

	public void LoadIconImage(Action<Texture2D> onLoaded) {
	}

	public void LoadIconImage(Action<Sprite> onLoaded) {
	}

}

#endif
