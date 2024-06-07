package com.example.myapplication

// imports all important classes
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
    private val entries: MutableList<JournalEntry>, // list of journal entries
    private val onDeleteClick: (String) -> Unit, // callback for delete clicks
    private val onItemClick: (JournalEntry) -> Unit, // callback for item clicks
    private var layoutType: String // layout to determine how many recyclerview items shown
) : RecyclerView.Adapter<JournalEntriesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.recent_entry_title)
        val contentTextView: TextView = view.findViewById(R.id.recent_entry_summary)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_entryBtn)

        init {
            deleteButton.setOnClickListener {  // set a click listener on the delete button
                val position = bindingAdapterPosition // get current position
                if (position != RecyclerView.NO_POSITION) {
                    val entry = entries.getOrNull(position) // proceeds if the entry and its id are not null
                    entry?.id?.let { nonNullEntryId ->
                        onDeleteClick(nonNullEntryId) // call the delete click function
                    }
                }
            }
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val entry = entries.getOrNull(position)
                    entry?.let { nonNullEntry ->
                        onItemClick(nonNullEntry) // call the item click function
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
        // sets title and content
        holder.titleTextView.text = entry.title
        holder.contentTextView.text = entry.content

        // formats the current date and sets it to TextView for display
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        holder.itemView.findViewById<TextView>(R.id.recent_entry_date).text = formattedDate
    }

    override fun getItemCount(): Int {
        // returns the number of items based on the layout type
        return when (layoutType) {
            "activity_home_page" -> minOf(entries.size, 2) // limits to 2 on homepage (for recents)
            "gallery_of_entries" -> entries.size // no minimum for gallery_of_entries, scroll
            else -> entries.size
        }
    }

    // updates the layout type
    fun updateLayoutType(newLayoutType: String) {
        layoutType = newLayoutType
        notifyDataSetChanged()
    }

    // updates list of entries
    fun updateEntries(newEntries: MutableList<JournalEntry>) {
        entries.clear() // clears list
        entries.addAll(newEntries) // adds new entries
        notifyDataSetChanged()
    }
}