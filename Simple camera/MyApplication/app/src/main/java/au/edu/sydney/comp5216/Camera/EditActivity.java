package au.edu.sydney.comp5216.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    Intent intent = null;
    //Path of file
    String path = null;
    ImageView imageview = null;
    //Buffer to undo
    Bitmap bmpBuffer = null;
    //Current bitmap
    Bitmap bitmap = null;

    public static Bitmap getColorBitmap(Bitmap bm, int type) {
        Bitmap bmp = null;
        //Get width and height of original bmp
        int width = bm.getWidth();
        int height = bm.getHeight();
        //Bmp as result bitmap
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //Set canvas and paint
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        //Set color matrix
        ColorMatrix matrix = new ColorMatrix();
        //Set saturation
        matrix.setSaturation(type);
        ColorMatrixColorFilter MatrixFilter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(MatrixFilter);
        //Draw Bitmap
        canvas.drawBitmap(bm, 0, 0, paint);
        return bmp;
    }

    public static Bitmap getSquarBitmap(Bitmap bm) {

        if (bm == null) {
            return null;
        }
        //Get original size
        int w = bm.getWidth();
        int h = bm.getHeight();
        //Which is longer height or width
        int wh = w > h ? h : w;
        //Get upper left corner of the square
        int retX = w > h ? (w - h) / 2 : 0;
        int retY = w > h ? 0 : (h - w) / 2;
        //Creat new Bitmap of square
        Bitmap bmp = Bitmap.createBitmap(bm, retX, retY, wh, wh, null, false);
        return bmp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        imageview = findViewById(R.id.imageView);
        intent = getIntent();
        //Get image path
        path = intent.getStringExtra("pathedit");
        //Initialize the initial view
        initial();
    }
    //Initialize the initial view
    public void initial() {
        try {
            FileInputStream fis = new FileInputStream(path);
            bitmap = BitmapFactory.decodeStream(fis);
            bmpBuffer = bitmap;
            //Preview the original pic
            imageview.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Change picture saturation
    public void coloring(int type) {
        bmpBuffer = bitmap;
        bitmap = getColorBitmap(bitmap, type);
        imageview.setImageBitmap(bitmap);
    }
    //Crop pic to square
    public void Cropping(View view) {
        bmpBuffer = bitmap;
        bitmap = getSquarBitmap(bitmap);
        imageview.setImageBitmap(bitmap);
    }


    //Save the image after finish
    public void saveimage(Bitmap bitmap, String path) {
        File tempfile = new File(path);
        //Delete the old one at first
        tempfile.delete();
        try {
            FileOutputStream fos = new FileOutputStream(tempfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //save to Content library
            EditActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + tempfile.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Graying the pic
    public void Graying(View view) {
        coloring(0);
    }
    //Coloring the pic
    public void Coloring(View view) {
        coloring(5);
    }
    //Undo the edit
    public void Undo(View view) {
        bitmap = bmpBuffer;
        imageview.setImageBitmap(bitmap);
    }
    //Cancel the edit
    public void Back(View view) {
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
        EditActivity.this.finish();
    }
    // Finish and save the image
    public void Finish(View view) {
        saveimage(bitmap, path);
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
        EditActivity.this.finish();
    }
}
