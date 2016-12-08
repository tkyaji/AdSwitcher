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

		// Copy Resource Files
		var resDirInfo = new DirectoryInfo(Path.Combine(path, "Libraries/AdSwitcher/Resources"));
		if (!resDirInfo.Exists) {
			resDirInfo.Create();
		}

		List<FileInfo> fileList = searchFiles(new DirectoryInfo("Assets/Plugins/iOS/AdSwitcher"), new string[] { ".png" });

		foreach (var fileInfo in fileList) {
			var toFile = Path.Combine(resDirInfo.FullName, fileInfo.Name);
			fileInfo.CopyTo(toFile);
			var f = Path.Combine("Libraries/AdSwitcher/Resources", fileInfo.Name);
			proj.AddFileToBuild(target, proj.AddFile(f, f, PBXSourceTree.Source));
		}

		if (EditorUserBuildSettings.development) {
			proj.AddBuildProperty(target, "GCC_PREPROCESSOR_DEFINITIONS", "ADSWITCHER_DEBUG=1");
		}

		File.WriteAllText(projPath, proj.WriteToString());
	}

	private static List<FileInfo> searchFiles(DirectoryInfo baseDirInfo, string[] searchExtensions) {
		var list = new List<FileInfo>();

		foreach (var fileInfo in baseDirInfo.GetFiles()) {
			if (searchExtensions.Any(ext => ext == fileInfo.Extension)) {
				list.Add(fileInfo);
			}
		}
		foreach (var dirInfo in baseDirInfo.GetDirectories()) {
			if (dirInfo.Name.EndsWith(".framework", System.StringComparison.Ordinal)) {
				continue;
			}
			var subList = searchFiles(dirInfo, searchExtensions);
			list.AddRange(subList);
		}

		return list;
	}

}

#endif
