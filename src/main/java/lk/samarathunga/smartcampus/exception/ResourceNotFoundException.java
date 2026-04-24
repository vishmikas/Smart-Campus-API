package lk.samarathunga.smartcampus.exception;

/**
 * Thrown when a requested resource (Room or Sensor) is not found.
 * Maps to HTTP 404 Not Found.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
