package com.algorythmsteam.algorythms;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.handlers.AnimationHandler;


public class QRScanFragment extends AlgorhythmsFragment {
    public interface QRScanCallback {
        void launchQRScanner();
    }

    public static final String TAG = "QRScanFragment";
    private TextView qrDescription, qrHintText;
    private ImageView qrTitle, qrHintImage;
    private View root;
    private AlgoryhmsMainActivity activity;
    private QRScanCallback scanCallback;

    public QRScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            activity = (AlgoryhmsMainActivity) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "Activity should be of type AlgoryhmsMainActivity");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_qr_scan, container, false);
        qrDescription = (TextView) root.findViewById(R.id.qr_scan_screen_description);
        qrHintText = (TextView) root.findViewById(R.id.qr_scan_screen_hint_txt);
        qrTitle = (ImageView) root.findViewById(R.id.qr_scan_screen_title);
        qrHintImage = (ImageView) root.findViewById(R.id.qr_scan_screen_hint_pic);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        qrHintText.setTypeface(tf);
        qrDescription.setTypeface(tf);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initAnimations();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scanCallback != null) {
                    scanCallback.launchQRScanner();
                }
            }
        }, 4400);
    }

    private void initAnimations() {
        float yOffset = ((AlgoryhmsMainActivity) getActivity()).convertDpToPixel(-300);
        ObjectAnimator titleDragDown = AnimationHandler.generateYAnimation(qrTitle, yOffset, 0, 750, 40, new OvershootInterpolator());
        ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(qrTitle, 0, 1, 30, 0, null);
        AnimatorSet descriptionPopIn = AnimationHandler.generatePopInAnimation(qrDescription, 0, 1, 600, 0, new OvershootInterpolator());
        AnimatorSet hintTextPopIn = AnimationHandler.generatePopInAnimation(qrHintText, 0, 1, 600, 150, new OvershootInterpolator());
        AnimatorSet hintImagePopIn = AnimationHandler.generatePopInAnimation(qrHintImage, 0, 1, 600, 300, new OvershootInterpolator());
        AnimatorSet popInAnims = new AnimatorSet();
        popInAnims.play(descriptionPopIn).with(hintTextPopIn).with(hintImagePopIn);
        popInAnims.setStartDelay(30);
        AnimatorSet as = new AnimatorSet();
        as.play(titleDragDown).with(titleFadeIn).before(popInAnims);
        as.start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            scanCallback = (QRScanCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement QRScanCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        scanCallback = null;
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
    public boolean handleNfcScan(String res) {
        //QRScanFragment should ignore nfc scans
        return true;
    }
}
