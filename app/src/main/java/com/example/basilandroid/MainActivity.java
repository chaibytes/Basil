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
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 1337;
    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.CAMERA"};

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCamera();

        //TODO: Show a Basil Photo animation
        displaySplashAnimation();
    }

    private void displaySplashAnimation() {
        // Custom animation on image
        final ImageView myView = findViewById(R.id.basil_leaf_image);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(myView, "scaleX",  0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(myView, "scaleY",  0f, 1f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(myView, "alpha", 0f, 1f);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.playTogether(scaleX, scaleY, fadeIn);
        mAnimationSet.setDuration(4000);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //myView.setVisibility(View.GONE);
            }
        });
        mAnimationSet.start();
    }

    private void openCamera() {
        if (checkCameraHardware(this)) {
            requestPermissions();
            // Open camera
            // Create an instance of Camera
            mCamera = getCameraInstance();

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            addOverlay();
        }
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
}
