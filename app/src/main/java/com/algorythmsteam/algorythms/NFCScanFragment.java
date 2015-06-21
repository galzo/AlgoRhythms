package com.algorythmsteam.algorythms;

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
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.handlers.AnimationHandler;
import com.handlers.ResourceResolver;

public class NFCScanFragment extends AlgorhythmsFragment {
    public static final String TAG = "NFCScanFragment";

    private View root;
    private TextView nfcDescription;
    private ImageView nfcTitle;
    private AlgoryhmsMainActivity activity;
    private boolean isAnimInit;

    public NFCScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            activity = (AlgoryhmsMainActivity) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "activity should be of type AlgoryhmsMainActivity");
        }

        isAnimInit = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_nfc_scan, container, false);
        nfcTitle = (ImageView) root.findViewById(R.id.nfc_scan_screen_title);
        nfcDescription = (TextView) root.findViewById(R.id.nfc_scan_screen_description);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        nfcDescription.setTypeface(tf);
        return root;
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

        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(-300);
        ObjectAnimator titleDragDown = AnimationHandler.generateYAnimation(nfcTitle, yOffset, 0, 750, 40, new OvershootInterpolator());
        ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(nfcTitle, 0, 1, 30, 0, null);
        AnimatorSet descriptionPopIn = AnimationHandler.generatePopInAnimation(nfcDescription, 0, 1, 600, 30, new OvershootInterpolator());
        AnimatorSet as = new AnimatorSet();
        as.play(titleDragDown).with(titleFadeIn).before(descriptionPopIn);
        as.start();
    }

    @Override
    public boolean handleBackPress() {
        if (activity != null) {
            AlgoryhmsMainFragment frag = new AlgoryhmsMainFragment();
            activity.launchFragment(frag, AlgoryhmsMainFragment.TAG, R.anim.enter_reverse, R.anim.exit_reverse);
            return true;
        }

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
            Toast.makeText(getActivity(), "Please scan a game-type card", Toast.LENGTH_LONG).show();
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
