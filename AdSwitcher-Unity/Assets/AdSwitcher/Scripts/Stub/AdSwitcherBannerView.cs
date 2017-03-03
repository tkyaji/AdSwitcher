#if UNITY_EDITOR || (!UNITY_IOS && !UNITY_ANDROID)

using UnityEngine;
using UnityEngine.UI;
using System;
using UnityEngine.EventSystems;

public class AdSwitcherBannerView {

	private Action<AdConfig, bool> adReceivedHandler;
	private Action<AdConfig> adShownHandler;
	private Action<AdConfig> adClickedHandler;

	private static Texture2D _texture2d;
	private static Texture2D texture2d {
		get {
			if (_texture2d == null) {
				_texture2d = new Texture2D(1, 1, TextureFormat.ARGB32, false);
				_texture2d.SetPixels(0, 0, 1, 1, new Color[] { Color.white });
				_texture2d.Apply(true, true);
			}
			return _texture2d;
		}
	}

	// 画面サイズを横375を基準とする
	private const float baseScreenWidth = 375f;

	private BannerAdSize adSize;
	private BannerAdAlign adAlign;
	private BannerAdMargin adMargin;
	private bool loaded;

	private GameObject canvasGO;

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


	public AdSwitcherBannerView(AdSwitcherConfigLoader configLoader, string category,
							BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
							float scale = 1.0f, bool testMode = false) {
		this.adSize = adSize;
		this.adAlign = adAlign;
		this.adMargin = adMargin;
	}

	public AdSwitcherBannerView(AdSwitcherConfigLoader configLoader, string category,
								BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
								bool testMode = false) {
		this.adSize = adSize;
		this.adAlign = adAlign;
		this.adMargin = adMargin;
	}

	public AdSwitcherBannerView(AdSwitcherConfig adSwitcherConfig,
							BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
							float scale = 1.0f, bool testMode = false) {
		this.adSize = adSize;
		this.adAlign = adAlign;
		this.adMargin = adMargin;
	}

	public AdSwitcherBannerView(AdSwitcherConfig adSwitcherConfig,
								BannerAdSize adSize, BannerAdAlign adAlign, BannerAdMargin adMargin = default(BannerAdMargin),
								bool testMode = false) {
		this.adSize = adSize;
		this.adAlign = adAlign;
		this.adMargin = adMargin;
	}

	public void SetPosition(BannerAdAlign adAlign, BannerAdMargin adMargin) {
		this.adAlign = adAlign;
		this.adMargin = adMargin;
	}

	public void Load(bool autoShow = false) {
		if (this.loaded || this.canvasGO != null) {
			return;
		}

		WithWaitInvoker.Register(() => {
			this.loaded = (Application.internetReachability != NetworkReachability.NotReachable);

			if (this.loaded) {
				if (this.adReceivedHandler != null) {
					this.adReceivedHandler.Invoke(adConfig, true);
				}
				if (autoShow) {
					this.Show();
				}
			}
		}, 0.5f);
	}

	public void Show() {
		if (this.loaded && this.canvasGO == null) {
			this.createCanvas();
			if (this.adShownHandler != null) {
				this.adShownHandler.Invoke(adConfig);
			}
		}
	}

	public void Hide() {
		if (this.canvasGO != null) {
			UnityEngine.Object.Destroy(this.canvasGO);
		}
		this.loaded = false;
	}

	public void SwitchAd() {
		this.Hide();
		this.Show();
	}

	public bool IsLoaded() {
		return this.loaded;
	}

	public Vector2 GetSize() {
		switch (this.adSize) {
			case BannerAdSize.Size_320x50:
				return new Vector2(320f, 50f);

			case BannerAdSize.Size_320x100:
				return new Vector2(320f, 100f);

			case BannerAdSize.Size_300x250:
				return new Vector2(300f, 250f);
		}
		return Vector2.zero;
	}

