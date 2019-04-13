// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.basilandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.basilandroid.animation.StringAnimationFramework;
import com.google.android.gms.common.annotation.KeepName;
import com.google.firebase.ml.common.FirebaseMLException;

import com.google.firebase.samples.apps.mlkit.common.CameraSource;
import com.google.firebase.samples.apps.mlkit.common.CameraSourcePreview;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.imagelabeling.ImageLabelingProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Demo app showing the various features of ML Kit for Firebase. This class is used to
 * set up continuous frame processing on frames from a camera source. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback {
  private static final String FACE_DETECTION = "Face Detection";
  private static final String TEXT_DETECTION = "Text Detection";
  private static final String BARCODE_DETECTION = "Barcode Detection";
  private static final String IMAGE_LABEL_DETECTION = "Label Detection";
  private static final String CLASSIFICATION_QUANT = "Classification (quantized)";
  private static final String CLASSIFICATION_FLOAT = "Classification (float)";
  private static final String FACE_CONTOUR = "Face Contour";
  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = IMAGE_LABEL_DETECTION;

  private boolean isAnimationBeingDisplayed;

  private StringAnimationFramework mAnimationFramework;
  private View mBottonOverlay;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    //Remove title bar
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

    //Remove notification bar
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_live_preview);

    preview = (CameraSourcePreview) findViewById(R.id.firePreview);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    List<String> options = new ArrayList<>();
    options.add(IMAGE_LABEL_DETECTION);
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    } else {
      getRuntimePermissions();
    }

    displaySplashAnimation();

  }


  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

          Log.i(TAG, "Using Image Label Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new ImageLabelingProcessor());
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }


  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    startCameraSource();

    if (mAnimationFramework != null && isAnimationBeingDisplayed) {
      startStringAnimation();
    }
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();

    if (mAnimationFramework != null) {
      stopStringAnimation();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }

  private String[] getRequiredPermissions() {
    try {
      PackageInfo info =
          this.getPackageManager()
              .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      if (ps != null && ps.length > 0) {
        return ps;
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      return new String[0];
    }
  }

  private boolean allPermissionsGranted() {
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        return false;
      }
    }
    return true;
  }

  private void getRuntimePermissions() {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(
          this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, String[] permissions, int[] grantResults) {
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private static boolean isPermissionGranted(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission granted: " + permission);
      return true;
    }
    Log.i(TAG, "Permission NOT granted: " + permission);
    return false;
  }


  private void displaySplashAnimation() {
    // Custom animation on image
    final ImageView myView = findViewById(R.id.basil_leaf_image);

    mBottonOverlay = findViewById(R.id.bottom_overlay);

    myView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideUserGuidance();
        hideOnClickButton();
        View produce_artichoke = findViewById(R.id.produce_artichoke);
        produce_artichoke.setVisibility(View.VISIBLE);

        //animateBasil();

      }
    });

    ObjectAnimator scaleX = ObjectAnimator.ofFloat(myView, "scaleX",  0f, 1f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(myView, "scaleY",  0f, 1f);
    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(myView, "alpha", 0f, 1f);

    final AnimatorSet mAnimationSet = new AnimatorSet();

    mAnimationSet.playTogether(scaleX, scaleY, fadeIn);
    mAnimationSet.setDuration(1000);

    mAnimationSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        showUserGuidance();
      }
    });
    mAnimationSet.start();
  }

  private void animateBasil() {
      final View myView = findViewById(R.id.artichoke_image);

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
              myView.setVisibility(View.VISIBLE);
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

  private void hideOnClickButton() {
      mBottonOverlay.setVisibility(View.GONE);
  }

  private void showOnClickButton() {
      mBottonOverlay.setVisibility(View.VISIBLE);
  }

  private void startStringAnimation() {
    mAnimationFramework.resumeAnimators();
  }

  private void stopStringAnimation() {
    mAnimationFramework.pauseAnimators();
  }
}
