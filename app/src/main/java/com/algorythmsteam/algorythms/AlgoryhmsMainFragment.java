package com.algorythmsteam.algorythms;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;



/**
 * A placeholder fragment containing a simple view.
 */
public class AlgoryhmsMainFragment extends Fragment
        implements OnClickListener, Animation.AnimationListener {
    public static final String TAG = "AlgoryhmsMainFragment";


    public interface QRCodeScanHandler {
        void initiateScan();
    }

    private View root;
    private boolean isAnimInit;
    private TextView formatTxt, contentTxt;
    private QRCodeScanHandler scannerActivity;
    private ImageView splashIcon, backgroundGrid;
    private ImageButton scanButton;

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
            scannerActivity = (QRCodeScanHandler) getActivity();
        } catch(ClassCastException e) {
            Log.e(TAG, "activity does not support QRCodeScanHandler interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_algoryhms_main, container, false);
        formatTxt = (TextView) root.findViewById(R.id.scan_format);
        contentTxt = (TextView) root.findViewById(R.id.scan_content);
        splashIcon = (ImageView) root.findViewById(R.id.splash_screen_app_icon);
        backgroundGrid = (ImageView) root.findViewById(R.id.splash_screen_background_grid);
        scanButton = (ImageButton) root.findViewById(R.id.splash_screen_scan_button);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initAnimations();
    }

    private void initAnimations() {
        //if anim was already initialized - do not replay it
        if (isAnimInit) {
            return;
        }

        Animation splashAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        Animation zoominAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
        splashIcon.setAnimation(splashAnim);
        backgroundGrid.setAnimation(zoominAnim);
        splashAnim.setAnimationListener(this);
        splashAnim.start();
        zoominAnim.start();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        ObjectAnimator logoUp = ObjectAnimator.ofFloat(splashIcon, "translationY", 0, -((AlgoryhmsMainActivity)getActivity()).convertDpToPixel(120));
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(scanButton, "alpha", 0, 1);
        logoUp.setDuration(1000);
        logoUp.setInterpolator(new AnticipateOvershootInterpolator());
        fadeIn.setDuration(300);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        final AnimatorSet as = new AnimatorSet();
        as.play(fadeIn).after(logoUp);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            private Fragment frag;

            @Override
            public void run() {
                as.start();
                setIsAnimInit(true);
                scanButton.setOnClickListener((AlgoryhmsMainFragment) frag);
            }

            public Runnable init(Fragment frag) {
                this.frag = frag;
                return this;
            }
        }.init(this), 500);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.splash_screen_scan_button:
                if (scannerActivity != null) {
                    scannerActivity.initiateScan();
                }

                break;

            default:
                break;
        }
    }
}
