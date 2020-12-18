package au.edu.sydney.comp5216.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    //Create an camera instance
    private Camera mCamera;
    //Used to catch surfaceView
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    //Used to identify front and back camera, 0 means back camera
    private int cameraId = 0;

    //A callback function called after camera taking picture
    private Camera.PictureCallback mpictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //Use current system time as the
            long current = System.currentTimeMillis();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(current);
            File tempfile = new File("/storage/emulated/0/DCIM/Camera/" + time + ".jpg");
            //Write the file
            try {
                FileOutputStream fos = new FileOutputStream(tempfile);
                // Byte type to bitmap type
                Bitmap bitmap = BytesToBimap(data);
                //Rotate the picture due to the camera's default settings
                bitmap = RotateBitmap(bitmap);
                //Save the photo
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                //Add photos to the content library
                CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + tempfile.getAbsolutePath())));
                //Go to preview activity
                Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                //Send the path of the picture
                intent.putExtra("picpath", tempfile.getAbsolutePath());
                startActivity(intent);
                //Close the activity
                CameraActivity.this.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //initial surfaceView
        mPreview = findViewById(R.id.surfaceView);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(CameraActivity.this);
        //Click to autofocus
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.autoFocus(null);
            }
        });
    }

    //Define shot method
    public void capture(View view) {
        //Use autoFocus
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                //Take picture when focus success
                if (success) {
                    mCamera.takePicture(null, null, mpictureCallback);
                }
            }
        });

    }

    //Byte type to Bitmap method
    public Bitmap BytesToBimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    //
    public Bitmap RotateBitmap(Bitmap bmp) {
        //Set rotate matrix
        Matrix matrix = new Matrix();
        matrix.postScale(1f, 1f);
        matrix.postRotate(90);
        //transfer
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return dstbmp;
    }

    //Get camera again when activity on resume (a phase of life cycle)
    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (mHolder != null) {
                setStartPreview(mCamera, mHolder);
            }
        }
    }

    //release camera when activity on pause
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    //Get camera back
    private Camera getCamera() {
        Camera camera;
        try {
            //open back camera
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    //Open preview view
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            //rotate the preview due to the default preview angle is not appropriate
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Define method to release the camera
    private void releaseCamera() {
        if (mCamera != null) {
            //stop preview first
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    //Set new SurfaceView
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //If surface changed , stop the old one first and restart the preview
        mCamera.stopPreview();
        setStartPreview(mCamera, mHolder);
    }

    //if surfaceView destroyed , release the camera
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

}


