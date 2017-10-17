using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System;

public class Sample : MonoBehaviour {

#if UNITY_IOS
	private const string jsonName = "adswitcher_ios";
#else
	private const string jsonName = "adswitcher_android";
#endif

	private static AdSwitcherBannerView bannerView_320x50;
	private static AdSwitcherBannerView bannerView_320x100;
	private static AdSwitcherBannerView bannerView_300x250;
	private static AdSwitcherInterstitial interstitial;
	private static AdSwitcherInterstitial video;
	private AdSwitcherNativeAd nativeAd;

	[SerializeField]
	private BannerControllers bannerControllers;

	[SerializeField]
	private InterstitialControllers interstitialControllers;

	[SerializeField]
	private VideoControllers videoControllers;

	[SerializeField]
	private NativeAdControllers nativeAdControllers;

	[SerializeField]
	private TabControllers tabControllers;



	void Awake() {
		AdSwitcherConfigLoader.Instance.LoadJson(Resources.Load<TextAsset>(jsonName).text);

		bannerView_320x50 = new AdSwitcherBannerView(
			AdSwitcherConfigLoader.Instance, "banner_320x50", BannerAdSize.Size_320x50, BannerAdAlign.BottomCenter, BannerAdMargin.Zero, true);

		bannerView_320x100 = new AdSwitcherBannerView(
			AdSwitcherConfigLoader.Instance, "banner_320x100", BannerAdSize.Size_320x100, BannerAdAlign.BottomCenter, BannerAdMargin.Zero, true);
		
		bannerView_300x250 = new AdSwitcherBannerView(
			AdSwitcherConfigLoader.Instance, "banner_300x250", BannerAdSize.Size_300x250, BannerAdAlign.BottomCenter, BannerAdMargin.Zero, true);

		interstitial = new AdSwitcherInterstitial(AdSwitcherConfigLoader.Instance, "interstitial", true);

		video = new AdSwitcherInterstitial(AdSwitcherConfigLoader.Instance, "video", true);

		nativeAd = new AdSwitcherNativeAd(AdSwitcherConfigLoader.Instance, "native", true);
	}

	void Start() {
		this.initBannerControllers();
		this.initInterstitialControllers();
		this.initVideoControllers();
		this.initNativeAdControllers();
		this.initTabControllers();
	}


	private void initBannerControllers() {
		this.bannerControllers.LoadButton.onClick.AddListener(() => {
			bool autoShow = this.bannerControllers.AutoShowToggle.isOn;
			this.selectBanner().Load(autoShow);
		});

		this.bannerControllers.ShowButton.onClick.AddListener(() => {
			this.selectBanner().Show();
		});

		this.bannerControllers.HideButton.onClick.AddListener(() => {
			bannerView_320x50.Hide();
			bannerView_320x100.Hide();
			bannerView_300x250.Hide();
			this.addLog(this.bannerControllers.LogText, "hide");
		});

		this.bannerControllers.AdSizeDropdown.onValueChanged.AddListener(val => {
			this.bannerControllers.HideButton.onClick.Invoke();
			this.addLog(this.bannerControllers.LogText, "switch : " + this.bannerControllers.AdSizeDropdown.captionText.text);
		});

		this.bindBannerEvents(bannerView_320x50);
		this.bindBannerEvents(bannerView_320x100);
		this.bindBannerEvents(bannerView_300x250);
	}

	private AdSwitcherBannerView selectBanner() {
		switch (this.bannerControllers.AdSizeDropdown.value) {
			case 0:
				return bannerView_320x50;
			case 1:
				return bannerView_320x100;
			case 2:
				return bannerView_300x250;
		}
		return null;
	}

	private void bindBannerEvents(AdSwitcherBannerView bannerView) {
		bannerView.SetAdReceivedHandler((config, result) => {
			string message = "BannerAd Received : className=" + config.ClassName + ", result=" + result;
			this.addLog(this.bannerControllers.LogText, message);
			Debug.Log(message);
		});
		bannerView.SetAdShownHandler(config => {
			string message = "BannerAd Shown : className=" + config.ClassName;
			Debug.Log(message);
		});
		bannerView.SetAdClickedHandler(config => {
			string message = "BannerAd Clicked : className=" + config.ClassName;
			Debug.Log(message);
		});
	}


	private void initInterstitialControllers() {
		this.interstitialControllers.ShowButton.onClick.AddListener(() => {
			interstitial.Show();
		});
		this.bindInterstitialEvents();
	}

	private void bindInterstitialEvents() {
		interstitial.SetAdLoadedHandler((config, result) => {
			string message = "InterstitialAd Loaded : className=" + config.ClassName + ", result=" + result;
			this.addLog(this.interstitialControllers.LogText, message);
			Debug.Log(message);
		});

		interstitial.SetAdShownHandler(config => {
			string message = "InterstitialAd Shown : className=" + config.ClassName;
			Debug.Log(message);
		});
		interstitial.SetAdClosedHandler((config, result, isSkipped) => {
			string message = "InterstitialAd Closed : className=" + config.ClassName + ", result=" + result + ", isSkipped=" + isSkipped;
			this.addLog(this.interstitialControllers.LogText, message);
			Debug.Log(message);
		});
		interstitial.SetAdClickedHandler(config => {
			string message = "InterstitialAd Click : className=" + config.ClassName;
			Debug.Log(message);
		});
	}


