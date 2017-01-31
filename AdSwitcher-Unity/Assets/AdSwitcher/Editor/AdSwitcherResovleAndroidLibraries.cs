using UnityEditor;
using UnityEngine;
using System.IO;
using System.Text.RegularExpressions;
using System.Collections.Generic;


public class AdSwitcherResovleAndroidLibraries {

	private static string sdkRoot = EditorPrefs.GetString("AndroidSdkRoot");
	private static string androidPluginDir = "Assets/Plugins/Android";


	[MenuItem("AdSwitcher/Jar Resolve")]
	private static void jarResolve() {
		addLibrary("com.google.android.gms", "play-services-basement");
		addLibrary("com.google.android.gms", "play-services-ads");

		// Nend
		if (Directory.Exists("Assets/Plugins/Android/AdSwitcher-NendAdapter")) {
			addLibrary("com.android.support", "recyclerview-v7");
			addLibrary("com.android.support", "cardview-v7");
			addLibrary("com.android.support", "percent");
		}

		// AdColony
		if (Directory.Exists("Assets/Plugins/Android/AdSwitcher-AdColonyAdapter")) {
			addLibrary("com.android.support", "support-annotations");
		}
	}



	private static void addLibrary(string group, string artifact, string version = "LATEST") {
		var searchRegex = artifact.Replace("-", "\\-") + "\\-[0-9\\.]+\\.aar";
		if (isExistLibrary(searchRegex)) {
			return;
		}

		var dir = (group.StartsWith("com.android.", System.StringComparison.Ordinal)) ?
			"extras/android/m2repository" : "extras/google/m2repository";

		var dirPath = Path.Combine(Path.Combine(Path.Combine(sdkRoot, dir), group.Replace('.', '/')), artifact);

		if (version == "+" || version.Equals("LATEST", System.StringComparison.OrdinalIgnoreCase)) {
			version = getLatestVersion(dirPath);
		}
		var targetDir = Path.Combine(dirPath, version);

		var libFileName = artifact + "-" + version + ".aar";
		var filePath = Path.Combine(targetDir, libFileName);
		if (!File.Exists(filePath)) {
			libFileName = artifact + "-" + version + ".jar";
			filePath = Path.Combine(targetDir, libFileName);
		}

		var destPath = Path.Combine(androidPluginDir, libFileName);

		File.Copy(filePath, destPath);
		Debug.Log(libFileName);
	}

	private static string getLatestVersion(string basePath) {

		var versionList = new List<string[]>();

		var baseDirInfo = new DirectoryInfo(basePath);
		foreach (var dirInfo in baseDirInfo.GetDirectories()) {
			var regex = new Regex("^[0-9\\.]+$");
			if (regex.IsMatch(dirInfo.Name)) {
				string[] s = dirInfo.Name.Split('.');
				versionList.Add(s);
			}
		}

		string[] maxTokens = { "0" };
		foreach (var versionTokens in versionList) {
			bool isReplace = false;
			for (int i = 0; i < versionTokens.Length; i++) {
				if (i >= maxTokens.Length) {
					isReplace = true;
					break;
				}
				var token = versionTokens[i];
				var maxToken = maxTokens[i];
				if (int.Parse(token) > int.Parse(maxToken)) {
					isReplace = true;
					break;

				} else if (int.Parse(token) < int.Parse(maxToken)) {
					break;
				}
			}
			if (isReplace) {
				maxTokens = versionTokens;
			}
		}

		return string.Join(".", maxTokens);
	}


	private static bool isExistLibrary(string libNameRegex) {
		var dInfo = new DirectoryInfo(androidPluginDir);
		var regex = new Regex(libNameRegex);
		foreach (var fInfo in dInfo.GetFiles("*.aar")) {
			if (regex.IsMatch(fInfo.Name)) {
				return true;
			}
		}
		return false;
	}

}
