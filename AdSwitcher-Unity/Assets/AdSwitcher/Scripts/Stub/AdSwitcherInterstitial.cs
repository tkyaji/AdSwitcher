#if UNITY_EDITOR || (!UNITY_IOS && !UNITY_ANDROID)

using System;

public class AdSwitcherInterstitial {

	private Action<AdConfig, bool> adLoadedHandler;
	private Action<AdConfig> adShownHandler;
	private Action<AdConfig, bool, bool> adClosedHandler;
	private Action<AdConfig> adClickedHandler;


	public AdSwitcherInterstitial(AdSwitcherConfigLoader configLoader, string category, bool testMode = false) {
	}

	public AdSwitcherInterstitial(AdSwitcherConfig adSwitcherConfig, bool testMode = false) {
	}


	public void Show() {
	}

	public bool IsLoaded() {
		return true;
	}


	public void SetAdLoadedHandler(Action<AdConfig, bool> handler) {
		this.adLoadedHandler = handler;
	}

	public void SetAdShownHandler(Action<AdConfig> handler) {
		this.adShownHandler = handler;
	}

	public void SetAdClosedHandler(Action<AdConfig, bool, bool> handler) {
		this.adClosedHandler = handler;
	}

	public void SetAdClickedHandler(Action<AdConfig> handler) {
		this.adClickedHandler = handler;
	}

}

#endif
