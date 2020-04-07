package com.example.registrationwithserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class RecordsActivity : AppCompatActivity() {
    var recordList: RecyclerView? = null
    var registeredDataArrayList: ArrayList<RegisteredData>? = null
    var recordAdapter: RecordAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)
        registeredDataArrayList = intent.getParcelableArrayListExtra("data")
        recordAdapter = RecordAdapter(this, registeredDataArrayList)
        setViews()
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
        recordList!!.layoutManager = layoutManager
        recordList!!.setHasFixedSize(true)
        recordList!!.adapter = recordAdapter
        recordAdapter!!.notifyDataSetChanged()
    }

    private fun setViews() {
        recordList = findViewById(R.id.record_list)
    }
}