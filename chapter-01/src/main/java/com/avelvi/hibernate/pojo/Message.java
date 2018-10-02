package com.avelvi.hibernate.pojo;

public class Message {

    private String text;

    public Message() {
    }

    public Message(String text) {
        setText(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
