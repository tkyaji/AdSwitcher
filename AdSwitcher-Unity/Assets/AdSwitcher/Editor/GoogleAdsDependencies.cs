using Google.JarResolver;
using UnityEditor;

/// <summary>
/// Sample dependencies file.  Copy this to a different name specific to your
/// plugin and add the Google Play Services and Android Support components that
/// your plugin depends on.
/// </summary>
[InitializeOnLoad]
public static class GoogleAdsDependencies {
	/// <summary>
	/// The name of your plugin.  This is used to create a settings file
	/// which contains the dependencies specific to your plugin.
	/// </summary>

	private static readonly string PluginName = "AdSwitcher";
	public static PlayServicesSupport svcSupport;

	/// <summary>
	/// Initializes static members of the <see cref="GoogleAdsDependencies"/> class.
	/// </summary>
	static GoogleAdsDependencies() {

		svcSupport = PlayServicesSupport.CreateInstance(
			PluginName,
			EditorPrefs.GetString("AndroidSdkRoot"),
			"ProjectSettings");
		
		RegisterDependencies();
	}

	public static void RegisterDependencies() {
		svcSupport.DependOn("com.google.android.gms", "play-services-ads", "+");
	}
}
