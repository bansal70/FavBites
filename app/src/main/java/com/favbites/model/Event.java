package com.favbites.model;

/*
 * Created by rishav on 8/21/2017.
 */

public class Event {
    private int key;
    private String value;

    public Event(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
