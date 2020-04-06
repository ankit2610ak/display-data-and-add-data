package com.example.registrationwithserver;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
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
