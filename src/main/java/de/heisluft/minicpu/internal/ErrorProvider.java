package de.heisluft.minicpu.internal;

public class ErrorProvider {

    private String message = "";
    private int position = -1;

    public void error(String message, int position) {
        this.message = message;
        this.position = position;
    }

    public boolean errored() {
        return !message.isEmpty();
    }

    public String getMessage() {
        return message;
    }

    public int getPosition() {
        return position;
    }
}

