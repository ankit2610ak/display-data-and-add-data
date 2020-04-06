package com.example.registrationwithserver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<RegisteredData> registeredDataArrayList;

    RecordAdapter(Context context, ArrayList<RegisteredData> registeredDataArrayList) {
        this.context = context;
        this.registeredDataArrayList = registeredDataArrayList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = layoutInflater.inflate(R.layout.registration_record, parent, false);
        return new CustomViewHolder(rootView);

    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        RegisteredData registeredData = registeredDataArrayList.get(position);
        holder.registeredName.setText(registeredData.getName());
        holder.registeredCity.setText(registeredData.getCity());
        String path = registeredData.getPhotoPath();
        loadImageFromPath(path, holder , registeredData);
    }

    private void loadImageFromPath(String path, CustomViewHolder holder, RegisteredData registeredData) {
        if (registeredData.getImageType().equalsIgnoreCase("camera")) {
            try {
                File file = new File(path, "profile.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                holder.registeredPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            Uri uri = Uri.parse(path);
            holder.registeredPhoto.setImageURI(uri);
        }
    }


    @Override
    public int getItemCount() {
        return registeredDataArrayList.size();

    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView registeredPhoto;
        TextView registeredName, registeredCity;

        CustomViewHolder(View view) {
            super(view);
            registeredPhoto = view.findViewById(R.id.registered_photo);
            registeredName = view.findViewById(R.id.registerd_name);
            registeredCity = view.findViewById(R.id.registered_city);
        }
    }
}
