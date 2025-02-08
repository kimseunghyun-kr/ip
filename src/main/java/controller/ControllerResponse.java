package controller;

public class ControllerResponse {

    private final String message;
    private final Object data;

    public ControllerResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ControllerResponse(String message) {
        this.message = message;
        this.data = null;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return message + (data != null ? ": " + data : "");
    }
}

