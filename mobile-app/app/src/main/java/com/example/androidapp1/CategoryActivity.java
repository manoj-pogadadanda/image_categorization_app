package com.example.androidapp1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CategoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Button server_btn = (Button) findViewById(R.id.server_button);
        //server_btn.setClickable(false);
        server_btn.setEnabled(false);

        Spinner category_list = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        category_list.setAdapter(adapter);

        category_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (category_list.getSelectedItem().toString().equals("None"))
                    server_btn.setEnabled(false);
                else
                    server_btn.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                server_btn.setEnabled(false);
            }
        });


        server_btn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent = getIntent();
                  String currentPhotoPath = intent.getExtras().getString("CapturedImage");
                  File photoFile = new File(currentPhotoPath);
                  Uri photoURI = FileProvider.getUriForFile(CategoryActivity.this,
                          "com.example.android.fileprovider",
                          photoFile);
                  Bitmap bitmap = null;
                  try {
                      bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                  } catch (FileNotFoundException e) {
                      e.printStackTrace();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }


                  Matrix matrix = new Matrix();
                  matrix.postRotate(90);
                  Bitmap rotatedImg = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                  bitmap.recycle();


                  //converting image to base64 string
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                  rotatedImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                  byte[] imageBytes = baos.toByteArray();
                  final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                  String Url = getString(R.string.file_server);
                  RequestQueue requestQueue = Volley.newRequestQueue(CategoryActivity.this);
                  StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>(){
                      @Override
                      public void onResponse(String s) {
                          if(s.equals("Success")){
                              Toast.makeText(CategoryActivity.this, photoFile.getName()
                                      + " Uploaded Successfully under category "
                                      + category_list.getSelectedItem().toString(),
                                      Toast.LENGTH_LONG).show();
                          }
                          else{
                              Toast.makeText(CategoryActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                          }
                      }},
                          new Response.ErrorListener(){
                              @Override
                              public void onErrorResponse(VolleyError volleyError) {
                                  Toast.makeText(CategoryActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                              }
                          }) {
                      @Nullable
                      @Override
                      protected Map<String, String> getParams() throws AuthFailureError {
                          Map<String, String> params = new HashMap<>();
                          params.put(getString(R.string.image_key), imageString);
                          params.put(getString(R.string.category_key), category_list.getSelectedItem().toString());
                          params.put("filename", photoFile.getName());
                          return params;
                      }
                  };
                  requestQueue.add(stringRequest);
              }
          }
        );
    }
}