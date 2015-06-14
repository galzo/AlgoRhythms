package com.algorythmsteam.algorythms;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.ImageView;

import com.handlers.AnimationHandler;

public class NFCScanFragment extends Fragment {
    public static final String TAG = "NFCScanFragment";

    private View root;
    private TextView nfcDescription;
    private ImageView nfcTitle;

    public NFCScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onStart() {
        super.onStart();
        initAnimations();
    }

    private void initAnimations() {
        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(-300);
        ObjectAnimator titleDragDown = AnimationHandler.generateYAnimation(nfcTitle, yOffset, 0, 750, 40, new OvershootInterpolator());
        ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(nfcTitle, 0, 1, 30, 0, null);
        AnimatorSet descriptionPopIn = AnimationHandler.generatePopInAnimation(nfcDescription, 0, 1, 600, 30, new OvershootInterpolator());
        AnimatorSet as = new AnimatorSet();
        as.play(titleDragDown).with(titleFadeIn).before(descriptionPopIn);
        as.start();
    }

    //TODO: handle onAttach and onDetach later on
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }
}
