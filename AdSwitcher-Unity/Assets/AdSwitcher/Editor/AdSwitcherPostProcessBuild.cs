#if UNITY_IOS

using UnityEditor;
using UnityEditor.Callbacks;
using System.IO;
using UnityEditor.iOS.Xcode;
using System.Linq;
using System.Collections.Generic;

public class AdSwitcherPostProcessBuild {

	[PostProcessBuild]
	public static void OnPostProcessBuild(BuildTarget buildTarget, string path) {
		string projPath = Path.Combine(path, "Unity-iPhone.xcodeproj/project.pbxproj");
		PBXProject proj = new PBXProject();
		proj.ReadFromString(File.ReadAllText(projPath));
		string target = proj.TargetGuidByName("Unity-iPhone");

		proj.SetBuildProperty(target, "ENABLE_BITCODE", "NO");
		proj.SetBuildProperty(target, "CLANG_ENABLE_MODULES", "YES");
		proj.AddBuildProperty(target, "GCC_ENABLE_OBJC_EXCEPTIONS", "YES");
		proj.AddBuildProperty(target, "OTHER_LDFLAGS", "-ObjC");

/*
		// UnityAds
		if (Directory.Exists("Assets/Adswitcher/Plugins/iOS/UnityAdsAdapter.framework")) {
			if (File.Exists(Path.Combine(path, "Frameworks/UnityAds.bundle"))) {
				File.Delete(Path.Combine(path, "Frameworks/UnityAds.bundle"));
			}
			FileUtil.CopyFileOrDirectory("Assets/AdSwitcher/Plugins/iOS/Libraries/UnityAds.bundle",
										 Path.Combine(path, "Frameworks/UnityAds.bundle"));
			proj.AddFileToBuild(target, proj.AddFile("Frameworks/UnityAds.bundle", "Frameworks/UnityAds.bundle"));
		}

		// AMoAd
		if (Directory.Exists("Assets/AdSwitcher/Plugins/iOS/AMoAdAdapter.framework")) {
			
			var resDirInfo = new DirectoryInfo(Path.Combine(path, "Libraries/AdSwitcher/Resources"));
			if (!resDirInfo.Exists) {
				resDirInfo.Create();
			}

			var amoadDirInfo = new DirectoryInfo(Path.Combine(resDirInfo.FullName, "AMoAd"));
			if (!amoadDirInfo.Exists) {
				amoadDirInfo.Create();
			}

			DirectoryInfo dirInfo = new DirectoryInfo("Assets/AdSwitcher/Plugins/iOS/Libraries/AMoAd");
			foreach (FileInfo fileInfo in dirInfo.GetFiles("*.png")) {
				var toFile = Path.Combine(amoadDirInfo.FullName, fileInfo.Name);
				fileInfo.CopyTo(toFile);
				var f = toFile.Replace(path, "");
				UnityEngine.Debug.Log("### " + f);
				proj.AddFileToBuild(target, proj.AddFile(f, f, PBXSourceTree.Source));
			}
		}
*/
		
		if (EditorUserBuildSettings.development) {
			proj.AddBuildProperty(target, "GCC_PREPROCESSOR_DEFINITIONS", "ADSWITCHER_DEBUG=1");
		}

		File.WriteAllText(projPath, proj.WriteToString());
	}

}

#endif
