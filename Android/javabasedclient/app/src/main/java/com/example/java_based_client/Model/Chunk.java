package com.example.java_based_client.Model;

public class Chunk {

    public String content;
    public String lang;
    public String mimeType;

    public Chunk(String content, String lang, String mimeType) {
        this.content = content;
        this.lang = lang;
        this.mimeType = mimeType;
    }
}
