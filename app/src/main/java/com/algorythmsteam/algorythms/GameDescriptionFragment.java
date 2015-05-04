package com.algorythmsteam.algorythms;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.handlers.ResourceResolver;
import com.handlers.VideoHandler;


public class GameDescriptionFragment extends Fragment {
    // the fragment initialization parameters
    public static final String TAG = "GameDescriptionFragment";
    private static final String GAME_ID = "game_id";
    private String gameId;
    private View root;
    private TextView gameTitle, gameCategory;
    private VideoHandler videoHandler;

    public static GameDescriptionFragment newInstance(String gameId) {
        GameDescriptionFragment fragment = new GameDescriptionFragment();
        Bundle args = new Bundle();
        args.putString(GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    public GameDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            gameId = getArguments().getString(GAME_ID);
        }

        if (videoHandler == null) {
            videoHandler = new VideoHandler(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_game_description, container, false);
        gameTitle = (TextView) root.findViewById(R.id.game_description_title);
        gameCategory = (TextView) root.findViewById(R.id.game_description_category);
        gameTitle.setText(ResourceResolver.resolveGameName("bubble_sort"));
        gameCategory.setText(ResourceResolver.resolveGameCategory("bubble_sort"));
        videoHandler.setVideoView((VideoView) root.findViewById(R.id.game_description_video));
        videoHandler.setVideoHiderView(root.findViewById(R.id.video_hider));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        titleInTransition();
    }

    private void titleInTransition() {
        ObjectAnimator textUp = ObjectAnimator.ofFloat(gameTitle, "translationY",
                ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(400), 0);
        textUp.setDuration(1100);
        textUp.setInterpolator(new AccelerateDecelerateInterpolator());
        textUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                gameTitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator textSide = ObjectAnimator.ofFloat(gameCategory, "translationX",
                -((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(400) ,0);
        textSide.setDuration(1200);
        textSide.setInterpolator(new AccelerateDecelerateInterpolator());
        textSide.setStartDelay(380);
        textSide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                gameCategory.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                titleOutTransition();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final AnimatorSet as = new AnimatorSet();
        as.play(textUp).with(textSide);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                as.start();
            }
        }, 500);
    }

    private void titleOutTransition() {
        ObjectAnimator textUp = ObjectAnimator.ofFloat(gameTitle, "translationY", 0,
                -((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(230));
        textUp.setDuration(800);
        textUp.setInterpolator(new AccelerateDecelerateInterpolator());
        textUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                videoHandler.playVideo("bubble_sort");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator textResizeX = ObjectAnimator.ofFloat(gameTitle, "scaleX", 0.7f);
        textResizeX.setDuration(800);
        textResizeX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator textResizeY = ObjectAnimator.ofFloat(gameTitle, "scaleY", 0.7f);
        textResizeY.setDuration(800);
        textResizeY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator textSide = ObjectAnimator.ofFloat(gameCategory, "translationX", 0,  -((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(300));
        textSide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                gameCategory.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        textSide.setDuration(800);
        textSide.setInterpolator(new AccelerateDecelerateInterpolator());

        final AnimatorSet as = new AnimatorSet();
        as.play(textUp).with(textResizeX).with(textResizeY).with(textSide);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                as.start();
            }
        }, 2200);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("Position")) {
            int position = savedInstanceState.getInt("Position");
            videoHandler.setVideoPosition(position);
            savedInstanceState.remove("Position");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        videoHandler.pauseVideo();
        int position = videoHandler.getVideoPosition();
        savedInstanceState.putInt("Position", position);
    }

    private boolean doesGameDescriptionContainVideo() {
        return (ResourceResolver.resolveVideoResource(gameId) != ResourceResolver.UNDEFINED_RESOURCE);
    }
}
