package com.algorythmsteam.algorythms;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handlers.AnimationHandler;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ImageView;


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
        scanButton.setOnClickListener(AlgoryhmsMainFragment.this);
        buzzerButton.setOnClickListener(AlgoryhmsMainFragment.this);
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
    private ImageButton scanButton, buzzerButton, aboutButton;

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
        scanButton = (ImageButton) root.findViewById(R.id.splash_screen_scan_button);
        buzzerButton = (ImageButton) root.findViewById(R.id.splash_screen_buzzer_button);
        aboutButton = (ImageButton) root.findViewById(R.id.splash_screen_about_button);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initAnims();
    }

    public void initAnims() {
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
        ObjectAnimator scanButtonUp = AnimationHandler.generateYAnimation(scanButton, yOffset, 0, 1400, 0, new AnticipateOvershootInterpolator());
        ObjectAnimator scanButtonFadeIn = AnimationHandler.generateAlphaAnimation(scanButton, 0, 1, 10, 0, null);

        ObjectAnimator buzzerButtonUp = AnimationHandler.generateYAnimation(buzzerButton, yOffset, 0, 1400, 120, new AnticipateOvershootInterpolator());
        ObjectAnimator buzzerButtonFadeIn = AnimationHandler.generateAlphaAnimation(buzzerButton, 0, 1, 10, 120, null);

        ObjectAnimator aboutButtonUp = AnimationHandler.generateYAnimation(aboutButton, yOffset, 0, 1400, 240, new AnticipateOvershootInterpolator());
        ObjectAnimator aboutButtonFadeIn = AnimationHandler.generateAlphaAnimation(aboutButton, 0, 1, 10, 240, null);
        aboutButtonUp.addListener(this);

        AnimatorSet as = new AnimatorSet();
        as.play(scanButtonUp).with(scanButtonFadeIn).with(buzzerButtonFadeIn).with(buzzerButtonUp)
                .with(aboutButtonFadeIn).with(aboutButtonUp).after(splashIconSpring);
        as.start();
    }

    private void handleButtonClickTransition(int clickedButtonId) {
        switch (clickedButtonId) {
            case R.id.splash_screen_scan_button:
                if (activityCallback != null) {
                    activityCallback.initiateScan();
                }

                break;

            case R.id.splash_screen_buzzer_button:
                if (activityCallback != null) {
                    activityCallback.openBuzzer();
                }
                break;

            case R.id.splash_screen_about_button:
                //TODO: not going to get here, add about button functionality to fix
                Toast.makeText(getActivity(), "coming soon", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    private void handleTransitionAnim(final int clickedButtonId) {
        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(600);
        ObjectAnimator splashIconUp = AnimationHandler.generateYAnimation(splashIcon, 0, -yOffset, 1400, 0, new AccelerateDecelerateInterpolator());
        ObjectAnimator aboutButtonAnim = AnimationHandler.generateYAnimation(aboutButton, 0, yOffset, 1400, 0, new AccelerateDecelerateInterpolator());
        aboutButtonAnim.addListener(new Animator.AnimatorListener() {


            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                handleButtonClickTransition(clickedButtonId);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator nonClickedButtonAnim;
        if (clickedButtonId == R.id.splash_screen_scan_button) {
            nonClickedButtonAnim = AnimationHandler.generateYAnimation(buzzerButton, 0, yOffset, 1400, 0, new AccelerateDecelerateInterpolator());
        } else {
            nonClickedButtonAnim = AnimationHandler.generateYAnimation(scanButton, 0, -yOffset, 1400, 0, new AccelerateDecelerateInterpolator());
        }

        AnimatorSet as = new AnimatorSet();
        as.play(splashIconUp).with(aboutButtonAnim).with(nonClickedButtonAnim);
        as.start();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.splash_screen_about_button) {
            Toast.makeText(getActivity(), "coming soon", Toast.LENGTH_LONG).show();
        } else {
            handleTransitionAnim(v.getId());
        }
    }
}
