package shared.network;
import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean success;
    private final String message;
    private final List<String> data;

    private Response(boolean success, String message, List<String> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static Response success(String message) { return new Response(true, message, null); }
    public static Response success(String message, List<String> data) { return new Response(true, message, data); }
    public static Response error(String message) { return new Response(false, message, null); }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<String> getData() { return data; }
}