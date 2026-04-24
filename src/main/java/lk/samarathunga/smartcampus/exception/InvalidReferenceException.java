package lk.samarathunga.smartcampus.exception;

/**
 * Thrown when a sensor references a roomId that does not exist.
 * Maps to HTTP 422 Unprocessable Entity.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
public class InvalidReferenceException extends RuntimeException {
    public InvalidReferenceException(String message) {
        super(message);
    }
}
