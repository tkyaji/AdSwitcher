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
import jp.tjkapp.adfurikunsdk.moviereward.AdfurikunMovieReward;
import jp.tjkapp.adfurikunsdk.moviereward.AdfurikunMovieRewardListener;
import jp.tjkapp.adfurikunsdk.moviereward.MovieInterData;
import jp.tjkapp.adfurikunsdk.moviereward.MovieRewardData;

/**
 * Created by tkyaji on 2016/11/11.
 */

public class AdfurikunVideoAdapter implements InterstitialAdAdapter {

    private enum AdType {
        Reward,
        Interstitial,
    }

    private static final String TAG = "AdfurikunVideoAdapter";

    private InterstitialAdListener interstitialAdListener;

    private Activity activity;
    private String appId;
    private AdType adType;
    private AdfurikunMovieReward adfurikunMovieReward;
    private AdfurikunMovieInter adfurikunMovieInter;
    private boolean result;
    private boolean isSkipped;
    private FrameLayout parentView;
    private LifecycleReceiveView lifecycleReceiveView;
    private boolean isLoading;

    @Override
    public void interstitialAdInitialize(Activity activity, InterstitialAdListener interstitialAdListener, Map<String, String> parameters, boolean testMode) {
        this.activity = activity;
        this.interstitialAdListener = interstitialAdListener;

        this.appId = parameters.get("app_id");
        String adType = parameters.get("ad_type");
        Log.d(TAG, "interstitialAdInitialize : app_id=" + this.appId + ", ad_type=" + adType);

        if ("interstitial".equals(adType)) {
            this.adType = AdType.Interstitial;
        } else {
            this.adType = AdType.Reward;
        }

        if (this.adType == AdType.Interstitial) {
            this.adfurikunMovieInter = new AdfurikunMovieInter(this.appId, this.activity);
            this.adfurikunMovieInter.setAdfurikunMovieInterListener(this.createInterstitialListener());
            this.lifecycleReceiveView = new LifecycleReceiveView(this.activity, this.adfurikunMovieInter);

        } else {
            this.adfurikunMovieReward = new AdfurikunMovieReward(this.appId, this.activity);
            this.adfurikunMovieReward.setAdfurikunMovieRewardListener(this.createRewardListener());
            this.lifecycleReceiveView = new LifecycleReceiveView(this.activity, this.adfurikunMovieReward);
        }

        this.parentView = new FrameLayout(this.activity);
        this.activity.addContentView(this.parentView, new ViewGroup.LayoutParams(0, 0));
        this.parentView.setVisibility(View.GONE);
    }

    @Override
    public void interstitialAdLoad() {
        Log.d(TAG, "interstitialAdLoad");

        if (this.adType == AdType.Interstitial) {
            this.adfurikunMovieInter.onResume();
        } else {
            this.adfurikunMovieReward.onResume();
        }

        if (this.lifecycleReceiveView.getParent() == null) {
            this.parentView.addView(this.lifecycleReceiveView);
        }
        this.parentView.setVisibility(View.VISIBLE);

        if (this.isPrepared()) {
            this.interstitialAdListener.interstitialAdLoaded(this, true);

        } else {
            this.isLoading = true;
            this.adLoad(1);
        }
    }

    @Override
    public void interstitialAdShow() {
        Log.d(TAG, "interstitialAdShow");
        if (this.adType == AdType.Interstitial) {
            this.adfurikunMovieInter.play();
        } else {
            this.adfurikunMovieReward.play();
        }
    }


    private boolean isPrepared() {
        if (this.adType == AdType.Interstitial) {
            return this.adfurikunMovieInter.isPrepared();
        } else {
            return this.adfurikunMovieReward.isPrepared();
        }
    }

