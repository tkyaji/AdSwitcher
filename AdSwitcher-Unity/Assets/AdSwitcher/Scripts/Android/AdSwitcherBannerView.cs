#if UNITY_ANDROID && !UNITY_EDITOR

using UnityEngine;
using System;

public class AdSwitcherBannerView {

	private static int viewVisible = new AndroidJavaClass("android.view.View").GetStatic<int>("VISIBLE");
	private static int viewGone = new AndroidJavaClass("android.view.View").GetStatic<int>("GONE");

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
			var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
			this.handler.Invoke(adConfig);
		}
	}

	private class AdClickedListener : AndroidJavaProxy {
		private Action<AdConfig> handler;
		public AdClickedListener(Action<AdConfig> handler) : base("net.adswitcher.AdSwitcherBannerView$AdClickedListener") {
			this.handler = handler;
		}
		void onAdClicked(AndroidJavaObject javaObj_adConfig) {
			var adConfig = JavaObjectConverter.JavaObjectToAdConfig(javaObj_adConfig);
			this.handler.Invoke(adConfig);
		}
	}


	private AndroidJavaObject javaObj;
	private AndroidJavaObject javaObj_activity;
	private bool isSizeToFit;


	public AdSwitcherBannerView(AdSwitcherConfigLoader configLoader, string category,
								BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
								bool testMode = false/*, bool isSizeToFit = false*/) {

		bool isSizeToFit = false;   // TODO
		this.isSizeToFit = isSizeToFit;

		this.javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
		var javaObj_configLoader = new AndroidJavaClass("net.adswitcher.config.AdSwitcherConfigLoader").CallStatic<AndroidJavaObject>("getInstance");
		this.javaObj = new AndroidJavaObject("net.adswitcher.AdSwitcherBannerView",
											 this.javaObj_activity,
											 javaObj_configLoader,
											 category,
											 JavaObjectConverter.BannerAdSizeToJavaObject(adSize),
											 testMode);

		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.addContentView(adAlign, adMargin, isSizeToFit);
		}));

		UnityActionExecuter.Initialize();
	}

	public AdSwitcherBannerView(AdSwitcherConfig adSwitcherConfig,
								BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
								bool testMode = false/*, bool isSizeToFit = false*/) {

		bool isSizeToFit = false;   // TODO
		this.isSizeToFit = isSizeToFit;

		this.javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
		this.javaObj = new AndroidJavaObject("net.adswitcher.AdSwitcherBannerView",
											 this.javaObj_activity,
											 JavaObjectConverter.AdSwitcherConfigToJavaObject(adSwitcherConfig),
											 JavaObjectConverter.BannerAdSizeToJavaObject(adSize),
											 testMode);

		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.addContentView(adAlign, adMargin, isSizeToFit);
		}));

		UnityActionExecuter.Initialize();
	}

	public void SetPosition(BannerAdAlign adAlign, BannerAdMargin adMargin) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			var javaObj_layoutParams = this.createLayoutParams(adAlign, adMargin, this.isSizeToFit);
			this.javaObj.Call("setLayoutParams", javaObj_layoutParams);
		}));
	}

	public void Load(bool autoShow = false) {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("load", autoShow);
		}));
	}

	public void Show() {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("show");
			this.javaObj.Call("setVisibility", viewVisible);
		}));
	}

	public void Hide() {
		this.javaObj_activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
			this.javaObj.Call("hide");
			this.javaObj.Call("setVisibility", viewGone);
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

	public Vector2 GetSize() {
		float w = this.javaObj.Call<float>("getDpiWidth");
		float h = this.javaObj.Call<float>("getDpiHeight");
		return new Vector2(w, h);
	}

	public Vector2 GetScreenSize() {
		var javaObj_metrics = this.getMetrics();
		float density = javaObj_metrics.Get<float>("density");
		float w = javaObj_metrics.Get<int>("widthPixels") / density;
		float h = javaObj_metrics.Get<int>("heightPixels") / density;

		return new Vector2(w, h);
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


	private void addContentView(BannerAdAlign adAlign, BannerAdMargin adMargin, bool isSizeToFit) {
		var javaObj_layoutParams = this.createLayoutParams(adAlign, adMargin, isSizeToFit);
		this.javaObj_activity.Call("addContentView", this.javaObj, javaObj_layoutParams);
	}

	private AndroidJavaObject createLayoutParams(BannerAdAlign adAlign, BannerAdMargin adMargin, bool isSizeToFit) {
		var javaObj_metrics = this.getMetrics();

		float density = javaObj_metrics.Get<float>("density");

		float scale = 1f;
		if (isSizeToFit) {
			scale = javaObj_metrics.Get<int>("widthPixels") / density / this.GetSize().x;
		}

		var javaCls_gravity = new AndroidJavaClass("android.view.Gravity");

		int gravity = 0;
		switch (adAlign) {
			case BannerAdAlign.TopLeft:
				gravity = javaCls_gravity.GetStatic<int>("TOP") | javaCls_gravity.GetStatic<int>("LEFT");

				if (isSizeToFit && scale > 1f) {
					this.javaObj.Call("setPivotX", 0f);
					this.javaObj.Call("setPivotY", 0f);
					this.javaObj.Call("setScaleX", scale);
					this.javaObj.Call("setScaleY", scale);
				}

				break;

			case BannerAdAlign.TopCenter:
				gravity = javaCls_gravity.GetStatic<int>("TOP") | javaCls_gravity.GetStatic<int>("CENTER_HORIZONTAL");

				if (isSizeToFit && scale > 1f) {
					this.javaObj.Call("setPivotX", this.javaObj.Call<float>("getPxWidth") / 2);
					this.javaObj.Call("setPivotY", 0f);
					this.javaObj.Call("setScaleX", scale);
					this.javaObj.Call("setScaleY", scale);
				}

				break;

			case BannerAdAlign.TopRight:
				gravity = javaCls_gravity.GetStatic<int>("TOP") | javaCls_gravity.GetStatic<int>("RIGHT");

				if (isSizeToFit && scale > 1f) {
					this.javaObj.Call("setPivotX", this.javaObj.Call<float>("getPxWidth"));
					this.javaObj.Call("setPivotY", 0f);
					this.javaObj.Call("setScaleX", scale);
					this.javaObj.Call("setScaleY", scale);
				}

				break;

			case BannerAdAlign.BottomLeft:
				gravity = javaCls_gravity.GetStatic<int>("BOTTOM") | javaCls_gravity.GetStatic<int>("LEFT");

				if (isSizeToFit && scale > 1f) {
					this.javaObj.Call("setPivotX", 0f);
					this.javaObj.Call("setPivotY", this.javaObj.Call<float>("getPxHeight"));
					this.javaObj.Call("setScaleX", scale);
					this.javaObj.Call("setScaleY", scale);
				}

				break;

			case BannerAdAlign.BottomCenter:
				gravity = javaCls_gravity.GetStatic<int>("BOTTOM") | javaCls_gravity.GetStatic<int>("CENTER_HORIZONTAL");

				if (isSizeToFit && scale > 1f) {
					this.javaObj.Call("setPivotX", this.javaObj.Call<float>("getPxWidth") / 2);
					this.javaObj.Call("setPivotY", this.javaObj.Call<float>("getPxHeight"));
					this.javaObj.Call("setScaleX", scale);
					this.javaObj.Call("setScaleY", scale);
				}

				break;

			case BannerAdAlign.BottomRight:
				gravity = javaCls_gravity.GetStatic<int>("BOTTOM") | javaCls_gravity.GetStatic<int>("RIGHT");

				if (isSizeToFit && scale > 1f) {
					this.javaObj.Call("setPivotX", this.javaObj.Call<float>("getPxWidth"));
					this.javaObj.Call("setPivotY", this.javaObj.Call<float>("getPxHeight"));
					this.javaObj.Call("setScaleX", scale);
					this.javaObj.Call("setScaleY", scale);
				}

				break;
		}

		int wrapContent = new AndroidJavaClass("android.view.ViewGroup$LayoutParams").GetStatic<int>("WRAP_CONTENT");
		var javaObj_layoutParams = new AndroidJavaObject("android.widget.FrameLayout$LayoutParams", wrapContent, wrapContent, gravity);

		int marginLeft = Mathf.FloorToInt(adMargin.Left * density + 0.5f);
		int marginRight = Mathf.FloorToInt(adMargin.Right * density + 0.5f);
		int marginTop = Mathf.FloorToInt(adMargin.Top * density + 0.5f);
		int marginBottom = Mathf.FloorToInt(adMargin.Bottom * density + 0.5f);

		javaObj_layoutParams.Call("setMargins", marginLeft, marginTop, marginRight, marginBottom);

		return javaObj_layoutParams;
	}

	private AndroidJavaObject getMetrics() {
		AndroidJavaObject display = this.javaObj_activity.Call<AndroidJavaObject>("getWindowManager")
										.Call<AndroidJavaObject>("getDefaultDisplay");
		AndroidJavaObject metrics = new AndroidJavaObject("android.util.DisplayMetrics");

		if (getApiLevel() >= 17) {
			display.Call("getRealMetrics", metrics);
		} else {
			display.Call("getMetrics", metrics);
		}

		return metrics;
	}

	private int getApiLevel() {
		return (new AndroidJavaClass("android.os.Build$VERSION")).GetStatic<int>("SDK_INT");
	}

}

#endif
