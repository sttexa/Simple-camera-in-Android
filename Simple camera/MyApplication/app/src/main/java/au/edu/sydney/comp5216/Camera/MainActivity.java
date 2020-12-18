package au.edu.sydney.comp5216.Camera;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Set permission type
    protected static int REQUEST_PERMISSION_CODE = 5;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    //Create a new adapter
    final MyAdapter adapter = new MyAdapter();
    //List to get all imagePath
    List<String> imagePath = new ArrayList<String>();
    //GridView on main activity
    private GridView gridView;

    //Get pic list via Cursor and Uri
    public static List<String> getSystemPhotoList(Context context) {
        List<String> result = new ArrayList<String>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            //If no pic
            Log.i("path is", "0000");
            return null;
        }
        //Get physical path
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index);
            File file = new File(path);
            if (file.exists()) {
                result.add(path);
                Log.i("path is", path);
            }
        }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check and request the permission
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        initial();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "permission name：" + permissions[i] + ",request result：" + grantResults[i]);
            }
        }
    }
    //Initialize all the component of the main activity layout
    protected void initial() {
        gridView = (GridView) findViewById(R.id.gridview1);
        //Get physical path list
        imagePath = getSystemPhotoList(this);
        if (imagePath == null) {
            return;
        }
        //Set adapter to GridView
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("path is", imagePath.get(position));
                //Go to edit activity to edit pic
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("pathedit", imagePath.get(position));
                startActivity(intent);
            }
        });
    }
    //Go to camera activity
    public void GotoCamera(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }
    //Customize MyAdapter and link myadapter to imagePath
    class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
// TODO Auto-generated method stub
            return imagePath.size();
        }


        @Override
        public Object getItem(int position) {
// TODO Auto-generated method stub
            return imagePath.get(position);
        }


        @Override
        public long getItemId(int position) {
// TODO Auto-generated method stub
            return position;
        }

        //Core method to link imageView to imagePath and set it into GridView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(MainActivity.this);
                //Set imageView 's size and padding
                imageView.setAdjustViewBounds(true);
                imageView.setMaxWidth(300);
                imageView.setMaxHeight(350);
                imageView.setPadding(2, 3, 2, 3);
            } else {
                imageView = (ImageView) convertView;
            }
            //Read file to bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath.get(position));
            imageView.setImageBitmap(bitmap);
            return imageView;
        }

    }


}

