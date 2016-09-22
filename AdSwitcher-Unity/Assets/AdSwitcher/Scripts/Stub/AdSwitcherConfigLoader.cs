#if UNITY_EDITOR || (!UNITY_IOS && !UNITY_ANDROID)

using UnityEngine;

public class AdSwitcherConfigLoader {

	private static AdSwitcherConfigLoader instance;
	public static AdSwitcherConfigLoader Instance {
		get {
			if (instance == null) {
				instance = new AdSwitcherConfigLoader();
			}
			return instance;
		}
	}

	protected AdSwitcherConfigLoader() {
	}

	public void StartLoad(string url) {
		Debug.Log("AdSwitcherConfigLoader.StartLoad : " + url);
	}

	public void LoadJson(string jsonText) {
		Debug.Log("AdSwitcherConfigLoader.LoadJson : " + jsonText);
	}

}


#endif
