#if UNITY_IOS && !UNITY_EDITOR

using System;
using System.Runtime.InteropServices;
using AOT;

public class AdSwitcherInterstitial {

	public IntPtr CInstance;
	public IntPtr CSInstance;

	private Action<AdConfig, bool> adLoadedHandler;
	private Action<AdConfig> adShownHandler;
	private Action<AdConfig, bool, bool> adClosedHandler;
	private Action<AdConfig> adClickedHandler;


	public AdSwitcherInterstitial(AdSwitcherConfigLoader configLoader, string category, bool testMode = false) {
		this.CInstance = _AdSwitcherInterstitial_new(configLoader.CInstance, category, testMode);
		this.CSInstance = (IntPtr)GCHandle.Alloc(this);
	}

	public AdSwitcherInterstitial(AdSwitcherConfig adSwitcherConfig, bool testMode = false) {
		string jsonStr = AdSwitcherJsonConverter.ToJson(adSwitcherConfig);
		this.CInstance = _AdSwitcherInterstitial_new_config(jsonStr, testMode);
		this.CSInstance = (IntPtr)GCHandle.Alloc(this);
	}

	~AdSwitcherInterstitial() {
		_AdSwitcherInterstitial_release(this.CInstance);
		GCHandle csInstanceHandle = (GCHandle)this.CSInstance;
		csInstanceHandle.Free();
	}


	public void Show() {
		_AdSwitcherInterstitial_show(this.CInstance);
	}

	public bool IsLoaded() {
		return _AdSwitcherInterstitial_isLoaded(this.CInstance);
	}


	public void SetAdLoadedHandler(Action<AdConfig, bool> handler) {
		this.adLoadedHandler = handler;
		_AdSwitcherInterstitial_setAdLoadedHandler(this.CInstance, this.CSInstance, adLoadedHandlerCaller);
	}

	public void SetAdShownHandler(Action<AdConfig> handler) {
		this.adShownHandler = handler;
		_AdSwitcherInterstitial_setAdShownHandler(this.CInstance, this.CSInstance, adShownHandlerCaller);
	}

	public void SetAdClosedHandler(Action<AdConfig, bool, bool> handler) {
		this.adClosedHandler = handler;
		_AdSwitcherInterstitial_setAdClosedHandler(this.CInstance, this.CSInstance, adClosedHandlerCaller);
	}

	public void SetAdClickedHandler(Action<AdConfig> handler) {
		this.adClickedHandler = handler;
		_AdSwitcherInterstitial_setAdClickedHandler(this.CInstance, this.CSInstance, adClickedHandlerCaller);
	}


	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherInterstitial_new(IntPtr configLoaderCInstance, string category, bool testMode);

	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherInterstitial_new_config(string adSwitcherConfigJsonStr, bool testMode);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherInterstitial_release(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherInterstitial_show(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern bool _AdSwitcherInterstitial_isLoaded(IntPtr cInstance);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherInterstitial_setAdLoadedHandler(IntPtr cInstance, IntPtr csInstance, delagete_adLoadedHandlerCaller handler);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherInterstitial_setAdShownHandler(IntPtr cInstance, IntPtr csInstance, delagete_adShownHandlerCaller handler);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherInterstitial_setAdClosedHandler(IntPtr cInstance, IntPtr csInstance, delagete_adClosedHandlerCaller handler);

	[DllImport("__Internal")]
	private static extern void _AdSwitcherInterstitial_setAdClickedHandler(IntPtr cInstance, IntPtr csInstance, delagete_adClickedHandlerCaller handler);


	delegate void delagete_adLoadedHandlerCaller(IntPtr csInstance, string adConfigJson, bool result);
	delegate void delagete_adShownHandlerCaller(IntPtr csInstance, string adConfigJson);
	delegate void delagete_adClosedHandlerCaller(IntPtr csInstance, string adConfigJson, bool result, bool isSkipped);
	delegate void delagete_adClickedHandlerCaller(IntPtr csInstance, string adConfigJson);


	[MonoPInvokeCallback(typeof(delagete_adLoadedHandlerCaller))]
	private static void adLoadedHandlerCaller(IntPtr csInstance, string adConfigJson, bool result) {
		var csInstanceHandle = (GCHandle)csInstance;
		var interstitial = csInstanceHandle.Target as AdSwitcherInterstitial;
		var adConfig = AdSwitcherJsonConverter.FromJson(adConfigJson);
		interstitial.adLoadedHandler(adConfig, result);
	}

	[MonoPInvokeCallback(typeof(delagete_adShownHandlerCaller))]
	private static void adShownHandlerCaller(IntPtr csInstance, string adConfigJson) {
		GCHandle csInstanceHandle = (GCHandle)csInstance;
		var interstitial = csInstanceHandle.Target as AdSwitcherInterstitial;
		var adConfig = AdSwitcherJsonConverter.FromJson(adConfigJson);
		interstitial.adShownHandler(adConfig);
	}

	[MonoPInvokeCallback(typeof(delagete_adClosedHandlerCaller))]
	private static void adClosedHandlerCaller(IntPtr csInstance, string adConfigJson, bool result, bool isSkipped) {
		GCHandle csInstanceHandle = (GCHandle)csInstance;
		var interstitial = csInstanceHandle.Target as AdSwitcherInterstitial;
		var adConfig = AdSwitcherJsonConverter.FromJson(adConfigJson);
		interstitial.adClosedHandler(adConfig, result, isSkipped);
	}

	[MonoPInvokeCallback(typeof(delagete_adClickedHandlerCaller))]
	private static void adClickedHandlerCaller(IntPtr csInstance, string adConfigJson) {
		GCHandle csInstanceHandle = (GCHandle)csInstance;
		var interstitial = csInstanceHandle.Target as AdSwitcherInterstitial;
		var adConfig = AdSwitcherJsonConverter.FromJson(adConfigJson);
		interstitial.adClickedHandler(adConfig);
	}

}

#endif
