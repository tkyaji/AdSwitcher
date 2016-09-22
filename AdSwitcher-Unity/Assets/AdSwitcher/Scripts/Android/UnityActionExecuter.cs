using UnityEngine;
using System;
using System.Collections.Generic;

public class UnityActionExecuter : MonoBehaviour {

	private static UnityActionExecuter instance;

	private List<Action> actionList = new List<Action>();

	public static void Initialize() {
		if (instance == null) {
			GameObject go = new GameObject("~UnityActionExecuter");
			DontDestroyOnLoad(go);
			instance = go.AddComponent<UnityActionExecuter>();
			instance.hideFlags = HideFlags.HideAndDontSave;
		}
	}

	public static void RunOnUnity(Action action) {
		instance.actionList.Add(action);
	}

	void Update () {
		for (int i = this.actionList.Count - 1; i >= 0; i--) {
			actionList[i].Invoke();
			actionList.RemoveAt(i);
		}
	}
}
