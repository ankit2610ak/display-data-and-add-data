package com.example.registrationwithserver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 1;
    private static int RESULT_LOAD_IMAGE_FROM_GALLERY = 2;
    String imageType = "";
    ImageView photo;
    TextView name, email, phoneNumber;
    Button submit, showData;
    Spinner city;
    String selectedCity = "";
    String photoPathString = "";
    ArrayList<RegisteredData> registeredDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
        clickListener();

    }

    private void clickListener() {
        photo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (!checkPermissionGrantedForReadExternalStorage()) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                } else {
                    selectImage();
                }

            }
        });

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedCity = parent.getItemAtPosition(position).toString();
                } else {
                    selectedCity = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNameNotExist() || checkEmailNotExist() || checkValidPhoneNumberNotExist() || checkCityNotExist()) {
                    Toast.makeText(MainActivity.this, "Please Fill All valid Details", Toast.LENGTH_SHORT).show();
                } else {
                    submitRecord();
                    clearForm();
                }

            }
        });
        showData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllRecords();
            }
        });
    }

    private void submitRecord() {
        registeredDataArrayList.add(new RegisteredData(imageType, photoPathString, name.getText().toString(), email.getText().toString(),
                phoneNumber.getText().toString(), selectedCity));
        Toast.makeText(MainActivity.this, "Registration Successful !!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (checkPermissionGrantedForReadExternalStorage()) {
                selectImage();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private boolean checkPermissionGrantedForReadExternalStorage() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void openAllRecords() {
        Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
        intent.putParcelableArrayListExtra("data", registeredDataArrayList);
        startActivity(intent);
    }

    private void clearForm() {
        name.setText("");
        email.setText("");
        phoneNumber.setText("");
        selectedCity = "";
        city.setSelection(0);
        photoPathString = "";
        imageType = "";
        setDefaultPhoto();
    }

    private void setDefaultPhoto() {
        int imageDefault = getResources().getIdentifier("@drawable/download", null, getPackageName());
        Drawable res = getResources().getDrawable(imageDefault);
        photo.setImageDrawable(res);
    }

    private boolean checkCityNotExist() {
        return selectedCity.equalsIgnoreCase("");
    }

    private boolean checkEmailNotExist() {
        return email.getText().toString().equalsIgnoreCase("");
    }

    private boolean checkNameNotExist() {
        return name.getText().toString().equalsIgnoreCase("");
    }

    private boolean checkValidPhoneNumberNotExist() {
        return phoneNumber.getText().toString().length() != 10;
    }

    private void setViews() {
        photo = findViewById(R.id.photo);
        city = findViewById(R.id.city_spinner);
        submit = findViewById(R.id.submit);
        showData = findViewById(R.id.showData);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
    }

    private void selectImage() {
        try {

            final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Option");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        takePictureFromCamera(dialog);
                    } else if (options[item].equals("Choose From Gallery")) {
                        takePictureFromGallery(dialog);
                    } else {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            cameraAccessPermissionError(e);
        }
    }

    private void cameraAccessPermissionError(Exception e) {
        Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void takePictureFromGallery(DialogInterface dialog) {
        dialog.dismiss();
            openGallery();
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, RESULT_LOAD_IMAGE_FROM_GALLERY);
    }


    private void takePictureFromCamera(DialogInterface dialog) {
        dialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_LOAD_IMAGE_FROM_CAMERA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE_FROM_GALLERY && resultCode == RESULT_OK && null != data) {
            try {
                setPictureFromGallery(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == RESULT_LOAD_IMAGE_FROM_CAMERA && resultCode == RESULT_OK && null != data) {
            SetPictureByCamera(data);
        }
    }

    private void SetPictureByCamera(@NotNull Intent data) {
        try {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            photo.setImageBitmap(bitmap);
            photoPathString = saveToInternalStorage(bitmap);
            imageType = "camera";
            Log.d("MainActivity", saveToInternalStorage(bitmap));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // path to /data/data/yourapp/app_data/imageDir
        @SuppressLint("SimpleDateFormat")
        File directory = cw.getDir("imageDir" +
                new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()), Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void setPictureFromGallery(@NotNull Intent data) throws IOException {
        Uri selectedImage = data.getData();
        photo.setImageURI(selectedImage);
        photoPathString = selectedImage.toString();
        imageType = "gallery";
    }


}