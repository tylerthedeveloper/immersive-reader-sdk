package com.example.java_based_client.Model;

public class Options {

    public String exitCallback;
    public String uiLang;
    public Integer timeout;

    public Options(String exitCallback, String uiLang, Integer timeout) {
        this.exitCallback = exitCallback;
        this.uiLang = uiLang;
        this.timeout = timeout;
    }
}
