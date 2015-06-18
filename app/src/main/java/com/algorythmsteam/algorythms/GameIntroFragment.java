package com.algorythmsteam.algorythms;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handlers.AnimationHandler;
import com.handlers.ResourceResolver;

public class GameIntroFragment extends AlgorhythmsFragment implements View.OnClickListener {
    public static final String TAG = "GameIntroFragment";
    private static final String ARG_GAME_TYPE = "game_type";
    private String gameType;
    private AlgoryhmsMainActivity activity;
    private TextView titleText, tipTitle, tipContent;
    private ImageView titleImage, background;
    private ImageButton backButton, playButton, instructionsButton;

    public static GameIntroFragment newInstance(String gameType) {
        GameIntroFragment fragment = new GameIntroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAME_TYPE, gameType);
        fragment.setArguments(args);
        return fragment;
    }

    public GameIntroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameType = getArguments().getString(ARG_GAME_TYPE);
        }

        try {
            activity = (AlgoryhmsMainActivity) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "activity should be of type AlgoryhmsMainActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_intro, container, false);
        titleText = (TextView) root.findViewById(R.id.game_intro_screen_title_text);
        tipTitle = (TextView) root.findViewById(R.id.game_intro_screen_tip_title);
        tipContent = (TextView) root.findViewById(R.id.game_intro_screen_tip_content);
        titleImage = (ImageView) root.findViewById(R.id.game_intro_screen_title_image);
        backButton = (ImageButton) root.findViewById(R.id.game_intro_screen_back_button);
        playButton = (ImageButton) root.findViewById(R.id.game_intro_screen_play_button);
        instructionsButton = (ImageButton) root.findViewById(R.id.game_intro_screen_instructions_button);
        background = (ImageView) root.findViewById(R.id.game_intro_screen_background_grid);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        titleText.setTypeface(tf);
        tipTitle.setTypeface(tf);
        tipContent.setTypeface(tf);

        titleImage.setImageResource(ResourceResolver.resolveGameNameImage(gameType));
        titleText.setText(ResourceResolver.resolveGameCategory(gameType));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initAnimations();
    }

    private void initAnimations() {
        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(-400);
        ObjectAnimator titleDragDown = AnimationHandler.generateYAnimation(titleImage, yOffset, 0, 1000, 1000, new OvershootInterpolator());
        ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(titleImage, 0, 1, 30, 0, null);

        ObjectAnimator categorySlideIn = AnimationHandler.generateAnimation(titleText, "translationX", -yOffset, 0, 1500, 0, new DecelerateInterpolator());
        ObjectAnimator categoryFadeIn = AnimationHandler.generateAlphaAnimation(titleText, 0, 1, 10, 0, null);

        AnimatorSet titleAnims = new AnimatorSet();
        titleAnims.play(titleDragDown).with(titleFadeIn);
        titleAnims.play(categorySlideIn).with(categoryFadeIn).after(titleDragDown);

        AnimatorSet backPopIn = AnimationHandler.generatePopInAnimation(backButton, 0, 1, 600, 0, new OvershootInterpolator());
        AnimatorSet playPopIn = AnimationHandler.generatePopInAnimation(playButton, 0, 1, 600, 120, new OvershootInterpolator());
        AnimatorSet instructionsPopIn = AnimationHandler.generatePopInAnimation(instructionsButton, 0, 1, 600, 240, new OvershootInterpolator());
        instructionsPopIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                playButton.setOnClickListener(GameIntroFragment.this);
                backButton.setOnClickListener(GameIntroFragment.this);
                instructionsButton.setOnClickListener(GameIntroFragment.this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet buttonAnims = new AnimatorSet();
        buttonAnims.play(backPopIn).with(playPopIn).with(instructionsPopIn).after(titleAnims);

        AnimatorSet tipAnims = new AnimatorSet();
        ObjectAnimator tipTitleFadeIn = AnimationHandler.generateAlphaAnimation(tipTitle, 0, 1, 1500, 0, new DecelerateInterpolator());
        ObjectAnimator tipContentFadeIn = AnimationHandler.generateAlphaAnimation(tipContent, 0, 1, 1500, 400, new DecelerateInterpolator());
        tipAnims.play(tipTitleFadeIn).with(tipContentFadeIn).after(buttonAnims);
        tipAnims.setStartDelay(300);

        final ObjectAnimator titleFlip = AnimationHandler.generateAnimation(titleImage, "rotationY", 0, 360f, 2200, 2000, new AnticipateOvershootInterpolator());
        titleFlip.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                titleFlip.setStartDelay(6000);
                titleFlip.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet flipAnim = new AnimatorSet();
        flipAnim.play(titleFlip).after(tipAnims);

        ObjectAnimator backgroundFadeIn = AnimationHandler.generateAlphaAnimation(background, 0, 0.4f, 3000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeX = AnimationHandler.generateAnimation(background, "scaleX", 1, 2f, 100000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeY = AnimationHandler.generateAnimation(background, "scaleY", 1, 2f, 100000, 0, new DecelerateInterpolator());
        AnimatorSet backgroundAnim = new AnimatorSet();
        backgroundAnim.playTogether(backgroundFadeIn, backgroundResizeX, backgroundResizeY);
        backgroundAnim.setStartDelay(300);

        backgroundAnim.start();
        titleAnims.start();
        buttonAnims.start();
        tipAnims.start();
        flipAnim.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_intro_screen_back_button:
                handleBackPress();
                break;

            case R.id.game_intro_screen_play_button:
                //TODO: implement this
                break;

            case R.id.game_intro_screen_instructions_button:
                //TODO: implement this
                break;

            default:
                break;
        }
    }

    @Override
    public boolean handleBackPress() {
        if (activity != null) {
            AlgoryhmsMainFragment frag = new AlgoryhmsMainFragment();
            activity.launchFragment(frag, AlgoryhmsMainFragment.TAG,
                    R.anim.enter_reverse, R.anim.exit_reverse);
            return true;
        }

        return false;
    }

    @Override
    public boolean handleNfcScan(String res) {
        //TODO: implement this
        return true;
    }
}
