using UnityEngine;
using System.Collections;
using System;

public class ImageLoader : MonoBehaviour {

	public static void Load(string url, Action<Texture2D> onLoaded) {
		var go = new GameObject("~ImageLoader");
		DontDestroyOnLoad(go);
		var imageLoader = go.AddComponent<ImageLoader>();
		imageLoader.onLoaded = onLoaded;
		imageLoader.url = url;
	}


	void Start() {
		StartCoroutine(this.load());
	}

	private string url;
	private Action<Texture2D> onLoaded;
	private int loadCounter;

	private IEnumerator load(int count = 0) {
		var www = new WWW(this.url);
		yield return www;

		if (www.error == null) {
			this.onLoaded.Invoke(www.textureNonReadable);

		} else {
			float waitTime = Mathf.Min(5f, (count + 1) * 0.5f);
			yield return new WaitForSeconds(waitTime);
			this.load(count + 1);
		}
	}

	public static Sprite ConvertToSprite(Texture2D tex2d) {
		return Sprite.Create(tex2d, new Rect(0, 0, tex2d.width, tex2d.height), Vector2.zero);
	}
}
