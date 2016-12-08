#if UNITY_ANDROID && !UNITY_EDITOR

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

	private AndroidJavaObject javaObj;

	public AndroidJavaObject getJavaObject() {
		return this.javaObj;
	}

	protected AdSwitcherConfigLoader() {
		AndroidJavaClass javaClass_adSwitcherConfigLoader = new AndroidJavaClass("net.adswitcher.config.AdSwitcherConfigLoader");
		this.javaObj = javaClass_adSwitcherConfigLoader.CallStatic<AndroidJavaObject>("getInstance");
	}

	public void StartLoad(string url) {
		AndroidJavaObject javaObj_activity = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
		try {
			AndroidJavaObject java_url = new AndroidJavaObject("java.net.URL", url);
			this.javaObj.Call("startLoad", javaObj_activity, java_url);
		} catch (AndroidJavaException) {
		}
	}

	public void LoadJson(string jsonText) {
		this.javaObj.Call("loadJson", jsonText);
	}

}


#endif
