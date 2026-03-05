package com.example.demo_api_rest_webflux;

public class Notification {
    private String id;
    private String message;
    private boolean urgent;

    public Notification(String id, String message, boolean urgent) {
        this.id = id;
        this.message = message;
        this.urgent = urgent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    @Override
    public String toString() {
        return "Notification{" + "id='" + id + '\'' + ", message='" + message + '\'' + ", urgent=" + urgent + '}';
    }
}
