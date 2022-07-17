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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chaquo.python.PyObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.lang.Math;

public class Compare2colors extends AppCompatActivity {
    ImageView img1,img2;
    Boolean x;
    int r1,g1,b1,r2,g2,b2;
    float h1,s1,v1,h2,s2,v2;
    private static final int GALLERY_REQUEST_CODE= 123;
    private  String currentPhotoPath;
    Button btn_cap;
    private Bitmap bitmap,bitmap1,bitmap2;
     ArrayList <String> array_tier,array_tier2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare2colors);
        prepared_lis();
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        btn_cap = findViewById(R.id.process);
        ImageView imgs1,imgs2;
        imgs1=findViewById(R.id.img_s1);
        imgs2=findViewById(R.id.img_s2);
        TextView tv_match,tv_match_h;
        tv_match= findViewById(R.id.match);
        tv_match_h=findViewById(R.id.match_);
        Button btn_process =findViewById(R.id.process);
        Spinner spinner,spinner2;
        if (ContextCompat.checkSelfPermission(Compare2colors.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Compare2colors.this, new String[]
                    {
                            Manifest.permission.CAMERA
                    }, 100);
        }

        ArrayAdapter<String>arrayadapter;
        arrayadapter= new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item,array_tier);
        spinner=findViewById(R.id.spinner_capture);
        spinner.setAdapter(arrayadapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1) {
                    x=true;
                    opencamera();
                }
                if(position==2)
                {
                    x=false;
                    opencamera();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        ArrayAdapter<String>arrayadapter2;
        arrayadapter2= new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item,array_tier2);
        spinner2=findViewById(R.id.spinner_gal);
        spinner2.setAdapter(arrayadapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1) {
                    x=true;
                    opengallery();
                }
                if(position==2)
                {
                    x=false;
                    opengallery();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });




        img1.setDrawingCacheEnabled(true);
        img1.buildDrawingCache(true);
        img1.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
                {

                    bitmap1=img1.getDrawingCache();
                    int pixel = bitmap1.getPixel((int)event.getX(),(int)event.getY());
                    r1= Color.red(pixel);
                    b1=Color.blue(pixel);
                    g1=Color.green(pixel);

                    float[] hsv = new float[3];
                    Color.RGBToHSV(r1,g1,b1, hsv);
                    h1 = hsv[0];
                    s1 = hsv[1];
                    v1 = hsv[2];
                    imgs1.setBackgroundColor(Color.rgb(r1,g1,b1));

                    //tv_col_hsv.setText("Hue: "+h + " Saturation: "+s +" Value: " + val);
                    //org_img.setBackgroundColor(Color.rgb(r,g,b));

                }
                return true;
            }
        });


        img2.setDrawingCacheEnabled(true);
        img2.buildDrawingCache(true);
        img2.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
                {

                    bitmap2=img2.getDrawingCache();
                    int pixel = bitmap2.getPixel((int)event.getX(),(int)event.getY());
                    r2= Color.red(pixel);
                    b2=Color.blue(pixel);
                    g2=Color.green(pixel);
                    float[] hsv = new float[3];
                    Color.RGBToHSV(r2,g2,b2, hsv);
                    h2 = hsv[0];
                    s2 = hsv[1];
                    v2 = hsv[2];
                    imgs2.setBackgroundColor(Color.rgb(r2,g2,b2));

                    //tv_col_hsv.setText("Hue: "+h + " Saturation: "+s +" Value: " + val);
                    //org_img.setBackgroundColor(Color.rgb(r,g,b));

                }
                return true;
            }
        });
        btn_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_match.setText("Match found: "+Math.abs(100-((Math.abs(h1-h2)/(h1+0.1)+Math.abs(s1-s2)/(s1+0.1)+Math.abs(v1-v2)/(v1+0.1))/3*100)));
                tv_match_h.setText("Match found neglecting white light effect: "+Math.abs(100-((Math.abs(h1-h2)/(h1+0.1)+Math.abs(s1-s2)/(s1+0.1))/2*100)));
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
            if(x){
                img1.setImageBitmap(bitmap);
            bitmap=img1.getDrawingCache();}
            else
            {
                img2.setImageBitmap(bitmap);
                bitmap=img2.getDrawingCache();
            }
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
                    if(x) {
                        img1.setImageBitmap(bitmap);
                        bitmap = img1.getDrawingCache();
                    }
                    else{
                        img2.setImageBitmap(bitmap);
                        bitmap = img2.getDrawingCache();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            ///

        }


    void prepared_lis(){
        array_tier= new ArrayList<>();
        array_tier.add("Capture");
        array_tier.add("Image-I");
        array_tier.add("Image-II");
        array_tier2= new ArrayList<>();
        array_tier2.add("Import");
        array_tier2.add("Image-I");
        array_tier2.add("Image-II");
       }
    void opencamera() {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 100);
        ///
        String  fileName="photo";
        File  storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
            currentPhotoPath= imageFile.getAbsolutePath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri imageUri=FileProvider.getUriForFile(Compare2colors.this,"com.example.designpro.fileprovider",imageFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(intent, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void opengallery() {
        Intent inte=new Intent();
        inte.setType("image/*");
        inte.setAction(inte.ACTION_GET_CONTENT);
//                startActivityForResult(inte, GALLERY_REQUEST_CODE);

//                    Intent inte=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(inte,GALLERY_REQUEST_CODE);
    }
}
