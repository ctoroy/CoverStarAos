package com.shinleeholdings.coverstar.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.databinding.ActivityContestPlayerBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;

/**
 *
 * 샘플 소스 참고 : https://github.com/google/ExoPlayer
 */
public class ContestPlayerActivity extends BaseActivity {

    private ActivityContestPlayerBinding binding;
    protected @Nullable ExoPlayer player;

    private static final String KEY_TRACK_SELECTION_PARAMETERS = "track_selection_parameters";
    private static final String KEY_ITEM_INDEX = "item_index";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private MediaItem mMediaItem;
    private DefaultTrackSelector.Parameters trackSelectionParameters;
    private TracksInfo lastSeenTracksInfo;
    private boolean startAutoPlay;
    private int startItemIndex;
    private long startPosition;

    private String contestUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO test 필요
        super.onCreate(savedInstanceState);
        binding = ActivityContestPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        binding.playerView.requestFocus();

        contestUrl = getIntent().getStringExtra(AppConstants.EXTRA.CONTEST_URL);

        if (savedInstanceState != null) {
            // Restore as DefaultTrackSelector.Parameters in case ExoPlayer specific parameters were set.
            trackSelectionParameters = DefaultTrackSelector.Parameters.CREATOR.fromBundle(savedInstanceState.getBundle(KEY_TRACK_SELECTION_PARAMETERS));
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startItemIndex = savedInstanceState.getInt(KEY_ITEM_INDEX);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectionParameters = new DefaultTrackSelector.ParametersBuilder(/* context= */ this).build();
            clearStartPosition();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        clearStartPosition();
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
            binding.playerView.onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            binding.playerView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            binding.playerView.onPause();
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            binding.playerView.onPause();
            releasePlayer();
        }
    }

    private static class PlayerErrorMessageProvider implements ErrorMessageProvider<PlaybackException> {

        @NonNull
        @Override
        public Pair<Integer, String> getErrorMessage(PlaybackException e) {
            String errorString = "Playback failed";
            Throwable cause = e.getCause();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.codecInfo == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = "Unable to query device decoders";
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = "This device does not provide a secure decoder for " + decoderInitializationException.mimeType;
                    } else {
                        errorString = "This device does not provide a decoder for  " + decoderInitializationException.mimeType;
                    }
                } else {
                    errorString = "Unable to instantiate decoder " + decoderInitializationException.codecInfo.name;
                }
            }
            return Pair.create(0, errorString);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putBundle(KEY_TRACK_SELECTION_PARAMETERS, trackSelectionParameters.toBundle());
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_ITEM_INDEX, startItemIndex);
        outState.putLong(KEY_POSITION, startPosition);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return binding.playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    protected void initializePlayer() {
        if (player == null) {
            Intent intent = getIntent();

            mMediaItem = MediaItem.fromUri(Uri.parse(contestUrl));

            // DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
            // DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
            RenderersFactory renderersFactory = new DefaultRenderersFactory(this).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

            lastSeenTracksInfo = TracksInfo.EMPTY;
            player = new ExoPlayer.Builder(/* context= */ this).setRenderersFactory(renderersFactory).build();
            player.setTrackSelectionParameters(trackSelectionParameters);
            player.addListener(new PlayerEventListener());
            player.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
            player.setPlayWhenReady(startAutoPlay);
            binding.playerView.setPlayer(player);
        }
        boolean haveStartPosition = startItemIndex != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startItemIndex, startPosition);
        }
        player.setMediaItem(mMediaItem, /* resetPosition= */ !haveStartPosition);
        player.prepare();
    }

    protected void releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            player.release();
            player = null;
            binding.playerView.setPlayer(/* player= */ null);
            mMediaItem = null;
        }
    }

    private void updateTrackSelectorParameters() {
        if (player != null) {
            // Until the demo app is fully migrated to TrackSelectionParameters, rely on ExoPlayer to use DefaultTrackSelector by default.
            trackSelectionParameters = (DefaultTrackSelector.Parameters) player.getTrackSelectionParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startItemIndex = player.getCurrentMediaItemIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    protected void clearStartPosition() {
        startAutoPlay = true;
        startItemIndex = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private class PlayerEventListener implements Player.Listener {

        @Override
        public void onPlaybackStateChanged(@Player.State int playbackState) {
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                player.seekToDefaultPosition();
                player.prepare();
            } else {
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksInfoChanged(TracksInfo tracksInfo) {
            if (tracksInfo == lastSeenTracksInfo) {
                return;
            }
            if (!tracksInfo.isTypeSupportedOrEmpty(C.TRACK_TYPE_VIDEO, /* allowExceedsCapabilities= */ true)) {
                showToast("Media includes video tracks, but none are playable by this device");
            }
            lastSeenTracksInfo = tracksInfo;
        }
    }
}
