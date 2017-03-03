using UnityEditor;
using UnityEngine;
using System.IO;
using System.Text.RegularExpressions;
using System.Collections.Generic;


public class AdSwitcherResovleAndroidLibraries {

	private static string sdkRoot = EditorPrefs.GetString("AndroidSdkRoot");
	private static string androidPluginDir = "Assets/Plugins/Android";


	[MenuItem("AdSwitcher/Resolve Jar")]
	private static void jarResolve() {
		addLibrary("com.google.android.gms", "play-services-basement");
		addLibrary("com.google.android.gms", "play-services-ads");
		addLibrary("com.google.android.gms", "play-services-ads-lite");

		var supportV4Version = "23.+";
		addLibrary("com.android.support", "support-v4", supportV4Version);

		// Nend
		if (Directory.Exists("Assets/Plugins/Android/AdSwitcher-NendAdapter")) {
			addLibrary("com.android.support", "recyclerview-v7", supportV4Version);
			addLibrary("com.android.support", "cardview-v7", supportV4Version);
			addLibrary("com.android.support", "percent", supportV4Version);
		}

		// AdColony
		if (Directory.Exists("Assets/Plugins/Android/AdSwitcher-AdColonyAdapter")) {
			addLibrary("com.android.support", "support-annotations", supportV4Version);
		}

		// MoPub
		if (Directory.Exists("Assets/Plugins/Android/AdSwitcher-MopubAdapter")) {
			addLibrary("com.android.support", "recyclerview-v7", supportV4Version);
			addLibrary("com.android.support", "support-annotations", supportV4Version);
		}

		AssetDatabase.Refresh();
	}



	private static void addLibrary(string group, string artifact, string version = "LATEST") {
		var searchRegex = artifact.Replace("-", "\\-") + "\\-[0-9\\.]+";
		if (isExistLibrary(searchRegex)) {
			return;
		}

		var dir = (group.StartsWith("com.android.", System.StringComparison.Ordinal)) ?
			"extras/android/m2repository" : "extras/google/m2repository";

		var dirPath = Path.Combine(Path.Combine(Path.Combine(sdkRoot, dir), group.Replace('.', '/')), artifact);

		if (version == "+" || version.Equals("LATEST", System.StringComparison.OrdinalIgnoreCase)) {
			version = getLatestVersion(dirPath);
		} else if (version.EndsWith(".+", System.StringComparison.Ordinal)) {
			version = getLatestVersion(dirPath, version.Substring(0, version.Length - 1));
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

	private static string getLatestVersion(string basePath, string versionPrefix = null) {

		var versionList = new List<string[]>();

		var baseDirInfo = new DirectoryInfo(basePath);
		foreach (var dirInfo in baseDirInfo.GetDirectories()) {
			if (versionPrefix != null) {
				if (!dirInfo.Name.StartsWith(versionPrefix, System.StringComparison.Ordinal)) {
					continue;
				}
			}
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
		var regex = new Regex(libNameRegex + "\\.aar");
		foreach (var fInfo in dInfo.GetFiles("*.aar")) {
			if (regex.IsMatch(fInfo.Name)) {
				return true;
			}
		}
		regex = new Regex(libNameRegex + "\\.jar");
		foreach (var fInfo in dInfo.GetFiles("*.jar")) {
			if (regex.IsMatch(fInfo.Name)) {
				return true;
			}
		}
		return false;
	}

}
