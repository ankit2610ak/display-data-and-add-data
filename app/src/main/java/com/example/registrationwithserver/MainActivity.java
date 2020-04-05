package com.example.registrationwithserver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 1;
    private static int RESULT_LOAD_IMAGE_FROM_GALLERY = 2;
    ImageView photo;
    TextView name, email, phoneNumber;
    Button submit, showData;
    Spinner city;
    String selectedCity = "";
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
            @Override
            public void onClick(View v) {
                selectImage();

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
                    registeredDataArrayList.add(new RegisteredData(name.getText().toString(), email.getText().toString(),
                            phoneNumber.getText().toString(), selectedCity));
                    Toast.makeText(MainActivity.this, "Registration Successful !!!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        showData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                intent.putParcelableArrayListExtra("data", registeredDataArrayList);
                startActivity(intent);
            }
        });
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
        if (!checkPermissionGrantedForReadExternalStorage()) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

        } else {
            dialog.dismiss();
            openGallery();
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, RESULT_LOAD_IMAGE_FROM_GALLERY);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (checkPermissionGrantedForReadExternalStorage()) {
                openGallery();
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkPermissionGrantedForReadExternalStorage() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
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
            setPictureFromGallery(data);
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
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
            Uri uri = Uri.parse(path);
            photo.setTag(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* Uri selectedImage = data.getData();
        photo.setImageURI(selectedImage);*/

    }

    private void setPictureFromGallery(@NotNull Intent data) {
        Uri selectedImage = data.getData();
        photo.setImageURI(selectedImage);
        photo.setTag(selectedImage.toString());
    }

}