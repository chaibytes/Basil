package com.example.basilandroid.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Animation Class for Strings animation
 * Strings keep rotating until the animation is cancelled.
 * 1. Fade in for 200ms
 * 2. Stay for 2 secs
 * 3. Fade out for 200ms
 * 4. Stay hidden for 1500ms
 * 5. Text change
 * 6. Repeat (Fade in for 200ms etc.)
 */

public class StringAnimator {
    private static final float START_ALPHA = 0.0f;
    private static final float END_APLHA = 1.0f;

    private static final long ANIM_DISPLAY_DURATION = 2000L;
    private static final long ANIM_FADE_OR_OUT_DURATION = 200L;
    private static final long ANIM_DISPLAY_LAG_DURATION = 1500L;

    private TextView mAnimationView;
    private List<String> mSmartStringList;
    private AnimatorSet mAnimatorSet;
    private int mCurrentStringIndex;
    private boolean mIsAnimationPaused;

    public StringAnimator(TextView view, List<String> smartStringList) {
        mAnimationView = view;
        mSmartStringList = smartStringList;
        mCurrentStringIndex = 0;
        mAnimatorSet = new AnimatorSet();

        // Special variable to check if the animation has been paused and will be resumed wherein
        // it should be reset from beginning
        mIsAnimationPaused = false;
        initializeAlphaAnimation();
    }

    private void initializeAlphaAnimation() {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mAnimationView,
                "alpha", START_ALPHA, END_APLHA);
        fadeIn.setDuration(ANIM_FADE_OR_OUT_DURATION);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mAnimationView,
                "alpha", END_APLHA, START_ALPHA);
        fadeOut.setDuration(ANIM_FADE_OR_OUT_DURATION);

        mAnimatorSet.play(fadeOut).after(ANIM_DISPLAY_DURATION).after(fadeIn)
                .after(ANIM_DISPLAY_LAG_DURATION);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                // Start with the next string in the array
                setText();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!mIsAnimationPaused) {
                    mAnimatorSet.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void resetState() {
        resetCounter();
        mAnimationView.setText(mSmartStringList.get(0));
    }

    private void resetCounter() {
        mCurrentStringIndex = 0;
    }

    private void setText() {
        mAnimationView.setText(getNextSmartString());
    }

    private String getNextSmartString() {
        String nextStringToDisplay = mSmartStringList.get(mCurrentStringIndex);
        mCurrentStringIndex = (mCurrentStringIndex + 1) % mSmartStringList.size();
        return nextStringToDisplay;
    }

    public void startAnimation() {
        mAnimatorSet.start();
    }

    public void stopAnimation() {
        pauseAnimation();
    }

    public void resumeAnimation() {
        mIsAnimationPaused = false;
        if (!mAnimatorSet.isRunning()) {
            resetState();
            mAnimationView.setVisibility(View.VISIBLE);
            mAnimatorSet.start();
        }
    }

    public void pauseAnimation() {
        mIsAnimationPaused = true;
        mAnimationView.setVisibility(View.INVISIBLE);
        mAnimatorSet.end();
    }
}




