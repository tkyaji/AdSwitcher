using UnityEngine;
using System.Collections;
using System;

public class WithWaitInvoker : MonoBehaviour {

	private Action action;
	private float waitTime;

	public static void Register(Action action, float waitTime) {
		var go = new GameObject("~WithWaitInvoker");
		DontDestroyOnLoad(go);
		var component = go.AddComponent<WithWaitInvoker>();
		component.action = action;
		component.waitTime = waitTime;
	}

	void Start() {
		StartCoroutine(runOnNextFrame());
	}

	private System.Collections.IEnumerator runOnNextFrame() {
		yield return new WaitForSecondsRealtime(this.waitTime);
		this.action.Invoke();
		Destroy(this.gameObject);
	}
}