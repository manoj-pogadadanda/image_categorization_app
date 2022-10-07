package com.example.androidapp1;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int CATEGORY_REQUEST_CODE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button capture_image_btn = (Button)findViewById(R.id.capture_image_btn);
        Button exit_btn = (Button)findViewById(R.id.exit_btn);

        exit_btn.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                 public void onClick(View view) {

                     DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             switch (which){
                                 case DialogInterface.BUTTON_POSITIVE:
                                     //Yes button clicked
                                     finish();
                                     System.exit(0);
                                     break;

                                 case DialogInterface.BUTTON_NEGATIVE:
                                     //No button clicked
                                     break;
                             }
                         }
                     };

                     AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                     builder.setMessage(R.string.exit_Message).setPositiveButton("Yes", dialogClickListener)
                             .setNegativeButton("No", dialogClickListener).show();
                 }
             }
        );

        capture_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_DENIED) {
                                    // Permission is not granted
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, STORAGE_PERMISSION_CODE);
                                }
                                else {
                                    captureImage();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(R.string.dialog_Message).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        }
        );


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                captureImage();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    String imagePath;

    void captureImage() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File imageFile = null;
            try {
                SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
                String currentDate = s.format(new Date());
                imageFile = File.createTempFile("CapturedImage_" + currentDate + "_",  ".jpg",
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                imagePath = imageFile.getAbsolutePath();
            } catch (IOException ex) {
            // Error occurred while creating the File
                ex.printStackTrace();
            }
            if (imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        imageFile);
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(captureImageIntent, CAMERA_REQUEST_CODE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE)
        {
            Intent selectCategoryIntent = new Intent(MainActivity.this, CategoryActivity.class);
            selectCategoryIntent.putExtra("CapturedImage", imagePath);
            startActivityForResult(selectCategoryIntent, CATEGORY_REQUEST_CODE);
        }

    }

}