package com.example.designpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class grain_size extends AppCompatActivity {
    ImageView img1,img2; Uri imageUri;
    Boolean x;

   private static final int GALLERY_REQUEST_CODE= 123;

    Button btn_crop,btn_process; ImageButton btn_capture;
    private Bitmap bitmap,bitmap_crop;
    String imagestring="";
    BitmapDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colormatch);
        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        Python py = Python.getInstance();


        setContentView(R.layout.activity_grain_size);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        btn_capture = findViewById(R.id.btn_capture);
        btn_crop=findViewById(R.id.btn_crop);
        btn_process =findViewById(R.id.process);
        TextView tv1; //EditText tv2;
        tv1= findViewById(R.id.width);
        //tv2 = (EditText)findViewById(R.id.editText);
        String w;
        if (ContextCompat.checkSelfPermission(grain_size.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(grain_size.this, new String[]
                    {
                            Manifest.permission.CAMERA
                    }, 100);
        }

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    x = true;
                    opengallery();

            }
        });
        btn_crop.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                x = false;
                startCropActivity();
                //tv2.setText("Enter width of image in actual view: ");

            }

            private void startCropActivity() {
                CropImage.activity(imageUri).start(grain_size.this);
            }
        });

        btn_process.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                drawable=(BitmapDrawable)img2.getDrawable();
                bitmap_crop= drawable.getBitmap();

            imagestring=getimagestring(bitmap_crop);
            PyObject pyobj = py.getModule("python_grain");

            PyObject obj= pyobj.callAttr("main",imagestring);
            float ans=obj.toFloat();
            tv1.setText("Width of grain : " +obj.toString());
           // String width=tv2.getText().toString();
            //float w=Float.parseFloat(tv2.getText().toString());
            //w=w*ans/img2.getWidth();
           // tv2.setText("Original width of the grain found is :"+ Float.toString(w));

            }
        });

    }



    private String getimagestring(Bitmap bitmap) {
        ByteArrayOutputStream  baos= new ByteArrayOutputStream();
        bitmap_crop.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage= android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST_CODE && data!=null && resultCode == RESULT_OK)
        {
//            bitmap = (Bitmap) data.getExtras().get("data");
//            img.setImageBitmap(bitmap);
               imageUri=data.getData();
//
//            img.setImageURI(imageData);


            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                if(x) {
                    img1.setImageBitmap(bitmap);
                    bitmap = img1.getDrawingCache();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri CropUri = result.getUri();
                try {
                    bitmap_crop = MediaStore.Images.Media.getBitmap(this.getContentResolver(), CropUri);
                    img2.setImageBitmap(bitmap_crop);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        ///

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