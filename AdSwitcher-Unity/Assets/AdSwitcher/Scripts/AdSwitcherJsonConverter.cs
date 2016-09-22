using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

public class AdSwitcherJsonConverter {

	[Serializable]
	public class AdSwitcherConfigSerializeObj {
		public string category;
		public int switchType;
		public int interval;
		public string[] adConfigList;
	}

	[Serializable]
	public class AdConfigSerializeObj {
		public string adName;
		public string className;
		public int ratio;
		public string[] parameters;
	}

	public static string ToJson(AdSwitcherConfig adSwitcherConfig) {
		var serializeObj = new AdSwitcherConfigSerializeObj();
		serializeObj.category = adSwitcherConfig.Category;
		serializeObj.switchType = (int)adSwitcherConfig.SwitchType;
		serializeObj.interval = adSwitcherConfig.Interval;

		var adConfigStrList = new List<string>();
		foreach (var adConfig in adSwitcherConfig.AdConfigList) {
			adConfigStrList.Add(ToJson(adConfig));
		}
		serializeObj.adConfigList = adConfigStrList.ToArray();

		return JsonUtility.ToJson(serializeObj);
	}

	public static string ToJson(AdConfig adConfig) {
		var serializeObj = new AdConfigSerializeObj {
			adName = adConfig.AdName,
			className = adConfig.ClassName,
			ratio = adConfig.Ratio,
			parameters = fromDictionary(adConfig.Parameters),
		};
		return JsonUtility.ToJson(serializeObj);
	}

	public static AdConfig FromJson(string jsonStr) {
		var serializeObj = JsonUtility.FromJson<AdConfigSerializeObj>(jsonStr);
		var adConfig = new AdConfig {
			AdName = serializeObj.adName,
			ClassName = serializeObj.className,
			Ratio = serializeObj.ratio,
			Parameters = toDictionary(serializeObj.parameters)
		};
		return adConfig;
	}

	private static Dictionary<string, string> toDictionary(string[] parameterStrList) {
		var dict = new Dictionary<string, string>();
		foreach (var paramKeyVal in parameterStrList) {
			var keyVal = paramKeyVal.Split('\t');
			dict.Add(keyVal[0], keyVal[1]);
		}
		return dict;
	}

	private static string[] fromDictionary(Dictionary<string, string> parameterDict) {
		var list = new List<string>();
		foreach (KeyValuePair<string, string> pair in parameterDict) {
			list.Add(string.Format("{0}\t{1}", pair.Key, pair.Value));
		}
		return list.ToArray();
	}
}
