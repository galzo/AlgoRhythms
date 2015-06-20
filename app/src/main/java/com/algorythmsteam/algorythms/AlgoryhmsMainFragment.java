package com.algorythmsteam.algorythms;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handlers.AnimationHandler;
import com.handlers.ResourceResolver;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class AlgoryhmsMainFragment extends AlgorhythmsFragment
        implements OnClickListener, Animator.AnimatorListener {
    public static final String TAG = "AlgoryhmsMainFragment";

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //used after all animations have been displayed
        nfcButton.setOnClickListener(AlgoryhmsMainFragment.this);
        qrButton.setOnClickListener(AlgoryhmsMainFragment.this);
        aboutButton.setOnClickListener(AlgoryhmsMainFragment.this);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    private View root;
    private boolean isAnimInit;
    private AlgoryhmsMainActivity activity;
    private ImageView splashIcon, backgroundGrid;
    private ImageButton nfcButton, qrButton, aboutButton;
    private TextView nfcDescription, qrDescription, tipTitle, tipContent;

    public AlgoryhmsMainFragment() {
        super();
        this.isAnimInit = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_algoryhms_main, container, false);
        splashIcon = (ImageView) root.findViewById(R.id.splash_screen_app_icon);
        backgroundGrid = (ImageView) root.findViewById(R.id.splash_screen_background_grid);
        nfcButton = (ImageButton) root.findViewById(R.id.splash_screen_nfc_scan_button);
        qrButton = (ImageButton) root.findViewById(R.id.splash_screen_qr_scan_button);
        aboutButton = (ImageButton) root.findViewById(R.id.splash_screen_about_button);
        nfcDescription = (TextView) root.findViewById(R.id.splash_screen_nfc_description);
        qrDescription = (TextView) root.findViewById(R.id.splash_screen_qr_description);
        tipTitle = (TextView) root.findViewById(R.id.splash_screen_tip_title);
        tipContent = (TextView) root.findViewById(R.id.splash_screen_tip_content);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        nfcDescription.setTypeface(tf);
        qrDescription.setTypeface(tf);
        tipTitle.setTypeface(tf);
        tipContent.setTypeface(tf);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            activity = (AlgoryhmsMainActivity) getActivity();
        } catch(ClassCastException e) {
            Log.e(TAG, "activity should be of type AlgorhythmsMainActivity");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initSplashAnims();
        isAnimInit = true;
    }

    public void initSplashAnims() {
        //if anim was already initialized - do not replay it
        if (isAnimInit) {
            return;
        }

        //set background animation
        ObjectAnimator backgroundFadeIn = AnimationHandler.generateAlphaAnimation(backgroundGrid, 0, 0.3f, 3000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeX = AnimationHandler.generateAnimation(backgroundGrid, "scaleX", 1, 2f, 100000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeY = AnimationHandler.generateAnimation(backgroundGrid, "scaleY", 1, 2f, 100000, 0, new DecelerateInterpolator());
        AnimatorSet backgroundAnim = new AnimatorSet();
        backgroundAnim.playTogether(backgroundFadeIn, backgroundResizeX, backgroundResizeY);
        backgroundAnim.setStartDelay(300);
        backgroundAnim.start();

        //set splash icon animation
        ObjectAnimator splashIconSpring = AnimationHandler.generateAnimation(splashIcon, "rotationY", 270f, 360f, 2200, 1000, new BounceInterpolator());

        //set an offset for the buttons animations
        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(300);

        //set buttons animations
        ObjectAnimator qrButtonUp = AnimationHandler.generateYAnimation(qrButton, yOffset, 0, 1400, 0, new AnticipateOvershootInterpolator());
        ObjectAnimator qrButtonFadeIn = AnimationHandler.generateAlphaAnimation(qrButton, 0, 1, 10, 0, null);

        ObjectAnimator nfcButtonUp = AnimationHandler.generateYAnimation(nfcButton, yOffset, 0, 1400, 120, new AnticipateOvershootInterpolator());
        ObjectAnimator nfcButtonFadeIn = AnimationHandler.generateAlphaAnimation(nfcButton, 0, 1, 10, 120, null);

        ObjectAnimator aboutButtonUp = AnimationHandler.generateYAnimation(aboutButton, yOffset, 0, 1400, 240, new AnticipateOvershootInterpolator());
        ObjectAnimator aboutButtonFadeIn = AnimationHandler.generateAlphaAnimation(aboutButton, 0, 1, 10, 240, null);
        aboutButtonUp.addListener(this);

        AnimatorSet tipAnims = new AnimatorSet();
        tipAnims.setStartDelay(120);
        ObjectAnimator tipTitleFadeIn = AnimationHandler.generateAlphaAnimation(tipTitle, 0, 1, 1400, 0, new DecelerateInterpolator());
        ObjectAnimator tipContentFadeIn = AnimationHandler.generateAlphaAnimation(tipContent, 0, 0.4f, 1400, 400, new DecelerateInterpolator());
        tipAnims.play(tipTitleFadeIn).with(tipContentFadeIn);

        AnimatorSet as = new AnimatorSet();
        as.play(qrButtonUp).with(qrButtonFadeIn).with(nfcButtonFadeIn).with(nfcButtonUp)
                .with(aboutButtonFadeIn).with(aboutButtonUp).with(tipAnims).after(splashIconSpring);

        as.start();

    }

    @Override
    public void onClick(View v) {
        handleButtonClickTransition(v.getId());
    }

    private void handleButtonClickTransition(final int clickedButtonID) {
        ArrayList<Animator> animators = new ArrayList<>();

        if (clickedButtonID == R.id.splash_screen_about_button) {
            aboutButton.setOnClickListener(null); //to avoid bugs related to multiple quick clicks on that button
            ObjectAnimator nfcButtonFlip = AnimationHandler.generateAnimation(nfcButton, "rotationY", 0, 90, 500, 0, new AnticipateInterpolator());
            ObjectAnimator qrButtonFlip = AnimationHandler.generateAnimation(qrButton, "rotationY", 0, 90, 600, 120, new AnticipateInterpolator());
            ObjectAnimator nfcTextFlip = AnimationHandler.generateAnimation(nfcDescription, "rotationY", 270, 360, 500, 500, new OvershootInterpolator());
            ObjectAnimator nfcTextFadeIn = AnimationHandler.generateAlphaAnimation(nfcDescription, 0, 1, 10, 500, null);
            ObjectAnimator qrTextFlip = AnimationHandler.generateAnimation(qrDescription, "rotationY", 270, 360, 500, 620, new OvershootInterpolator());
            ObjectAnimator qrTextFadeIn = AnimationHandler.generateAlphaAnimation(qrDescription, 0, 1, 10, 620, null);
            animators.add(nfcButtonFlip);
            animators.add(nfcTextFlip);
            animators.add(nfcTextFadeIn);
            animators.add(qrButtonFlip);
            animators.add(qrTextFlip);
            animators.add(qrTextFadeIn);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator nfcTextFlip = AnimationHandler.generateAnimation(nfcDescription, "rotationY", 0, 90, 500, 0, new AnticipateInterpolator());
                    ObjectAnimator nfcTextFadeOut = AnimationHandler.generateAlphaAnimation(nfcDescription, 1, 0, 1500, 0, null);
                    ObjectAnimator qrTextFlip = AnimationHandler.generateAnimation(qrDescription, "rotationY", 0, 90, 500, 120, new AnticipateInterpolator());
                    ObjectAnimator qrTextFadeOut = AnimationHandler.generateAlphaAnimation(qrDescription, 1, 0, 1500, 120, null);

                    ObjectAnimator nfcButtonFlip = AnimationHandler.generateAnimation(nfcButton, "rotationY", 270, 360, 500, 500, new OvershootInterpolator());
                    final ObjectAnimator qrButtonFlip = AnimationHandler.generateAnimation(qrButton, "rotationY", 270, 360, 500, 620, new OvershootInterpolator());
                    qrButtonFlip.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            aboutButton.setOnClickListener(AlgoryhmsMainFragment.this);
                            qrButtonFlip.removeAllListeners();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    AnimatorSet as = new AnimatorSet();
                    as.playTogether(nfcTextFlip, nfcTextFadeOut, qrTextFlip, qrTextFadeOut, nfcButtonFlip, qrButtonFlip);
                    as.start();
                }
            }, 1500);

        }

        //if we clicked one of the scan buttons (NFC or QR)
        else {
            animators.add(AnimationHandler.generatePopOutAnimation(splashIcon, 1, 0, 500, 0, new AnticipateInterpolator()));
            animators.add(AnimationHandler.generateAlphaAnimation(backgroundGrid, 0.3f, 0, 1200, 0, null));
            if (clickedButtonID == R.id.splash_screen_qr_scan_button) {
                int toMove = calcHorizontalCenterPoint(qrButton);
                animators.add(AnimationHandler.generateXAnimation(qrButton, 0, toMove, 500, 360, new DecelerateInterpolator()));
                animators.add(AnimationHandler.generatePopOutAnimation(nfcButton, 1, 0, 500, 120, new AnticipateInterpolator()));
                animators.add(AnimationHandler.generatePopOutAnimation(aboutButton, 1, 0, 500, 240, new AnticipateInterpolator()));
            } else if (clickedButtonID == R.id.splash_screen_nfc_scan_button) {
                animators.add(AnimationHandler.generatePopOutAnimation(qrButton, 1, 0, 500, 120, new AnticipateInterpolator()));
                animators.add(AnimationHandler.generatePopOutAnimation(aboutButton, 1, 0, 500, 240, new AnticipateInterpolator()));
            }

            animators.add(AnimationHandler.generateAlphaAnimation(tipTitle, 1, 0, 500, 360, null));
            animators.add(AnimationHandler.generateAlphaAnimation(tipContent, 0.4f, 0, 500, 480, null));

            View clickedButton = root.findViewById(clickedButtonID);
            float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(300);
            ObjectAnimator clickedButtonYTransition = AnimationHandler.generateYAnimation(clickedButton, 0, yOffset, 700, 1200, new AnticipateInterpolator());
            clickedButtonYTransition.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String fragTag;
                            AlgorhythmsFragment frag;

                            if (clickedButtonID == R.id.splash_screen_nfc_scan_button) {
                                frag = new NFCScanFragment();
                                fragTag = NFCScanFragment.TAG;
                            } else {
                                frag = new QRScanFragment();
                                fragTag = QRScanFragment.TAG;
                            }


                            if (activity != null) {
                                activity.launchFragment(frag, fragTag, null, null);
                            }
                        }
                    }, 500);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animators.add(clickedButtonYTransition);
        }

        AnimatorSet as = new AnimatorSet();
        as.playTogether(animators);
        as.setStartDelay(50);
        as.start();
    }

    private int calcHorizontalCenterPoint(View v) {
        int[] viewLocation = new int[2];
        v.getLocationOnScreen(viewLocation);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidthMid = (size.x / 2);
        int viewWidthMid = (v.getWidth() / 2);
        int xOffset = Math.abs(viewLocation[0] - screenWidthMid);
        return Math.abs(xOffset - viewWidthMid);
    }

    @Override
    public boolean handleBackPress() {
        //we want to exit the application upon clicking back in main screen
        //thus we return false to the activity, so that it'll handle the backpress
        return false;
    }

    @Override
    public boolean handleNfcScan(String result) {
        if (activity == null) {
            return true;
        }

        //the scan result is defected or non-relevant, in such case - simply ignore it
        if (result == null || result.length() == 0 ||
                !ResourceResolver.isValidGameId(result)) {
            return true;
        }

        //otherwise - lets launch the relevant game intro screen
        else{
            activity.launchFragment(GameIntroFragment.newInstance(result),
                    GameIntroFragment.TAG, null, null);
        }


        return true;
    }
}

