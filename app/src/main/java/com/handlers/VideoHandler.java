package com.handlers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoHandler {
    private Activity ctx;
    private VideoView video;
    private MediaController mediaControls;
    private int position;

    public VideoHandler(FragmentActivity ctx) {
        this.ctx = ctx;
        this.position = 0;
        //set the media controller buttons
        mediaControls = new MediaController(ctx);
    }

    public void setVideoView(VideoView video) {
        this.video = video;
    }

    public int getVideoPosition() {
        return video.getCurrentPosition();
    }

    public void setVideoPosition(int position) {
        this.position = position;
    }


    public void pauseVideo() {
        this.position = video.getCurrentPosition();
        video.pause();
    }

    public void playVideo(String gameId) {
        video.setVisibility(View.VISIBLE);
        try {
            int videoResource = ResourceResolver.resolveVideoResource(gameId);
            //set the media controller in the VideoView
            video.setMediaController(mediaControls);
            //set the uri of the video to be played
            video.setVideoURI(Uri.parse("android.resource://" + ctx.getPackageName() + "/" + videoResource));
        }

        catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        video.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                //set the anchor view of the mediaControls once the video gets its actual dimensions
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mediaControls.setAnchorView(video);
                    }
                });

                //set the video to the specified position, if position is 0
                // the video playback should start
                video.seekTo(position);
                if (position == 0) {
                    video.start();
                } else {
                    //if we come from a resumed activity, video playback will be paused
                    //(in case position is not 0)
                    video.pause();
                }
            }
        });

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video.seekTo(0);
            }
        });
    }
}
