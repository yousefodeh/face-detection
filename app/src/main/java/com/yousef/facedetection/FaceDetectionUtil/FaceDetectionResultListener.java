package com.yousef.facedetection.FaceDetectionUtil;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yousef.facedetection.FaceDetectionUtil.common.FrameMetadata;
import com.yousef.facedetection.FaceDetectionUtil.common.GraphicOverlay;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.util.List;

public interface FaceDetectionResultListener {
    void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay);

    void onFailure(@NonNull Exception e);
}
