package com.Dialogues;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.algorythmsteam.algorythms.AlgorhythmsFragment;
import com.algorythmsteam.algorythms.AlgoryhmsMainActivity;
import com.handlers.AnimationHandler;

public abstract class AlgorhythmsDialogFragment extends DialogFragment {
    public interface DialogClosedCallback {
        void onDialogClosed();
    }

    public static final String TAG = "AlgorhythmsDialog";
    private static final String BACKGROUND_VIEW_ID = "background_view_id";

    protected View backgroundView;
    protected int backgroundViewId;
    private DialogClosedCallback callback;

    public AlgorhythmsDialogFragment() {
        //required empty constructor
    }

    public abstract void handleNfcScan(String res);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            backgroundViewId = getArguments().getInt(BACKGROUND_VIEW_ID);
            backgroundView = getActivity().findViewById(backgroundViewId);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            AlgoryhmsMainActivity act = (AlgoryhmsMainActivity) getActivity();
            AlgorhythmsFragment frag = act.getCurrFrag();
            if (frag != null) {
                callback = (DialogClosedCallback) frag;
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "fragment should implement DialogClosedCallback interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public void setBackgroundView() {
        if (backgroundView != null) {
            backgroundView.setAlpha(0);
            backgroundView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.00f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);

        if (backgroundView != null && backgroundView.getAlpha() == 0) {
            ObjectAnimator backgroundFadeIn = AnimationHandler.generateAlphaAnimation(backgroundView, 0, 0.95f, 400, 30, new AccelerateDecelerateInterpolator());
            backgroundFadeIn.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (backgroundView != null) {
            ObjectAnimator backgroundFadeOut = AnimationHandler.generateAlphaAnimation(backgroundView, 0.95f, 0, 400, 0, new AccelerateDecelerateInterpolator());
            backgroundFadeOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    backgroundView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            backgroundFadeOut.start();
        }

        if (callback != null) {
            callback.onDialogClosed();
        }
    }
}
