#if UNITY_IOS && !UNITY_EDITOR

using System;
using System.Runtime.InteropServices;


public class AdSwitcherConfigLoader {

	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherConfigLoader_sharedInstance();

	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherConfigLoader_startLoad(IntPtr configLoader, string url);

	[DllImport("__Internal")]
	private static extern IntPtr _AdSwitcherConfigLoader_loadJson(IntPtr configLoader, string jsonText);



	private static AdSwitcherConfigLoader instance;
	public static AdSwitcherConfigLoader Instance {
		get {
			if (instance == null) {
				instance = new AdSwitcherConfigLoader();
			}
			return instance;
		}
	}

	public IntPtr CInstance;

	protected AdSwitcherConfigLoader() {
		this.CInstance = _AdSwitcherConfigLoader_sharedInstance();
	}

	public void StartLoad(string url) {
		_AdSwitcherConfigLoader_startLoad(CInstance, url);
	}

	public void LoadJson(string jsonText) {
		_AdSwitcherConfigLoader_loadJson(CInstance, jsonText);
	}

}

#endif
