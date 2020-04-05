package com.example.registrationwithserver;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {
    RecyclerView recordList;
    ArrayList<RegisteredData> registeredDataArrayList;
    RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        registeredDataArrayList = getIntent().getParcelableArrayListExtra("data");
        recordAdapter = new RecordAdapter(this, registeredDataArrayList);
        setViews();
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recordList.setLayoutManager(layoutManager);
        recordList.setHasFixedSize(true);
        recordList.setAdapter(recordAdapter);
        recordAdapter.notifyDataSetChanged();
    }

    private void setViews() {
        recordList = findViewById(R.id.record_list);
    }
}