	public Vector2 GetScreenSize() {
		var height = baseScreenWidth * (1f * Screen.height / Screen.width);
		return new Vector2(baseScreenWidth, height);
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


	private void createCanvas() {

		this.canvasGO = new GameObject("~AdSwitcherBannerView_Canvas");
		UnityEngine.Object.DontDestroyOnLoad(this.canvasGO);
		var canvas = canvasGO.AddComponent<Canvas>();
		canvas.renderMode = RenderMode.ScreenSpaceOverlay;
		canvas.sortingOrder = 100;
		canvasGO.AddComponent<GraphicRaycaster>();

		var canvasSize = canvas.GetComponent<RectTransform>().rect.size;
		var size = this.GetSize();
		var screenSize = this.GetScreenSize();
		var bannerSize = new Vector2(canvasSize.x * (size.x / screenSize.x), canvasSize.y * (size.y / screenSize.y));

		var imageGO = new GameObject("~AdSwitcherBannerView_Image");
		var rawImage = imageGO.AddComponent<RawImage>();
		imageGO.transform.SetParent(canvasGO.transform, false);
		var imageRT = imageGO.GetComponent<RectTransform>();

		imageRT.offsetMin = Vector2.zero;
		imageRT.offsetMax = Vector2.zero;
		imageRT.sizeDelta = bannerSize;

		float posX = ((this.adMargin.Left / screenSize.x) * canvasSize.x) - ((this.adMargin.Right / screenSize.x) * canvasSize.x);
		float posY = ((this.adMargin.Bottom / screenSize.y) * canvasSize.y) - ((this.adMargin.Top / screenSize.y) * canvasSize.y);
		imageRT.anchoredPosition = new Vector2(posX, posY);

		switch (this.adAlign) {
			case BannerAdAlign.TopLeft:
				imageRT.anchorMin = new Vector2(0, 1f);
				imageRT.anchorMax = new Vector2(0, 1f);
				imageRT.pivot = new Vector2(0, 1f);
				break;

			case BannerAdAlign.TopCenter:
				imageRT.anchorMin = new Vector2(0.5f, 1f);
				imageRT.anchorMax = new Vector2(0.5f, 1f);
				imageRT.pivot = new Vector2(0.5f, 1f);
				break;

			case BannerAdAlign.TopRight:
				imageRT.anchorMin = new Vector2(1f, 1f);
				imageRT.anchorMax = new Vector2(1f, 1f);
				imageRT.pivot = new Vector2(1f, 1f);
				break;

			case BannerAdAlign.BottomLeft:
				imageRT.anchorMin = new Vector2(0, 0);
				imageRT.anchorMax = new Vector2(0, 0);
				imageRT.pivot = new Vector2(0, 0);
				break;

			case BannerAdAlign.BottomCenter:
				imageRT.anchorMin = new Vector2(0.5f, 0);
				imageRT.anchorMax = new Vector2(0.5f, 0);
				imageRT.pivot = new Vector2(0.5f, 0);
				break;

			case BannerAdAlign.BottomRight:
				imageRT.anchorMin = new Vector2(1f, 0);
				imageRT.anchorMax = new Vector2(1f, 0);
				imageRT.pivot = new Vector2(1f, 0);
				break;
		}

		imageGO.AddComponent<PointerClick>().OnPointerClickListener = (pointerEventData) => {
			if (this.adClickedHandler != null) {
				this.adClickedHandler.Invoke(adConfig);
			}
		};

		rawImage.texture = texture2d;
		rawImage.color = Color.gray;

		var textGO = new GameObject("~AdSwitcherBannerView_Text");
		var text = textGO.AddComponent<Text>();
		textGO.transform.SetParent(imageGO.transform);
		var textRT = textGO.GetComponent<RectTransform>();
		textRT.anchoredPosition = Vector2.zero;
		textRT.sizeDelta = bannerSize;
		text.font = Resources.GetBuiltinResource<Font>("Arial.ttf");
		text.alignment = TextAnchor.MiddleCenter;
		text.text = "BannerAd";
		text.resizeTextForBestFit = true;
	}


	private class PointerClick : MonoBehaviour, IPointerClickHandler {
		public Action<PointerEventData> OnPointerClickListener;
		public void OnPointerClick(PointerEventData eventData) {
			if (OnPointerClickListener != null) {
				OnPointerClickListener.Invoke(eventData);
			}
		}
	}

}

#endif
