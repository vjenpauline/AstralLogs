package com.example.myapplication

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class JournalEntriesAdapter(
    private val entries: MutableList<JournalEntry>,
    private val onDeleteClick: (String) -> Unit,
    private val onItemClick: (JournalEntry) -> Unit, // Add this callback for item clicks
    private var layoutType: String
) : RecyclerView.Adapter<JournalEntriesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.recent_entry_title)
        val contentTextView: TextView = view.findViewById(R.id.recent_entry_summary)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_entryBtn)

        init {
            deleteButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Safely get the JournalEntry at the position, if it exists
                    val entry = entries.getOrNull(position)
                    // Only proceed if the entry and its id are not null
                    entry?.id?.let { nonNullEntryId ->
                        onDeleteClick(nonNullEntryId)
                    }
                }
            }
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val entry = entries.getOrNull(position)
                    entry?.let { nonNullEntry ->
                        onItemClick(nonNullEntry) // Invoke the click callback with the entry
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.titleTextView.text = entry.title
        holder.contentTextView.text = entry.content

        // Format the current date and set it to the date TextView
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        holder.itemView.findViewById<TextView>(R.id.recent_entry_date).text = formattedDate
    }

    override fun getItemCount(): Int {
        // Return the size based on the layout type
        return when (layoutType) {
            "activity_home_page" -> minOf(entries.size, 2)
            "gallery_of_entries" -> entries.size // No minimum for gallery_of_entries, scroll
            else -> entries.size
        }
    }

    // Method to update the layout type
    fun updateLayoutType(newLayoutType: String) {
        layoutType = newLayoutType
        notifyDataSetChanged()
    }

    fun updateEntries(newEntries: MutableList<JournalEntry>) {
        entries.clear()
        entries.addAll(newEntries)
        notifyDataSetChanged()
    }
}