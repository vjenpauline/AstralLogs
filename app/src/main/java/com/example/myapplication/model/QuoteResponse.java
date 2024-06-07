package com.example.myapplication.model;

public class QuoteResponse {
    private final String q; // The quote text
    private final String a; // The author

    // Constructor to set the quote text and author
    public QuoteResponse(String quote, String author) {
        this.q = quote;
        this.a = author;
    }

    public String getQuote() { return q; }
    public String getAuthor() { return a; }
}