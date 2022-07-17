package com.example.designpro;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.view.MotionEvent;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Colormatch extends AppCompatActivity {
    ImageView img,imag;
    ImageButton btn_cap;

    private  String currentPhotoPath;
    private static final int GALLERY_REQUEST_CODE= 123;
    private Bitmap bitmap;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colormatch);
        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        Python py = Python.getInstance();
        final PyObject pyobj = py.getModule("design");
        ImageView org_img,det_img;
        TextView tv_col_name,tv_col_code,tv_mat_per;
        imag= findViewById(R.id.imag);
        img = findViewById(R.id.img);
        btn_cap = findViewById(R.id.capture);

        TextView tv_col_hsv=findViewById(R.id.col_HSV);
        TextView tv_org=findViewById(R.id.orgcol);

        TextView tv_det=findViewById(R.id.detcol);
        org_img=(ImageView)findViewById(R.id.img_org);
        det_img=(ImageView)findViewById(R.id.img_det);
        tv_col_name = (TextView) findViewById(R.id.Color_name);
        tv_col_code = (TextView) findViewById(R.id.Color_code);
        tv_mat_per = (TextView) findViewById(R.id.precent_mat);


        if (ContextCompat.checkSelfPermission(Colormatch.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Colormatch.this, new String[]
                    {
                            Manifest.permission.CAMERA
                    }, 100);
        }
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]
//                    {
//                            Manifest.permission.READ_EXTERNAL_STORAGE
//                    }, GALLERY_REQUEST_CODE);
//        }
        btn_cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 100);
                ///
                imag.setVisibility(View.INVISIBLE);
                String  fileName="photo";
                File  storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                    currentPhotoPath= imageFile.getAbsolutePath();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri imageUri=FileProvider.getUriForFile(Colormatch.this,"com.example.designpro.fileprovider",imageFile);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent, 100);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ///

            }
        });
        ImageButton Ad_img=findViewById(R.id.add_img);
        Ad_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imag.setVisibility(View.INVISIBLE);
                Intent inte=new Intent();
                inte.setType("image/*");
                inte.setAction(inte.ACTION_GET_CONTENT);

//                startActivityForResult(inte, GALLERY_REQUEST_CODE);

//                    Intent inte=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(inte,GALLERY_REQUEST_CODE);
            }
        });
        img.setDrawingCacheEnabled(true);
        img.buildDrawingCache(true);
        img.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
                {

                    bitmap=img.getDrawingCache();
                    int pixel = bitmap.getPixel((int)event.getX(),(int)event.getY());
                    int r= Color.red(pixel);
                    int b=Color.blue(pixel);
                    int g=Color.green(pixel);
                    float[] hsv = new float[3];
                    Color.RGBToHSV(r,g,b, hsv);

                    float h = hsv[0];

                    float s = hsv[1];

                    float val = hsv[2];


                    tv_col_hsv.setText("Hue: "+h + " Saturation: "+s +" Value: " + val);
                    org_img.setBackgroundColor(Color.rgb(r,g,b));
                    PyObject obj = pyobj.callAttr("getColorName", r, g, b);
                    tv_col_name.setText("Color found: "+obj.toString());
                    PyObject obj_code = pyobj.callAttr("getColorCode", r, g, b);
                    tv_col_code.setText("Color Code: "+obj_code.toString());
                    PyObject obj_pixelR = pyobj.callAttr("getMatchedColorPixelR", r, g, b);
                    PyObject obj_pixelG = pyobj.callAttr("getMatchedColorPixelG", r, g, b);
                    PyObject obj_pixelB = pyobj.callAttr("getMatchedColorPixelB", r, g, b);
                    det_img.setBackgroundColor(Color.rgb(obj_pixelR.toInt(),obj_pixelG.toInt(),obj_pixelB.toInt()));
                    PyObject obj_per = pyobj.callAttr("precentMatch", r, g, b);
                    tv_mat_per.setText("Percent match: "+obj_per.toFloat());

                    tv_org.setText("Original");
                    tv_det.setText("Detected");
                }
                return true;
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {//capture image
//            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
//            //set capture to image wiew
//            img.setImageBitmap(captureImage);
            ///
            bitmap= BitmapFactory.decodeFile(currentPhotoPath);
            img.setImageBitmap(bitmap);
            bitmap=img.getDrawingCache();

            ///

        }
        if(requestCode==GALLERY_REQUEST_CODE && data!=null && resultCode == RESULT_OK)
        {
//             bitmap = (Bitmap) data.getExtras().get("data");
//            img.setImageBitmap(bitmap);
//            Uri imageData=data.getData();
//
//            img.setImageURI(imageData);


            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);
                bitmap=img.getDrawingCache();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}