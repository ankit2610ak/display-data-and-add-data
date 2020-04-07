package com.example.registrationwithserver

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.registrationwithserver.RecordAdapter.CustomViewHolder
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

class RecordAdapter(
    private val context: Context,
    private val registeredDataArrayList: ArrayList<RegisteredData>?
) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView =
            layoutInflater.inflate(R.layout.registration_record, parent, false)
        return CustomViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val registeredData = registeredDataArrayList!![position]
        holder.registeredName.text = registeredData.name
        holder.registeredCity.text = registeredData.city
        val path = registeredData.photoPath
        loadImageFromPath(path, holder, registeredData)
    }

    private fun loadImageFromPath(
        path: String,
        holder: CustomViewHolder,
        registeredData: RegisteredData
    ) {
        if (registeredData.imageType.equals("camera", ignoreCase = true)) {
            try {
                val file = File(path, "profile.jpg")
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                holder.registeredPhoto.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } else {
            val uri = Uri.parse(path)
            holder.registeredPhoto.setImageURI(uri)
        }
    }

    override fun getItemCount(): Int {
        return registeredDataArrayList!!.size
    }

    class CustomViewHolder(view: View) : ViewHolder(view) {
        var registeredPhoto: ImageView
        var registeredName: TextView
        var registeredCity: TextView

        init {
            registeredPhoto = view.findViewById(R.id.registered_photo)
            registeredName = view.findViewById(R.id.registerd_name)
            registeredCity = view.findViewById(R.id.registered_city)
        }
    }

}