package com.example.myapplication

data class JournalEntry(
    val id: String,
    val title: String,
    val content: String,
    val tags: MutableList<String> = mutableListOf() // Add this line to include tags
)