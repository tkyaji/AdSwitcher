#if UNITY_IOS && !UNITY_EDITOR

using UnityEngine;
using System;
using System.Runtime.InteropServices;
using AOT;

public class AdSwitcherBannerView {

	public IntPtr CInstance;
	public IntPtr CSInstance;

	private Action<AdConfig, bool> adReceivedHandler;
	private Action<AdConfig> adShownHandler;
	private Action<AdConfig> adClickedHandler;


	public AdSwitcherBannerView(AdSwitcherConfigLoader configLoader, string category,
	                            BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
	                            bool testMode = false/*, bool isSizeToFit = false*/) {
		bool isSizeToFit = false; // TODO
		float[] adMarginArr = new float[] { adMargin.Left, adMargin.Top, adMargin.Right, adMargin.Bottom };
		this.CInstance = _AdSwitcherBannerView_new(configLoader.CInstance, category, (int)adSize, (int)adAlign, adMarginArr, isSizeToFit, testMode);
		this.CSInstance = (IntPtr)GCHandle.Alloc(this);
	}

	public AdSwitcherBannerView(AdSwitcherConfig adSwitcherConfig,
								BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
	                            bool testMode = false/*, bool isSizeToFit = false*/) {
		bool isSizeToFit = false; // TODO
		float[] adMarginArr = new float[] { adMargin.Left, adMargin.Top, adMargin.Right, adMargin.Bottom };
		string jsonStr = AdSwitcherJsonConverter.ToJson(adSwitcherConfig);
		this.CInstance = _AdSwitcherBannerView_new_config(jsonStr, (int)adSize, (int)adAlign, adMarginArr, isSizeToFit, testMode);
		this.CSInstance = (IntPtr)GCHandle.Alloc(this);
	}

	~AdSwitcherBannerView() {
		_AdSwitcherBannerView_release(this.CInstance);
		GCHandle csInstanceHandle = (GCHandle)this.CSInstance;
		csInstanceHandle.Free();
	}


	public void Show() {
		_AdSwitcherBannerView_show(this.CInstance);
	}

	public void Hide() {
		_AdSwitcherBannerView_hide(this.CInstance);
	}

	public void SwitchAd() {
		_AdSwitcherBannerView_switchAd(this.CInstance);
	}

	public bool IsLoaded() {
		return _AdSwitcherBannerView_isLoaded(this.CInstance);
	}

	public Vector2 GetSize() {
		float w = _AdSwitcherBannerView_getWidth(this.CInstance);
		float h = _AdSwitcherBannerView_getHeight(this.CInstance);
		return new Vector2(w, h);
	}

	public Vector2 GetScreenSize() {
		float w = _AdSwitcherBannerView_getScreenWidth(this.CInstance);
		float h = _AdSwitcherBannerView_getScreenHeight(this.CInstance);
		return new Vector2(w, h);
	}

	public void SetAdReceivedHandler(Action<AdConfig, bool> handler) {
		this.adReceivedHandler = handler;
		_AdSwitcherBannerView_setAdReceivedHandler(this.CInstance, this.CSInstance, adReceivedHandlerCaller);
	}

	public void SetAdShownHandler(Action<AdConfig> handler) {
		this.adShownHandler = handler;
		_AdSwitcherBannerView_setAdShownHandler(this.CInstance, this.CSInstance, adShownHandlerCaller);
	}

	public void SetAdClickedHandler(Action<AdConfig> handler) {
		this.adClickedHandler = handler;
		_AdSwitcherBannerView_setAdClickedHandler(this.CInstance, this.CSInstance, adClickedHandlerCaller);
	}


	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherBannerView_new(IntPtr configLoaderCInstance, string category, int adSize, int adAlign, float[] adMarginArr, bool isSizeToFit, bool testMode);

	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherBannerView_new_config(string adSwitcherConfigJsonStr, int adSize, int adAlign, float[] adMarginArr, bool isSizeToFit, bool testMode);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherBannerView_release(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherBannerView_show(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherBannerView_hide(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherBannerView_switchAd(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern bool _AdSwitcherBannerView_isLoaded(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern float _AdSwitcherBannerView_getWidth(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern float _AdSwitcherBannerView_getHeight(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern float _AdSwitcherBannerView_getScreenWidth(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern float _AdSwitcherBannerView_getScreenHeight(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherBannerView_setAdReceivedHandler(IntPtr cInstance, IntPtr csInstance, delagete_adReceivedHandlerCaller handler);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherBannerView_setAdShownHandler(IntPtr cInstance, IntPtr csInstance, delagete_adShownHandlerCaller handler);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherBannerView_setAdClickedHandler(IntPtr cInstance, IntPtr csInstance, delagete_adClickedHandlerCaller handler);


	delegate void delagete_adReceivedHandlerCaller(IntPtr csInstance, string adConfigJson, bool result);
	delegate void delagete_adShownHandlerCaller(IntPtr csInstance, string adConfigJson);
	delegate void delagete_adClickedHandlerCaller(IntPtr csInstance, string adConfigJson);


	[MonoPInvokeCallback(typeof(delagete_adReceivedHandlerCaller))]
	private static void adReceivedHandlerCaller(IntPtr csInstance, string adConfigJson, bool result) {
		var csInstanceHandle = (GCHandle)csInstance;
		var bannerView = csInstanceHandle.Target as AdSwitcherBannerView;
		var adConfig = AdSwitcherJsonConverter.FromJson(adConfigJson);
		bannerView.adReceivedHandler(adConfig, result);
	}

	[MonoPInvokeCallback(typeof(delagete_adShownHandlerCaller))]
	private static void adShownHandlerCaller(IntPtr csInstance, string adConfigJson) {
		GCHandle csInstanceHandle = (GCHandle)csInstance;
		var bannerView = csInstanceHandle.Target as AdSwitcherBannerView;
		var adConfig = AdSwitcherJsonConverter.FromJson(adConfigJson);
		bannerView.adShownHandler(adConfig);
	}

	[MonoPInvokeCallback(typeof(delagete_adClickedHandlerCaller))]
	private static void adClickedHandlerCaller(IntPtr csInstance, string adConfigJson) {
		GCHandle csInstanceHandle = (GCHandle)csInstance;
		var bannerView = csInstanceHandle.Target as AdSwitcherBannerView;
		var adConfig = AdSwitcherJsonConverter.FromJson(adConfigJson);
		bannerView.adClickedHandler(adConfig);
	}
}

#endif