    private void adLoad(final int count) {
        Log.d(TAG, "adLoad : count=" + count);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AdfurikunVideoAdapter.this.isPrepared())  {
                    AdfurikunVideoAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdfurikunVideoAdapter.this.isLoading = false;
                            AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdLoaded(AdfurikunVideoAdapter.this, true);
                        }
                    });
                } else if (count == 5) {
                    AdfurikunVideoAdapter.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdfurikunVideoAdapter.this.isLoading = false;
                            AdfurikunVideoAdapter.this.parentView.removeView(AdfurikunVideoAdapter.this.lifecycleReceiveView);
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

    private AdfurikunMovieInterListener createInterstitialListener() {
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
//                AdfurikunVideoAdapter.this.isSkipped = true;
            }

            @Override
            public void onFinishedPlaying(MovieInterData movieInterData) {
                Log.d(TAG, "onFinishedPlaying");
//                AdfurikunVideoAdapter.this.isSkipped = false;
            }

            @Override
            public void onFailedPlaying(MovieInterData movieInterData) {
                Log.d(TAG, "onFailedPlaying");
                if (!AdfurikunVideoAdapter.this.isLoading) {
                    AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdClosed(AdfurikunVideoAdapter.this, false, false);
                }
            }

            @Override
            public void onAdClose(MovieInterData movieInterData) {
                Log.d(TAG, "onAdClose");
                AdfurikunVideoAdapter.this.parentView.removeView(AdfurikunVideoAdapter.this.lifecycleReceiveView);
                AdfurikunVideoAdapter.this.parentView.setVisibility(View.GONE);

                // 動画インタースティシャルは、onFinishedPlayingがonCloseの後に呼ばれる。さらにスキップ終了でコールバックが呼ばれない。
//                AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdClosed(
//                        AdfurikunVideoAdapter.this, AdfurikunVideoAdapter.this.result, AdfurikunVideoAdapter.this.isSkipped);

                AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdClosed(
                        AdfurikunVideoAdapter.this, true, false);
            }
        };
    }

    private AdfurikunMovieRewardListener createRewardListener() {
        return new AdfurikunMovieRewardListener() {
            @Override
            public void onPrepareSuccess() {
                Log.d(TAG, "onPrepareSuccess");
            }

            @Override
            public void onStartPlaying(MovieRewardData movieRewardData) {
                Log.d(TAG, "onStartPlaying");
                AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdShown(AdfurikunVideoAdapter.this);
                AdfurikunVideoAdapter.this.result = true;
                AdfurikunVideoAdapter.this.isSkipped = true;
            }

            @Override
            public void onFinishedPlaying(MovieRewardData movieRewardData) {
                Log.d(TAG, "onFinishedPlaying");
                AdfurikunVideoAdapter.this.isSkipped = false;
            }

            @Override
            public void onFailedPlaying(MovieRewardData movieRewardData) {
                Log.d(TAG, "onFailedPlaying");
                if (!AdfurikunVideoAdapter.this.isLoading) {
                    AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdClosed(AdfurikunVideoAdapter.this, false, false);
                }
            }

            @Override
            public void onAdClose(MovieRewardData movieRewardData) {
                Log.d(TAG, "onAdClose");
                AdfurikunVideoAdapter.this.parentView.removeView(AdfurikunVideoAdapter.this.lifecycleReceiveView);
                AdfurikunVideoAdapter.this.parentView.setVisibility(View.GONE);

                AdfurikunVideoAdapter.this.interstitialAdListener.interstitialAdClosed(
                        AdfurikunVideoAdapter.this, AdfurikunVideoAdapter.this.result, AdfurikunVideoAdapter.this.isSkipped);
            }
        };
    }


    private class LifecycleReceiveView extends View {

        private AdfurikunMovieInter adfurikunMovieInter;
        private AdfurikunMovieReward adfurikunMovieReward;

        public LifecycleReceiveView(Context context, AdfurikunMovieInter adfurikunMovieInter) {
            super(context);
            this.adfurikunMovieInter = adfurikunMovieInter;
        }

        public LifecycleReceiveView(Context context, AdfurikunMovieReward adfurikunMovieReward) {
            super(context);
            this.adfurikunMovieReward = adfurikunMovieReward;
        }

        @Override
        protected void onVisibilityChanged(View changedView, int visibility) {
            super.onVisibilityChanged(changedView, visibility);
            if (visibility == View.VISIBLE) {
                if (this.adfurikunMovieInter != null) {
                    this.adfurikunMovieInter.onStart();
                } else if (this.adfurikunMovieReward != null) {
                    this.adfurikunMovieReward.onStart();
                }
            } else {
                if (this.adfurikunMovieInter != null) {
                    this.adfurikunMovieInter.onStop();
                } else if (this.adfurikunMovieReward != null) {
                    this.adfurikunMovieReward.onStop();
                }
            }
        }

        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (hasWindowFocus) {
                if (this.adfurikunMovieInter != null) {
                    this.adfurikunMovieInter.onResume();
                } else if (this.adfurikunMovieReward != null) {
                    this.adfurikunMovieReward.onResume();
                }
            } else {
                if (this.adfurikunMovieInter != null) {
                    this.adfurikunMovieInter.onPause();
                } else if (this.adfurikunMovieReward != null) {
                    this.adfurikunMovieReward.onPause();
                }
            }
        }

        /*
        @Override
        protected void onDetachedFromWindow() {
            if (this.adfurikunMovieInter != null) {
                this.adfurikunMovieInter.onDestroy();
            } else if (this.adfurikunMovieReward != null) {
                this.adfurikunMovieReward.onDestroy();
            }
            super.onDetachedFromWindow();
        }
        */
    }

}
