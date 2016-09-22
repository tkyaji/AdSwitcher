#if UNITY_EDITOR || (!UNITY_IOS && !UNITY_ANDROID)

using System;

public class AdSwitcherBannerView {

	private Action<AdConfig, bool> adReceivedHandler;
	private Action<AdConfig> adShownHandler;
	private Action<AdConfig, bool, bool> adClosedHandler;
	private Action<AdConfig> adClickedHandler;


	public AdSwitcherBannerView(AdSwitcherConfigLoader configLoader, string category,
	                            BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
	                            bool testMode = false) {
	}

	public AdSwitcherBannerView(AdSwitcherConfig adSwitcherConfig,
								BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
	                            bool testMode = false) {
	}


	public void Show() {
	}

	public void Hide() {
	}

	public void SwitchAd() {
	}

	public bool IsLoaded() {
		return true;
	}


	public void SetAdReceivedHandler(Action<AdConfig, bool> handler) {
		this.adReceivedHandler = handler;
	}

	public void SetAdShownHandler(Action<AdConfig> handler) {
		this.adShownHandler = handler;
	}

	public void SetAdClickedHandler(Action<AdConfig> handler) {
		this.adClickedHandler = handler;
	}

}

#endif
