#if UNITY_EDITOR || (!UNITY_IOS && !UNITY_ANDROID)

using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using System;

public class AdSwitcherInterstitial {

	private Action<AdConfig, bool> adLoadedHandler;
	private Action<AdConfig> adShownHandler;
	private Action<AdConfig, bool, bool> adClosedHandler;
	private Action<AdConfig> adClickedHandler;

	private bool loaded;

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

	public AdSwitcherInterstitial(AdSwitcherConfigLoader configLoader, string category, bool testMode = false) {
		this.load();
	}

	public AdSwitcherInterstitial(AdSwitcherConfig adSwitcherConfig, bool testMode = false) {
		this.load();
	}

	public void Show() {
		if (!this.loaded) {
			this.close(false);
			return;
		}

		createCanvas();

		if (this.adShownHandler != null) {
			this.adShownHandler.Invoke(adConfig);
		}
	}

	public bool IsLoaded() {
		return this.loaded;
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


	private void load() {
		WithWaitInvoker.Register(() => {
            this.loaded = (Application.internetReachability != NetworkReachability.NotReachable);
			if (this.adLoadedHandler != null) {
				if (this.loaded) {
					this.adLoadedHandler.Invoke(adConfig, this.loaded);
				} else {
					Debug.Log("load failed -> reload");
					this.load();
				}
			}
		}, 0.5f);
	}

	private void close(bool result, bool isSkipped = false) {
		if (this.adClosedHandler != null) {
			this.adClosedHandler.Invoke(adConfig, result, isSkipped);
		}
		this.loaded = false;
		this.load();
	}

	private void createCanvas() {
		var canvasGO = new GameObject("~AdSwitcherInterstitial_Canvas");
		var canvas = canvasGO.AddComponent<Canvas>();
		canvas.renderMode = RenderMode.ScreenSpaceOverlay;
		canvas.sortingOrder = 101;
		canvasGO.AddComponent<GraphicRaycaster>();
		var canvasScaler = canvasGO.AddComponent<CanvasScaler>();
		canvasScaler.uiScaleMode = CanvasScaler.ScaleMode.ScaleWithScreenSize;
		canvasScaler.referenceResolution = new Vector2(320f, 568f);

		var imageGO = new GameObject("~AdSwitcherInterstitial_Image");
		var rawImage = imageGO.AddComponent<RawImage>();
		imageGO.transform.SetParent(canvasGO.transform, false);
		var imageRT = imageGO.GetComponent<RectTransform>();
		imageRT.anchorMin = Vector2.zero;
		imageRT.anchorMax = Vector2.one;
		imageRT.offsetMin = Vector2.zero;
		imageRT.offsetMax = Vector2.zero;

		rawImage.texture = texture2d;
		rawImage.color = Color.gray;

		var textGO = new GameObject("~AdSwitcherInterstitial_Text");
		var text = textGO.AddComponent<Text>();
		textGO.transform.SetParent(canvasGO.transform);
		var textRT = textGO.GetComponent<RectTransform>();
		textRT.anchoredPosition = new Vector2(0, 150f);
		textRT.sizeDelta = new Vector2(300f, 100f);
		text.font = Resources.GetBuiltinResource<Font>("Arial.ttf");
		text.alignment = TextAnchor.MiddleCenter;
		text.text = "InterstitialAd";
		text.fontSize = 40;

		if (EventSystem.current == null) {
			var esGO = new GameObject("~AdSwitcher_EventSystem");
			esGO.AddComponent<EventSystem>();
			esGO.AddComponent<StandaloneInputModule>();
			esGO.transform.SetParent(canvasGO.transform);
		}

		this.createButton(canvasGO, "Close\n(success)", new Vector2(0, 300f)).onClick.AddListener(() => {
			this.close(true);
		});

		this.createButton(canvasGO, "Close\n(success, skip)", new Vector2(0, 240f)).onClick.AddListener(() => {
			this.close(true, true);
		});

		this.createButton(canvasGO, "Close\n(fail)", new Vector2(0, 180f)).onClick.AddListener(() => {
			this.close(false);
		});

		this.createButton(canvasGO, "Click", new Vector2(0, 100f)).onClick.AddListener(() => {
			if (this.adClickedHandler != null) {
				this.adClickedHandler.Invoke(adConfig);
			}
			this.close(true);
		});
	}

	private Button createButton(GameObject canvasGO, string textValue, Vector2 position) {
		var buttonGO = new GameObject("~AdSwitcherInterstitial_Button");
		var button = buttonGO.AddComponent<Button>();
		var buttonRawImage = buttonGO.AddComponent<RawImage>();
		buttonGO.transform.SetParent(canvasGO.transform);
		var buttonRT = buttonGO.GetComponent<RectTransform>();

		buttonRT.sizeDelta = new Vector2(150f, 50f);
		buttonRT.anchorMin = new Vector2(0.5f, 0);
		buttonRT.anchorMax = new Vector2(0.5f, 0);
		buttonRT.pivot = new Vector2(0.5f, 0);
		buttonRT.anchoredPosition = position;

		buttonRawImage.texture = texture2d;
		buttonRawImage.color = Color.white;

		var buttonTextGO = new GameObject("~Text");
		var buttonText = buttonTextGO.AddComponent<Text>();
		buttonTextGO.transform.SetParent(buttonGO.transform);
		var buttonTextRT = buttonTextGO.GetComponent<RectTransform>();
		buttonTextRT.anchoredPosition = Vector2.zero;
		buttonTextRT.sizeDelta = buttonRT.sizeDelta;
		buttonText.font = Resources.GetBuiltinResource<Font>("Arial.ttf");
		buttonText.alignment = TextAnchor.MiddleCenter;
		buttonText.color = Color.black;
		buttonText.text = textValue;

		button.onClick.AddListener(() => {
			UnityEngine.Object.Destroy(canvasGO);
		});

		return button;
	}

}

#endif
