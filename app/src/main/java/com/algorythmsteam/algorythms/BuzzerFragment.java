package com.algorythmsteam.algorythms;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.handlers.AnimationHandler;

import java.io.IOException;


public class BuzzerFragment extends Fragment implements View.OnClickListener, Animator.AnimatorListener {
    public static String TAG = "buzzerFragment";
    private boolean isAnimInit;
    private View root;
    private ImageButton buzzerButton;
    private ImageView buzzerTitle, background;
    private MediaPlayer mediaPlayer;

    public static BuzzerFragment newInstance() {
        return new BuzzerFragment();
    }

    public BuzzerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAnimInit = false;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_buzzer, container, false);
        background = (ImageView) root.findViewById(R.id.buzzer_background_grid);
        buzzerTitle = (ImageView) root.findViewById(R.id.buzzer_title);
        buzzerButton = (ImageButton) root.findViewById(R.id.buzzer);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initAnims();
    }

    private void initAnims() {
        if (isAnimInit) {
            return;
        }

        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(400);
        float topYOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(190);

        ObjectAnimator titleUp = AnimationHandler.generateYAnimation(buzzerTitle, -yOffset, -topYOffset, 700, 600, new OvershootInterpolator());
        ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(buzzerTitle, 0, 1, 10, 0, null);

        ObjectAnimator backgroundFadeIn = AnimationHandler.generateAlphaAnimation(background, 0, 0.7f, 3000, 300, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeX = AnimationHandler.generateAnimation(background, "scaleX", 1, 2f, 100000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeY = AnimationHandler.generateAnimation(background, "scaleY", 1, 2f, 100000, 0, new DecelerateInterpolator());

        ObjectAnimator buzzerFadeIn = AnimationHandler.generateAlphaAnimation(buzzerButton, 0, 1, 1, 0, null);
        ObjectAnimator buzzerResizeX = AnimationHandler.generateAnimation(buzzerButton, "scaleX", 0, 1, 1200, 0, new BounceInterpolator());
        ObjectAnimator buzzerResizeY = AnimationHandler.generateAnimation(buzzerButton, "scaleY", 0, 1, 1200, 0, new BounceInterpolator());
        buzzerFadeIn.addListener(this);

        AnimatorSet as = new AnimatorSet();
        as.play(titleUp).with(titleFadeIn);
        as.play(backgroundFadeIn).with(backgroundResizeX).with(backgroundResizeY);
        as.play(buzzerFadeIn).with(buzzerResizeX).with(buzzerResizeY).after(titleUp);
        as.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buzzer:
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

                try {
                    mediaPlayer.reset();
                    AssetFileDescriptor afd;
                    afd = getActivity().getAssets().openFd("buzzer_sound.mp3");
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    mediaPlayer = null;
                }

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //used on the buzzerButtonBounceIn animation
        buzzerButton.setOnClickListener(this);
        isAnimInit = true;
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
