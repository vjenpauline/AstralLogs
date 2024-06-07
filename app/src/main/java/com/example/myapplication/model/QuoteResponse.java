package com.example.myapplication.model;

public class QuoteResponse { // from ZenQuotes
    private final String q; // gets the quote itself
    private final String a; // name of the quote author

    // sets text and author
    public QuoteResponse(String quote, String author) {
        this.q = quote;
        this.a = author;
    }

    //  returns the text and author
    public String getQuote() { return q; }
    public String getAuthor() { return a; }
}