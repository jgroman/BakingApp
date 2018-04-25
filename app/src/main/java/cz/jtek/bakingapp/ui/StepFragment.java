package cz.jtek.bakingapp.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe.Step;

public class StepFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = StepFragment.class.getSimpleName();

    private Context mContext;
    private Step mStep;

    // Instance State bundle keys
    private static final String KEY_STEP = "step";

    public StepFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        Activity activity = getActivity();
        if (activity == null) { return null; }

        mContext = activity.getApplicationContext();

        if (savedInstanceState != null) {
            // Restoring step from saved instance state
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

        // Fill out step instruction
        TextView instructionsTextView = rootView.findViewById(R.id.tv_step_instruction);
        instructionsTextView.setText(mStep.getDescription());

        String videoURL = mStep.getVideoUrl();
        String thumbnailURL = mStep.getThumbnailUrl();

        // If there is video URL available, show ExoPlayer
        if (videoURL != null && videoURL.length() > 0) {
            Handler mainHandler = new Handler();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);

            PlayerView playerView = rootView.findViewById(R.id.pv_step_video);

            playerView.setPlayer(player);
        }
        // If there is thumbnail URL available, show image
        else if (thumbnailURL != null && thumbnailURL.length() > 0) {

        }


        return rootView;
    }
}
