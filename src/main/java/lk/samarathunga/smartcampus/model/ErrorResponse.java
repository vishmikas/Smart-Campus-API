package lk.samarathunga.smartcampus.model;

/**
 * Standard JSON error body returned by all exception mappers.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
public class ErrorResponse {

    private String message;
    private int statusCode;
    private String link;

    public ErrorResponse() {}

    public ErrorResponse(String message, int statusCode, String link) {
        this.message = message;
        this.statusCode = statusCode;
        this.link = link;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
