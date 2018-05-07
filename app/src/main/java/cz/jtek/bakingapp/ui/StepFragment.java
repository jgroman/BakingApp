package cz.jtek.bakingapp.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe.Step;

import static android.content.Context.NOTIFICATION_SERVICE;

public class StepFragment extends Fragment implements ExoPlayer.EventListener {

    @SuppressWarnings("unused")
    private static final String TAG = StepFragment.class.getSimpleName();

    private Context mContext;
    private Step mStep;

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private PlayerControlView mPlayerControlView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private NotificationManager mNotificationManager;

    // Instance State bundle keys
    private static final String KEY_STEP = "step";

    public StepFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView: ");

        Activity activity = getActivity();
        if (activity == null) { return null; }

        mContext = activity.getApplicationContext();

        if (savedInstanceState != null) {
            // Restoring step from saved instance state
            Log.d(TAG, "onCreateView: restoring mStep");
            mStep = savedInstanceState.getParcelable(KEY_STEP);
        }
        else {
            // Get current step from passed arguments
            Bundle args = getArguments();
            if (args != null && args.containsKey(StepActivity.BUNDLE_STEP)) {
                mStep = args.getParcelable(StepActivity.BUNDLE_STEP);
            }
        }

        final View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        // Set description title
        TextView titleTextView = rootView.findViewById(R.id.tv_step_description_title);
        if (titleTextView != null) {
            titleTextView.setText(mStep.getShortDescription());
        }

        // Fill out step instruction
        TextView instructionsTextView = rootView.findViewById(R.id.tv_step_instruction);
        instructionsTextView.setText(mStep.getDescription());

        // Show video player or image view respectively
        mPlayerView = rootView.findViewById(R.id.pv_step_video);
        mPlayerControlView = rootView.findViewById(R.id.pv_step_video_control);
        ImageView thumbnailView = rootView.findViewById(R.id.iv_step_thumbnail);
        ImageView noPreviewImageView = rootView.findViewById(R.id.iv_step_no_preview);

        String videoURL = mStep.getVideoUrl();
        String thumbnailURL = mStep.getThumbnailUrl();

        // Check MIME types of source URLs
        //String mimeType

        // If there is video URL available, initialize ExoPlayer
        if (videoURL != null && videoURL.length() > 0) {
            // Make sure player and controls are visible
            mPlayerView.setVisibility(View.VISIBLE);
            if (mPlayerControlView != null) {
                mPlayerControlView.setVisibility(View.VISIBLE);
            }

            // Hide other views
            thumbnailView.setVisibility(View.GONE);
            noPreviewImageView.setVisibility(View.GONE);

            // Initialize player
            initializeMediaSession();
            initializePlayer(Uri.parse(videoURL));
        }
        // If there is thumbnail URL available, show image
        else if (thumbnailURL != null && thumbnailURL.length() > 0) {
            // Make sure thumbnail view is visible
            thumbnailView.setVisibility(View.VISIBLE);

            // Hide other views
            noPreviewImageView.setVisibility(View.GONE);
            mPlayerView.setVisibility(View.GONE);
            if (mPlayerControlView != null) {
                mPlayerControlView.setVisibility(View.GONE);
            }

            // Display thumbnail or placeholder
            Picasso.get()
                    .load(thumbnailURL)
                    .placeholder(R.drawable.ic_format_list_numbered_black_64dp)
                    .into(thumbnailView);
        }
        // Otherwise show no preview placeholder
        else {
            // Make sure "No preview" view is visible
            noPreviewImageView.setVisibility(View.VISIBLE);

            // Hide other views
            thumbnailView.setVisibility(View.GONE);
            mPlayerView.setVisibility(View.GONE);
            if (mPlayerControlView != null) {
                mPlayerControlView.setVisibility(View.GONE);
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_STEP, mStep);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mExoPlayer != null) {
            releasePlayer();
            mMediaSession.setActive(false);
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(mContext, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                );

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MediaSessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MediaSessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    /**
     * Shows Media Style notification, with actions that depend on the current MediaSession
     * PlaybackState.
     * @param state The PlaybackState of the MediaSession.
     */
    private void showNotification(PlaybackStateCompat state) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        int icon;
        String play_pause;

        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.player_pause);
        }
        else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.player_play);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon,
                play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mContext,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.player_restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (mContext, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (mContext, 0, new Intent(mContext, StepActivity.class), 0);

        builder.setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_play_circle_outline_black_24dp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0,1));

        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(mContext, "BakingAppApplication");

            // Produces DataSource instances through which media data is loaded
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, userAgent, null);

            // This is the MediaSource representing the media to be played
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaUri);

            // Init standalone PlayerControlView if it exists
            if (mPlayerControlView != null) {
                // Disable built-in player controls in PlayerView
                mPlayerView.setUseController(false);

                // Bind custom player control view
                mPlayerControlView.setPlayer(mExoPlayer);
            }

            mExoPlayer.prepare(videoSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mNotificationManager.cancelAll();
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }

        mMediaSession.setPlaybackState(mStateBuilder.build());
        showNotification(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
