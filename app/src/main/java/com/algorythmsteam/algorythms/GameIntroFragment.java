package com.algorythmsteam.algorythms;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handlers.AnimationHandler;
import com.handlers.ResourceResolver;

import java.io.IOException;

public class GameIntroFragment extends AlgorhythmsFragment implements View.OnClickListener {
    public static final String TAG = "GameIntroFragment";
    private static final String ARG_GAME_TYPE = "game_type";
    private boolean isAnimInit, isGameRunning, isButtonDescriptionDisplayed;
    private String gameType;
    private AlgoryhmsMainActivity activity;
    private View root, soundButtonsHolder, playGameButtonsHolder;
    private TextView titleText, tipTitle, tipContent, nfcButtonDescription;
    private ImageView titleImage, background, likeButtonDescription, dislikeButtonDescription;
    private ImageButton backButton, playButton, instructionsButton, likeButton, dislikeButton, nfcButton, descriptionButton;
    private MediaPlayer mediaPlayer;

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
        isGameRunning = false;
        isAnimInit = false;
        isButtonDescriptionDisplayed = false;

        if (getArguments() != null) {
            gameType = getArguments().getString(ARG_GAME_TYPE);
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
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
        root = inflater.inflate(R.layout.fragment_game_intro, container, false);
        titleText = (TextView) root.findViewById(R.id.game_intro_screen_title_text);
        tipTitle = (TextView) root.findViewById(R.id.game_intro_screen_tip_title);
        tipContent = (TextView) root.findViewById(R.id.game_intro_screen_tip_content);
        titleImage = (ImageView) root.findViewById(R.id.game_intro_screen_title_image);
        backButton = (ImageButton) root.findViewById(R.id.game_intro_screen_back_button);
        playButton = (ImageButton) root.findViewById(R.id.game_intro_screen_play_button);
        instructionsButton = (ImageButton) root.findViewById(R.id.game_intro_screen_instructions_button);
        background = (ImageView) root.findViewById(R.id.game_intro_screen_background_grid);
        nfcButtonDescription = (TextView) root.findViewById(R.id.game_intro_screen_nfc_button_description);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        titleText.setTypeface(tf);
        tipTitle.setTypeface(tf);
        tipContent.setTypeface(tf);
        nfcButtonDescription.setTypeface(tf);

        titleImage.setImageResource(ResourceResolver.resolveGameNameImage(gameType));
        titleText.setTextColor(getActivity().getResources().getColor(ResourceResolver.resolveGameTypeColor(gameType)));
        titleText.setText(ResourceResolver.resolveGameCategory(gameType));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        initAnimations();
        isAnimInit = true;
    }

