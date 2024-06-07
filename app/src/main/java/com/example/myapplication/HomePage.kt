package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.model.QuoteResponse
import com.example.myapplication.network.ZenQuotesApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomePage : AppCompatActivity() {
    private lateinit var recyclerViewAdapter: JournalEntriesAdapter
    private lateinit var entries: MutableList<JournalEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        // Fetch the current list of entries
        entries = getEntriesList(this)
        updateEntryTracker()

        // Initialize the adapter with the current list of entries and a lambda to handle deletion
        recyclerViewAdapter = JournalEntriesAdapter(entries, { entryId ->
            deleteEntryFromUI(entryId)
        }, { entry ->
            // Handle item click, navigate to AddJournalEntry with the entry data
            val editIntent = Intent(this, AddJournalEntry::class.java)
            editIntent.putExtra("entryId", entry.id)
            editIntent.putExtra("entryTitle", entry.title)
            editIntent.putExtra("entryContent", entry.content)
            // ... pass other properties as needed ...
            startActivity(editIntent)
        }, "activity_home_page")

        recyclerViewAdapter.updateLayoutType("activity_home_page")

        // Set the adapter to the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_recent)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        // Set up the rest of your buttons and fetch quotes as before
        findViewById<ImageButton>(R.id.homeBtn).apply {
            alpha = 0.2f
            setOnClickListener {
                Toast.makeText(this@HomePage, "You're already on this page!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageButton>(R.id.addBtn).setOnClickListener {
            startActivity(Intent(this, AddJournalEntry::class.java))
        }

        findViewById<ImageButton>(R.id.indexBtn).setOnClickListener {
            startActivity(Intent(this, GalleryOfEntries::class.java))
        }

        val userName = intent.getStringExtra("user")
        val inputNameTextView = findViewById<TextView>(R.id.input_name)
        userName?.let {
            inputNameTextView.text = it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } + "."
        }

        fetchDailyQuote()
    }

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://zenquotes.io")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var service = retrofit.create(ZenQuotesApiService::class.java)

    private fun fetchDailyQuote() {
        service.getDailyQuote().enqueue(object : Callback<List<QuoteResponse>> {
            override fun onResponse(call: Call<List<QuoteResponse>>, response: Response<List<QuoteResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val quotesList = response.body()!!
                    if (quotesList.isNotEmpty()) {
                        val firstQuote = quotesList.first()
                        runOnUiThread {
                            val quoteTextView: TextView = findViewById(R.id.quote_full)
                            val authorTextView: TextView = findViewById(R.id.quote_author)
                            quoteTextView.text = firstQuote.getQuote()
                            authorTextView.text = firstQuote.getAuthor()
                        }
                    } else {
                        showError("No quotes received.")
                    }
                } else {
                    showError("Failed to load: ${response.errorBody()?.string()}.")
                }
            }

            override fun onFailure(call: Call<List<QuoteResponse>>, t: Throwable) {
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun getEntriesList(context: Context): MutableList<JournalEntry> {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val entriesJson = sharedPreferences.getString("journal_entries", "[]")
        val type = object : TypeToken<MutableList<JournalEntry>>() {}.type
        return Gson().fromJson(entriesJson, type)
    }

    private fun deleteEntryFromUI(deletedEntryId: String) {
        // Find the entry in the list and remove it
        val entryIndex = entries.indexOfFirst { it.id == deletedEntryId }
        if (entryIndex != -1) {
            entries.removeAt(entryIndex)
            recyclerViewAdapter.notifyItemRemoved(entryIndex)
            // Save the updated list to SharedPreferences or database
            saveEntriesList(entries)
        }
        updateEntryTracker()
    }

    private fun saveEntriesList(entries: MutableList<JournalEntry>) {
        // Serialize and save the entries list to SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val entriesJson = Gson().toJson(entries)
        editor.putString("journal_entries", entriesJson)
        editor.apply()
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        updateRecentEntries()
        // Update the entry tracker with the new count
        updateEntryTracker()
    }

    private fun updateRecentEntries() {
        val entries = getEntriesList(this)
        if (entries.isNotEmpty()) {
            // Update your RecyclerView with the new entries
            recyclerViewAdapter.updateEntries(entries)
        }
    }

    private fun updateEntryTracker() {
        val trackerTextView: TextView = findViewById(R.id.tracker_amnt)
        trackerTextView.text = entries.size.toString()
    }

}