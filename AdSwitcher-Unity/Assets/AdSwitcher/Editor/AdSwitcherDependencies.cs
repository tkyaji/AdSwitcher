using System;
using System.Collections.Generic;
using UnityEditor;

[InitializeOnLoad]
public class SampleDependencies : AssetPostprocessor {
	public static object svcSupport;

	static SampleDependencies() {
		RegisterDependencies();
	}

	public static void RegisterDependencies() {
		RegisterAndroidDependencies();
	}

	public static void RegisterAndroidDependencies() {
		Type playServicesSupport =
			Google.VersionHandler.FindClass("Google.JarResolver", "Google.JarResolver.PlayServicesSupport");
		
		if (playServicesSupport == null) {
			return;
		}
		svcSupport = svcSupport ?? Google.VersionHandler.InvokeStaticMethod(
			playServicesSupport, "CreateInstance",
			new object[] {
				"GooglePlayGames",
				EditorPrefs.GetString("AndroidSdkRoot"),
				"ProjectSettings"
			});

		Google.VersionHandler.InvokeInstanceMethod(
			svcSupport, "DependOn",
			new object[] { "com.google.android.gms", "play-services-ads", "+" },
			namedArgs: new Dictionary<string, object>() {
				{ "packageIds", new string[] { "extra-google-m2repository" } }
			});
	}
}