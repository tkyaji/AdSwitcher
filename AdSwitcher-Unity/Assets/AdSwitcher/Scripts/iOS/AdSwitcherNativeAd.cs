#if UNITY_IOS && !UNITY_EDITOR

using UnityEngine;
using System;
using System.Runtime.InteropServices;
using AOT;

public class AdSwitcherNativeAd {

	public IntPtr CInstance;
	public IntPtr CSInstance;

	private Action<AdConfig, bool> adReceivedHandler;


	public AdSwitcherNativeAd(AdSwitcherConfigLoader configLoader, string category, bool testMode = false) {
		this.CInstance = _AdSwitcherNativeAd_new(configLoader.CInstance, category, testMode);
		this.CSInstance = (IntPtr)GCHandle.Alloc(this);
	}

	public AdSwitcherNativeAd(AdSwitcherConfig adSwitcherConfig, bool testMode = false) {
		string jsonStr = AdSwitcherJsonConverter.ToJson(adSwitcherConfig);
		this.CInstance = _AdSwitcherNativeAd_new_config(jsonStr, testMode);
		this.CSInstance = (IntPtr)GCHandle.Alloc(this);
	}

	~AdSwitcherNativeAd() {
		_AdSwitcherNativeAd_release(this.CInstance);
		GCHandle csInstanceHandle = (GCHandle)this.CSInstance;
		csInstanceHandle.Free();
	}


	public void Load() {
		_AdSwitcherNativeAd_load(this.CInstance);
	}

	public AdSwitcherNativeAdData GetAdData() {
		IntPtr c_adData = _AdSwitcherNativeAd_getAdData(this.CInstance);
		if (c_adData == IntPtr.Zero) {
			return null;
		}

		var adData = new AdSwitcherNativeAdData();
		adData.shortText = _AdSwitcherNativeAd_getAdDataProperty(c_adData, "shortText");
		adData.longText = _AdSwitcherNativeAd_getAdDataProperty(c_adData, "longText");
		adData.buttonText = _AdSwitcherNativeAd_getAdDataProperty(c_adData, "buttonText");
		adData.imageUrl = _AdSwitcherNativeAd_getAdDataProperty(c_adData, "imageUrl");
		adData.iconImageUrl = _AdSwitcherNativeAd_getAdDataProperty(c_adData, "iconImageUrl");

		return adData;
	}

	public bool IsLoaded() {
		return _AdSwitcherNativeAd_isLoaded(this.CInstance);
	}

	public void OpenUrl() {
		_AdSwitcherNativeAd_openUrl(this.CInstance);
	}

	public void SendImpression() {
		_AdSwitcherNativeAd_sendImpression(this.CInstance);
	}

	public void SetAdReceivedHandler(Action<AdConfig, bool> handler) {
		this.adReceivedHandler = handler;
		_AdSwitcherBannerView_setAdReceivedHandler(this.CInstance, this.CSInstance, adReceivedHandlerCaller);
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


	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherNativeAd_new(IntPtr configLoaderCInstance, string category, bool testMode);

	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherNativeAd_new_config(string adSwitcherConfigJsonStr, bool testMode);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherNativeAd_release(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherNativeAd_load(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherNativeAd_getAdData(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern string _AdSwitcherNativeAd_getAdDataProperty(IntPtr cInstance, string propertyName);

	[DllImport("__Internal")]
	private static extern bool _AdSwitcherNativeAd_isLoaded(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherNativeAd_openUrl(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherNativeAd_sendImpression(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherBannerView_setAdReceivedHandler(IntPtr cInstance, IntPtr csInstance, delagete_adReceivedHandlerCaller handler);


	delegate void delagete_adReceivedHandlerCaller(IntPtr csInstance, string adConfigJson, bool result);


	[MonoPInvokeCallback(typeof(delagete_adReceivedHandlerCaller))]
	private static void adReceivedHandlerCaller(IntPtr csInstance, string adConfigJson, bool result) {
		var csInstanceHandle = (GCHandle)csInstance;
		var nativeAd = csInstanceHandle.Target as AdSwitcherNativeAd;
		var adConfig = AdSwitcherJsonConverter.FromJson(adConfigJson);
		nativeAd.adReceivedHandler(adConfig, result);
	}

}

#endif