    private void initAnimations() {
        if (isAnimInit) {
            return;
        }

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
        final AnimatorSet instructionsPopIn = AnimationHandler.generatePopInAnimation(instructionsButton, 0, 1, 600, 240, new OvershootInterpolator());
        instructionsPopIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                playButton.setOnClickListener(GameIntroFragment.this);
                backButton.setOnClickListener(GameIntroFragment.this);
                instructionsButton.setOnClickListener(GameIntroFragment.this);
                instructionsPopIn.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet tipAnims = new AnimatorSet();
        ObjectAnimator tipTitleFadeIn = AnimationHandler.generateAlphaAnimation(tipTitle, 0, 1, 600, 0, new DecelerateInterpolator());
        ObjectAnimator tipContentFadeIn = AnimationHandler.generateAlphaAnimation(tipContent, 0, 1, 600, 200, new DecelerateInterpolator());
        tipAnims.playTogether(tipTitleFadeIn, tipContentFadeIn);

        AnimatorSet buttonAnims = new AnimatorSet();
        buttonAnims.play(backPopIn).with(playPopIn).with(instructionsPopIn).with(tipAnims).after(titleAnims);

        final ObjectAnimator titleFlip = AnimationHandler.generateAnimation(titleImage, "rotationY", 0, 360f, 2200, 2000, new AnticipateOvershootInterpolator());
        titleFlip.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                titleFlip.setStartDelay(10000);
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
        flipAnim.play(titleFlip).after(buttonAnims);

        ObjectAnimator backgroundFadeIn = AnimationHandler.generateAlphaAnimation(background, 0, 0.4f, 3000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeX = AnimationHandler.generateAnimation(background, "scaleX", 1, 2f, 100000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeY = AnimationHandler.generateAnimation(background, "scaleY", 1, 2f, 100000, 0, new DecelerateInterpolator());
        AnimatorSet backgroundAnim = new AnimatorSet();
        backgroundAnim.playTogether(backgroundFadeIn, backgroundResizeX, backgroundResizeY);
        backgroundAnim.setStartDelay(300);

        backgroundAnim.start();
        titleAnims.start();
        buttonAnims.start();
        flipAnim.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_intro_screen_back_button:
                handleBackPress();
                break;

            case R.id.game_intro_screen_play_button:
                playButton.setOnClickListener(null); //to avoid bugs related to the play transition
                startGame();
                break;

            case R.id.game_intro_screen_instructions_button:
                Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                break;

            case R.id.game_intro_screen_nfc_button:
                BubbleSortScanDialog dialog = BubbleSortScanDialog.newInstance(R.id.game_intro_screen_background_overlay);
                FragmentManager fm = activity.getSupportFragmentManager();
                dialog.show(fm, BubbleSortScanDialog.TAG);

                break;

            case R.id.game_intro_screen_description_button:
                showButtonsDescription();
                break;

            case R.id.game_intro_screen_like_button:
                handleSoundButtonClick(v);
                break;

            case R.id.game_intro_screen_dislike_button:
                handleSoundButtonClick(v);
                break;

            default:
                break;
        }
    }

    private void handleSoundButtonClick(final View clickedButton) {
        clickedButton.setOnClickListener(null); //to avoid bugs related to multiple fast clicks
        ObjectAnimator likeXScaleIn = AnimationHandler.generateXScaleAnimation(clickedButton, 1, 0.5f, 200, 0, null);
        final ObjectAnimator likeYScaleIn = AnimationHandler.generateYScaleAnimation(clickedButton, 1, 0.5f, 200, 0, null);
        likeYScaleIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                clickedButton.setOnClickListener(GameIntroFragment.this);
                ObjectAnimator likeXScaleOut = AnimationHandler.generateXScaleAnimation(clickedButton, 0.5f, 1, 400, 0, new OvershootInterpolator());
                ObjectAnimator likeYScaleOut = AnimationHandler.generateYScaleAnimation(clickedButton, 0.5f, 1, 400, 0, new OvershootInterpolator());
                AnimatorSet likeScaleOut = new AnimatorSet();
                likeScaleOut.playTogether(likeXScaleOut, likeYScaleOut);
                likeScaleOut.start();
                likeYScaleIn.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet likeScaleIn = new AnimatorSet();
        likeScaleIn.playTogether(likeXScaleIn, likeYScaleIn);
        likeScaleIn.start();

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        String playSound = (clickedButton.getId() == R.id.game_intro_screen_like_button)?
                "yay.mp3" : "boo.mp3";

        try {
            mediaPlayer.reset();
            AssetFileDescriptor afd;
            afd = getActivity().getAssets().openFd(playSound);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mp == mediaPlayer) {
                        mediaPlayer.start();
                    }
                }
            });
            mediaPlayer.prepare();
        }

        catch (IOException e) {
            mediaPlayer = null;
            Log.e(TAG, "error loading the media player");
            Log.e(TAG, e.getMessage());
        }
    }

    private void showButtonsDescription() {
        isButtonDescriptionDisplayed = true;
        descriptionButton.setOnClickListener(null); //to avoid bugs related to multiple quick clicks on that button
        ObjectAnimator nfcButtonFlip = AnimationHandler.generateAnimation(nfcButton, "rotationY", 0, 90, 500, 0, new AnticipateInterpolator());
        ObjectAnimator likeButtonFlip = AnimationHandler.generateAnimation(likeButton, "rotationY", 0, 90, 500, 120, new AnticipateInterpolator());
        ObjectAnimator dislikeButtonFlip = AnimationHandler.generateAnimation(dislikeButton, "rotationY", 0, 90, 500, 240, new AnticipateInterpolator());

        ObjectAnimator nfcTextFlip = AnimationHandler.generateAnimation(nfcButtonDescription, "rotationY", 270, 360, 500, 500, new OvershootInterpolator());
        ObjectAnimator nfcTextFadeIn = AnimationHandler.generateAlphaAnimation(nfcButtonDescription, 0, 1, 10, 500, null);
        ObjectAnimator likeTextFlip = AnimationHandler.generateAnimation(likeButtonDescription, "rotationY", 270, 360, 500, 620, new OvershootInterpolator());
        ObjectAnimator likeTextFadeIn = AnimationHandler.generateAlphaAnimation(likeButtonDescription, 0, 1, 10, 620, null);
        ObjectAnimator dislikeTextFlip = AnimationHandler.generateAnimation(dislikeButtonDescription, "rotationY", 270, 360, 500, 740, new OvershootInterpolator());
        ObjectAnimator dislikeTextFadeIn = AnimationHandler.generateAlphaAnimation(dislikeButtonDescription, 0, 1, 10, 740, null);
        AnimatorSet as = new AnimatorSet();
        as.playTogether(nfcButtonFlip, likeButtonFlip, dislikeButtonFlip, nfcTextFlip, nfcTextFadeIn, likeTextFlip, likeTextFadeIn, dislikeTextFlip, dislikeTextFadeIn);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator nfcTextFlip = AnimationHandler.generateAnimation(nfcButtonDescription, "rotationY", 0, 90, 500, 0, new AnticipateInterpolator());
                ObjectAnimator nfcTextFadeOut = AnimationHandler.generateAlphaAnimation(nfcButtonDescription, 1, 0, 1500, 0, null);
                ObjectAnimator likeTextFlip = AnimationHandler.generateAnimation(likeButtonDescription, "rotationY", 0, 90, 500, 120, new AnticipateInterpolator());
                ObjectAnimator likeTextFadeOut = AnimationHandler.generateAlphaAnimation(likeButtonDescription, 1, 0, 1500, 120, null);
                ObjectAnimator dislikeTextFlip = AnimationHandler.generateAnimation(dislikeButtonDescription, "rotationY", 0, 90, 500, 240, new AnticipateInterpolator());
                ObjectAnimator dislikeTextFadeOut = AnimationHandler.generateAlphaAnimation(dislikeButtonDescription, 1, 0, 1500, 240, null);

                ObjectAnimator nfcButtonFlip = AnimationHandler.generateAnimation(nfcButton, "rotationY", 270, 360, 500, 500, new OvershootInterpolator());
                ObjectAnimator likeButtonFlip = AnimationHandler.generateAnimation(likeButton, "rotationY", 270, 360, 500, 620, new OvershootInterpolator());
                final ObjectAnimator dislikeButtonFlip = AnimationHandler.generateAnimation(dislikeButton, "rotationY", 270, 360, 500, 740, new OvershootInterpolator());
                dislikeButtonFlip.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        descriptionButton.setOnClickListener(GameIntroFragment.this);
                        dislikeButtonFlip.removeAllListeners();
                        isButtonDescriptionDisplayed = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                AnimatorSet as = new AnimatorSet();
                as.playTogether(nfcTextFlip, nfcTextFadeOut, likeTextFlip, likeTextFadeOut, dislikeTextFlip, dislikeTextFadeOut, nfcButtonFlip, likeButtonFlip, dislikeButtonFlip);
                as.start();
            }
        }, 4000);

        as.start();
    }

    private void startGame() {
        soundButtonsHolder = root.findViewById(R.id.game_intro_screen_sound_buttons);
        playGameButtonsHolder = root.findViewById(R.id.game_intro_screen_play_game_buttons);
        soundButtonsHolder.setVisibility(View.VISIBLE);
        playGameButtonsHolder.setVisibility(View.VISIBLE);

        nfcButton = (ImageButton) root.findViewById(R.id.game_intro_screen_nfc_button);
        descriptionButton = (ImageButton) root.findViewById(R.id.game_intro_screen_description_button);
        likeButton = (ImageButton) root.findViewById(R.id.game_intro_screen_like_button);
        dislikeButton = (ImageButton) root.findViewById(R.id.game_intro_screen_dislike_button);

        likeButtonDescription = (ImageView) root.findViewById(R.id.game_intro_screen_like_button_description);
        dislikeButtonDescription = (ImageView) root.findViewById(R.id.game_intro_screen_dislike_button_description);

        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(400);
        float topYOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(80);
        ObjectAnimator titleUpper = AnimationHandler.generateYAnimation(titleImage, 0, -topYOffset, 1000, 0, new AnticipateOvershootInterpolator());
        ObjectAnimator categorySlideOut = AnimationHandler.generateAnimation(titleText, "translationX", 0, -yOffset, 900, 0, new AccelerateInterpolator());
        AnimatorSet titleSlide = new AnimatorSet();
        titleSlide.playTogether(titleUpper, categorySlideOut);

        ObjectAnimator tipTitleFadeOut = AnimationHandler.generateAlphaAnimation(tipTitle, 1, 0, 300, 0, null);
        ObjectAnimator tipContentFadeOut = AnimationHandler.generateAlphaAnimation(tipContent, 1, 0, 300, 0, null);
        AnimatorSet tipFadeOut = new AnimatorSet();
        tipFadeOut.playTogether(tipTitleFadeOut, tipContentFadeOut);

        AnimatorSet backButtonPopOut = AnimationHandler.generatePopOutAnimation(backButton, 1, 0, 500, 0, new AnticipateInterpolator());
        AnimatorSet instructionsButtonPopOut = AnimationHandler.generatePopOutAnimation(instructionsButton, 1, 0, 500, 120, new AnticipateInterpolator());
        AnimatorSet playButtonPopOut = AnimationHandler.generatePopOutAnimation(playButton, 1, 0, 500, 240, new AnticipateInterpolator());
        AnimatorSet buttonsPopOut = new AnimatorSet();
        buttonsPopOut.playTogether(backButtonPopOut, instructionsButtonPopOut, playButtonPopOut);

        AnimatorSet likeButtonPopIn = AnimationHandler.generatePopInAnimation(likeButton, 0, 1, 500, 0, new OvershootInterpolator(1));
        final AnimatorSet dislikeButtonPopIn = AnimationHandler.generatePopInAnimation(dislikeButton, 0, 1, 500, 120, new OvershootInterpolator(1));
        dislikeButtonPopIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                likeButton.setOnClickListener(GameIntroFragment.this);
                dislikeButton.setOnClickListener(GameIntroFragment.this);
                backButton.setOnClickListener(null);
                playButton.setOnClickListener(null);
                instructionsButton.setOnClickListener(null);
                dislikeButtonPopIn.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet soundButtonsPopIn = new AnimatorSet();
        soundButtonsPopIn.playTogether(likeButtonPopIn, dislikeButtonPopIn);
        soundButtonsPopIn.setStartDelay(350);

        AnimatorSet nfcButtonPopIn = AnimationHandler.generatePopInAnimation(nfcButton, 0, 1, 500, 0, new OvershootInterpolator(1));
        final AnimatorSet descriptionPopIn = AnimationHandler.generatePopInAnimation(descriptionButton, 0, 1, 500, 120, new OvershootInterpolator(1));
        AnimatorSet playGameButtonsPopIn = new AnimatorSet();
        playGameButtonsPopIn.playTogether(nfcButtonPopIn, descriptionPopIn);
        descriptionPopIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                nfcButton.setOnClickListener(GameIntroFragment.this);
                descriptionButton.setOnClickListener(GameIntroFragment.this);
                isGameRunning = true;
                descriptionPopIn.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet as = new AnimatorSet();
        as.play(titleSlide).with(tipFadeOut).with(buttonsPopOut).with(soundButtonsPopIn).before(playGameButtonsPopIn);
        as.setStartDelay(100);
        as.start();
    }

    private void exitGame() {
        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(400);
        float topYOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(80);
        isGameRunning = false;

        ObjectAnimator titleLower = AnimationHandler.generateYAnimation(titleImage, -topYOffset, 0, 1000, 0, new AnticipateOvershootInterpolator());
        ObjectAnimator categorySlideIn = AnimationHandler.generateAnimation(titleText, "translationX", -yOffset, 0, 900, 0, new DecelerateInterpolator());
        AnimatorSet titleSlide = new AnimatorSet();
        titleSlide.playTogether(titleLower, categorySlideIn);
        titleSlide.setStartDelay(200);

        AnimatorSet nfcButtonPopOut = AnimationHandler.generatePopOutAnimation(nfcButton, 1, 0, 500, 0, new AnticipateInterpolator(0.8f));
        AnimatorSet descriptionButtonPopOut = AnimationHandler.generatePopOutAnimation(descriptionButton, 1, 0, 500, 150, new AnticipateInterpolator(0.8f));
        AnimatorSet buttonsPopOut = new AnimatorSet();
        AnimatorSet likeButtonPopOut = AnimationHandler.generatePopOutAnimation(likeButton, 1, 0, 500, 300, new AnticipateInterpolator(1.5f));
        final AnimatorSet dislikeButtonPopOut = AnimationHandler.generatePopOutAnimation(dislikeButton, 1, 0, 500, 450, new AnticipateInterpolator(1.5f));
        dislikeButtonPopOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                nfcButton.setOnClickListener(null);
                descriptionButton.setOnClickListener(null);
                likeButton.setOnClickListener(null);
                dislikeButton.setOnClickListener(null);
                playGameButtonsHolder.setVisibility(View.GONE);
                soundButtonsHolder.setVisibility(View.GONE);
                dislikeButtonPopOut.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        buttonsPopOut.playTogether(nfcButtonPopOut, descriptionButtonPopOut, likeButtonPopOut, dislikeButtonPopOut);

        final AnimatorSet backPopIn = AnimationHandler.generatePopInAnimation(backButton, 0, 1, 600, 0, new OvershootInterpolator());
        AnimatorSet playPopIn = AnimationHandler.generatePopInAnimation(playButton, 0, 1, 600, 120, new OvershootInterpolator());
        final AnimatorSet instructionsPopIn = AnimationHandler.generatePopInAnimation(instructionsButton, 0, 1, 600, 240, new OvershootInterpolator());
        instructionsPopIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                backButton.setOnClickListener(GameIntroFragment.this);
                playButton.setOnClickListener(GameIntroFragment.this);
                instructionsButton.setOnClickListener(GameIntroFragment.this);
                instructionsPopIn.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet buttonsPopIn = new AnimatorSet();
        buttonsPopIn.playTogether(backPopIn, playPopIn, instructionsPopIn);

        AnimatorSet tipAnims = new AnimatorSet();
        ObjectAnimator tipTitleFadeIn = AnimationHandler.generateAlphaAnimation(tipTitle, 0, 1, 600, 600, new DecelerateInterpolator());
        ObjectAnimator tipContentFadeIn = AnimationHandler.generateAlphaAnimation(tipContent, 0, 1, 600, 150, new DecelerateInterpolator());
        tipAnims.playTogether(tipTitleFadeIn, tipContentFadeIn);

        AnimatorSet as = new AnimatorSet();
        as.play(buttonsPopOut).with(titleSlide).with(tipAnims).before(buttonsPopIn);
        as.setStartDelay(100);
        as.start();
    }

    @Override
    public boolean handleBackPress() {
        if (activity != null) {
            //if buttons description is displayed - ignore the back press
            if (isButtonDescriptionDisplayed) {
                return true;
            }

            //if the game is currently running (play game was pressed)
            //we want to move back to gameIntroScreen
            if (isGameRunning) {
                exitGame();
                return true;
            }

            //otherwise - we want to go back to the main menu
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
