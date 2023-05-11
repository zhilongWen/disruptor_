package com.at.disruptor._04_netty_disruptor.common;

import java.io.Serializable;

/**
 * @author zero
 * @create 2023-05-10
 */
public class TranslatorData implements Serializable {

    private static final long serialVersionUID = 7981939437001234032L;

    private String id;
    private String name;
    private String message;

    public TranslatorData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TranslatorData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
