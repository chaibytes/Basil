package com.example.basilandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.basilandroid.animation.StringAnimationFramework;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 1337;
    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.CAMERA"};

    private Camera mCamera;
    private CameraPreview mPreview;

    private boolean iscameraPaused;
    private boolean isAnimationBeingDisplayed;

    private StringAnimationFramework mAnimationFramework;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        openCamera();

        //addOverlay();

        displaySplashAnimation();
    }

    private void displaySplashAnimation() {
        // Custom animation on image
        final ImageView myView = findViewById(R.id.basil_leaf_image);

        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUserGuidance();
                View produce_artichoke = findViewById(R.id.produce_artichoke);
                produce_artichoke.setVisibility(View.VISIBLE);

            }
        });

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(myView, "scaleX",  0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(myView, "scaleY",  0f, 1f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(myView, "alpha", 0f, 1f);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.playTogether(scaleX, scaleY, fadeIn);
        mAnimationSet.setDuration(2000);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showUserGuidance();
            }
        });
        mAnimationSet.start();
    }

    private void showUserGuidance() {
        TextView view = findViewById(R.id.user_guidance_text);
        mAnimationFramework = new StringAnimationFramework(view);
        startStringAnimation();
        isAnimationBeingDisplayed = true;
    }

    private void hideUserGuidance() {
        if (mAnimationFramework != null) {
            stopStringAnimation();
        }
        isAnimationBeingDisplayed = false;
    }

    private void startStringAnimation() {
        mAnimationFramework.resumeAnimators();
    }

    private void stopStringAnimation() {
        mAnimationFramework.pauseAnimators();
    }


    private void openCamera() {
        if (checkCameraHardware(this)) {
            requestPermissions();
            // Open camera
            resumeCamera();
        }
    }

    private void resumeCamera() {
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        iscameraPaused = false;
    }

    private void addOverlay() {
        final Rect rect = new Rect();

        final View cameraView = findViewById(R.id.camera_preview);
        cameraView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cameraView.getLocalVisibleRect(rect);
                RectF rectF = new RectF(rect.left + 100, rect.top + 100,
                        rect.right - 100, rect.bottom - 100);
                OverlayImageView overlayImage = findViewById(R.id.overlay);
                overlayImage.setRect(rectF);
            }
        });

    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void requestPermissions() {
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Result success
            } else if (resultCode == RESULT_CANCELED){
                // Cancelled
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!iscameraPaused) {
            closeCamera();
            iscameraPaused = true;
        }

        if (mAnimationFramework != null) {
            stopStringAnimation();
        }
    }

    public void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCamera == null) {
            resumeCamera();
        }

        if (mAnimationFramework != null && isAnimationBeingDisplayed) {
            startStringAnimation();
        }
    }

}
