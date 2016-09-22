#if UNITY_ANDROID && !UNITY_EDITOR

using UnityEngine;
using System;

public class AdSwitcherBannerView {

	private class AdReceivedListener : AndroidJavaProxy {
		private Action<AdConfig, bool> handler;
		public AdReceivedListener(Action<AdConfig, bool> handler) : base("net.adswitcher.AdSwitcherBannerView$AdReceivedListener") {
			this.handler = handler;
		}
		void onAdReceived(AndroidJavaObject javaObj_adConfig, bool result) {
			UnityActionExecuter.RunOnUnity(() => {
				var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
				this.handler.Invoke(adConfig, result);
			});
		}
	}

	private class AdShownListener : AndroidJavaProxy {
		private Action<AdConfig> handler;
		public AdShownListener(Action<AdConfig> handler) : base("net.adswitcher.AdSwitcherBannerView$AdShownListener") {
			this.handler = handler;
		}
		void onAdShown(AndroidJavaObject javaObj_adConfig) {
			UnityActionExecuter.RunOnUnity(() => {
				var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
				this.handler.Invoke(adConfig);
			});
		}
	}

	private class AdClickedListener : AndroidJavaProxy {
		private Action<AdConfig> handler;
		public AdClickedListener(Action<AdConfig> handler) : base("net.adswitcher.AdSwitcherBannerView$AdClickedListener") {
			this.handler = handler;
		}
		void onAdClicked(AndroidJavaObject javaObj_adConfig) {
			UnityActionExecuter.RunOnUnity(() => {
				var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
				this.handler.Invoke(adConfig);
			});
		}
	}


	private AndroidJavaObject javaObj;
	private AndroidJavaObject javaObj_activity;


	public AdSwitcherBannerView(AdSwitcherConfigLoader configLoader, string category,
	                            BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
	                            bool testMode = false) {
	}

	public AdSwitcherBannerView(AdSwitcherConfig adSwitcherConfig,
								BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
	                            bool testMode = false) {

		this.javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj = new AndroidJavaObject("net.adswitcher.AdSwitcherBannerView",
												 this.javaObj_activity,
												 JavaObjectConverter.AdSwitcherConfigToJavaObject(adSwitcherConfig),
												 testMode,
												 JavaObjectConverter.BannerAdSizeToJavaObject(adSize));
			this.addContentView(this.javaObj, adAlign, adMargin);
		}));
		UnityActionExecuter.Initialize();
	}


	public void Show() {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("load");
		}));
	}

	public void Hide() {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("hide");
		}));
	}

	public void SwitchAd() {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("switchAd");
		}));
	}

	public bool IsLoaded() {
		return this.javaObj != null && this.javaObj.Call<bool>("isLoaded");
	}


	public void SetAdReceivedHandler(Action<AdConfig, bool> handler) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("setAdReceivedListener", new AdReceivedListener((adConfig, result) => {
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

	public void SetAdClickedHandler(Action<AdConfig> handler) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("setAdClickedListener", new AdClickedListener(adConfig => {
				handler.Invoke(adConfig);
			}));
		}));
	}


	private void addContentView(AndroidJavaObject javaObj_view, BannerAdAlign adAlign, BannerAdMargin adMargin) {
		var javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");

		int wrapContent = new AndroidJavaClass("android.view.ViewGroup$LayoutParams").GetStatic<int>("WRAP_CONTENT");
		var javaObj_layoutParams = new AndroidJavaObject("android.widget.FrameLayout$LayoutParams", wrapContent, wrapContent);

		var javaCls_gravity = new AndroidJavaClass("android.view.Gravity");

		int gravity = 0;
		switch (adAlign) {
			case BannerAdAlign.TopLeft:
				gravity = javaCls_gravity.GetStatic<int>("TOP") | javaCls_gravity.GetStatic<int>("LEFT");
				break;

			case BannerAdAlign.TopCenter:
				gravity = javaCls_gravity.GetStatic<int>("TOP") | javaCls_gravity.GetStatic<int>("CENTER_HORIZONTAL");
				break;

			case BannerAdAlign.TopRight:
				gravity = javaCls_gravity.GetStatic<int>("TOP") | javaCls_gravity.GetStatic<int>("RIGHT");
				break;

			case BannerAdAlign.BottomLeft:
				gravity = javaCls_gravity.GetStatic<int>("BOTTOM") | javaCls_gravity.GetStatic<int>("LEFT");
				break;

			case BannerAdAlign.BottomCenter:
				gravity = javaCls_gravity.GetStatic<int>("BOTTOM") | javaCls_gravity.GetStatic<int>("CENTER_HORIZONTAL");
				break;

			case BannerAdAlign.BottomRight:
				gravity = javaCls_gravity.GetStatic<int>("BOTTOM") | javaCls_gravity.GetStatic<int>("RIGHT");
				break;
		}

		javaObj_layoutParams.Set("gravity", gravity);

		float scale = javaObj_activity.Call<AndroidJavaObject>("getResources")
									  .Call<AndroidJavaObject>("getDisplayMetrics")
									  .Get<float>("density");

		int marginLeft = Mathf.FloorToInt(adMargin.Left * scale + 0.5f);
		int marginRight = Mathf.FloorToInt(adMargin.Right * scale + 0.5f);
		int marginTop = Mathf.FloorToInt(adMargin.Top * scale + 0.5f);
		int marginBottom = Mathf.FloorToInt(adMargin.Bottom * scale + 0.5f);

		javaObj_layoutParams.Call("setMargins", marginLeft, marginTop, marginRight, marginBottom);

		javaObj_activity.Call("addContentView", javaObj_view, javaObj_layoutParams);
	}

}

#endif
