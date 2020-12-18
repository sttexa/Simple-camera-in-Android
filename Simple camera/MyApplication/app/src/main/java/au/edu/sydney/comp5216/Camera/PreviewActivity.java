package au.edu.sydney.comp5216.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class PreviewActivity extends AppCompatActivity {

    String path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        //Get picture path
        path = getIntent().getStringExtra("picpath");
        ImageView imageview = findViewById(R.id.image);
        try {
            //Read the picture
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            //Set to imageView
            imageview.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void Finishback(View view) {
        //Back to main activiy
        Intent intent = new Intent(PreviewActivity.this, MainActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
        PreviewActivity.this.finish();
    }
}
