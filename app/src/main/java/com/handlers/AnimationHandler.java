package com.handlers;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;

public class AnimationHandler {

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

    public static ObjectAnimator generateYAnimation(View v, float firstVal, float secondVal,
                                                    int duration, int startDelay, TimeInterpolator interpolator) {
        return generateAnimation(v, "translationY", firstVal, secondVal, duration, startDelay, interpolator);
    }

    public static ObjectAnimator generateAlphaAnimation(View v, float firstVal, float secondVal,
                                                        int duration, int startDelay, TimeInterpolator interpolator) {
        return generateAnimation(v, "alpha", firstVal, secondVal, duration, startDelay, interpolator);
    }
}