	private void initVideoControllers() {
		this.videoControllers.ShowButton.onClick.AddListener(() => {
			video.Show();
		});
		this.bindVideoEvents();
	}

	private void bindVideoEvents() {
		video.SetAdLoadedHandler((config, result) => {
			string message = "VideoAd Loaded : className=" + config.ClassName + ", result=" + result;
			this.addLog(this.videoControllers.LogText, message);
			Debug.Log(message);
		});

		video.SetAdShownHandler(config => {
			string message = "VideoAd Shown : className=" + config.ClassName;
			Debug.Log(message);
		});
		video.SetAdClosedHandler((config, result, isSkipped) => {
			string message = "VideoAd Closed : className=" + config.ClassName + ", result=" + result + ", isSkipped=" + isSkipped;
			this.addLog(this.videoControllers.LogText, message);
			Debug.Log(message);
		});
		video.SetAdClickedHandler(config => {
			string message = "VideoAd Click : className=" + config.ClassName;
			Debug.Log(message);
		});
	}


	private void initNativeAdControllers() {
		this.nativeAdControllers.LoadButton.onClick.AddListener(() => {
			nativeAd.Load();
		});
		this.bindNativeAdEvents();
	}

	private void bindNativeAdEvents() {
		nativeAd.SetAdReceivedHandler((config, result) => {
			string message = "NativeAd Received : className=" + config.ClassName + ", result=" + result;
			this.addLog(this.nativeAdControllers.LogText, message);
			Debug.Log(message);

			if (result) {
				var adData = this.nativeAd.GetAdData();
				this.nativeAdControllers.NativeAdShortText.text = adData.shortText;
				this.nativeAdControllers.NativeAdLongText.text = adData.longText;
				this.nativeAd.LoadImage((Sprite sprite) => {
					this.nativeAdControllers.NativeAdImage.sprite = sprite;
				});
				this.nativeAd.LoadIconImage((Sprite sprite) => {
					this.nativeAdControllers.NativeAdIconImage.sprite = sprite;
				});
				this.nativeAd.SendImpression();
			}
		});
	}


	private void initTabControllers() {
		this.bindTabEvents();
	}

	private void bindTabEvents() {
		this.tabControllers.BannerButton.onClick.AddListener(() => {
			this.switchPanel(this.tabControllers.BannerPanel);
		});

		this.tabControllers.InterstitialButton.onClick.AddListener(() => {
			this.switchPanel(this.tabControllers.InterstitialPanel);
		});

		this.tabControllers.VideoButton.onClick.AddListener(() => {
			this.switchPanel(this.tabControllers.VideoPanel);
		});

		this.tabControllers.NativeAdButton.onClick.AddListener(() => {
			this.switchPanel(this.tabControllers.NativeAdPanel);
		});
	}

	private void switchPanel(GameObject panel) {
		this.tabControllers.BannerPanel.SetActive(false);
		this.tabControllers.InterstitialPanel.SetActive(false);
		this.tabControllers.VideoPanel.SetActive(false);
		this.tabControllers.NativeAdPanel.SetActive(false);
		panel.SetActive(true);
	}


	private void addLog(Text logText, string line) {
		logText.text = line + "\n" + logText.text;
	}



	[Serializable]
	public class BannerControllers {
		[SerializeField]
		public Button LoadButton;

		[SerializeField]
		public Button ShowButton;

		[SerializeField]
		public Button HideButton;

		[SerializeField]
		public Dropdown AdSizeDropdown;

		[SerializeField]
		public Toggle AutoShowToggle;

		[SerializeField]
		public Text LogText;
	}

	[Serializable]
	public class InterstitialControllers {
		[SerializeField]
		public Button ShowButton;

		[SerializeField]
		public Text LogText;
	}

	[Serializable]
	public class VideoControllers {
		[SerializeField]
		public Button ShowButton;

		[SerializeField]
		public Text LogText;
	}

	[Serializable]
	public class NativeAdControllers {
		[SerializeField]
		public Button LoadButton;

		[SerializeField]
		public Text LogText;

		[SerializeField]
		public Text NativeAdShortText;

		[SerializeField]
		public Text NativeAdLongText;

		[SerializeField]
		public Image NativeAdImage;

		[SerializeField]
		public Image NativeAdIconImage;
	}

	[Serializable]
	public class TabControllers {
		[SerializeField]
		public Button BannerButton;

		[SerializeField]
		public Button InterstitialButton;

		[SerializeField]
		public Button VideoButton;

		[SerializeField]
		public Button NativeAdButton;

		[SerializeField]
		public GameObject BannerPanel;

		[SerializeField]
		public GameObject InterstitialPanel;

		[SerializeField]
		public GameObject VideoPanel;

		[SerializeField]
		public GameObject NativeAdPanel;
	}
}
