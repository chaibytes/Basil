package com.example.basilandroid.animation;

import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Animation Framework class to display smart string texts.
 *
 * Sample usage:
 *     onCreate() { startAnimation(); }
 *
 *   private void startAnimation() {
 *      TextView textView = findViewById(R.id.textView);
 *       mAnimationFramework = new SmartStringAnimationFramework(textView, "text");
 *        mAnimationFramework.startAnimators();
 *   }
 * onResume() { mAnimationFramework.resumeAnimators(); }
 * onPause() { mAnimationFramework.pauseAnimators(); }
 *
 */

public class StringAnimationFramework {
    private StringAnimator mSmartStringAnimator;

    public StringAnimationFramework(TextView animationView) {
        mSmartStringAnimator = new StringAnimator(animationView, getListOfDisplayStrings());
    }

    public void startAnimators() {
        mSmartStringAnimator.startAnimation();
    }

    public void cancelAnimators() {
        mSmartStringAnimator.stopAnimation();
    }

    public void resumeAnimators() {
        mSmartStringAnimator.resumeAnimation();
    }

    public void pauseAnimators() {
        mSmartStringAnimator.pauseAnimation();
    }

    private List<String> getListOfDisplayStrings() {
        // Get the list of strings to display
        List<String> stringsList = new LinkedList<>();

        stringsList.add("Welcome to Basil");
        stringsList.add("Point to a produce");
        stringsList.add("Click to start");

        return stringsList;
    }
}

