package com.shinleeholdings.coverstar.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.databinding.ActivityContestPlayerBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.prnd.YouTubePlayerView;

/**
 *
 * 샘플 소스 참고 : https://github.com/PRNDcompany/YouTubePlayerView
 */
public class ContestPlayerActivity extends BaseActivity {
    ActivityContestPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityContestPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String contestUrl = getIntent().getStringExtra(AppConstants.EXTRA.CONTEST_URL);
        String videoId = Util.getVideoId(contestUrl);

        DebugLogger.i("ContestPlayerActivity videoId : " + videoId + ", contestUrl : " + contestUrl);

        if (TextUtils.isEmpty(videoId)) {
            return;
        }

        binding.playerView.requestFocus();
        binding.playerView.play(videoId, new YouTubePlayerView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(@NonNull YouTubePlayer.Provider provider, @NonNull YouTubePlayer player, boolean wasRestored) {
                DebugLogger.i("ContestPlayerActivity onInitializationSuccess");
                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {
                        DebugLogger.i("ContestPlayerActivity onLoading");
                    }

                    @Override
                    public void onLoaded(String s) {
                        DebugLogger.i("ContestPlayerActivity onLoaded");
                        player.play();
                    }

                    @Override
                    public void onAdStarted() {
                        DebugLogger.i("ContestPlayerActivity onAdStarted");
                    }

                    @Override
                    public void onVideoStarted() {
                        DebugLogger.i("ContestPlayerActivity onVideoStarted");
                    }

                    @Override
                    public void onVideoEnded() {
                        DebugLogger.i("ContestPlayerActivity onVideoEnded");
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        DebugLogger.i("ContestPlayerActivity onError");
                    }
                });
            }

            @Override
            public void onInitializationFailure(@NonNull YouTubePlayer.Provider provider, @NonNull YouTubeInitializationResult result) {
                DebugLogger.i("ContestPlayerActivity onInitializationFailure");
            }
        });
    }
}
