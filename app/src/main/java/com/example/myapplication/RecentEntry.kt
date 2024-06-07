package com.example.myapplication

// imports all important classes
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecentEntry : AppCompatActivity() {
    // variable to access shared prefs
    private lateinit var sharedPreferences: SharedPreferences

    // gets list of data from shared preferences
    private fun getEntriesList(): MutableList<JournalEntry> {
        val entriesJson = sharedPreferences.getString("journal_entries", "[]")
        val type = object : TypeToken<MutableList<JournalEntry>>() {}.type
        return Gson().fromJson(entriesJson, type)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.recent_entry)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // finds the TextView for the header/title and text/content
        val recentEntryTitle: TextView = findViewById(R.id.recent_entry_title)
        val recentEntrySummary: TextView = findViewById(R.id.recent_entry_summary)

        // gets available header and text from SharedPreferences
        val header = sharedPreferences.getString("header", "")
        val text = sharedPreferences.getString("text", "")

        // updates TextViews
        recentEntryTitle.text = header
        recentEntrySummary.text = text

        val entries = getEntriesList()
        if (entries.isNotEmpty()) { // retrieves and displays most recent entry
            val mostRecentEntry = entries.first()
            recentEntryTitle.text = mostRecentEntry.title
            recentEntrySummary.text = mostRecentEntry.content
        }
    }
}
