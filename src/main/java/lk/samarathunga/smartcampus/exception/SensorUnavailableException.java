package lk.samarathunga.smartcampus.exception;

/**
 * Thrown when a POST reading is attempted on a sensor in MAINTENANCE status.
 * Maps to HTTP 403 Forbidden.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
