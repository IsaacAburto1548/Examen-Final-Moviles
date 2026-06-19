package com.example.pasturecontrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PastureAdapter(
    private var pastures: List<Pasture>,
    private val onItemClick: (Pasture) -> Unit,
    private val onItemLongClick: (Pasture) -> Unit
) : RecyclerView.Adapter<PastureAdapter.PastureViewHolder>() {

    class PastureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArea: TextView = view.findViewById(R.id.tvArea)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pasture, parent, false)
        return PastureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastureViewHolder, position: Int) {
        val pasture = pastures[position]
        holder.tvName.text = pasture.name
        holder.tvArea.text = "${pasture.area} m²"
        holder.tvDate.text = pasture.creationDate

        holder.itemView.setOnClickListener { onItemClick(pasture) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(pasture)
            true
        }
    }

    override fun getItemCount() = pastures.size

    fun updateData(newPastures: List<Pasture>) {
        pastures = newPastures
        notifyDataSetChanged()
    }
}
