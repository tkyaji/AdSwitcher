using UnityEngine;
using UnityEngine.UI;

public class Sample : MonoBehaviour {

	private AdSwitcherInterstitial interstitial;
	private AdSwitcherBannerView bannerView;

	private TextAsset jsonText {
		get {
#if UNITY_IOS
			return Resources.Load<TextAsset>("adswitcher_ios");
#else
			return Resources.Load<TextAsset>("adswitcher_android");
#endif
		}
	}

	[SerializeField]
	private Button bannerShowButton;

	[SerializeField]
	private Button bannerHideButton;

	[SerializeField]
	private Button interstitialShowButton;

	[SerializeField]
	private Text bannerText;

	[SerializeField]
	private Text interstitialText;


	void Start () {

		this.bindEvents();

		AdSwitcherConfigLoader.Instance.LoadJson(jsonText.text);

		this.initInterstitial();
		this.initBannerView();
	}

	private void bindEvents() {
		this.bannerShowButton.onClick.AddListener(() => {
			this.bannerView.Show();
		});

		this.bannerHideButton.onClick.AddListener(() => {
			this.bannerView.Hide();
			this.bannerText.text = "";
		});

		this.interstitialShowButton.onClick.AddListener(() => {
			this.interstitial.Show();
		});
	}

	private void initInterstitial() {
		this.interstitial = new AdSwitcherInterstitial(AdSwitcherConfigLoader.Instance, "interstitial", true);

		this.interstitial.SetAdLoadedHandler((config, result) => {
			string message = "InterstitialAd Loaded : className=" + config.ClassName + ", result=" + result;
			this.interstitialText.text = message;
			Debug.Log(message);
		});

		this.interstitial.SetAdShownHandler(config => {
			string message = "InterstitialAd Shown : className=" + config.ClassName;
			Debug.Log(message);
		});
		this.interstitial.SetAdClosedHandler((config, result, isSkipped) => {
			string message = "InterstitialAd Closed : className=" + config.ClassName + ", result=" + result + ", isSkipped=" + isSkipped;
			this.interstitialText.text = message;
			Debug.Log(message);
		});
		this.interstitial.SetAdClickedHandler(config => {
			string message = "InterstitialAd Click : className=" + config.ClassName;
			Debug.Log(message);
		});
	}

	private void initBannerView() {

		this.bannerView = new AdSwitcherBannerView(AdSwitcherConfigLoader.Instance, "banner_320x50", BannerAdSize.Size_320x50, BannerAdAlign.BottomCenter, BannerAdMargin.Zero, true);

		this.bannerView.SetAdReceivedHandler((config, result) => {
			string message = "BannerAd Received : className=" + config.ClassName + ", result=" + result;
			this.bannerText.text = message;
			Debug.Log(message);
		});
		this.bannerView.SetAdShownHandler(config => {
			string message = "BannerAd Shown : className=" + config.ClassName;
			this.bannerText.text = message;
			Debug.Log(message);

		});
		this.bannerView.SetAdClickedHandler(config => {
			string message = "BannerAd Clicked : className=" + config.ClassName;
			Debug.Log(message);
		});

		this.bannerView.Show();
	}

}
