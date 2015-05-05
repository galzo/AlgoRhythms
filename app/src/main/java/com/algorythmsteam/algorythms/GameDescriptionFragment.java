package com.algorythmsteam.algorythms;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.VideoView;

import com.handlers.AnimationHandler;
import com.handlers.ResourceResolver;
import com.handlers.VideoHandler;


public class GameDescriptionFragment extends Fragment implements Animator.AnimatorListener {
    // the fragment initialization parameters
    public static final String TAG = "GameDescriptionFragment";
    private static final String GAME_ID = "game_id";
    private String gameId;
    private boolean isAnimInit;
    private View root, videoHider;
    private ImageView gameTitle, background;
    private TextView gameCategory;
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
        isAnimInit = false;
        root = inflater.inflate(R.layout.fragment_game_description, container, false);
        gameTitle = (ImageView) root.findViewById(R.id.game_description_title);
        gameTitle.setImageResource(ResourceResolver.resolveGameNameImage(gameId));
        gameCategory = (TextView) root.findViewById(R.id.game_description_category);
        videoHider = root.findViewById(R.id.video_hider);
        background = (ImageView) root.findViewById(R.id.game_description_background_grid);
        gameCategory.setText(ResourceResolver.resolveGameCategory(gameId));
        String fontPath = "fonts/coopbl.TTF";
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), fontPath);
        gameCategory.setTypeface(tf);
        videoHandler.setVideoView((VideoView) root.findViewById(R.id.game_description_video));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initAnims();
    }

    private void initAnims() {
        if (isAnimInit) {
            //make sure that video hider is gone
            videoHider.setVisibility(View.GONE);

            //play the video
            videoHandler.playVideo(gameId);
            return;
        }

        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(400);
        float topYOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(190);
        ObjectAnimator titleUp = AnimationHandler.generateYAnimation(gameTitle, yOffset, 0, 2300, 400, new AnticipateOvershootInterpolator());
        ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(gameTitle, 0, 1, 10, 0, null);

        ObjectAnimator categorySlideIn = AnimationHandler.generateAnimation(gameCategory, "translationX", -yOffset, 0, 1500, 0, new DecelerateInterpolator());
        ObjectAnimator categoryFadeIn = AnimationHandler.generateAlphaAnimation(gameCategory, 0, 1, 10, 0, null);

        ObjectAnimator titleUpper = AnimationHandler.generateYAnimation(gameTitle, 0, -topYOffset, 1000, 1200, new AccelerateDecelerateInterpolator());
        ObjectAnimator categorySlideOut = AnimationHandler.generateAnimation(gameCategory, "translationX", 0, -yOffset, 1200, 0, new DecelerateInterpolator());

        ObjectAnimator videoFadeIn = AnimationHandler.generateAlphaAnimation(videoHider, 1, 0, 1000, 400, null);
        videoFadeIn.addListener(this);

        ObjectAnimator backgroundFadeIn = AnimationHandler.generateAlphaAnimation(background, 0, 0.7f, 3000, 300, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeX = AnimationHandler.generateAnimation(background, "scaleX", 1, 2f, 100000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeY = AnimationHandler.generateAnimation(background, "scaleY", 1, 2f, 100000, 0, new DecelerateInterpolator());

        AnimatorSet as = new AnimatorSet();
        as.play(titleUp).with(titleFadeIn);
        as.play(categorySlideIn).with(categoryFadeIn).after(titleUp);
        as.play(titleUpper).with(categorySlideOut).after(categorySlideIn);
        as.play(videoFadeIn).with(backgroundFadeIn).with(backgroundResizeX).with(backgroundResizeY).after(titleUpper);

        as.start();
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

    @Override
    public void onAnimationStart(Animator animation) {
        isAnimInit = true;
        videoHandler.playVideo(gameId);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        videoHider.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
