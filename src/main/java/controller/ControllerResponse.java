package controller;

import lombok.Data;

/**
 * Generic response wrapper for controller operations.
 *
 * @param <T> The type of data contained in the response.
 */
@Data
public class ControllerResponse<T> {
    /**
     * Message describing the response.
     */
    private final String message;

    /**
     * The data associated with the response, if any.
     */
    private final T data;

    /**
     * Constructs a response with a message and data.
     *
     * @param message The response message.
     * @param data The data associated with the response.
     */
    public ControllerResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    /**
     * Constructs a response with only a message.
     *
     * @param message The response message.
     */
    public ControllerResponse(String message) {
        this.message = message;
        this.data = null;
    }

    /**
     * Retrieves the data associated with this response.
     * present for backwards compatibility.
     * @return The response data, as an Object.
     */
    public Object getData() {
        return data;
    }

    /**
     * Returns a string representation of the response.
     *
     * @return A formatted response string.
     */
    @Override
    public String toString() {
        return message + (data != null ? ": " + data : "");
    }
}
