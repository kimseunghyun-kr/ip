package controller;

import lombok.Data;

@Data
public class ControllerResponse<T> {
    private final String message;
    private final T data;

    public ControllerResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public ControllerResponse(String message) {
        this.message = message;
        this.data = null;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return message + (data != null ? ": " + data : "");
    }
}

