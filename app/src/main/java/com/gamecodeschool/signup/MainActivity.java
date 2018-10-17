package com.gamecodeschool.signup;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    TextView txtPhoto, txtTitle;
    EditText editTextUsername, editTextEmail, editTextPassword;
    ImageView imgPhoto;
    Button btnSignUp;
    String mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindWidget();

        Hawk.init(this).build();

        if (Hawk.contains("username")) {
            String username = Hawk.get("username", "");
            String password = Hawk.get("email", "");
            String email = Hawk.get("password", "");
            String image = Hawk.get("image", "defaultImage");

            txtTitle.setText("Personal Infos");
            btnSignUp.setVisibility(View.GONE);

            if (image != "defaultImage") {
                txtPhoto.setVisibility(View.GONE);
                String base = image;
                byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
                imgPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            }

            editTextUsername.setText(username);
            editTextEmail.setText(email);
            editTextPassword.setText(password);

        } else {
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("Camera", "Permission is not granted, requesting");
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 123);
                    }
                    Intent photo = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(photo, 100);
                }
            });

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Hawk.put("username", editTextUsername.getText().toString());
                    Hawk.put("email", editTextEmail.getText().toString());
                    Hawk.put("password", editTextPassword.getText().toString());
                    Hawk.put("image", mImageUri);
                }
            });
        }
    }

    public void bindWidget() {
        txtPhoto = findViewById(R.id.txtPhoto);
        txtTitle = findViewById(R.id.txtTitle);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        imgPhoto = findViewById(R.id.imgPhoto);
        btnSignUp = findViewById(R.id.btnSignUp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    imgPhoto.setImageBitmap(imageBitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    byte[] image = stream.toByteArray();
                    mImageUri = Base64.encodeToString(image, 0);
                }
            }
        }
    }
}
