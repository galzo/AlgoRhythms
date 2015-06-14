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

import android.app.Activity;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class AlgoryhmsMainFragment extends Fragment
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
        isAnimInit = true;
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public interface MainActivityCallback {
        void openBuzzer();
        void initiateScan();
    }

    private View root;
    private boolean isAnimInit;
    private MainActivityCallback activityCallback;
    private ImageView splashIcon, backgroundGrid;
    private ImageButton nfcButton, qrButton, aboutButton;
    private TextView nfcDescription, qrDescription;

    public AlgoryhmsMainFragment() {
        super();
        this.isAnimInit = false;
    }

    public void setIsAnimInit(boolean initAnim) {
        this.isAnimInit = initAnim;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (MainActivityCallback) getActivity();
        } catch(ClassCastException e) {
            Log.e(TAG, "activity does not support QRCodeScanHandler interface");
        }
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

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        nfcDescription.setTypeface(tf);
        qrDescription.setTypeface(tf);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initSplashAnims();
    }

    public void initSplashAnims() {
        //if anim was already initialized - do not replay it
        if (isAnimInit) {
            return;
        }

        //set animation background
        Animation zoominAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
        backgroundGrid.setAnimation(zoominAnim);
        zoominAnim.start();

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

        AnimatorSet as = new AnimatorSet();
        as.play(qrButtonUp).with(qrButtonFadeIn).with(nfcButtonFadeIn).with(nfcButtonUp)
                .with(aboutButtonFadeIn).with(aboutButtonUp).after(splashIconSpring);
        as.start();
    }

//    private void handleButtonClickTransition(int clickedButtonId) {
//        switch (clickedButtonId) {
//            case R.id.splash_screen_nfc_scan_button:
//                if (activityCallback != null) {
//                    activityCallback.initiateScan();
//                }
//
//                break;
//
//            case R.id.splash_screen_qr_scan_button:
//                if (activityCallback != null) {
//                    activityCallback.openBuzzer();
//                }
//                break;
//
//            case R.id.splash_screen_about_button:
//                //TODO: not going to get here, add about button functionality to fix
//                Toast.makeText(getActivity(), "coming soon", Toast.LENGTH_LONG).show();
//                break;
//
//            default:
//                break;
//        }
//    }


//    private void handleTransitionAnim(final int clickedButtonId) {
//        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(600);
//        ObjectAnimator splashIconUp = AnimationHandler.generateYAnimation(splashIcon, 0, -yOffset, 1400, 0, new AccelerateDecelerateInterpolator());
//        ObjectAnimator aboutButtonAnim = AnimationHandler.generateYAnimation(aboutButton, 0, yOffset, 1400, 0, new AccelerateDecelerateInterpolator());
//        aboutButtonAnim.addListener(new Animator.AnimatorListener() {
//
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                handleButtonClickTransition(clickedButtonId);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//
//        ObjectAnimator nonClickedButtonAnim;
//        if (clickedButtonId == R.id.splash_screen_nfc_scan_button) {
//            nonClickedButtonAnim = AnimationHandler.generateYAnimation(qrButton, 0, yOffset, 1400, 0, new AccelerateDecelerateInterpolator());
//        } else {
//            nonClickedButtonAnim = AnimationHandler.generateYAnimation(nfcButton, 0, -yOffset, 1400, 0, new AccelerateDecelerateInterpolator());
//        }
//
//        AnimatorSet as = new AnimatorSet();
//        as.play(splashIconUp).with(aboutButtonAnim).with(nonClickedButtonAnim);
//        as.start();
//    }

    @Override
    public void onClick(View v) {
        handleButtonClickAnimations(v.getId());
    }

    private void handleButtonClickAnimations(final int clickedButtonID) {
        ArrayList<Animator> animators = new ArrayList<>();
        if (clickedButtonID == R.id.splash_screen_about_button) {
            if (qrDescription.getAlpha() != 0) return;

            animators.add(AnimationHandler.generatePopInAnimation(qrDescription, 0, 1, 500, 0, new OvershootInterpolator()));
            animators.add(AnimationHandler.generatePopInAnimation(nfcDescription, 0, 1, 500, 120, new OvershootInterpolator()));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Animator> animators = new ArrayList<>();
                    animators.add(AnimationHandler.generatePopOutAnimation(qrDescription, 1, 0, 500, 0, new AnticipateInterpolator()));
                    animators.add(AnimationHandler.generatePopOutAnimation(nfcDescription, 1, 0, 500, 120, new AnticipateInterpolator()));
                    AnimatorSet as = new AnimatorSet();
                    as.playTogether(animators);
                    as.start();
                }
            }, 1500);
        }

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

            View clickedButton = root.findViewById(clickedButtonID);
            float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(300);
            ObjectAnimator clickedButtonYTransition = AnimationHandler.generateYAnimation(clickedButton, 0, yOffset, 700, 1200, new AnticipateInterpolator());
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


}
