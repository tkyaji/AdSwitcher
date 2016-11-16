package net.adswitcher.adapter.adfurikunvideo;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.adswitcher.adapter.InterstitialAdAdapter;
import net.adswitcher.adapter.InterstitialAdListener;

import java.util.Map;

import jp.tjkapp.adfurikunsdk.moviereward.AdfurikunMovieInter;
import jp.tjkapp.adfurikunsdk.moviereward.AdfurikunMovieInterListener;
import jp.tjkapp.adfurikunsdk.moviereward.MovieInterData;

/**
 * Created by tkyaji on 2016/11/11.
 */

public class AdfurikunVideoAdapter implements InterstitialAdAdapter {

    private static final String TAG = "AdfurikunVideoAdapter";

    private InterstitialAdListener interstitialAdListener;

    private Activity activity;
    private String appId;
    private AdfurikunMovieInter adfurikunMovieInter;
    private boolean result;
    private boolean isSkipped;
    private FrameLayout parentView;
    private LifecycleReceiveView lifecycleReceiveView;

    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        this.appId = parameters.get("app_id");
        Log.d(TAG, "interstitialAdInitialize : app_id=" + this.appId);

        this.parentView = new FrameLayout(this.activity);
        this.activity.addContentView(this.parentView, new ViewGroup.LayoutParams(0, 0));
        this.parentView.setVisibility(View.GONE);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        this.adfurikunMovieInter = new AdfurikunMovieInter(this.appId, this.activity);
        this.adfurikunMovieInter.setAdfurikunMovieInterListener(this.createListener());

        this.parentView.setVisibility(View.VISIBLE);
        this.lifecycleReceiveView = new LifecycleReceiveView(this.activity, this.adfurikunMovieInter);
        this.parentView.addView(this.lifecycleReceiveView);
        this.adfurikunMovieInter.onResume();

        if (this.adfurikunMovieInter.isPrepared()) {
            this.interstitialAdListener.interstitialAdLoaded(this, true);

        } else {
            this.adLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        this.adfurikunMovieInter.play();
    }


    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AdfurikunVideoAdapter.this.adfurikunMovieInter.isPrepared())  {
                    AdfurikunVideoAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdLoaded(AdfurikunVideoAdapter.this, true);
                        }
                    });
                } else if (count == 5) {
                    AdfurikunVideoAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdfurikunVideoAdapter.this.parentView.removeView(AdfurikunVideoAdapter.this.lifecycleReceiveView);
                            AdfurikunVideoAdapter.this.lifecycleReceiveView = null;
                            AdfurikunVideoAdapter.this.parentView.setVisibility(View.GONE);

                            AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdLoaded(AdfurikunVideoAdapter.this, false);
                        }
                    });
                } else {
                    AdfurikunVideoAdapter.this.adLoad(count + 1);
                }
            }
        }, 1000);
    }

    private AdfurikunMovieInterListener createListener() {
        return new AdfurikunMovieInterListener() {
            @Override
            public void onPrepareSuccess() {
                Log.d(TAG, "onPrepareSuccess");
            }

            @Override
            public void onStartPlaying(MovieInterData movieInterData) {
                Log.d(TAG, "onStartPlaying");
                AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdShown(AdfurikunVideoAdapter.this);
                AdfurikunVideoAdapter.this.result = true;
                AdfurikunVideoAdapter.this.isSkipped = true;
            }

            @Override
            public void onFinishedPlaying(MovieInterData movieInterData) {
                Log.d(TAG, "onFinishedPlaying");
                AdfurikunVideoAdapter.this.isSkipped = false;

                AdfurikunVideoAdapter.this.parentView.removeView(AdfurikunVideoAdapter.this.lifecycleReceiveView);
                AdfurikunVideoAdapter.this.lifecycleReceiveView = null;
                AdfurikunVideoAdapter.this.parentView.setVisibility(View.GONE);

                AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdClosed(
                        AdfurikunVideoAdapter.this, AdfurikunVideoAdapter.this.result, AdfurikunVideoAdapter.this.isSkipped);
            }

            @Override
            public void onFailedPlaying(MovieInterData movieInterData) {
                Log.d(TAG, "onFailedPlaying");
            }

            @Override
            public void onAdClose(MovieInterData movieInterData) {
                Log.d(TAG, "onAdClose");
            }
        };
    }


    private class LifecycleReceiveView extends View {

        private AdfurikunMovieInter adfurikunMovieInter;

        public LifecycleReceiveView(Context context, AdfurikunMovieInter adfurikunMovieInter) {
            super(context);
            this.adfurikunMovieInter = adfurikunMovieInter;
        }

        @Override
        protected void onVisibilityChanged(View changedView, int visibility) {
            super.onVisibilityChanged(changedView, visibility);
            if (visibility == View.VISIBLE) {
                this.adfurikunMovieInter.onStart();
            } else {
                this.adfurikunMovieInter.onStop();
            }
        }

        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (hasWindowFocus) {
                this.adfurikunMovieInter.onResume();
            } else {
                this.adfurikunMovieInter.onPause();
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            this.adfurikunMovieInter.onDestroy();
            super.onDetachedFromWindow();
        }
    }

}
