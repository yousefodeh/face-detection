package com.yousef.facedetection.FaceDetectionUtil;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yousef.facedetection.FaceDetectionUtil.common.CameraImageGraphic;
import com.yousef.facedetection.FaceDetectionUtil.common.FrameMetadata;
import com.yousef.facedetection.FaceDetectionUtil.common.GraphicOverlay;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.List;

public class FaceContourDetectorProcessor extends VisionProcessorBase<List<FirebaseVisionFace>> {

    private static final String TAG = "FaceContourDetectorProc";

    private final FirebaseVisionFaceDetector detector;

    FaceDetectionResultListener faceDetectionResultListener;


    public FaceContourDetectorProcessor() {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .enableTracking()
                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    public FaceContourDetectorProcessor(FirebaseVisionFaceDetector detector) {
        this.detector=detector;
    }

    public FaceDetectionResultListener getFaceDetectionResultListener() {
        return faceDetectionResultListener;
    }

    public void setFaceDetectionResultListener(FaceDetectionResultListener faceDetectionResultListener) {
        this.faceDetectionResultListener = faceDetectionResultListener;
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Contour Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }
        for (int i = 0; i < faces.size(); ++i) {
            FirebaseVisionFace face = faces.get(i);
            FaceContourGraphic faceGraphic = new FaceContourGraphic(graphicOverlay, face);
            graphicOverlay.add(faceGraphic);
        }
        graphicOverlay.postInvalidate();

        if(faceDetectionResultListener!=null)
            faceDetectionResultListener.onSuccess(originalCameraImage,faces,frameMetadata,graphicOverlay);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {

        if(faceDetectionResultListener!=null)
            faceDetectionResultListener.onFailure(e);

        Log.e(TAG, "Face detection failed " + e);
    }
}