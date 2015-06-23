package com.handlers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.AnimationSet;

import java.util.ArrayList;

public class AnimationHandler {

    /**
     * Generates a single animation that runs on the given view
     * @param v - the view to run animation on
     * @param property - the property whose values are changed for animating the view
     * @param firstVal - initial property value
     * @param secondVal - changed propery value
     * @param duration - animation's duration
     * @param startDelay - animation's start delay
     * @param interpolator - animations Timeinterpolator, provide NULL for default interpolator
     * @return The required ObjectAnimator
     */
    public static ObjectAnimator generateAnimation(View v, String property, float firstVal, float secondVal,
                                                   int duration, int startDelay, TimeInterpolator interpolator) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, property, firstVal, secondVal);
        anim.setDuration(duration);
        anim.setStartDelay(startDelay);
        if (interpolator != null) {
            anim.setInterpolator(interpolator);
        }

        return anim;
    }

    /**
     * Generate multiple animations with the same effect and properties on multiple views
     * @return animatorSet which plays all animations together upon running it
     */
    public static AnimatorSet generateMultipleAnims(ArrayList<View> viewsList, String property, float firstVal, float secondVal,
                                                     int duration, int startDelay, TimeInterpolator interpolator) {
        AnimatorSet as = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<Animator>();
        for (View v : viewsList) {
            animators.add(generateAnimation(v, property, firstVal, secondVal, duration, startDelay, interpolator));
        }

        as.playTogether(animators);
        return as;
    }

    public static ObjectAnimator generateXAnimation(View v, float firstVal, float secondVal,
                                                    int duration, int startDelay, TimeInterpolator interpolator) {
        return generateAnimation(v, "translationX", firstVal, secondVal, duration, startDelay, interpolator);
    }

    public static ObjectAnimator generateYAnimation(View v, float firstVal, float secondVal,
                                                    int duration, int startDelay, TimeInterpolator interpolator) {
        return generateAnimation(v, "translationY", firstVal, secondVal, duration, startDelay, interpolator);
    }

    public static ObjectAnimator generateAlphaAnimation(View v, float firstVal, float secondVal,
                                                        int duration, int startDelay, TimeInterpolator interpolator) {
        return generateAnimation(v, "alpha", firstVal, secondVal, duration, startDelay, interpolator);
    }

    public static ObjectAnimator generateXScaleAnimation(View v, float firstVal, float secondVal,
                                                        int duration, int startDelay, TimeInterpolator interpolator) {
        return generateAnimation(v, "scaleX", firstVal, secondVal, duration, startDelay, interpolator);
    }

    public static ObjectAnimator generateYScaleAnimation(View v, float firstVal, float secondVal,
                                                         int duration, int startDelay, TimeInterpolator interpolator) {
        return generateAnimation(v, "scaleY", firstVal, secondVal, duration, startDelay, interpolator);
    }

    public static AnimatorSet generatePopInAnimation(View v, float firstVal, float secondVal, int duration, int startDelay, TimeInterpolator interpolator) {
        ObjectAnimator popX = generateXScaleAnimation(v, firstVal, secondVal, duration, 0, interpolator);
        ObjectAnimator popY = generateYScaleAnimation(v, firstVal, secondVal, duration, 0, interpolator);
        ObjectAnimator fadeIn = generateAlphaAnimation(v, 0, 1, 30, startDelay, null);
        AnimatorSet as = new AnimatorSet();
        as.play(fadeIn).with(popX).with(popY);
        return as;
    }

    public static AnimatorSet generatePopOutAnimation(View v, float firstVal, float secondVal, int duration, int startDelay, TimeInterpolator interpolator) {
        ObjectAnimator popX = generateXScaleAnimation(v, firstVal, secondVal, duration, 0, interpolator);
        ObjectAnimator popY = generateYScaleAnimation(v, firstVal, secondVal, duration, 0, interpolator);
        ObjectAnimator fadeOut = generateAlphaAnimation(v, 1, 0, duration, startDelay, null);
        AnimatorSet as = new AnimatorSet();
        as.play(fadeOut).with(popX).with(popY);
        return as;
    }

    public static AnimatorSet generateMultipleYAnims(ArrayList<View> viewsList, float firstVal, float secondVal,
                                                         int duration, int startDelay, TimeInterpolator interpolator) {
        return generateMultipleAnims(viewsList, "translationY", firstVal, secondVal, duration, startDelay, interpolator);
    }

    public static AnimatorSet generateMultipleAlphaAnims(ArrayList<View> viewsList, float firstVal, float secondVal,
                                                         int duration, int startDelay, TimeInterpolator interpolator) {
        return generateMultipleAnims(viewsList, "alpha", firstVal, secondVal, duration, startDelay, interpolator);
    }

    public static AnimatorSet generateMultiplePopInAnims(ArrayList<View> viewsList, float firstVal, float secondVal,
                                                         int duration, int startDelay, TimeInterpolator interpolator) {
        ArrayList<Animator> popAnims = new ArrayList<Animator>();
        for (View v : viewsList) {
            popAnims.add(generatePopInAnimation(v, firstVal, secondVal, duration, startDelay, interpolator));
        }

        AnimatorSet as = new AnimatorSet();
        as.playTogether(popAnims);
        return as;
    }

    public static AnimatorSet generateMultipleAnimsOnObject(View v, ArrayList<String> props, float firstVal, float secondVal, int duration, int startDelay, TimeInterpolator interpolator) {
        ArrayList<Animator> animators = new ArrayList<Animator>();
        AnimatorSet as = null;
        for (String prop : props) {
            ObjectAnimator objAnimator = generateAnimation(v, prop, firstVal, secondVal, duration, 0, interpolator);
            animators.add(objAnimator);
        }
        if (animators.size() > 0) {
            animators.get(0).setStartDelay(startDelay);
            as = new AnimatorSet();
            as.playTogether(animators);
        }

        return as;
    }

}
