package com.example.registrationwithserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.registrationwithserver.databinding.ActivityRecordsBinding
import java.util.*

class RecordsActivity : AppCompatActivity() {
    var registeredDataArrayList: ArrayList<RegisteredData>? = null
    var recordAdapter: RecordAdapter? = null
    private lateinit var binding: ActivityRecordsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registeredDataArrayList = intent.getParcelableArrayListExtra("data")
        recordAdapter = RecordAdapter(this, registeredDataArrayList)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
        binding.recordList.layoutManager = layoutManager
        binding.recordList.setHasFixedSize(true)
        binding.recordList.adapter = recordAdapter
        recordAdapter!!.notifyDataSetChanged()
    }

}