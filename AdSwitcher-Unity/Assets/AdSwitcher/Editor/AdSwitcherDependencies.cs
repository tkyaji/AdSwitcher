#if UNITY_ANDROID && UNITY_EDITOR

using UnityEditor;
using Google.JarResolver;

[InitializeOnLoad]
public class AdSwitcherDependencies : AssetPostprocessor {
	public static PlayServicesSupport svcSupport;

	static AdSwitcherDependencies() {
		RegisterDependencies();
	}

	public static void RegisterDependencies() {
		RegisterAndroidDependencies();
	}

	public static void RegisterAndroidDependencies() {
		svcSupport = PlayServicesSupport.CreateInstance("AdSwitcher",
												EditorPrefs.GetString("AndroidSdkRoot"),
														"ProjectSettings");
		svcSupport.DependOn("com.google.android.gms", "play-services-ads", "+");
	}
}

#endif
