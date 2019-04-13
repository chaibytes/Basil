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
package com.google.firebase.samples.apps.mlkit.imagelabeling;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay.Graphic;

import java.util.List;

/** Graphic instance for rendering a label within an associated graphic overlay view. */
public class LabelGraphic extends Graphic {

  private final Paint textPaint;
  private final GraphicOverlay overlay;

  private final List<FirebaseVisionImageLabel> labels;

  LabelGraphic(GraphicOverlay overlay, List<FirebaseVisionImageLabel> labels) {
    super(overlay);
    this.overlay = overlay;
    this.labels = labels;
    textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    textPaint.setTextSize(35.0f);
    textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
  }

  @Override
  public synchronized void draw(Canvas canvas) {
    //float x = overlay.getWidth() / 4.0f;
    //float y = overlay.getHeight() / 2.0f;
    float y = 100;
    float x = 10;

    for (FirebaseVisionImageLabel label : labels) {
      canvas.drawText(label.getText(), x, y, textPaint);
      y = y - 35.0f;
    }
  }
}
