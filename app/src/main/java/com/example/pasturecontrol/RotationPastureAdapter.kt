package com.example.pasturecontrol

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RotationPastureAdapter(
    private var pastures: List<PastureDisplay>,
    private val onSelectionChanged: (Int) -> Unit,
    private val onItemLongClick: (PastureDisplay) -> Unit
) : RecyclerView.Adapter<RotationPastureAdapter.PastureViewHolder>() {

    private val selectedPastureIds = mutableSetOf<Int>()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    class PastureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.cardPastureRotation)
        val checkSelection: CheckBox = view.findViewById(R.id.chkSelected)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArea: TextView = view.findViewById(R.id.tvArea)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pasture_rotation, parent, false)
        return PastureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastureViewHolder, position: Int) {
        val pastureDisplay = pastures[position]
        val pasture = pastureDisplay.pasture
        val isSelected = selectedPastureIds.contains(pasture.id)
        val backgroundColor = colorForState(pastureDisplay.state)

        holder.card.setCardBackgroundColor(backgroundColor)
        holder.card.strokeColor = if (isSelected) Color.BLACK else Color.TRANSPARENT
        holder.card.strokeWidth = if (isSelected) 6 else 0

        holder.checkSelection.isChecked = isSelected
        holder.tvName.text = pasture.name
        holder.tvArea.text = "${pasture.area} m2"
        holder.tvStatus.text = pastureDisplay.state.label
        holder.tvDate.text = formatRotationDates(pastureDisplay)

        holder.itemView.setOnClickListener { toggleSelection(pasture.id) }
        holder.checkSelection.setOnClickListener { toggleSelection(pasture.id) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(pastureDisplay)
            true
        }
    }

    override fun getItemCount() = pastures.size

    fun updateData(newPastures: List<PastureDisplay>) {
        pastures = newPastures
        val visibleIds = newPastures.map { it.pasture.id }.toSet()
        selectedPastureIds.retainAll(visibleIds)
        notifyDataSetChanged()
        onSelectionChanged(selectedPastureIds.size)
    }

    fun clearSelection() {
        if (selectedPastureIds.isEmpty()) return
        selectedPastureIds.clear()
        notifyDataSetChanged()
        onSelectionChanged(0)
    }

    fun getSelectedItems(): List<PastureDisplay> {
        return pastures.filter { selectedPastureIds.contains(it.pasture.id) }
    }

    private fun toggleSelection(pastureId: Int) {
        if (!selectedPastureIds.add(pastureId)) {
            selectedPastureIds.remove(pastureId)
        }
        notifyDataSetChanged()
        onSelectionChanged(selectedPastureIds.size)
    }

    private fun colorForState(state: RotationState): Int {
        return when (state) {
            RotationState.Occupied -> Color.rgb(198, 40, 40)
            RotationState.Recovering -> Color.rgb(239, 108, 0)
            RotationState.Available -> Color.rgb(46, 125, 50)
        }
    }

    private fun formatRotationDates(pastureDisplay: PastureDisplay): String {
        val redStart = pastureDisplay.redStartMillis
        val redEnd = pastureDisplay.redEndMillis
        val orangeStart = pastureDisplay.orangeStartMillis
        val orangeEnd = pastureDisplay.orangeEndMillis

        if (redStart == null || redEnd == null || orangeStart == null || orangeEnd == null) {
            return "Sin rotaciones registradas"
        }

        return "Rojo: ${formatDate(redStart)} - ${formatDate(redEnd)}\n" +
            "Anaranjado: ${formatDate(orangeStart)} - ${formatDate(orangeEnd)}"
    }

    private fun formatDate(millis: Long): String = dateFormatter.format(Date(millis))
}
